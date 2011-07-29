package com.orange.groupbuy.fetcher;

import java.util.UUID;

import com.mongodb.DBObject;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.BasicProcessorRequest;
import com.orange.common.processor.CommonProcessor;
import com.orange.common.utils.http.HttpDownload;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.manager.FetchTaskManager;
import com.orange.groupbuy.parser.CommonGroupBuyParser;

public class FetchGroupBuyDataRequest extends BasicProcessorRequest {

	public static String DEFAULT_FILE_PATH = "./data/";	
	DBObject task;
	
	@Override
	public void execute(CommonProcessor mainProcessor) {
		boolean result = true;
		MongoDBClient mongoClient = mainProcessor.getMongoDBClient();		
		
		// HTTP fetch data and save file locally
		String url = (String)task.get(DBConstants.F_TASK_URL);
		String localFilePath = DEFAULT_FILE_PATH + UUID.randomUUID().toString() + ".xml";
		mainProcessor.info(this, "Download file from "+url+", save to "+localFilePath);
		result = HttpDownload.downloadFile(url, localFilePath);
		if (!result){
			mainProcessor.warning(this, "Fail to download file from "+url+", save to "+localFilePath);
			return;
		}		
		mainProcessor.info(this, "Download file OK! from "+url+", save to "+localFilePath);		
		
		// update task status to save OK
		FetchTaskManager.taskDownloadFileSuccess(mongoClient, task, localFilePath);
		
		// get parser for parsing data
		int parseType = ((Double)task.get(DBConstants.F_TASK_PARSER_TYPE)).intValue();
		String siteId = (String)task.get(DBConstants.F_TASK_SITE_ID);		
		CommonGroupBuyParser parser = CommonGroupBuyParser.getParser(siteId);
		parser.setMongoClient(mongoClient);
		parser.setSiteId(siteId);
		
		// start parsing data file and save data to DB
		result = parser.parse(localFilePath);
		if (!result){
			mainProcessor.warning(this, "Fail to parse file "+localFilePath);
			return;
		}
		
		// update task status to finish
		FetchTaskManager.taskClose(mongoClient, task);
	}

	public void setTask(DBObject task) {
		this.task = task;
	}

}
