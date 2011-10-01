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

public class Coo8Parser extends CommonGroupBuyParser {

	public static final String COO8_WEBSITE = "库巴购物网";
	public static final String COO8_SITEURL = "http://tuan.coo8.com";

	@Override
	public boolean parseElement(Element root, CommonAddressParser addressParser) {
		List<?> productList = getFieldBlock(root, "goods");
		if (productList == null)
			return false;

		Iterator<?> it = productList.iterator();
		while (it.hasNext()) {
			Element productElement = (Element) it.next();
			if (productElement == null)
				continue;

			String loc = getFieldValue(productElement, "url");
			String title = getFieldValue(productElement, "title");
			String city = convertCity(getFieldValue(productElement, "cityname"));
			String image = getFieldValue(productElement, "smallimg");
			String startTime = getFieldValue(productElement, "begintime");
			String endTime = getFieldValue(productElement, "endtime");
			String brief = getFieldValue(productElement, "brief");
			int bought = StringUtil.intFromString(getFieldValue(productElement,
					"buycount"));
			int major = 0;
			double value = StringUtil.doubleFromString(getFieldValue(
					productElement, "marketprice"));
			double price = StringUtil.doubleFromString(getFieldValue(
					productElement, "groupprice"));

			brief = brief.replaceAll("<br />", "");
			brief = brief.replaceAll("\n", "");
			
			int category = detectCategory(getFieldValue(productElement, "category"), city, title);
			
			SimpleDateFormat formatter = new SimpleDateFormat(
					"EEE, d MMM yyyy HH:mm:ss Z", java.util.Locale.US);
			Date startDate = new Date();
			Date endDate = new Date();
			try {
				startDate = formatter.parse(startTime);
				endDate = formatter.parse(endTime);
			} catch (ParseException e) {
				log.error("<parseElement> but fail to parse startTime or endTime " + 
						startTime + ", " + endTime, e);
			}

			Product product = saveProduct(mongoClient, city, loc, image, title,
					startDate, endDate, price, value, bought, siteId,
					COO8_WEBSITE, COO8_SITEURL, major, null, addressParser,
					null);

			if (product != null) {
				// save extra fields
				product.setDescription(brief);
				product.setWapLoc(generateWapLoc(loc, image));
				product.setCategory(category);

				ProductManager.save(mongoClient, product);
				ProductManager.createSolrIndex(product, false);
			}

		}

		return true;
	}

	@Override
	public int convertCategory(String category) {
		return DBConstants.C_CATEGORY_SHOPPING;
	}

	@Override
	public String generateWapLoc(String webURL, String imageURL) {
		return null;
	}

	@Override
	public boolean disableAddressParsing() {
		return true;
	}
	
	@Override
	public String convertCity(String city){
		return "全国";
	}

}
