package com.orange.groupbuy.fetcher;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;

import com.mongodb.DBObject;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.CommonProcessor;
import com.orange.common.utils.FileUtils;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.manager.FetchTaskManager;

public class Fetcher extends CommonProcessor {

	public static MongoDBClient mongoClient = new MongoDBClient(DBConstants.D_GROUPBUY);
	
	@Override
	public MongoDBClient getMongoDBClient() {
		return mongoClient;
	}
	
	private void putReadTaskRequest(DBObject task){
		FetchGroupBuyDataRequest request = new FetchGroupBuyDataRequest();
		request.setTask(task);
		putRequest(request);
	}

	// read task from DB
	static class ReadTaskTimerTask extends java.util.TimerTask{
		
		Fetcher dataFetcher;
		
		public ReadTaskTimerTask(Fetcher dataFetcher){
			this.dataFetcher = dataFetcher;
		}
		
        @Override
        public void run() {

        	// fetch active task and put into queue
    		DBObject task = FetchTaskManager.obtainOneTask(dataFetcher.getMongoDBClient());
    		if (task == null)
    			return;
    		
    		dataFetcher.putReadTaskRequest(task);
        }
    }
	
	// reset and activate all task in DB 
	static class ResetTaskTimer extends java.util.TimerTask{
		
		Fetcher dataFetcher;
		
		public ResetTaskTimer(Fetcher dataFetcher){
			this.dataFetcher = dataFetcher;
		}
		
        @Override
        public void run() {
        	
        	log.info("<ResetTaskTimer> timer fired");

        	// fetch active task and put into queue
    		FetchTaskManager.resetAllTask(dataFetcher.getMongoDBClient());
    		
    		// set next timer
			Timer newResetTaskTimer = new Timer();
			newResetTaskTimer.schedule(new ResetTaskTimer(dataFetcher), ResetTaskTimer.getTaskDate());
        }
        
        static public Date getTaskDate(){
        	
        	int scheduleHour = 3;		// 3 AM of the day
        	
    		TimeZone timeZone = TimeZone.getTimeZone("GMT+0800");
    		Calendar now = Calendar.getInstance(timeZone);
    		now.setTime(new Date());
    		
    		if (now.get(Calendar.HOUR_OF_DAY) >= scheduleHour){
    			now.add(Calendar.DAY_OF_MONTH, 1);
    		}
    		
    		now.set(Calendar.HOUR_OF_DAY, scheduleHour);
    		now.set(Calendar.MINUTE, 0);
    		now.set(Calendar.SECOND, 0);
    		now.set(Calendar.MILLISECOND, 0);    			
    		
        	log.info("<ResetTaskTimer> next timer set to "+now.getTime().toString());
    		
    		return now.getTime();
        }
    }
	
	   public static void deleteOldData(File file) throws IOException{
	        
	        if (file.exists() && file.isDirectory()) {
	            for (File subf : file.listFiles()) {
	                if (subf.isDirectory()) {
	                    checkDateAndDelete(new File(subf.getAbsolutePath()));
	                }
	            }
	        }
	    }
	    
	    public static void checkDateAndDelete(File file) throws IOException{
	        Calendar calnow = Calendar.getInstance();
	        Calendar cal=Calendar.getInstance();
	        
	        Date datenow = new Date();
	        calnow.setTime(datenow);
	        
	        Date date_modify = new Date(file.lastModified());
	        cal.setTime(date_modify);
	        
	        int dec = calnow.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
	        if (dec > 7) {
	            FileUtils.delFileOrDir(file);
	        }
	    }
	
	public static void main(String[] args){		
						
		final int MAX_THREAD_NUM = 5;
		final int READ_TASK_INTERVAL = 1000;		// 1 second
		
		boolean reset = false;
		if (args != null && args.length > 0){
			reset = (Integer.parseInt(args[0]) == 1);
		}
		
		for (int i=0; i<MAX_THREAD_NUM; i++){
			
			Fetcher dataFetcher = new Fetcher();
			Thread thread = new Thread(dataFetcher);
			thread.start();
			
			// use the first dataFetcher for timer initlization
			if (i == 0){
				if (reset){
					log.info("<ResetTaskTimer> immediately");
					FetchTaskManager.resetAllTask(dataFetcher.getMongoDBClient());

                    try {
                        deleteOldData(new File(FetchGroupBuyDataRequest.DEFAULT_FILE_PATH));
                    } catch (IOException e) {
                        log.info("delete old downloaded file in ./data failure.");
                    }
				}
				
				Timer readTaskTimer = new Timer();
				readTaskTimer.schedule(new ReadTaskTimerTask(dataFetcher), READ_TASK_INTERVAL, READ_TASK_INTERVAL);

				Timer resetTaskTimer = new Timer();
				resetTaskTimer.schedule(new ResetTaskTimer(dataFetcher), ResetTaskTimer.getTaskDate());
				
				// the following code is just for test now
				// resetTaskTimer.schedule(new ResetTaskTimer(dataFetcher), new Date());
			}
		}
		
	}

}
