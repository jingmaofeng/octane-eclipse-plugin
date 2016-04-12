package com.example.testplugin.views;

import com.hpe.nga.ide.restclient.RestClient;
import com.hpe.nga.ide.restclient.RestClientException;
import com.hpe.nga.ide.restclient.RestClientProvider;

public class ApplRestClientTest {

	public static String[] getRestClient(String serverUrl) {
		
		String[] response = { "", "" };
		String[] url = ParsingURL.parsing(serverUrl);
		RestClient restClient = RestClientProvider.getRestClient();	
		try {
			restClient.connect(SecureStorage.getInstance(), url[0], true);
			response[1] = restClient.getWorkspace(Integer.valueOf(url[1]), Integer.valueOf(url[2])).toString();
		} catch (RestClientException e) {
			e.getMessage();
		}
		response[0] = Integer.toString(restClient.getLastResponseCode());
		restClient.disconnect();
		return response;
	}

}
