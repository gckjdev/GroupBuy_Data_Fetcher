package com.orange.groupbuy.parser;


import com.mongodb.DBObject;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.manager.ProductManager;

public class FiveEightParser extends Tuan800Parser {
	final static String WEBSITE = "58";
	final static String SITEURL = "t.58.com";
	
	@Override
	public boolean isCheckExternalID(String siteId, String externalID) {
		DBObject obj = ProductManager.findProductByExternalID(mongoClient, siteId, externalID);
		if(obj == null)
			return false;
		else 
			return true;
	}
	
	@Override
	public int convertCategory(String category) {
		return DBConstants.C_CATEGORY_UNKNOWN;
	}

	@Override
	public String generateWapLoc(String webURL, String imageURL) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getDefaultSiteURL(){
		return "http://t.58.com";
	}


}
