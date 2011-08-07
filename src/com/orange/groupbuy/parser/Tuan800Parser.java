package com.orange.groupbuy.parser;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Element;

import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.dao.Gps;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class Tuan800Parser extends CommonGroupBuyParser {

	@Override
	public int convertCategory(String category) {
		return 0;
	}

	@Override
	public String generateWapLoc(String webURL, String imageURL) {
		return null;
	}

	@Override
	public boolean parseElement(Element root, CommonAddressParser addressParser) {
		List<?> productList = getFieldBlock(root, "url");
		if (productList == null)
			return false;
				
		Iterator<?> it = productList.iterator();
		while (it.hasNext()){
			Element productElement = (Element)it.next();
			if (productElement == null)
				continue;
			
			String  loc = getFieldValue(productElement, "loc");
			loc = loc.replaceAll("\\?source=tuan800", "");
			
			Element data = getFieldElement(productElement, "data", "display");
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
			String startTimeString = getFieldValue(data, "startTime");
			String endTimeString = getFieldValue(data, "endTime");
			String merchantEndTimeString = getFieldValue(data, "merchantEndTime");
//			System.out.println("merchantEndTime="+merchantEndTimeString);
			double value = StringUtil.doubleFromString(getFieldValue(data, "value"));
			double price = StringUtil.doubleFromString(getFieldValue(data, "price"));
			String description = getFieldValue(data, "tip");
			int bought = StringUtil.intFromString(getFieldValue(data, "bought"));
			String detail = getFieldValue(data, "detail");
			int major = StringUtil.intFromString(getFieldValue(data, "major"));
			int category = convertCategory(getFieldValue(data, "cate"));
			List<String> range = StringUtil.stringToList(getFieldValue(data, "range"));
			
			String isPostString = getFieldValue(data, "post");
			String soldOutString = getFieldValue(data, "soldOut");
			int maxQuota = StringUtil.intFromString(getFieldValue(data, "maxQuota"));
			int minQuota = StringUtil.intFromString(getFieldValue(data, "minQuota"));
			int priority = StringUtil.intFromString(getFieldValue(data, "priortity"));
			if (priority == 0)
				priority = StringUtil.intFromString(getFieldValue(data, "priority"));
			
			List<String> tag = StringUtil.stringToList(getFieldValue(data, "tag")); 
			
			Date startDate = StringUtil.dateFromIntString(startTimeString);
			Date endDate = StringUtil.dateFromIntString(endTimeString);
			Date merchantEndDate = StringUtil.dateFromIntString(merchantEndTimeString);
			
			List<String> telList = new LinkedList<String>();
			List<String> shopNameList = new LinkedList<String>();
			List<List<Double>> gpsList = new LinkedList<List<Double>>();
			List<String> address = new LinkedList<String>(); 
			
			List<?> shopList = getShopListBlock(productElement, data);
			if (shopList != null){
				Iterator<?> shopListIter = shopList.iterator();
				while (shopListIter.hasNext()){
					Element shop = (Element)shopListIter.next();
					String shopName = getFieldValue(shop, "name");
					String shopTel = getFieldValue(shop, "tel");
					String shopAddress = getFieldValue(shop, "addr");
					if (shopAddress == null){
						shopAddress = getFieldValue(shop, "address");	// adapt to Jingdong
					}

					String longitude = getFieldValue(shop, "longitude");
					String latitude = getFieldValue(shop, "latitude");					
					
					// TODO add into address list
					System.out.println("shop="+shopName+","+shopTel+","+shopAddress+","+latitude+","+longitude);
					
					if (shopAddress != null && shopAddress.length() > 0)
						address.add(shopAddress);
					
					if (shopTel != null && shopTel.length() > 0)
						telList.add(shopTel);
					
					if (shopName != null && shopName.length() > 0)
						shopNameList.add(shopName);

					if (longitude != null && longitude.length() > 0 &&
						latitude != null && latitude.length() > 0){
						Gps gps = new Gps(latitude, longitude);
						gpsList.add(gps.toDoubleList());
					}
					
				}
			}

			Product product = saveProduct(mongoClient, city, loc, image, title, startDate, endDate, 
					price, value, bought, siteId, website, siteurl, major, address, addressParser, gpsList);
			
			if (product != null){							
				// save extra fields
				product.setMerchantEndDate(merchantEndDate);
				if (isPostString != null)
					product.setPost(StringUtil.booleanFromString(isPostString));
				if (soldOutString != null)
					product.setSoldOut(StringUtil.booleanFromString(soldOutString));
				product.setQuota(maxQuota, minQuota);
				product.setTag(tag);
				product.setPriority(priority);
				product.setDescription(deleteXmlTag(description));
				product.setDetail(deleteXmlTag(detail));
				product.setRange(range);
				product.setCategory(category);
				product.setWapLoc(generateWapLoc(loc, image));
				product.setTel(telList);
				product.setShopList(shopNameList);
				product.setGPS(gpsList);

				ProductManager.save(mongoClient, product);
				log.info("save final product="+product.toString());
			}					
			
		}		
		
		return true;
	}

	private String getDefaultSiteURL() {
		return "";
	}
	
	public List<?> getShopListBlock(Element productElement, Element dataElement){
		return getFieldBlock(productElement, "data", "shops", "shop");		
	}

	@Override
	public boolean disableAddressParsing() {
		return false;
	}

}
