package com.orange.groupbuy.addressparser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

@Deprecated
public class MeituanAddressParser extends CommonAddressParser {
	private List<String> addList = new LinkedList<String>();
	@Override
	public List<String> doParseAddress(String url) {
		try {
			addList.clear();
			HttpURLConnection connection = (HttpURLConnection)(new URL(url)).openConnection();
			if (connection != null){
				Parser parser = new Parser((HttpURLConnection)(new URL(url)).openConnection());
				find_Meituan_add(parser);
				connection.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return addList;
	}
	/**
	 * meituan search
	 */
	private void find_Meituan_add(Parser parser){
		try {
			NodeFilter filter = new TagNameFilter ("HEAD");
			NodeList nodes = parser.parse(filter);
			String str = null;
			for (NodeIterator i = nodes.elements(); i.hasMoreNodes(); ){
				Node node = i.nextNode();
				str = node.toHtml();
				meituan_recursion(str);
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 递归调用
	 */
	private void meituan_recursion(String str){
		int index;
		if((index=str.indexOf("地址")) != -1){
			String s1 = str.substring(index+3);
			s1 = s1.trim();
			String s2 = s1.substring(0, 50);
			int index2 = s2.indexOf(" ");
			String s3;
			if (index2 != -1){
				s3 = s2.substring(0, index2);
			}
			else{
				s3 = s2;
			}
			if(isLeagle(s3)){
				addtoList(s3);
			}
			int len = s3.length();
			int index3 = str.indexOf(s3);
			meituan_recursion(str.substring(index3 + len));
		}
	}
	/**
	 * 
	 */
	private static boolean isLeagle(String str){
		boolean flag = false;
		if(str.contains("区") || str.contains("路") || str.contains("")){
			flag = true;
		}
		return flag;
	}
	/**
	 * 
	 */
	private void addtoList(String str){
		str = str.trim();
		int index_phone = str.indexOf("电话");
		int index_add = str.indexOf("地址");
		int start = 0;
		int end = str.length();
		if(index_add != -1){
			start = index_add + 3;
		}
		if(index_phone != -1){
			end = index_phone;
		}
		String address = str.substring(start, end);
		if(address.length() > 5 && address.length() < 50){
			if(!addList.contains(address)){
				addList.add(address);
			}
		}
	}

}
