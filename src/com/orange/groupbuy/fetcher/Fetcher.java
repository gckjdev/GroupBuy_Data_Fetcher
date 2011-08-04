package com.orange.groupbuy.fetcher;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;

import com.mongodb.DBObject;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.BasicProcessorRequest;
import com.orange.common.processor.CommonProcessor;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.manager.FetchTaskManager;

public class Fetcher extends CommonProcessor {

	static final String MONGO_SERVER = "localhost";
	static final String MONGO_USER = "";
	static final String MONGO_PASSWORD = "";
	
	public static MongoDBClient mongoClient = new MongoDBClient(MONGO_SERVER, DBConstants.D_GROUPBUY, MONGO_USER, MONGO_PASSWORD);
	
	@Override
	public MongoDBClient getMongoDBClient() {
		return mongoClient;
	}
	
	private void putReadTaskRequest(DBObject task){
		FetchGroupBuyDataRequest request = new FetchGroupBuyDataRequest();
		request.setTask(task);
		putRequest(request);
	}

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
	
	public static void main(String[] args){		
				
		final int MAX_THREAD_NUM = 5;
		
		for (int i=0; i<MAX_THREAD_NUM; i++){
			Fetcher dataFetcher = new Fetcher();
			Thread thread = new Thread(dataFetcher);
			thread.start();
			
			if (i == 0){
				Timer readTaskTimer = new Timer();
				readTaskTimer.schedule(new ReadTaskTimerTask(dataFetcher), 1000, 1000);

				Timer resetTaskTimer = new Timer();
				resetTaskTimer.schedule(new ResetTaskTimer(dataFetcher), ResetTaskTimer.getTaskDate());
				
				// the following code is just for test
				// resetTaskTimer.schedule(new ResetTaskTimer(dataFetcher), new Date());
			}
		}
		
	}

}
