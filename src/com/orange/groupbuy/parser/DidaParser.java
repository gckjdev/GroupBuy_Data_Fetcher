package com.orange.groupbuy.parser;

public class DidaParser extends Hao123Parser {

	@Override
	public String generateWapLoc(String loc, String imageURL) {
		
		final String basicwapURL = "http://www.didatuan.com/wap/wap_index.php?gid=";
		String id = getIDFromWeb("team/", null, loc);
		if(id == null)
			return null;
		else 
			return basicwapURL.concat(id);
	}
}
