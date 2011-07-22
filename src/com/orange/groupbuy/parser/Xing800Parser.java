
package com.orange.groupbuy.parser;

import com.orange.groupbuy.constant.DBConstants;

public class Xing800Parser extends Hao123Parser {

	@Override
	public int convertCategory(String category) {		
		return DBConstants.C_CATEGORY_SHOPPING;
	}
}
