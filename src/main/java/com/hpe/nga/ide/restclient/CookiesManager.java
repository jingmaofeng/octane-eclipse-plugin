package com.hpe.nga.ide.restclient;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;

public class CookiesManager {
	private static final String HPSSO_COOKIE_CSRF = "HPSSO_COOKIE_CSRF";
	private static CookiesManager INSTANCE = null;
	private CookieManager manager;
	private CookieStore cookieStore;

	private CookiesManager() {
		manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
		cookieStore = manager.getCookieStore();
	}
	
	public static CookiesManager getInstance() {
		if (INSTANCE == null) {
			return new CookiesManager();
		}
		return INSTANCE;
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	// search token in cookies
	public String getToken() {
		if (cookieStore.getCookies() != null)
			for (HttpCookie result : cookieStore.getCookies())
				if (result.getName().equals(HPSSO_COOKIE_CSRF))
					return result.getValue();
		return null;
	}
}
