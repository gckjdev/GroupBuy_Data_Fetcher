package com.orange.groupbuy.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import org.jdom.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.utils.DateUtil;
import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.Gps;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class FiveEightParser extends Tuan800Parser {
	final static String WEBSITE = "58";
	final static String SITEURL = "t.58.com";
	
	@Override
	public boolean isExsit(MongoDBClient mongoClient, String extraID) {
		BasicDBObject query = new BasicDBObject();
		query.put(DBConstants.F_EXTRA_ID, extraID);
		DBObject obj = mongoClient.findOne(DBConstants.T_PRODUCT, query);
		if (obj == null)
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
