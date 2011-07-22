package com.orange.groupbuy.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public abstract class CommonGroupBuyParser {

	enum COUNTER_TYPE {		
		EXIST,
		UPDATE,		
		INSERT,
		FAIL				
	};
	
	public static final Logger log = Logger.getLogger(CommonGroupBuyParser.class
			.getName());
	
	final static int PARSER_HAO123 = 1;
	final static int PARSER_MEITUAN = 2;
	final static int PARSER_LASHOU = 3;
	
	
	MongoDBClient mongoClient;
	String siteId;
	int insertCounter = 0;
	int updateCounter = 0;
	int failCounter = 0;
	int existCounter = 0;
	int totalCounter = 0;

	public void incCounter(COUNTER_TYPE counterType){
		totalCounter ++;
		switch (counterType){
		case EXIST:
			existCounter ++;			
			break;
		case UPDATE:
			updateCounter ++;
			break;
		case INSERT:
			insertCounter ++;
			break;
		case FAIL:
			failCounter ++;
			break;
		}
	}
	
	public void incInsertCounter(){
		insertCounter ++;
		totalCounter ++;
	}
	
	public void incFailCounter(){
		failCounter ++;
		totalCounter ++;
	}
	
	public void incUpdateCounter(){
		updateCounter ++;
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
			case PARSER_LASHOU:
				return new LashouParser();
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
			
			CommonAddressParser addressParser = CommonAddressParser.findParserById(siteId);
			if (addressParser == null){
				log.severe("cannot find address parser for site = "+siteId);
				return false;
			}
			
			boolean result = parseElement(root, addressParser);			
			log.info("parse finish, total "+totalCounter+" parsed, "+
					insertCounter+" insert, "+
					updateCounter+" update, "+
					existCounter+" exist, "+
					failCounter+" failed.");
			
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
	
	public abstract boolean parseElement(Element root, CommonAddressParser addressParser);
	
	public abstract int convertCategory(String category);

	public void setMongoClient(MongoDBClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	
	public Product saveProduct(MongoDBClient mongoClient, String city, String loc, String image, String title, Date startDate, Date endDate, 
			double price, double value, int bought, String siteId, String siteName, String siteURL,
			int major, List<String> addressList, CommonAddressParser addressParser){
		
		// check if product exist
		Product product;
		product = ProductManager.findProduct(mongoClient, loc, city);
		if (product != null){
			// update bought
			if (product.getBought() != bought){
				product.setBought(bought);
				log.info("update existing product, product = "+product.toString());
				ProductManager.save(mongoClient, product);
				
				incCounter(COUNTER_TYPE.UPDATE);
			}
			else{
				log.info("product exist, no need to update, product id="+product.getObjectId());
				incCounter(COUNTER_TYPE.EXIST);				
			}

			return null;
		}
		
		// create a new product
		product = new Product();
		if (product.setMandantoryFields(city, loc, image, title, startDate, endDate, 
				price, value, bought, siteId, siteName, siteURL)){
			
			product.setMajor(major);			
			
			// read address if not given
			if (addressList == null || addressList.size() == 0){
				addressList = addressParser.parseAddress(loc);
			}

			// set address
			if (addressList != null && addressList.size() > 0){
				product.setAddress(addressList);
			}
			
			if (ProductManager.createProduct(mongoClient, product)){		
				log.info("create new product success, product = "+product.toString());
				incCounter(COUNTER_TYPE.INSERT);
				return product;
			}
			else{
				log.info("create new product failure, product = "+product.toString());
				incCounter(COUNTER_TYPE.FAIL);
				return null;
			}
		}
		else{			
			log.info("fail to set product mandantory fields, loc="+loc+",city="+city+
					",image="+image+",title="+title+",startDate="+startDate+",endDate="+endDate+
					",price="+price+",value="+value+",bought="+bought+
					",siteId="+siteId+",siteName="+siteName+",siteURL="+siteURL);
			incCounter(COUNTER_TYPE.FAIL);
			return null;
		}		
	}
	
	
}
