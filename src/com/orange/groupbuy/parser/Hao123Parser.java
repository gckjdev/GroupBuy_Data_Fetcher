package com.orange.groupbuy.parser;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class Hao123Parser extends CommonGroupBuyParser {

	public void readXML(String localFilePath) {
		try {
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(new FileInputStream(localFilePath));
			Element el = doc.getRootElement();
			List ls = el.getChildren("url");
			Iterator it = ls.iterator();
			while (it.hasNext()) {	// read each groupbuy product
				Element sub_e = (Element) it.next();
				Element sub_e_2 = sub_e.getChild("loc");// product url
				System.err.println(sub_e_2.getName() + "     "
						+ sub_e_2.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean doParse(String localFilePath) {
		readXML(localFilePath);
		return false;
	}

}
