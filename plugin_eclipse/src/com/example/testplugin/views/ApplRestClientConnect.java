package com.example.testplugin.views;

import java.util.List;
import com.hpe.nga.ide.restclient.Entity;
import com.hpe.nga.ide.restclient.FetchOptions;
import com.hpe.nga.ide.restclient.RestClient;
import com.hpe.nga.ide.restclient.RestClientException;
import com.hpe.nga.ide.restclient.RestClientProvider;

public class ApplRestClientConnect {

	public static List<Entity> getRestClient() {

		int idWorkSpace = 0;
		int idShareSpace = 0;
		List<Entity> entities = null;
		RestClient restClient = RestClientProvider.getRestClient();
		SecureStorage credentialsStoreEclipse = SecureStorage.getInstance();
		String serverURL = credentialsStoreEclipse.getLocation();

		if (serverURL != null && serverURL != "") {
			String[] res = ParsingURL.parsing(serverURL);
			if (res[0].equals("") || res[1].equals("") || res[2].equals("")) {
				DialogPreferencePage.preferenceDialog("Server URL is incorrect!");
			} else {
				String urlShort = res[0];
				idShareSpace = Integer.parseInt(res[1]);
				idWorkSpace = Integer.parseInt(res[2]);
				try {
					restClient.connect(credentialsStoreEclipse, urlShort, true);
				} catch (RestClientException e) {
					if (e.getMessage().contains("404")) {
						DialogPreferencePage.preferenceDialog("Server URL is not correct!");
					}
					DialogPreferencePage.preferenceDialog("connection/user name/password incorect!");
				}
			}
		}

		if (restClient.getLastResponseCode() == 200 && idShareSpace > 0 && idWorkSpace > 0) {
			try {
				entities = restClient.getWorkspace(idShareSpace, idWorkSpace).getEntities(new FetchOptions("defect"));
			} catch (RestClientException e1) {
				DialogPreferencePage.preferenceDialog("Sharespace or Workspace does not exist!");
			}
		}
		restClient.disconnect();
		return entities;
	}

}
