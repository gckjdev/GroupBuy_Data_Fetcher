package com.orange.groupbuy.parser;


import org.jdom.Element;

public class ZuiTaoParser extends Hao123Parser {
	
	@Override
	Element getDataElement(Element productElement){
		return getFieldElement(productElement, "data");
	}
	
	@Override
	String getStartTimeString(Element data){
		return getFieldValue(data, "starttime");
	}

	@Override
	String getEndTimeString(Element data){
		return getFieldValue(data, "endtime");
	}

	@Override
	String getImage(Element data){
		String image = getFieldValue(data, "image");
		if (image == null)
			return null;
		
		return image.replaceAll("http://www.zuitao.com", ""); 
	}

}
