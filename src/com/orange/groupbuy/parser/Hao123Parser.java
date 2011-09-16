package com.orange.groupbuy.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.cassandra.cli.CliParser.newColumnFamily_return;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.orange.common.solr.SolrClient;
import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class Hao123Parser extends CommonGroupBuyParser {

	String getLoc(Element productElement){
		return getFieldValue(productElement, "loc");
	}
	
	Element getDataElement(Element productElement){
		return getFieldElement(productElement, "data", "display");
	}
	
	String getStartTimeString(Element data){
		return getFieldValue(data, "startTime");
	}

	String getEndTimeString(Element data){
		return getFieldValue(data, "endTime");
	}
	
	String getImage(Element data){	
		return getFieldValue(data, "image");
	}
	

	@Override
	public boolean parseElement(Element root, CommonAddressParser addressParser){
		List<?> productList = getFieldBlock(root, "url");
		if (productList == null)
			return false;
				
		Iterator<?> it = productList.iterator();
		while (it.hasNext()){
			Element productElement = (Element)it.next();
			if (productElement == null)
				continue;
			
			String  loc = getLoc(productElement);
			Element data = getDataElement(productElement);
			if (data == null)
				continue;
			
			String website = getFieldValue(data, "website");
			String siteurl = getFieldValue(data, "siteurl");
			if (siteurl == null){
				siteurl = getDefaultSiteURL();
			}
			
			String city = convertCity(getFieldValue(data, "city"));
			//more consideration
			String title = getFieldValue(data, "title");
			String image = getImage(data);
			
			String startTimeString = getStartTimeString(data);
			String endTimeString = getEndTimeString(data);
			
			double value = StringUtil.doubleFromString(getFieldValue(data, "value"));
			double price = StringUtil.doubleFromString(getFieldValue(data, "price"));
			String description = getFieldValue(data, "description");
			int bought = StringUtil.intFromString(getFieldValue(data, "bought"));
			String detail = getFieldValue(data, "detail");
			int major = StringUtil.intFromString(getFieldValue(data, "major"));
						
			
			int category = detectCategory(getFieldValue(data, "category"), city, title);
			
			List<String> range = StringUtil.stringToList(getFieldValue(data, "range"));
			List<String> addressList = StringUtil.stringToList(getFieldValue(data, "address")); 
			List<String> address = new ArrayList<String>();
			if (addressList != null){
				for (int i=0; i<addressList.size(); i++) {
					String addressString = parseAddress(addressList.get(i));
					if (addressString != null && !addressString.isEmpty()) 
						address.add(addressString);
				}
			} else {
				address = null;
			}
			
			Date startDate = StringUtil.dateFromIntString(startTimeString);
			Date endDate = StringUtil.dateFromIntString(endTimeString);
			
			Product product = saveProduct(mongoClient, city, loc, image, title, startDate, endDate, 
					price, value, bought, siteId, website, siteurl, major, address, addressParser, null);
			
			if (product != null){							
				// save extra fields
				product.setDescription(description);
				product.setDetail(detail);
				product.setRange(range);
				product.setCategory(category);
				product.setWapLoc(generateWapLoc(loc, image));
				
				ProductManager.save(mongoClient, product);
				ProductManager.createSolrIndex(product, false);
			}				
			
		}		
		
		return true;
	}



	@Override
	public int convertCategory(String category) {	
		
		if (siteId.equalsIgnoreCase(DBConstants.C_SITE_XING800)){
			return DBConstants.C_CATEGORY_SHOPPING;
		}
		
		return StringUtil.intFromString(category);
	}

	@Override
	public String convertCity(String city){
		if (city == null)
			return null;
		
		if (city.equalsIgnoreCase("商品")){
			return "全国";
		}

		if(city.contains(",")){
			return "全国";
		}
		
		return city;
	}

	public String generateWapLoc(String loc, String imageURL) {
		return null;
	}
	
	public String getDefaultSiteURL(){
		return null;
	}

	@Override
	public boolean disableAddressParsing() {
		return false;
	}
	

}
