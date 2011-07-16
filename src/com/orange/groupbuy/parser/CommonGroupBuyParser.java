package com.orange.groupbuy.parser;

public abstract class CommonGroupBuyParser {

	final static int PARSER_HAO123 = 1;
	
	public static CommonGroupBuyParser getParser(int parserType) {
		
		switch (parserType){
			case PARSER_HAO123:
				return new Hao123Parser();
		}
		
		return null;
	}

	public boolean parse(String localFilePath){
		doParse(localFilePath);
		return true;
	}
	
	public abstract boolean doParse(String localFilePath);

}
