package com.orange.groupbuy.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class ZuiTaoParser extends Hao123Parser {
	
	Element getDataElement(Element productElement){
		return getFieldElement(productElement, "data");
	}
	
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
			String image = getFieldValue(data, "image");
			image = removeZuitaoURL(image);
			
			String startTimeString = getFieldValue(data, "starttime");
			String endTimeString = getFieldValue(data, "endtime");
			
			double value = StringUtil.doubleFromString(getFieldValue(data, "value"));
			double price = StringUtil.doubleFromString(getFieldValue(data, "price"));
			String description = getFieldValue(data, "description");
			int bought = StringUtil.intFromString(getFieldValue(data, "bought"));
			String detail = getFieldValue(data, "detail");
			int major = StringUtil.intFromString(getFieldValue(data, "major"));
			int category = convertCategory(getFieldValue(data, "category"));
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
	
	private String removeZuitaoURL(String image) {
		return image.replaceAll("http://www.zuitao.com", "");
		// TODO Auto-generated method stub
	}

}
