package com.hpe.nga.ide.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Workspace {
	private RestClient restClient;
	private int sharedSpaceId;
	private int workspaceId;
	private String sharedSpaceName;
	private String workspaceName;

	public Workspace(RestClient restClient, int sharedSpaceId, int workspaceId) throws RestClientException {
		this.restClient = restClient;
		this.sharedSpaceId = sharedSpaceId;
		this.workspaceId = workspaceId;
	}

	private String getWorkspaceDisplayName() {
		if (sharedSpaceName == null || workspaceName == null) {
			String urlShareSpace = restClient.getServerUrl() + String.format(Constants.URL_SHARED_SPACE, sharedSpaceId);
			String urlWorkspace = restClient.getServerUrl()
					+ String.format(Constants.URL_SHARED_SPACE_AND_WORKSPACE, sharedSpaceId, workspaceId);
			HttpURLConnection response = null;
			try {
			response = restClient.get(urlShareSpace);
			String resultResponse = getResultFromResponse(response);
			sharedSpaceName = ((Map<String, Object>) JSONparseResult.parseJSON(resultResponse)).get("name").toString();
			response = restClient.get(urlWorkspace);
			resultResponse = getResultFromResponse(response);
			workspaceName = ((Map<String, Object>) JSONparseResult.parseJSON(resultResponse)).get("name").toString();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
		return sharedSpaceName + "\\" + workspaceName;
	}

	@SuppressWarnings("unchecked")
	public List<Entity> getEntities(FetchOptions options) throws RestClientException {
		String url = restClient.getServerUrl()
				+ String.format(Constants.URL_SHARED_SPACE_AND_WORKSPACE, sharedSpaceId, workspaceId);
		HttpURLConnection response = restClient.get(url + options.toString());
		String jsonResult = getResultFromResponse(response);
		Map<String, Object> mapResult = JSONparseResult.parseJSON(jsonResult);
		return (ArrayList<Entity>) mapResult.get("data");
	}

	@SuppressWarnings("unchecked")
	public int addEntity(String entityType, String json) throws RestClientException {
		String url = restClient.getServerUrl() + String.format(Constants.URL_SHARED_SPACE_AND_WORKSPACE + "%ss",
				sharedSpaceId, workspaceId, entityType);
		HttpURLConnection response = restClient.post(url, json);
		String jsonResult = getResultFromResponse(response);
		Entity entityNew = ((Entity) ((ArrayList<Entity>) JSONparseResult.parseJSON(jsonResult).get("data")).get(0));
		return entityNew.getId();
	}

	// return json from last request to server
	private String getResultFromResponse(HttpURLConnection responseFromServer) {
		BufferedReader in;
		String inputLine;
		StringBuffer response = new StringBuffer();
		try {
			in = new BufferedReader(new InputStreamReader(responseFromServer.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return getWorkspaceDisplayName();
	}

	// parse the response and create list of metadata
	public List<Map<String, Object>> getMetadata(String entityType) throws RestClientException {
		String url = restClient.getServerUrl()
				+ String.format(Constants.URL_SHARED_SPACE_AND_WORKSPACE, sharedSpaceId, workspaceId)
				+ Constants.URL_METADATA;
		HttpURLConnection response = restClient.get(String.format(url, entityType));
		String result = getResultFromResponse(response);
		return getMetadataList(result);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getMetadataList(String json) {

		Map<String, Object> map = JSONparseResult.parseJSON(json);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> metadata : ((List<Map<String, Object>>) map.get("data"))) {
			if ((Boolean) metadata.get("visible_in_ui"))
				result.add(metadata);
		}
		return result;
	}
}