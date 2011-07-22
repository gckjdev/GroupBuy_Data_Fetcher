package com.orange.groupbuy.parser;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.addressparser.MeituanAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class MeituanParser extends CommonGroupBuyParser {
	@Override
	public boolean parseElement(Element root, CommonAddressParser addressParser) {
		List<?> productList = getFieldBlock(root, "deals", "data");
		if (productList == null)
			return false;

		Iterator<?> it = productList.iterator();
		while (it.hasNext()) {
			Element productElement = (Element) it.next();
			if (productElement == null)
				continue;
			Element deal = getFieldElement(productElement, "deal");
			String website = getFieldValue(deal, "website");
			String city = getFieldValue(deal, "city_name");
			String siteurl = getFieldValue(deal, "city_url");
			String title = getFieldValue(deal, "deal_title");
			String loc = getFieldValue(deal, "deal_url");
			String image = getFieldValue(deal, "deal_img");
			int category = convertCategory(getFieldValue(deal, "deal_cate"));
			String description = getFieldValue(deal, "deal_desc");
			double value = StringUtil.doubleFromString(getFieldValue(deal, "value"));
			double price = StringUtil.doubleFromString(getFieldValue(deal, "price"));
			int bought = StringUtil.intFromString(getFieldValue(deal, "sales_num"));
			String startTimeString = getFieldValue(deal, "start_time");
			String endTimeString = getFieldValue(deal, "end_time");
			int major = DBConstants.C_NOT_MAJOR;
			String detail = getFieldValue(deal, "deal_tips");

			Date startDate = StringUtil.dateFromIntString(startTimeString);
			Date endDate = StringUtil.dateFromIntString(endTimeString);
			
			Element shops = getFieldElement(productElement, "shops");
			List<?> shopsList = getFieldBlock(shops, "shop");
			List<String> addressList = new LinkedList<String>();
			List<String> phoneList = new LinkedList<String>();
			Iterator<?> shop_it = shopsList.iterator();
			while (shop_it.hasNext()) {
				Element addressElement = (Element) shop_it.next();
				if (addressElement == null)
					continue;
				String phone = getFieldValue(addressElement, "shop_tel");
				String address   = getFieldValue(addressElement, "shop_addr");
				if (phone == null || phone.length() > 0)
					phoneList.add(phone);
				if (address == null || address.length() > 0)
					addressList.add(address);
			}

			Product product = saveProduct(mongoClient, city, loc, image, title, startDate, endDate, 
					price, value, bought, siteId, website, siteurl, major, addressList, addressParser);
			
			if (product != null){							
				// save extra fields
				product.setDescription(description);
				product.setDetail(detail);
				product.setTel(phoneList);
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
