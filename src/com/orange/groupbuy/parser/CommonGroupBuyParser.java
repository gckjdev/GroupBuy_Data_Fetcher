package com.orange.groupbuy.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.jdom.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;


import com.orange.common.mongodb.MongoDBClient;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public abstract class CommonGroupBuyParser {

	enum COUNTER_TYPE {		
		EXIST,
		UPDATE,		
		INSERT,
		FAIL				
	};
	
	enum ADDRESS_COUNTER_TYPE {		
		FROM_API,
		FROM_HTML,		
		FAIL
	};
	
	public static final Logger log = Logger.getLogger(CommonGroupBuyParser.class
			.getName());
	
	final static int PARSER_HAO123 = 1;
	final static int PARSER_MEITUAN = 2;
	final static int PARSER_LASHOU = 3;
	final static int PARSER_WOWO = 4;
	final static int PARSER_58 = 5;
	final static int PARSER_DIANPING = 6;
	final static int PARSER_DIDA = 7;
	final static int PARSER_HAOTEHUI = 8;
	final static int PARSER_TUANHAO = 9;
	final static int PARSER_XING800 = 10;
	
	@Deprecated
	public static CommonGroupBuyParser getParser(int parserType) {
		
		switch (parserType){
			case PARSER_HAO123:
				return new Hao123Parser();
			case PARSER_MEITUAN:
				return new MeituanParser();
			case PARSER_LASHOU:
				return new LashouParser();
			case PARSER_58:
				return new FiveEightParser();
			case PARSER_DIANPING:
				return new DianpingParser();
			case PARSER_DIDA:
				return new DidaParser();
			case PARSER_HAOTEHUI:
				return new HaotehuiParser();
			case PARSER_TUANHAO:
				return new TuanhaoParser();
		}
		
		return null;
	}
	
	public static CommonGroupBuyParser getParser(String siteId) {
		
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_DIANPIAN))
			return new DianpingParser();
		
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_MEITUAN))
			return new MeituanParser();

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_DIDA))
			return new DidaParser();

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_58))
			return new FiveEightParser();

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_HAOTEHUI))
			return new HaotehuiParser();

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_LASHOU))
			return new LashouParser();
		
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_TUANHAO))
			return new TuanhaoParser();

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_SOUHU))
			return new SouhuParser();
		
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_NUOMI))
			return new NuomiParser();
		
		return new Hao123Parser();			
	}
	
	String encoding = "UTF-8";
	
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

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
	
	int totalAddressCounter;
	int addressApiCounter;
	int addressHtmlCounter;
	int addressFailCounter;
	
	public void incAddressCounter(ADDRESS_COUNTER_TYPE counterType){
		totalAddressCounter ++;
		switch (counterType){
		case FROM_API:
			addressApiCounter ++;			
			break;
		case FROM_HTML:
			addressHtmlCounter ++;
			break;
		case FAIL:
			addressFailCounter ++;
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
		if (subElement.getText().trim().equals(""))
			return null;
		else
			return subElement.getText().trim();
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
	
	public String getFieldBlockString(Element e, String... fieldNames){
		
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
		
		Iterator<?> it = elementList.iterator();
		if(it.hasNext() == false){
			return null;
		} else{
			Element subElement = (Element)it.next();
			return subElement.getText();
		} 
			
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
			log.info("address parse statistic, total "+totalAddressCounter+" parsed, "+
					addressApiCounter+" from API, "+
					addressHtmlCounter+" from HTML, "+
					addressFailCounter+" failure/none");
			
			
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
	
	public abstract String generateWapLoc(String webURL, String imageURL);

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
			
			boolean updateFlag = false;
			
			// update bought
			if (product.getBought() != bought){
				product.setBought(bought);
				updateFlag = true;				
			}
			
			// update address if product has no address, this might caused by address parsing failure last time
			List<String> list = product.getAddress();
			if (list == null || list.size() == 0){

				// try fetch from HTML page
				addressList = addressParser.parseAddress(loc);					
				if (addressList != null && addressList.size() > 0){
					product.setAddress(addressList);
					updateFlag = true;					
					incAddressCounter(ADDRESS_COUNTER_TYPE.FROM_HTML);					
				}
				else{
					log.warning("fail to get address for product="+product.toString());
					incAddressCounter(ADDRESS_COUNTER_TYPE.FAIL);									
				}
			}			
			
			if (updateFlag){
				incCounter(COUNTER_TYPE.UPDATE);
				log.info("update existing product, product = "+product.toString());
				ProductManager.save(mongoClient, product);
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
				// no address, try fetch from HTML page
				addressParser.setEncoding(getEncoding());
				addressList = addressParser.parseAddress(loc);	
				
				if (addressList != null && addressList.size() > 0){
					incAddressCounter(ADDRESS_COUNTER_TYPE.FROM_HTML);					
				}
				else{
					incAddressCounter(ADDRESS_COUNTER_TYPE.FAIL);									
				}
			}
			else{
				// already have address
				incAddressCounter(ADDRESS_COUNTER_TYPE.FROM_API);
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
	
	/**
	 * �ַ�ָ�
	 * @param expression
	 *            ������ʽ�ַ�
	 * @param text
	 *            Ҫ���зָ�������ַ�
	 */
	public static String[] splitText(String Expression, String text) {
		Pattern p = Pattern.compile(Expression); // ������ʽ
		String[] a = p.split(text);
		return a;
	}
	/**
	 * 
	 */
	public String getIDFromWeb(String prefixExpression, String suffixExpression, String webURL) {
		if(prefixExpression == null)
			return null;
		
		String[] str = splitText(prefixExpression, webURL); 
		String id = null;
		if (str.length >= 2) {
			if(suffixExpression == null) {
				id = str[1];
			} else {
				String[] ids = splitText(suffixExpression, str[1]);
				if(ids.length >= 1){
					 id = ids[0];
				} 
			}	
		 } 
		return id;
	}
	/**
	 * 
	 */
	public String deleteXmlTag(String str) {
		str = str.replaceAll("\"", "");
		org.jsoup.nodes.Document doc = Jsoup.parse(str);
		str = doc.text();
    	return doc.text();
	}
	
}
