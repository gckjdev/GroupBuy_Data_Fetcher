package com.orange.groupbuy.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class Hao123Parser extends CommonGroupBuyParser {


	
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
			
			String  loc = getFieldValue(productElement, "loc");
			Element data = getFieldElement(productElement, "data", "display");
			if (data == null)
				continue;
			
			String website = getFieldValue(data, "website");
			String siteurl = getFieldValue(data, "siteurl");
			String city = getFieldValue(data, "city");
			//more consideration
			if(city.contains(",")){
				city = "È«¹ú";
			}
			String title = getFieldValue(data, "title");
			String image = getFieldValue(data, "image");
			String startTimeString = getFieldValue(data, "startTime");
			String endTimeString = getFieldValue(data, "endTime");
			double value = StringUtil.doubleFromString(getFieldValue(data, "value"));
			double price = StringUtil.doubleFromString(getFieldValue(data, "price"));
			String description = getFieldValue(data, "description");
			int bought = StringUtil.intFromString(getFieldValue(data, "bought"));
			String detail = getFieldValue(data, "detail");
			int major = StringUtil.intFromString(getFieldValue(data, "major"));
			int category = convertCategory(getFieldValue(data, "category"));
			List<String> range = StringUtil.stringToList(getFieldValue(data, "range"));
			List<String> address = StringUtil.stringToList(getFieldValue(data, "address")); 
			
			Date startDate = StringUtil.dateFromIntString(startTimeString);
			Date endDate = StringUtil.dateFromIntString(endTimeString);
			
			Product product = saveProduct(mongoClient, city, loc, image, title, startDate, endDate, 
					price, value, bought, siteId, website, siteurl, major, address, addressParser);
			
			if (product != null){							
				// save extra fields
				product.setDescription(description);
				product.setDetail(detail);
				product.setRange(range);
				ProductManager.save(mongoClient, product);
				product.setCategory(category);
			}					
			
		}		
		
		return true;
	}

	@Override
	public int convertCategory(String category) {		
		return StringUtil.intFromString(category);
	}

	

}
