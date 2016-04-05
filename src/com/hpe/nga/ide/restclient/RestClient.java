package com.hpe.nga.ide.restclient;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;

public class RestClient {
	static int i=1;
	private String serverUrl;
	private String token;
	public boolean authorized = false;
	private boolean shouldTryReconnect = true;
	private boolean autoReconnect = false;
	private int lastResponseCode = 0;
	protected CookiesManager cookiesManager = CookiesManager.getInstance();
	private CredentialsStore credentialsStore;

	public RestClient() {
	}

	public int getLastResponseCode() {
		return lastResponseCode;
	}

	protected String getServerUrl() {
		return serverUrl;
	}

	/**
	 * connect to serverUrl and authorization
	 * 
	 * @param credentialsStore
	 * @param serverUrl
	 * @param autoReconnect
	 * @throws RestClientException
	 */
	public void connect(CredentialsStore credentialsStore, String serverUrl, boolean autoReconnect)
			throws RestClientException {
		if (!authorized) {
			validateArguments(credentialsStore, serverUrl);
			this.credentialsStore = credentialsStore;
			this.serverUrl = validateUrlHasSlashAtEnd(serverUrl);
			this.autoReconnect = autoReconnect;
			authorization();
		}
	}

	private void validateArguments(CredentialsStore credentialsStore, String url) throws RestClientException {
		String username = credentialsStore.getUserName();
		if (username == null || username.isEmpty()) {
			throw new RestClientException(String.format(Constants.EXCEPTION_NULL_OR_EMPTY, "username"));
		}

		if (url == null || url.isEmpty()) {
			throw new RestClientException(String.format(Constants.EXCEPTION_NULL_OR_EMPTY, "URL"));
		}
	}

	private String validateUrlHasSlashAtEnd(String url) {
		return url.charAt(url.length() - 1) != '/' ? url + "/" : url;
	}

	public void disconnect() {
		if (authorized) {
			HttpURLConnection connection;
			try {
				connection = (HttpURLConnection) new URL(serverUrl + Constants.URL_LOGOUT).openConnection();
				connection.setRequestMethod("POST");
			} catch (Exception e) {
				e.printStackTrace();
			}
			lastResponseCode = 0;
			token = null;
			authorized = false;
		}
	}

	public Workspace getWorkspace(int sharedspaceId, int workspaceId) throws RestClientException {
		return new Workspace(this, sharedspaceId, workspaceId);
	}

	protected HttpURLConnection get(String url) throws RestClientException {
		Map<String, String> property = new HashMap<String, String>();
		property.put("Accept", "application/json");
		property.put("HPECLIENTTYPE", "HPE_MQM_PLUGIN_UI");
		return sendRequest(url, "GET", property, HttpURLConnection.HTTP_OK);
	}

	public HttpURLConnection post(String url, String data) throws RestClientException {
		Map<String, String> property = new HashMap<String, String>();
		property.put("HPECLIENTTYPE", "HPE_MQM_PLUGIN_UI");
		property.put("HPSSO-HEADER-CSRF", token);
		property.put("Content-Type","application/json;charset=UTF-8");
		return sendRequest(url, "POST",  property, HttpURLConnection.HTTP_CREATED, data);
	}

	/**
	 * Send request to url, by HTTP-method method
	 * 
	 * @param method
	 *            - one of { GET, POST, PUT, DELETE }
	 * @param url
	 * @param data
	 * @return data of connection
	 * @throws RestClientException
	 */
	private HttpURLConnection sendRequest(String url, String method, Map<String, String> requestProperty, int statusCodeOk, String... data) throws RestClientException {
		if (!authorized) {
			throw new RestClientException(Constants.EXCEPTION_ERROR_NO_CONNECTION);
		}
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod(method);
			for(Entry<String, String> entry: requestProperty.entrySet()){
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
			if (data.length == 1) {
				connection.setDoOutput(true);
				OutputStream output = connection.getOutputStream();
				output.write(data[0].getBytes());
				output.flush();
				output.close();
			}
			lastResponseCode = connection.getResponseCode();
			if (lastResponseCode == HttpURLConnection.HTTP_UNAUTHORIZED && tryReconnect()) {
				// auto-reconnect - try once
				shouldTryReconnect = false;
				disconnect();
				authorization();
				if(data.length == 1) connection = sendRequest(url, method, requestProperty, statusCodeOk, data);
				else connection = sendRequest(url, method, requestProperty, statusCodeOk);
				shouldTryReconnect = true;
			}
		} catch (Exception e) {
			throw new RestClientException(String.format(Constants.EXCEPTION_ERROR_SEND_REQUEST, url));
		}
		if (lastResponseCode != statusCodeOk) {
			throw new RestClientException(String.format(Constants.EXCEPTION_ERROR_STATUS, lastResponseCode));
		}
		return connection;
	}

	private boolean tryReconnect() {
		return autoReconnect && shouldTryReconnect;
	}

	/**
	 * Authorization on server with username/password, encode by Base64
	 * 
	 * @throws RestClientException
	 */
	private void authorization() throws RestClientException {
		String url = serverUrl + Constants.URL_AUTHENTICATION;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "text/plain");
			connection.setRequestProperty("Authorization",
					"Basic " + getEncode64(credentialsStore.getUserName(), credentialsStore.getPassword()));
			lastResponseCode = connection.getResponseCode();
		} catch (MalformedURLException e) {
			throw new RestClientException(String.format(Constants.EXCEPTION_ERROR_URL, serverUrl));
		} catch (Exception e) {
			throw new RestClientException(
					String.format(Constants.EXCEPTION_AUTH_FAILED, credentialsStore.getUserName(), serverUrl));
		}
		// check connection response
		if (lastResponseCode == HttpURLConnection.HTTP_OK) {
			token = cookiesManager.getToken();
			authorized = true;
		} else {
			throw new RestClientException(String.format(Constants.EXCEPTION_ERROR_STATUS, lastResponseCode));
		}
	}

	/**
	 * Encode user and password in Base64, encode in format <user>:<pass>
	 * 
	 * @param user
	 * @param password
	 * @return encode string line
	 */
	private String getEncode64(String user, String password) {
		try {
			return new String(Base64.encodeBase64(new String(user + ":" + password).getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
}
