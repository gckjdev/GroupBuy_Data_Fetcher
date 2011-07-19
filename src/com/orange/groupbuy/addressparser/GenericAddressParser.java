package com.orange.groupbuy.addressparser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

public class GenericAddressParser extends CommonAddressParser {
	private List<String> addList = new LinkedList<String>();
	@Override
	public List<String> doParseAddress(String url) {
		try {
			addList.clear();
			Parser parser = new Parser((HttpURLConnection)(new URL(url)).openConnection());
			find_common_add(parser, url);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return addList;
	}
	/**
	 * find the address
	 * @param str
	 */
	private int searchAdd(String str){
		int cnt = 0;
		int index = str.indexOf("��ַ");
//		System.out.println("enter searchAdd");
		if(index != -1){
			String s1 = str.substring(index+3);
			s1 = s1.trim();
			String s2 = s1.substring(0, 50);
			//index of the blank
			int index2 = s2.indexOf("\\s");
			if(index2 != -1){
				String s3 = s2.substring(0, index2);
				if(isLeagle(s3)){
					addtoList(s3);
					cnt++;
				}
				int len = s3.length();
				int index3 = str.indexOf(s3);
				searchAdd(str.substring(index3 + len));
			} else {
				addtoList(s2);
			}
		}
		return cnt;
	}
	/**
	 * find the address from ��,·���֣�,��...
	 * 
	 */
	private void searchRoad(String str){
		String[] ss = new String[10000];
//		System.out.println("enter searchRoad");
		//split the blank
		ss = str.split("\\s");
		int len = ss.length;
		if(len > 10){
			int[] scores = new int[len];
			for(int i=0; i<len; i++)
				scores[i] = 0;
			for(int i=0; i<len; i++){
				if(ss[i].length() > 8 && ss[i].length() < 80){
					if(ss[i].indexOf("��") != -1)
						scores[i]++;
					if(ss[i].indexOf("��") != -1)
						scores[i]++;
					if(ss[i].indexOf("·") != -1)
						scores[i]++;
					if(ss[i].indexOf("��") != -1)
						scores[i]++;
					if(ss[i].indexOf("��") != -1)
						scores[i]++;
					if(ss[i].indexOf("��") != -1)
						scores[i]++;
					if(ss[i].indexOf("¥") != -1)
						scores[i]++;
					if(ss[i].indexOf("��") != -1)
						scores[i]++;
					if(ss[i].indexOf("��") != -1)
						scores[i]++;
					if(ss[i].indexOf("��") != -1)
						scores[i]++;
				}
			}
			// bubble sort
			int temp;
			String stemp;
			for(int i=0; i<scores.length; i++){
				for(int j=0; j<scores.length-i-1; j++){
					if(scores[j] < scores[j+1]){
						temp = scores[j];
						scores[j] = scores[j+1];
						scores[j+1] = temp;
						//swap the string at the same time
						stemp = ss[j];
						ss[j] = ss[j+1];
						ss[j+1] = stemp;
					}
				}
			}
			int i=0;
			//consider the score
			while(scores[i] >= 3) {
				addtoList(ss[i]);
				i++;
			}		
		}
	}
	/**
	 * 
	 */
	private boolean isLeagle(String str){
		boolean flag = false;
		if(str.contains("��") || str.contains("·")){
			flag = true;
		}
		return flag;
	}
	/**
	 * �ݹ�ɾ��script�ڵ�
	 * @param list
	 */
	private void deletJSnode(NodeList list) {
		for (int i = 0; i < list.size(); i++) {
			Node node = list.elementAt(i);
			if (node instanceof Tag) {
				if (node instanceof ScriptTag) {
					list.remove(i);
					continue;
				}
			}
			NodeList children = node.getChildren();
			if (children != null && children.size() > 0)
				deletJSnode(children);
		}
	}
	/**
	 * 
	 */
	private void addtoList(String str){
		str = str.trim();
		int index_phone = str.indexOf("�绰");
		int index_add = str.indexOf("��ַ");
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
//			System.out.println(address.length());
//			System.out.println(address);
			if(!addList.contains(address)){
				addList.add(address);
			}
		}
	}
	/**
	 * 
	 */
	private void find_common_add(Parser parser, String url){
		try {
			NodeList nodes = parser.parse(null);
			deletJSnode(nodes);
//			System.out.println("nodes.size() = " + nodes.size());
			for (NodeIterator i = nodes.elements(); i.hasMoreNodes(); ) {
				Node node = i.nextNode();
				String text = node.toPlainTextString();
				int cnt = searchAdd(text);
				if(cnt == 0){
					searchRoad(text);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
}