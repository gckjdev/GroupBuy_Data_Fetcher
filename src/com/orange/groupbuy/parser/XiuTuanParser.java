package com.orange.groupbuy.parser;

import org.jdom.Element;

public class XiuTuanParser extends Tuan800Parser {
	
	@Override
	public String getLoc(Element productElement){		
		String loc =  getFieldValue(productElement, "loc");
		if(loc == null)
			return null;
		int index = loc.indexOf("?");
		if(index >= 0){
			loc = loc.substring(0, index);
		}
		return loc;
	}


}
