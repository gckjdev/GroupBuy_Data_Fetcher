package com.orange.groupbuy.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.orange.common.mongodb.MongoDBClient;

public abstract class CommonGroupBuyParser {

	public static final Logger log = Logger.getLogger(CommonGroupBuyParser.class
			.getName());
	
	final static int PARSER_HAO123 = 1;
	final static int PARSER_MEITUAN = 2;
	
	
	MongoDBClient mongoClient;
	String siteId;
	int successCounter = 0;
	int failCounter = 0;
	int totalCounter = 0;
	
	public void incSuccessCounter(){
		successCounter ++;
		totalCounter ++;
	}
	
	public void incFailCounter(){
		failCounter ++;
		totalCounter ++;
	}
	
	public String getFieldValue(Element e, String fieldName){
		Element subElement = e.getChild(fieldName);
		if (subElement == null)
			return null;
		else
			return subElement.getText();
	}
	
	public Element getFieldElement(Element e, String... fieldNames){
		
		List<?> elementList = getFieldBlock(e, fieldNames);
		if (elementList == null)
			return null;
		
		Iterator<?> it = elementList.iterator();
		if (it.hasNext() == false)
			return null;
		
		return (Element)it.next();
	}
	
	
	public List<?> getFieldBlock(Element e, String... fieldNames){
		
		if (fieldNames == null || fieldNames.length == 0)
			return null;
		
		List<?> elementList = e.getChildren(fieldNames[0]);
		for (int i=1; i<fieldNames.length; i++){

			if (elementList == null)
				return null;
			
			Iterator<?> it = elementList.iterator();
			if (it.hasNext() == false)
				return null;			
			
			Element firstElement = (Element)it.next();
			if (firstElement == null)
				return null;
			
			elementList = firstElement.getChildren(fieldNames[i]);
		}
		
		return elementList;		
	}
	
	public static CommonGroupBuyParser getParser(int parserType) {
		
		switch (parserType){
			case PARSER_HAO123:
				return new Hao123Parser();
			case PARSER_MEITUAN:
				return new MeituanParser();
		}
		
		return null;
	}

	public boolean parse(String localFilePath){
		doParse(localFilePath);
		return true;
	}
	
	public boolean doParse(String localFilePath) {
		SAXBuilder sb = new SAXBuilder();
		Document doc;
		try {
			doc = sb.build(new FileInputStream(localFilePath));
			Element root = doc.getRootElement();
			
			if (root == null)
				return false;
			
			boolean result = parseElement(root);			
			log.info("parse finish, total "+totalCounter+" parsed, "+successCounter+" succeed, "+failCounter+" failed.");
			
			return result;

		} catch (FileNotFoundException e) {
			log.severe("<doParse> file="+localFilePath+", FileNotFoundException="+e.toString());
			return false;
		} catch (JDOMException e) {
			log.severe("<doParse> file="+localFilePath+", JDOMException="+e.toString());
			return false;
		} catch (IOException e) {
			log.severe("<doParse> file="+localFilePath+", IOException="+e.toString());
			return false;
		}

	}
	
	public abstract boolean parseElement(Element root);

	public void setMongoClient(MongoDBClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
}
