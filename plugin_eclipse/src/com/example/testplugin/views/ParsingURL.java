package com.example.testplugin.views;

public class ParsingURL {

	public static String[] parsing(String url) {

		String res[] = { "", "", "" };

		try {
			// Server URL short
			res[0] = url.substring(0, url.indexOf("/ui"));
			// id ShareSpace
			res[1] = url.substring(url.indexOf("?p=") + 3, url.indexOf("#")).split("/")[0];
			// id WorkSpace
			res[2] = url.substring(url.indexOf("?p=") + 3, url.indexOf("#")).split("/")[1];
		} catch (Exception e) {
			return res;
		}
		
		try {
			Integer.parseInt(res[1]);
			Integer.parseInt(res[2]);
		} catch (NumberFormatException e) {
			res[1] = "";
			res[2] = "";
		}
		return res;
	}
}
