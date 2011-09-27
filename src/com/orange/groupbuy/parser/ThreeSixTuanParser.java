package com.orange.groupbuy.parser;

import com.orange.groupbuy.constant.DBConstants;

public class ThreeSixTuanParser extends Hao123Parser {
	
	@Override
	public boolean disableAddressParsing() {
		return true;
	}
	
	@Override
	public int convertCategory(String category) {
		return DBConstants.C_CATEGORY_FACE;
	}
}
