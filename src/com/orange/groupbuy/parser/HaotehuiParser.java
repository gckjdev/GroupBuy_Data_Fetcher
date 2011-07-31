package com.orange.groupbuy.parser;

public class HaotehuiParser extends Hao123Parser {

	@Override
	public String generateWapLoc(String loc, String imageURL) {
		
		final String basicwapURL = "http://m.haotehui.com/detail.jhtml?id=";
		String id = getIDFromWeb("2011/", ".html", loc);
		if(id == null)
			return null;
		else 
			return basicwapURL.concat(id);
	}
}
