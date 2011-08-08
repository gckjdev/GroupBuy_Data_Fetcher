package com.orange.groupbuy.parser.test;

import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.utils.html.HtmlUtils;
import com.orange.groupbuy.addressparser.GenericAddressParser;
import com.orange.groupbuy.addressparser.MeituanAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.parser.CommonGroupBuyParser;
import com.orange.groupbuy.parser.FiveEightParser;
import com.orange.groupbuy.parser.Hao123Parser;
import com.orange.groupbuy.parser.LashouParser;
import com.orange.groupbuy.parser.MeituanParser;
import com.orange.groupbuy.parser.SouhuParser;

public class ParserTest {

	static MongoDBClient mongoClient;
	Random seed;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() {
		mongoClient = new MongoDBClient("localhost", "groupbuy", "", "");
		seed = new Random();
	}
	
	@Test
	public void testFtuanParser() {

		String siteId = DBConstants.C_SITE_FTUAN ;	
		CommonGroupBuyParser parser = new Hao123Parser();
		parser.setMongoClient(mongoClient);
		parser.setSiteId(siteId);
		
		// start parsing data file and save data to DB
//		boolean result = parser.parse("C:/Temp/groupbuy_raw_file/xinlang.xml");
//		Assert.assertTrue(result);
	}
	
	@Test
	public void testAiBangParser() {

		String siteId = DBConstants.C_SITE_SINA;	
		CommonGroupBuyParser parser = new Hao123Parser();
		parser.setMongoClient(mongoClient);
		parser.setSiteId(siteId);
		
		// start parsing data file and save data to DB
//		boolean result = parser.parse("C:/Temp/groupbuy_raw_file/xinlang.xml");
//		Assert.assertTrue(result);
	}
	
	@Test
	public void testGenericAddressParser() {

		
		GenericAddressParser addressParser = new GenericAddressParser();
//		addressParser.setEncoding("GBK");
//		List<String> addressList = addressParser.doParseAddress("http://life.sina.com.cn/tuan/deal/1300");
//		String siteId = DBConstants.C_SITE_SINA;	
//		CommonGroupBuyParser parser = new Hao123Parser();
//		parser.setMongoClient(mongoClient);
//		parser.setSiteId(siteId);
		
		// start parsing data file and save data to DB
//		boolean result = parser.parse("C:/Temp/groupbuy_raw_file/xinlang.xml");
//		Assert.assertTrue(addressList != null && addressList.size() > 0);
	}
	
	@Test
	public void test1(){
		String myStr = "222:333-999";
		Assert.assertTrue(myStr.matches("(?i).*[:-].*"));
		myStr = "222:333";
		Assert.assertTrue(myStr.matches("(?i).*[:-].*"));
		myStr = "999-999";
		Assert.assertTrue(myStr.matches("(?i).*[:-].*"));
		myStr = ":999999";
		Assert.assertTrue(myStr.matches("(?i).*[:-].*"));
		myStr = "123456";
		Assert.assertFalse(myStr.matches("(?i).*[:-].*"));
		myStr = "123456";
		Assert.assertFalse(myStr.matches("(?i).*[:-].*"));

		myStr = "美食";
		Assert.assertTrue(myStr.matches(".*(美食|食品|菜|餐|吃).*"));
		myStr = "娱乐,美食";
		Assert.assertTrue(myStr.matches(".*(美食|食品|菜|餐|吃).*"));
		myStr = "娱乐,吃";
		Assert.assertTrue(myStr.matches(".*(美食|食品|菜|餐|吃).*"));
		myStr = "娱乐西餐";
		Assert.assertTrue(myStr.matches(".*(美食|食品|菜|餐|吃).*"));
		myStr = "美休闲";
		Assert.assertFalse(myStr.matches(".*(美食|食品|菜|餐|吃).*"));
		myStr = "其他";
		Assert.assertFalse(myStr.matches(".*(美食|食品|菜|餐|吃).*"));
	}

}
