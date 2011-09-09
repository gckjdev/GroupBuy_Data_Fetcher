package com.orange.groupbuy.parser;


import org.jdom.Element;

public class ZuiTaoParser extends Hao123Parser {
	
	@Override
	Element getDataElement(Element productElement){
		Element e = getFieldElement(productElement, "data");
		if (e == null){
			return getFieldElement(productElement, "data", "display");
		}
		else {
			return e;
		}
	}
	
	@Override
	String getStartTimeString(Element data){
		String value = getFieldValue(data, "starttime");
		if (value == null)
			return getFieldValue(data, "startTime");
		else
			return value;
	}

	@Override
	String getEndTimeString(Element data){
		String value = getFieldValue(data, "endtime");
		if (value == null)
			return getFieldValue(data, "endTime");
		else
			return value;
	}

	@Override
	String getImage(Element data){
		String image = getFieldValue(data, "image");
		if (image == null)
			return null;
		
		return image.replaceAll("http://www.zuitao.com", ""); 
	}

}
