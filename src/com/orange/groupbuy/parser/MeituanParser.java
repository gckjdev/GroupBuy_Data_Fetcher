package com.orange.groupbuy.parser;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.addressparser.CommonAddressParser;
import com.orange.groupbuy.addressparser.MeituanAddressParser;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;

public class MeituanParser extends CommonGroupBuyParser {
	final static String WEBSITE = "√¿Õ≈";
	@Override
	public boolean parseElement(Element root) {
		List<?> productList = getFieldBlock(root, "deals", "deal");
		if (productList == null)
			return false;
		CommonAddressParser addressParser = CommonAddressParser
				.findParserById(siteId);
		if (addressParser == null) {
			log.severe("cannot find address parser for site = " + siteId);
		}

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

			Date startDate = StringUtil.dateFromString(startTimeString);
			Date endDate = StringUtil.dateFromString(endTimeString);

			// for test output only
			System.out.println("loc=" + loc + ",website=" + website
					+ ",siteurl=" + siteurl + ",city=" + city + ",title="
					+ title + ",image=" + image + ",startTime="
					+ startTimeString + ",endTime=" + endTimeString + ",value="
					+ value + ",price=" + price + ",description=" + description
					+ ",bought=" + bought + ",detail=" + detail);

			// save product into DB
			Product product = new Product();
			if (product.setMandantoryFields(city, loc, image, title, startDate,
					endDate, price, value, bought, siteId, website, siteurl)) {

				// set extra fields
				product.setDescription(description);
				product.setDetail(detail);

				// read address and set address
				List<String> addressList = addressParser.parseAddress(loc);
				if (addressList != null && addressList.size() > 0) {
					log.info("parser address = "+addressList.get(0));
					product.setAddress(addressList);
				}

				if (ProductManager.createProduct(mongoClient, product)) {
					log.info("create product, product id=" + product.getId());
					incSuccessCounter();
				} else {
					incFailCounter();
				}
			} else {
				incFailCounter();
			}

		}

		return false;
	}

}
