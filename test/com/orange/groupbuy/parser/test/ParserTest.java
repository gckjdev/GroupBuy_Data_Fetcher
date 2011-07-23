package com.orange.groupbuy.parser.test;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.groupbuy.addressparser.GenericAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.parser.CommonGroupBuyParser;
import com.orange.groupbuy.parser.FiveEightParser;
import com.orange.groupbuy.parser.Hao123Parser;
import com.orange.groupbuy.parser.FtuanParser;
import com.orange.groupbuy.parser.LashouParser;

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

	/*@Test
	public void test58Parser() {

		String siteId = DBConstants.C_SITE_58;	
		CommonGroupBuyParser parser = new FiveEightParser();
		parser.setMongoClient(mongoClient);
		parser.setSiteId(siteId);
		
		// start parsing data file and save data to DB
		boolean result = parser.parse("C:/Temp/groupbuy_raw_file/4352b860-a8b6-4183-ae66-e30812534ce3.xml");
		Assert.assertTrue(result);
	}*/
	
	@Test
	public void testFtuanParser() {

		String siteId = DBConstants.C_SITE_GAOPENG;	
		CommonGroupBuyParser parser = new Hao123Parser();
		parser.setMongoClient(mongoClient);
		parser.setSiteId(siteId);
		
		// start parsing data file and save data to DB
		boolean result = parser.parse("C:/Temp/groupbuy_raw_file/gaopeng.php");
		Assert.assertTrue(result);
	}
}
