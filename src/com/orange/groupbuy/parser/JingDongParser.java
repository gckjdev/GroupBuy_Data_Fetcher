package com.orange.groupbuy.parser;

import com.orange.groupbuy.constant.DBConstants;

public class JingDongParser extends Tuan800Parser {

	@Override
	public boolean disableAddressParsing() {
		return true;
	}
	
	@Override
	public int convertCategory(String category) {			
		return DBConstants.C_CATEGORY_SHOPPING;
	}
}
