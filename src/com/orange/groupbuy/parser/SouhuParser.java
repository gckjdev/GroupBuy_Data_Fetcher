package com.orange.groupbuy.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class SouhuParser extends CommonGroupBuyParser {
	final static String WEBSITE = "หับü";
	final static String SITEURL = "http://tuan.sohu.com/cn/all/list";
	@Override
	public boolean parseElement(Element root, CommonAddressParser addressParser) {
		List<?> productList = getFieldBlock(root, "deals", "deal");
		if (productList == null)
			return false;

		Iterator<?> it = productList.iterator();
		while (it.hasNext()) {
			Element deal = (Element) it.next();
			if (deal == null)
				continue;
			String website = WEBSITE;
			String city = getFieldValue(deal, "city_name");
			String siteurl = SITEURL;
			String title = getFieldValue(deal, "title");
			String loc = getFieldValue(deal, "deal_url");
			String image = getFieldValue(deal, "image_url");
			int category = convertCategory(getFieldValue(deal, "deal_cate"));
			double value = StringUtil.doubleFromString(getFieldValue(deal, "value"));
			double price = StringUtil.doubleFromString(getFieldValue(deal, "price"));
			int bought = StringUtil.intFromString(getFieldValue(deal, "quantity_sold"));
			String startTimeString = getFieldValue(deal, "start_date");
			String endTimeString = getFieldValue(deal, "start_date");
			int major = 0;
			String detail = getFieldValue(deal, "deal_tips");
			Element conditionElement = getFieldElement(deal, "condition");
			String maxPruchase = getFieldBlockString(conditionElement, "maximum_purchase");
			String expirationTimeString = getFieldBlockString(conditionElement, "expiration_date");
			
			Date startDate = stringToDate(startTimeString);
			Date endDate = stringToDate(endTimeString);
			Date expirationDate = stringToDate(expirationTimeString);
		
			Product product = saveProduct(mongoClient, city, loc, image, title, startDate, endDate, 
					price, value, bought, siteId, website, siteurl, major, null, addressParser);
			
			if (product != null){							
				// save extra fields
				product.setDetail(detail);
				product.setRange(null);
				product.setCategory(category);
				ProductManager.save(mongoClient, product);
			}		
		}
		
		return false;
	}

	@Override
	public int convertCategory(String category) {
		return DBConstants.C_CATEGORY_LIFE;
	}

	@Override
	public String generateWapLoc(String webURL, String imageURL) {
		return null;
	}
	
	public Date stringToDate(String timeString) {
		// TODO  2011-07-28T00:00:00+08:00
		SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+08:00'");
		Date date = new Date();
		try {
			date = myDateFormat.parse(timeString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

}
