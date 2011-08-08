package com.orange.groupbuy.parser;

import java.util.List;

import org.jdom.Element;

public class TwoFourQuanParser extends Tuan800Parser {

	@Override
	public List<?> getShopListBlock(Element productElement, Element dataElement){
		return getFieldBlock(dataElement, "shops", "shop");		
	}
	
	@Override
	public String generateWapLoc(String webURL, String imageURL){
//		wap：http://m.24quan.com/deal?id=27410
//		web：http://www.24quan.com/team/27410.html
		
		final String basicWapURL = "http://m.24quan.com/deal?id=";
		String id = getIDFromWeb("team/", ".html", webURL);
		if(id == null || id.trim().isEmpty())
			return null;
		String wapURL = basicWapURL.concat(id);
		return wapURL;
	}
}
