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
		return null;
	}
}
