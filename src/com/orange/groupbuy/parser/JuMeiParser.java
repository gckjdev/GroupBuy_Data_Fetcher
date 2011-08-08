package com.orange.groupbuy.parser;

import org.omg.CORBA.PRIVATE_MEMBER;

public class JuMeiParser extends Hao123Parser {

	@Override
	public boolean disableAddressParsing() {
		return true;
	}
	
	@Override
	public String generateWapLoc(String webURL, String imageURL){
		
//		wap：http://m.jumei.com/i/MobileHome/brief_view?hash_id=skinzjy0808
//		web：http://www.jumei.com/i/deal/skinzjy0808.html
		
		final String basicWapURL = "http://m.jumei.com/i/MobileHome/brief_view?hash_id=";
		String id = getIDFromWeb("deal/", ".html", webURL);
		if(id == null || id.trim().isEmpty())
			return null;
		
		String wapURL = basicWapURL.concat(id);
		return wapURL;
	}

}
