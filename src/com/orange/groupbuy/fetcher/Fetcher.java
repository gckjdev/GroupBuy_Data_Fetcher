package com.orange.groupbuy.fetcher;

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
	
	protected MongoDBClient mongoClient = new MongoDBClient(MONGO_SERVER, DBConstants.D_GROUPBUY, MONGO_USER, MONGO_PASSWORD);
	
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
	
	public static void main(String[] args){		
				
		Fetcher dataFetcher = new Fetcher();
		Thread thread = new Thread(dataFetcher);
		thread.start();
		
		Timer readTaskTimer = new Timer();
		readTaskTimer.schedule(new ReadTaskTimerTask(dataFetcher), 1000, 1000);
	}

}
