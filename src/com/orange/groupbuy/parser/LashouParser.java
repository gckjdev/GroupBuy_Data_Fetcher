package com.orange.groupbuy.parser;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.antlr.grammar.v3.ANTLRv3Parser.element_return;
import org.jdom.Element;

import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.Gps;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class LashouParser extends CommonGroupBuyParser {

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
			List<String> address = new LinkedList<String>(); 
			
			Date startDate = StringUtil.dateFromIntString(startTimeString);
			Date endDate = StringUtil.dateFromIntString(endTimeString);
			
			List<String> telList = new LinkedList<String>();
			List<String> shopNameList = new LinkedList<String>();
			List<List<Double>> gpsList = new LinkedList<List<Double>>();
			
			List<?> shopList = getFieldBlock(data, "shops", "shop");
			if (shopList != null){
				Iterator<?> shopListIter = shopList.iterator();
				while (shopListIter.hasNext()){
					Element shop = (Element)shopListIter.next();
					String shopName = getFieldValue(shop, "name");
					String shopTel = getFieldValue(shop, "tel");
					String shopAddress = getFieldValue(shop, "addr");
					String longitude = getFieldValue(shop, "longitude");
					String latitude = getFieldValue(shop, "latitude");					
					
					// TODO add into address list
					System.out.println("shop="+shopName+","+shopTel+","+shopAddress+","+latitude+","+longitude);
					
					if (shopAddress != null && shopAddress.length() > 0)
						address.add(shopAddress);
					
					if (shopTel != null & shopTel.length() > 0)
						telList.add(shopTel);
					
					if (shopName != null & shopName.length() > 0)
						shopNameList.add(shopName);

					if (longitude != null && longitude.length() > 0 &&
						latitude != null && latitude.length() > 0){
						Gps gps = new Gps(latitude, longitude);
						gpsList.add(gps.toDoubleList());
					}
					
				}
			}
			
			Product product = saveProduct(mongoClient, city, loc, image, title, startDate, endDate, 
					price, value, bought, siteId, website, siteurl, major, address, addressParser);
			
			if (product != null){							
				// save extra fields
				product.setDescription(description);
				product.setDetail(detail);
				product.setRange(range);
				product.setCategory(category);
				product.setTel(telList);
				product.setShopList(shopNameList);
				product.setGPS(gpsList);
				product.setWapLoc(generateWapLoc(loc, image));
				
				ProductManager.save(mongoClient, product);
			}					
			
		}		
		
		return true;
	}

	@Override
	public int convertCategory(String category) {	
		
		if (category == null)
			return DBConstants.C_CATEGORY_UNKNOWN;			
		
		if (category.equalsIgnoreCase("休闲")){
			return DBConstants.C_CATEGORY_FUN;
		}
		else if (category.equalsIgnoreCase("美食")){
			return DBConstants.C_CATEGORY_EAT;
		}
		else if (category.equalsIgnoreCase("美容")){
			return DBConstants.C_CATEGORY_FACE;			
		}
		else if (category.equalsIgnoreCase("网购")){
			return DBConstants.C_CATEGORY_SHOPPING;			
		}
		else if (category.equalsIgnoreCase("其他")){
			return DBConstants.C_CATEGORY_UNKNOWN;			
		}
		
		return DBConstants.C_CATEGORY_UNKNOWN;
	}

	@Override
	public String generateWapLoc(String loc, String imageURL) {
		
		final String basicwapURL = "http://m.lashou.com/action/index.php?func=show_one&id=";
		String id = getIDFromWeb("deal/", ".html", loc);
		if(id == null)
			return null;
		else 
			return basicwapURL.concat(id);
	}


}
