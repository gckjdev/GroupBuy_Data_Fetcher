package com.orange.groupbuy.addressparser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GenericAddressParser extends CommonAddressParser {

	private List<String> addList = new LinkedList<String>();

	@Override
	public List<String> doParseAddress(String url) {
		try {
			addList.clear();
			HttpURLConnection connection = (HttpURLConnection) (new URL(url))
					.openConnection();
			if (connection != null) {
				long fetchTime = System.currentTimeMillis();
				Document doc = Jsoup.connect(url).get();
				Document doc = connection.get();
				if (doc != null) {
					long parseStartTime = System.currentTimeMillis();
					find_common_add(doc, url);
					long parseEndTime = System.currentTimeMillis();
					connection.disconnect();
					System.out.println("<debug> parsing addrestrs, network "
							+ (parseStartTime - fetchTime)
							+ " millseconds, parse "
							+ (parseEndTime - parseStartTime) + " millseconds");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return addList;
	}

	/**
	 * 
	 */
	private void find_common_add(Document doc, String url) {
		try {
			Elements list = (Elements) doc.getElementsByTag("div");
			for (Element element : list) {
				String content = element.text();
				String[] strs = content.split("\\s");
				if (strs == null)
					return;
				int len = strs.length;
				// TODO
				for (int i = 0; i < strs.length; i++) {
					String str = strs[i];
					int index = str.indexOf("地址�?);
					if (index != -1) {
						str = str.substring(index + 3);
						strs[i] = str;
					}
					index = str.indexOf("电话");
					if (index != -1) {
						str = str.substring(0, index);
						strs[i] = str;
					}
				}
				int[] scores = new int[len];
				for (int i = 0; i < len; i++)
					scores[i] = 0;
				for (int i = 0; i < len; i++) {
					if (strs[i].length() > 8 && strs[i].length() < 80) {
						scores[i] = addScore(strs[i]);
					}
				}
				// bubble sort
				int temp;
				String stemp;
				for (int i = 0; i < scores.length; i++) {
					for (int j = 0; j < scores.length - i - 1; j++) {
						if (scores[j] < scores[j + 1]) {
							temp = scores[j];
							scores[j] = scores[j + 1];
							scores[j + 1] = temp;
							// swap the string at the same time
							stemp = strs[j];
							strs[j] = strs[j + 1];
							strs[j + 1] = stemp;
						}
					}
				}
				int i = 0;
				// consider the score
				for (i = 0; i < len; i++) {
					if (scores[i] >= 3) {
						addtoList(strs[i]);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private boolean addtoList(String str) { // return true if a valid address is
											// found
		str = str.trim();
		int len = str.length();
		if (len <= 0)
			return false;
		int index = str.indexOf("地址");
		if (index != -1) {
			str = str.substring(index + 3);
		}
		
		// delete the illegal string
		str = delString(str);

		// TODO remove
		System.out.println("<debug> parse str result=" + str);

		if (str.length() > 5 && str.length() < 50) {
			if (str.contains("�?) || str.contains("�?) || (addScore(str) < 2)) {
				System.out.println("<debug> have the illegal code= " + str);
			} else if (addList.indexOf(str) == -1) {
				addList.add(str);
				// TODO remove
				System.out.println("<debug> final result=" + str);
			} else {
				System.out.println("<debug> have the same address!");
			}
			return true;
		} else {
			System.out.println("<debug> parse str, address length "
					+ str.length() + " too short or too long, skip");
			return false;
		}
	}

	/**
	 * 
	 */
	private int addScore(String str) {
		int score = 0;
		if (str.contains("�?))
			score++;
		if (str.contains("�?))
			score++;
		if (str.contains("�?))
			score++;
		if (str.contains("�?))
			score++;
		if (str.contains("�?))
			score++;
		if (str.contains("�?))
			score++;
		if (str.contains("�?))
			score++;
		if (str.contains("�?))
			score++;
		if (str.contains("�?))
			score++;
		if (str.contains("�?))
			score++;
		if (str.contains("广场"))
			score++;

		return score;
	}

	/**
	 * 
	 */
	private String delString(String str) {
		int index = 0;
		// TODO for gaopang
		str = str.replace(" ", "");
		// TODO for meituan
		index = str.indexOf("店：");
		if (index != -1) {
			str = str.substring(index + 2);
		}
		index = str.indexOf("（查看地图）");
		if (index != -1) {
			str = str.substring(0, index);
		}
		// TODO for manzuo
		index = str.indexOf("公交信息");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("联系");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("联络");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("咨询");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("预订");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("预约");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("公交");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("客服");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("营业");
		if (index != -1) {
			str = str.substring(0, index);
		}
		index = str.indexOf("交�?");
		if (index != -1) {
			str = str.substring(0, index);
		}

		return str;
	}

}
