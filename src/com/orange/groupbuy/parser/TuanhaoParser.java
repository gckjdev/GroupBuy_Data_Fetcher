package com.orange.groupbuy.parser;

public class TuanhaoParser extends Tuan800Parser {
	
	@Override
	public String generateWapLoc(String loc, String imageURL) {
		final String basicwapURL = "http://m.tuanok.com/mobile_team.php?id=";
		String id = getIDFromWeb("id=", null, loc);
		if(id == null)
			return null;
		else 
			return basicwapURL.concat(id);
	}
}
