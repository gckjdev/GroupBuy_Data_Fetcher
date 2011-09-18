package com.orange.groupbuy.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.jsoup.Jsoup;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.solr.SolrClient;
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
		FAIL,
		SKIP
	};
	
	public static final Logger log = Logger.getLogger(CommonGroupBuyParser.class
			.getName());		
	
	public int getInsertCounter() {
		return insertCounter;
	}

	public void setInsertCounter(int insertCounter) {
		this.insertCounter = insertCounter;
	}

	public int getUpdateCounter() {
		return updateCounter;
	}

	public void setUpdateCounter(int updateCounter) {
		this.updateCounter = updateCounter;
	}

	public int getFailCounter() {
		return failCounter;
	}

	public void setFailCounter(int failCounter) {
		this.failCounter = failCounter;
	}

	public int getExistCounter() {
		return existCounter;
	}

	public void setExistCounter(int existCounter) {
		this.existCounter = existCounter;
	}

	public int getTotalCounter() {
		return totalCounter;
	}

	public void setTotalCounter(int totalCounter) {
		this.totalCounter = totalCounter;
	}

	public int getTotalAddressCounter() {
		return totalAddressCounter;
	}

	public void setTotalAddressCounter(int totalAddressCounter) {
		this.totalAddressCounter = totalAddressCounter;
	}

	public int getAddressApiCounter() {
		return addressApiCounter;
	}

	public void setAddressApiCounter(int addressApiCounter) {
		this.addressApiCounter = addressApiCounter;
	}

	public int getAddressHtmlCounter() {
		return addressHtmlCounter;
	}

	public void setAddressHtmlCounter(int addressHtmlCounter) {
		this.addressHtmlCounter = addressHtmlCounter;
	}

	public int getAddressFailCounter() {
		return addressFailCounter;
	}

	public void setAddressFailCounter(int addressFailCounter) {
		this.addressFailCounter = addressFailCounter;
	}	
	
	public int getAddressSkipCounter() {
		return addressSkipCounter;
	}

	public void setAddressSkipCounter(int addressSkipCounter) {
		this.addressSkipCounter = addressSkipCounter;
	}
	
	public static CommonGroupBuyParser getParser(String siteId, MongoDBClient mongoClient){
		CommonGroupBuyParser parser = getParser(siteId);
		if (parser != null){
			parser.setMongoClient(mongoClient);
			parser.setSiteId(siteId);
		}
		
		return parser;
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
		
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_24QUAN))
			return new TwoFourQuanParser();

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_JUMEIYOUPIN))
			return new JuMeiParser();
		
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_JINGDONG))
			return new JingDongParser();		
		
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_MANZUO))
			return new Tuan800Parser();

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_TUANBAO))
			return new Hao123Parser();

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_KAIXIN))
			return new Hao123Parser();		

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_JUQI))
			return new Tuan800Parser();		
		
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_FANTONG))
			return new Hao123Parser();		

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_GANJI))
			return new Hao123Parser();		

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_QUNAER))
			return new Hao123Parser();		

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_LETAO))
			return new LeTaoParser();		

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_ZTUAN))
			return new Tuan800Parser();		

		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_FENTUAN))
			return new FenTuanParser();	
		
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_ZUITAO))
			return new ZuiTaoParser();
		
		if(siteId.equalsIgnoreCase(DBConstants.C_SITE_TGBABA))
			return new Hao123Parser();
		
		if(siteId.equalsIgnoreCase(DBConstants.C_SITE_CHECKOO))
			return new Hao123Parser();
		
		if(siteId.equalsIgnoreCase(DBConstants.C_SITE_XIUTUAN))
			return new XiuTuanParser();
		
		if(siteId.equalsIgnoreCase(DBConstants.C_SITE_5151TUAN))
			return new Tuan800Parser();
		
		if(siteId.equalsIgnoreCase(DBConstants.C_SITE_5151POPO))
			return new Tuan800Parser();
		
		if(siteId.equalsIgnoreCase(DBConstants.C_SITE_MIQI))
			return new Tuan800Parser();
		
		if(siteId.equalsIgnoreCase(DBConstants.C_SITE_COO8))
			return new Coo8Parser();

		
		return new Hao123Parser();			
	}
	
