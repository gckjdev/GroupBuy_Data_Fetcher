package com.orange.groupbuy.fetcher;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.BasicProcessorRequest;
import com.orange.common.processor.CommonProcessor;
import com.orange.common.utils.DateUtil;
import com.orange.common.utils.http.HttpDownload;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.manager.FetchTaskManager;
import com.orange.groupbuy.parser.CommonGroupBuyParser;

public class FetchGroupBuyDataRequest extends BasicProcessorRequest {

	public static String DEFAULT_FILE_PATH = "./data";	
	DBObject task;
	
	public synchronized static String getPath(){
		String dateDir = DateUtil.dateToStringByFormat(new Date(), "yyyyMMdd");
		String fileDirPath = DEFAULT_FILE_PATH + "/" + dateDir; 
		File dir = new File(fileDirPath);
		if (!dir.exists()){
			dir.mkdirs();
		}		

		return fileDirPath;
	}
	
	@Override
	public void execute(CommonProcessor mainProcessor) {
		boolean result = true;
		MongoDBClient mongoClient = mainProcessor.getMongoDBClient();		
		
		// HTTP fetch data and save file locally
		String url = (String)task.get(DBConstants.F_TASK_URL);
		String siteId = (String)task.get(DBConstants.F_TASK_SITE_ID);	
		String localFilePath = (String)task.get(DBConstants.F_TASK_FILE_PATH);
		
		mainProcessor.severe(this, "execute task="+task.toString());
		
		if (siteId == null || url == null){
			mainProcessor.severe(this, "siteId("+siteId+") or url("+url+") is null");
			return;
		}
		
		String dir = getPath();
		String timeStamp = String.valueOf(System.currentTimeMillis());
		String filename = siteId.concat("_").concat(timeStamp);
		
		if (localFilePath == null || localFilePath.length() == 0){
			localFilePath = dir.concat("/").concat(filename);
			mainProcessor.info(this, "Download file from "+url+", save to "+localFilePath);
			result = HttpDownload.downloadFile(url, localFilePath);
			if (!result){
				mainProcessor.warning(this, "Fail to download file from "+url+", save to "+localFilePath);
				return;
			}		
			mainProcessor.info(this, "Download file OK! from "+url+", save to "+localFilePath);
		}
		
		// update task status to save OK
		FetchTaskManager.taskDownloadFileSuccess(mongoClient, task, localFilePath);
		
		// get parser for parsing data
		CommonGroupBuyParser parser = CommonGroupBuyParser.getParser(siteId);
		parser.setMongoClient(mongoClient);
		parser.setSiteId(siteId);
		
		// start parsing data file and save data to DB
		result = parser.parse(localFilePath);
		if (!result){
			mainProcessor.warning(this, "Fail to parse file "+localFilePath);
			setTaskStatistic(task, parser);
			FetchTaskManager.taskFailure(mongoClient, task);
			return;
		}
				
		// update task status to finish
		setTaskStatistic(task, parser);
		FetchTaskManager.taskClose(mongoClient, task);
	}
	
	private void setTaskStatistic(DBObject task, CommonGroupBuyParser parser){
		DBObject stat = new BasicDBObject();
		
		stat.put(DBConstants.F_COUNTER_ADDRESS_TOTAL, parser.getTotalAddressCounter());
		stat.put(DBConstants.F_COUNTER_ADDRESS_API, parser.getAddressApiCounter());
		stat.put(DBConstants.F_COUNTER_ADDRESS_FAIL, parser.getAddressFailCounter());
		stat.put(DBConstants.F_COUNTER_ADDRESS_HTML, parser.getAddressHtmlCounter());

		stat.put(DBConstants.F_COUNTER_TOTAL, parser.getTotalCounter());
		stat.put(DBConstants.F_COUNTER_INSERT, parser.getInsertCounter());
		stat.put(DBConstants.F_COUNTER_UPDATE, parser.getUpdateCounter());
		stat.put(DBConstants.F_COUNTER_EXIST, parser.getExistCounter());
		stat.put(DBConstants.F_COUNTER_FAIL, parser.getFailCounter());
		
		task.put(DBConstants.F_STAT, stat);

	}

	public void setTask(DBObject task) {
		this.task = task;
	}

}
