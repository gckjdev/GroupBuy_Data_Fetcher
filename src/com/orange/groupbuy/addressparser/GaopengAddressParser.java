package com.orange.groupbuy.addressparser;

import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.orange.groupbuy.fetcher.RequestConstants;

public class GaopengAddressParser extends GenericAddressParser {

	@Override
	public List<String> doParseAddress(String url) {
		try {
			super.addList.clear();
			long fetchTime = System.currentTimeMillis();
			Connection connection = Jsoup.connect(url).timeout(
					RequestConstants.ADDRESS_PARSER_CONNECTION_TIMEOUT);
			Document doc = connection.get();
			String urlString = null;
			if (doc != null) {
				String content = doc.html();
				String[] strs = content.split("\\s");
				for (String string : strs) {
					if (string.contains("u=")) {
						urlString = getUrlFromWeb("u=\'", "\';", string);
						break;
					}
				}
				if (urlString == null || urlString.isEmpty())
					return null;
				
				connection = Jsoup.connect(urlString).timeout(
						RequestConstants.ADDRESS_PARSER_CONNECTION_TIMEOUT);
				doc = connection.get();
//				long parseStartTime = System.currentTimeMillis();
				super.find_common_add(doc, urlString);
//				long parseEndTime = System.currentTimeMillis();
				return super.addList;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error("<doParseAddress> url = " + url + " catch exception = "
					+ e.toString(), e);
			return null;
		}
	}

	public static String getUrlFromWeb(String prefixExpression,
			String suffixExpression, String content) {
		if (prefixExpression == null)
			return null;

		String[] str = splitText(prefixExpression, content);
		String id = null;
		if (str.length >= 2) {
			if (suffixExpression == null) {
				id = str[1];
			} else {
				String[] ids = splitText(suffixExpression, str[1]);
				if (ids.length >= 1) {
					id = ids[0];
				}
			}
		}
		return id;
	}

	public static String[] splitText(String Expression, String text) {
		Pattern p = Pattern.compile(Expression);
		String[] a = p.split(text);
		return a;
	}

}
