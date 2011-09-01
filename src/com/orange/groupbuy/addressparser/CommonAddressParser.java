package com.orange.groupbuy.addressparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.orange.groupbuy.constant.DBConstants;

public abstract class CommonAddressParser {

	public static final Logger log = Logger.getLogger(CommonAddressParser.class
			.getName());
	
	Map<String, List<String>> cache = new HashMap<String, List<String>>(); 
	String encoding = "UTF-8";
	public List<String> parseAddress(String url){
		
		// read from cache
		if (cache.containsKey(url))
			return cache.get(url);
		
		// read from external server
		List<String> resultList = doParseAddress(url);
		
		// write into cache
		if (resultList != null && resultList.size() > 0){
			cache.put(url, resultList);
		}		
		
		return resultList;
	}
	
	public abstract List<String> doParseAddress(String url);

	public static CommonAddressParser findParserById(String siteId){
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_GAOPENG))
			return new GaopengAddressParser();
		else 
			return new GenericAddressParser();
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	

}
