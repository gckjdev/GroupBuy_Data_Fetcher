package com.orange.groupbuy.parser;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.addressparser.MeituanAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class MeituanParser extends CommonGroupBuyParser {
	final static String WEBSITE = "美团";
	@Override
	public boolean parseElement(Element root, CommonAddressParser addressParser) {
		List<?> productList = getFieldBlock(root, "deals", "deal");
		if (productList == null)
			return false;

		Iterator<?> it = productList.iterator();
		while (it.hasNext()) {
			Element productElement = (Element) it.next();
			if (productElement == null)
				continue;
			Element data = productElement;
			String loc = getFieldValue(data, "deal_url");
			String title = getFieldValue(data, "title");
			String website = WEBSITE;
			String siteurl = "";
			String image = getFieldValue(data, "medium_image_url");
			String city = getFieldValue(data, "division_name");
			String startTimeString = getFieldValue(data, "start_date");
			String endTimeString = getFieldValue(data, "end_date");
			double value = StringUtil.doubleFromString(getFieldValue(data,
					"value"));
			double price = StringUtil.doubleFromString(getFieldValue(data,
					"price"));
			String description = getFieldValue(data, "description");
			int bought = StringUtil.intFromString(getFieldValue(data,
					"quantity_sold"));
			//List<String> details = (List<String>) getFieldBlock(data, "detail");
			//String detail = details.get(0);
			String detail = "";
			int major = DBConstants.C_NOT_MAJOR;

			Date startDate = StringUtil.dateFromString(startTimeString);
			Date endDate = StringUtil.dateFromString(endTimeString);

			Product product = saveProduct(mongoClient, city, loc, image, title, startDate, endDate, 
					price, value, bought, siteId, website, siteurl, major, null, addressParser);
			
			if (product != null){							
				// save extra fields
				product.setDescription(description);
				product.setDetail(detail);
				product.setRange(null);
				ProductManager.save(mongoClient, product);
			}		

		}

		return false;
	}

	@Override
	public int convertCategory(String category) {
		if (category == null)
			return DBConstants.C_CATEGORY_UNKNOWN;			
		
		if (category.equalsIgnoreCase("文体娱乐")){
			return DBConstants.C_CATEGORY_FUN;
		}
		else if (category.equalsIgnoreCase("餐饮")){
			return DBConstants.C_CATEGORY_EAT;
		}
		else if (category.equalsIgnoreCase("健康丽人")){
			return DBConstants.C_CATEGORY_FACE;			
		}
		else if (category.equalsIgnoreCase("生活服务")){
			return DBConstants.C_CATEGORY_LIFE;			
		}
		else if (category.equalsIgnoreCase("实物")){
			return DBConstants.C_CATEGORY_SHOPPING;			
		}
		
		return DBConstants.C_CATEGORY_UNKNOWN;
	}

}
