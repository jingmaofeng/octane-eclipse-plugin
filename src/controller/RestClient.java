package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.codec.binary.Base64;
import model.ConnectionClientInfo;

public final class RestClient {
	private static RestClient INSTANCE = null;
	private ConnectionClientInfo clientInfo;

	private HttpURLConnection lastConnection;
	private boolean auth = false;
	private String token = null;
	private String response = null;
	private int responseCode = 0;
	
	private CookieManager manager;
	private CookieStore cookieStore;

	private RestClient() {
	}

	public static RestClient getInstance() {
		if (INSTANCE == null) {
			return new RestClient();
		}
		return INSTANCE;
	}

	public void Authenticate(String serverUrl, String username, String password, int sharedSpaceId, int workSpaceId) {
		CheckArgumentOnNullAndEmpty(serverUrl, "url");
		CheckArgumentOnNullAndEmpty(username, "user name");
		clientInfo = new ConnectionClientInfo(serverUrl, username, password, sharedSpaceId, workSpaceId);

		manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
		cookieStore = manager.getCookieStore();
	}

	public int Authorization() {
		String url =clientInfo.urlAuth;
		sendRequest(url, "POST", headersForAuthorization());
		if (responseCode == 200) {
			this.auth = true;
			token = cookieStore.toString();
		}
		return responseCode;
	}

	public String getEntity(String entityType) {
		String url = clientInfo.urlWithSharespaceAndWorkspace + entityType;
		sendRequest(url, "GET", headersForGetRequest());
		if (responseCode == 200)
			return this.response;
		else
			return null;
	}	

	public String getResponse() {
		return response;
	}

	public boolean is_auth() {
		return auth;
	}

	private static void CheckArgumentOnNullAndEmpty(String arg, String name) {
		if (arg == null)
			throw new IllegalArgumentException(name + " is null");
		if (arg.isEmpty())
			throw new IllegalArgumentException(name + " is empty");
	}
	
	private Map<String, String> headersForGetRequest() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		headers.put("HPECLIENTTYPE", "HPE_MQM_UI");
		headers.put("HPSSO_HEADER_CSRF", token);
		return headers;
	}
	
	private Map<String, String> headersForAuthorization() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "text/plain");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Authorization", "Basic " + getEncode64(clientInfo.username, clientInfo.password));
		return headers;
	}

	private void sendRequest(String url, String method, Map<String, String> headers) {
		try {
			lastConnection = (HttpURLConnection) new URL(url).openConnection();
			lastConnection.setRequestMethod(method);
			for (Entry<String, String> entry : headers.entrySet()) {
				lastConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
			responseCode = lastConnection.getResponseCode();
			if (auth)
				response = setResponse();
		} catch (Exception e) {
			String msg = "HttpClient sendRequest exception: ";
			System.out.println("Method: " + method);
			System.out.println("URL: " + clientInfo.serverUrl);
			System.out.println("Headers: " + headers);
			System.out.println(msg + e.getMessage());
			throw new IllegalArgumentException("sendRequest exception");
		}
	}

	private String setResponse() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(this.lastConnection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	private String getEncode64(String user, String password) {
		try {
			return new String(Base64.encodeBase64(new String(user + ":" + password).getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public int getResponseCode() {
		return responseCode;
	}
}