//	String encoding = "UTF-8";	
//	
//	
//	public String getEncoding() {
//		return encoding;
//	}
//
//	public void setEncoding(String encoding) {
//		this.encoding = encoding;
//	}

	MongoDBClient mongoClient;
	String siteId;

	int insertCounter = 0;
	int updateCounter = 0;
	int failCounter = 0;
	int existCounter = 0;
	int totalCounter = 0;

	int totalAddressCounter;
	int addressApiCounter;
	int addressHtmlCounter;
	int addressFailCounter;
	int addressSkipCounter;
	
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
		case SKIP:
			addressSkipCounter ++;
			break;
		}
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
	
	private void printCounter(){
		log.info("parse finish, total "+totalCounter+" parsed, "+
				insertCounter+" insert, "+
				updateCounter+" update, "+
				existCounter+" exist, "+
				failCounter+" failed.");
		log.info("address parse statistic, total "+totalAddressCounter+" parsed, "+
				addressApiCounter+" from API, "+
				addressHtmlCounter+" from HTML, "+
				addressSkipCounter+" skip, "+
				addressFailCounter+" failure/none");		
	}
	
	public boolean parse(String localFilePath){
		return doParse(localFilePath);
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
				log.error("cannot find address parser for site = "+siteId);
				return false;
			}
			
			boolean result = parseElement(root, addressParser);		
			SolrClient.commit();
			printCounter();
			
			return result;

		} catch (FileNotFoundException e) {
			log.error("<doParse> file="+localFilePath+", FileNotFoundException="+e.toString());
			return false;
		} catch (JDOMException e) {
			log.error("<doParse> file="+localFilePath+", JDOMException="+e.toString());
			return false;
		} catch (IOException e) {
			log.error("<doParse> file="+localFilePath+", IOException="+e.toString());
			return false;
		}

	}
	
	public void commitSolrIndex(){
		final int COUNT_FOR_COMMIT = 200;
		if (insertCounter > 0 && insertCounter % COUNT_FOR_COMMIT == 0){
			SolrClient.commit();
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
	
	private boolean isForAllCity(String city){
		if (city == null)
			return false;
		return (city.equals("全国"));
	}
	
	public Product saveProduct(MongoDBClient mongoClient, String city, String loc, String image, String title, Date startDate, Date endDate, 
			double price, double value, int bought, String siteId, String siteName, String siteURL,
			int major, List<String> addressList, CommonAddressParser addressParser, List<List<Double>> gpsList){
		
		if (totalCounter > 0 && totalCounter % 20 == 0){
			printCounter();			
		}
		
		// check if product exist
		Product product;
		product = ProductManager.findProduct(mongoClient, loc, city);
		if (product != null){
			
			boolean updateFlag = false;
			
			// update bought
			if (product.getBought() != bought){
				product.setBought(bought);
				product.calcAndSetTopScore(bought, product.getStartDate());
				updateFlag = true;				
			}
			
			// update address if product has no address, this might caused by address parsing failure last time
			List<String> list = product.getAddress();
			if (list == null || list.size() == 0){

				// TODO refactor the code
				if (disableAddressParsing() || isForAllCity(city)){
					incAddressCounter(ADDRESS_COUNTER_TYPE.SKIP);
				}
				else{					
					// try fetch from HTML page
					addressList = addressParser.parseAddress(loc);					
					if (addressList != null && addressList.size() > 0){
						product.setAddress(addressList);
						updateFlag = true;					
						incAddressCounter(ADDRESS_COUNTER_TYPE.FROM_HTML);					
					}
					else{
//						log.warning("fail to get address for product="+product.toString());
						incAddressCounter(ADDRESS_COUNTER_TYPE.FAIL);									
					}
				}
			}			
			
			if (updateFlag){
				incCounter(COUNTER_TYPE.UPDATE);
				product.updateModifyDate();
//				log.info("update existing product, product = "+product.toString());
				ProductManager.save(mongoClient, product);
			}
			else{
//				log.info("product exist, no need to update, product id="+product.getObjectId());
				incCounter(COUNTER_TYPE.EXIST);
			}

			return null;
		}
		
		// create a new product
		product = new Product();
		if (product.setMandantoryFields(city, loc, image, title, startDate, endDate, 
				price, value, bought, siteId, siteName, siteURL)){
			
			product.setMajor(major);	
			product.setGPS(gpsList);
			
			// read address if not given
			if (addressList == null || addressList.size() == 0){
				
				if (disableAddressParsing() || isForAllCity(city)){
					incAddressCounter(ADDRESS_COUNTER_TYPE.SKIP);
				}
				else{
					// no address, try fetch from HTML page
					addressList = addressParser.parseAddress(loc);	
					
					if (addressList != null && addressList.size() > 0){
						incAddressCounter(ADDRESS_COUNTER_TYPE.FROM_HTML);					
					}
					else{
						incAddressCounter(ADDRESS_COUNTER_TYPE.FAIL);									
					}
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
//				log.info("create new product success, product = "+product.toString());
				incCounter(COUNTER_TYPE.INSERT);											
				return product;
			}
			else{
				log.error("create new product failure, product = "+product.toString());
				incCounter(COUNTER_TYPE.FAIL);
				return null;
			}
		}
		else{			
			log.error("fail to set product mandantory fields, loc="+loc+",city="+city+
					",image="+image+",title="+title+",startDate="+startDate+",endDate="+endDate+
					",price="+price+",value="+value+",bought="+bought+
					",siteId="+siteId+",siteName="+siteName+",siteURL="+siteURL);
			incCounter(COUNTER_TYPE.FAIL);
			return null;
		}		
	}
	
	/**
	 * 
	 * @param expression
	 *            
	 * @param text
	 *            
	 */
	public static String[] splitText(String Expression, String text) {
		Pattern p = Pattern.compile(Expression);
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
		if (str == null || str.isEmpty())
			return str;
		//str = str.replaceAll("\"", "");
		org.jsoup.nodes.Document doc = Jsoup.parse(str);
		if (doc == null)
			return null;
		
		str = doc.text();
    	return doc.text();
	}
	
	public String convertCity(String city){
		return city;
	}
	
	public abstract boolean disableAddressParsing();

	private String getDefaultSiteURL() {
		return null;
	}
	
	public  String parseAddress(String shopAddress) {
		if(shopAddress == null || shopAddress.isEmpty())
			return null;
		String[] strs = shopAddress.split("\\s");
		if (strs == null || strs.length == 0)
			return null;
		int len = strs.length;
		for (int i = 0; i < strs.length; i++) {
			strs[i] = getCorrectString(strs[i]);
		}
		int[] scores = new int[len];
		for (int i = 0; i < len; i++)
			scores[i] = 0;
		for (int i = 0; i < len; i++) {
			if (strs[i] == null)
				continue;
			if (strs[i].length() >= 5) {
				scores[i] = addScore(strs[i]);
			}
		}
		// bubble sort
		int temp;
		String stemp;
		for (int i = 0; i < scores.length; i++) {
			for (int j = 0; j < scores.length - i - 1; j++) {
				if (scores[j] < scores[j + 1]) {
					temp = scores[j];
					scores[j] = scores[j + 1];
					scores[j + 1] = temp;
					// swap the string at the same time
					stemp = strs[j];
					strs[j] = strs[j + 1];
					strs[j + 1] = stemp;
				}
			}
		}
		// consider the score
		if (scores[0] >= 2 && strs[0].length() > 5 && strs[0].length() < 40)
			return strs[0];
		else 
			return null;
	}
	
	private int addScore(String str) {
		int score = 0;
		if (str.contains("市"))
			score++;
		if (str.contains("区"))
			score++;
		if (str.contains("路"))
			score++;
		if (str.contains("街"))
			score++;
		if (str.contains("道"))
			score++;
		if (str.contains("号"))
			score++;
		if (str.contains("楼"))
			score++;
		if (str.contains("巷"))
			score++;
		if (str.contains("层"))
			score++;
		if (str.contains("铺"))
			score++;
		if (str.contains("广场"))
			score++;

		return score;
	}
	
	private String getCorrectString(String str) {
		
		int index = str.indexOf("地址：");
		if (index != -1) {
			str = str.substring(index + 3);
		}
		index = str.indexOf("电话");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("店：");
		if (index != -1) {
			str = str.substring(index + 2);
		}
		index = str.indexOf("（查看地图）");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("公交信息");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("联系");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("联络");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("咨询");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("预订");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("预约");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("公交");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("客服");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("营业");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("交通");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("乘车");
		if (index != -1) {
			str = str.substring(0, index);
		}
		str = str.replace("，", ",");
		str = str.replace("【", "(");
		str = str.replace("】", ")");
		str = str.replace("、", ",");
		str = str.replace("—", "-");
		
		if (str.contains("？"))
			return "";
		if(str.length() < 5 || str.length() > 40)
			return "";
		return str.trim();
	}
	
	int detectCategory(String categoryString, String city, String...strings){
		
		int initialCategory = convertCategory(categoryString);		
		int retCategory = DBConstants.C_CATEGORY_UNKNOWN;

		retCategory = strongDetectCategory(initialCategory, city, strings);
		if (retCategory == DBConstants.C_CATEGORY_UNKNOWN){
			// still not detect, then use default category
			if (initialCategory != DBConstants.C_CATEGORY_UNKNOWN){
				return initialCategory;
			}
			else{
				return weakDetectCategory(city, strings);
			}
		}
		else {								
			return retCategory;
		}
	}

	private int weakDetectCategory(String city, String[] strings) {
		int retCategory = DBConstants.C_CATEGORY_UNKNOWN;
		if (strings == null || strings.length == 0 || city == null)
			return retCategory;				
		
		for (int i=0; i<strings.length; i++){
			if (strings[i] == null)
				continue;
			
			if (strings[i].matches(".*(美食|食品|粤菜|湘菜|川菜|西餐|自助餐|东北菜|寿司|韩国料理|火锅).*") && !city.equalsIgnoreCase("全国")){
				retCategory = DBConstants.C_CATEGORY_EAT;
				return retCategory;
			}
			else if (strings[i].matches(".*(香港游|澳门游|北京游|旅游|一日游).*")){
				retCategory = DBConstants.C_CATEGORY_TRAVEL;
				return retCategory;
			}
			else if (strings[i].matches(".*(酒店|大床房|公寓|度假村|三星级|四星级|五星级).*")){
				retCategory = DBConstants.C_CATEGORY_HOTEL;
				return retCategory;
			}
			else if (strings[i].matches(".*(KTV|K歌|游戏币|咖啡厅|酒吧|桌游|棋牌|足疗|按摩|桑拿|水疗).*")){
				retCategory = DBConstants.C_CATEGORY_FUN;
				return retCategory;
			}
			else if (strings[i].matches(".*(摄影|写真).*")){
				retCategory = DBConstants.C_CATEGORY_PHOTO;
				return retCategory;
			}
		}
		return retCategory;
	}

	private int strongDetectCategory(int initialCategory, String city,
			String[] strings) {
		
		int retCategory = DBConstants.C_CATEGORY_UNKNOWN;
		if (strings == null || strings.length == 0 || city == null)
			return retCategory;
		
		for (int i=0; i<strings.length; i++){
			if (strings[i] == null)
				continue;
			
			if (strings[i].matches(".*(电影).*") && !city.equalsIgnoreCase("全国")){
				retCategory = DBConstants.C_CATEGORY_FILM;
				return retCategory;
			}

			if (strings[i].matches(".*(代金券|现金券).*")){
				retCategory = DBConstants.C_CATEGORY_COUPON;
				return retCategory;
			}
		}
		return retCategory;
	}
	
}
