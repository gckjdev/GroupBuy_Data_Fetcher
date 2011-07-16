package com.orange.groupbuy.fetcher;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.BasicProcessorRequest;
import com.orange.common.processor.CommonProcessor;
import com.orange.groupbuy.constant.DBConstants;

public class Fetcher extends CommonProcessor {

	static final String MONGO_SERVER = "localhost";
	static final String MONGO_USER = "";
	static final String MONGO_PASSWORD = "";
	
	protected MongoDBClient mongoClient = new MongoDBClient(MONGO_SERVER, DBConstants.D_GROUPBUY, MONGO_USER, MONGO_PASSWORD);
	
	@Override
	public MongoDBClient getMongoDBClient() {
		return mongoClient;
	}
	
	public void putReadTaskRequest(){
		ReadTaskRequest request = new ReadTaskRequest();
		putRequest(request);
	}

	public static void main(String[] args){		
		Fetcher dataFetcher = new Fetcher();
		dataFetcher.putReadTaskRequest();
		Thread thread = new Thread(dataFetcher);
		thread.start();
	}

}
