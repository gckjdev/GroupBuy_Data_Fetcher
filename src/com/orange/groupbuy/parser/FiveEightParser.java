package com.orange.groupbuy.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

import org.jdom.Element;

import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class FiveEightParser extends CommonGroupBuyParser {
	final static String WEBSITE = "58";
	final static String SITEURL = "t.58.com";
	@Override
	public boolean parseElement(Element root, CommonAddressParser addressParser) {
		// TODO Auto-generated method stub
		List<?> cityList = getFieldBlock(root, "city");
		if (cityList == null)
			return false;

		Iterator<?> city_it = cityList.iterator();
		while (city_it.hasNext()) {
			Element cityElement = (Element) city_it.next();
			String city = getFieldValue(cityElement, "name");
			List<?> productList = getFieldBlock(cityElement, "product");
			
			if(productList == null)
				return false;
			Iterator<?> product_it = productList.iterator();
			while(product_it.hasNext()){
				Element data = (Element) product_it.next();
				String loc = getFieldValue(data, "url");
				String title = getFieldValue(data, "name");
				String phone = getFieldValue(data, "vendor_phone");
				double value = StringUtil.doubleFromString(getFieldValue(data, "market_price"));
				double price = StringUtil.doubleFromString(getFieldValue(data, "group_price"));
				int bought = StringUtil.intFromString(getFieldValue(data, "quantity_sold"));
				String startTimeString = getFieldValue(data, "begin_date");
				String endTimeString = getFieldValue(data, "end_date");
				String expiredTimeString = getFieldValue(data, "expired_date");
				String image = getFieldValue(data, "image");
				String description = getFieldValue(data, "introduction");
				String detail = getFieldBlockString(data, "comment");
				String website = WEBSITE;
				List<String> range = StringUtil.stringToList(getFieldValue(data, "region"));
				int major = DBConstants.C_NOT_MAJOR;
				String siteurl = SITEURL;
				
				Date startDate = dateFromString(startTimeString);
				Date endDate = dateFromString(endTimeString);
				Date expiredDate = dateFromString(expiredTimeString);
				List<String> address = null;
				
				Product product = saveProduct(mongoClient, city, loc, image, title, startDate, endDate, 
						price, value, bought, siteId, website, siteurl, major, address, addressParser);
				
				if (product != null){							
					// save extra fields
					product.setDescription(description);
					product.setDetail(detail);
					product.setRange(range);
					ProductManager.save(mongoClient, product);
				}		
				
			}
			
		}
		
		return false;
	}

	@Override
	public int convertCategory(String category) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Date dateFromString(String str){
		try {
			
			str = str.replaceAll("年", "-");
			str = str.replaceAll("月", "-");
			str = str.replaceAll("日", "");
			
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = myFormatter.parse(str);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String generateWapLoc(String webURL, String imageURL) {
		// TODO Auto-generated method stub
		return null;
	}


}
