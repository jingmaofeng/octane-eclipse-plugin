package com.hpe.nga.ide.restclient.test;

import java.net.HttpURLConnection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hpe.nga.ide.restclient.Entity;
import com.hpe.nga.ide.restclient.FetchOptions;
import com.hpe.nga.ide.restclient.Filter;
import com.hpe.nga.ide.restclient.RestClient;
import com.hpe.nga.ide.restclient.RestClientException;
import com.hpe.nga.ide.restclient.Workspace;

import junit.framework.Assert;

public class SystemTestConnect extends Assert {
	public String url = "http://my-server:8081";
	public int sharespaceId = 1001; // your shared space ID
	public int workspaceId = 1002; // your workspace ID
	RestClient restClient;
	public String entityType = "defect";
	public int invalidId = -1;
	CredentialsStoreMock credentialStore = new CredentialsStoreMock();
	public String nameNewDefect = "SystemTestADD";
	public String newDefectJSON = String.format(
			"{\"data\":[{\"parent\":{\"id\":1001,\"type\":\"work_item\",\"subtype\":\"work_item_root\",\"name\":\"Backlog\"},\"author\":{\"groups\":[],\"email\":\"%s\",\"phone1\":\"123456\",\"fullName\":\"%s  \",\"name\":\"%s\",\"language\":\"lang.en\",\"id\":1001,\"type\":\"workspace_user\"},\"subtype\":\"defect\",\"phase\":{\"id\":1001,\"type\":\"phase\"},\"detected_by\":{\"id\":1001,\"type\":\"workspace_user\"},\"name\":\"%s\",\"severity\":{\"id\":1002,\"name\":\"Low\",\"type\":\"list_node\"}}]}",
			credentialStore.username, credentialStore.username, credentialStore.username, nameNewDefect);

	@Before
	public void setUp() {
		restClient = new RestClient();
	}

	@After
	public void setAfter() {
		restClient.disconnect();
	}

	@Test
	public void testConnectToServer() throws RestClientException {
		restClient.connect(new CredentialsStoreMock(), url, true);
		assertEquals(restClient.getLastResponseCode(), HttpURLConnection.HTTP_OK);
		restClient.disconnect();
	}

	@Test(expected = RestClientException.class)
	public void checkEmptyURL() throws RestClientException {
		restClient.connect(new CredentialsStoreMock(), "", true);
	}

	@Test(expected = RestClientException.class)
	public void checkNotFoundURL() throws RestClientException {
		restClient.connect(new CredentialsStoreMock(), "abc+3546", true);
	}

	@Test(expected = RestClientException.class)
	public void checkEmptyUserName() throws RestClientException {
		restClient.connect(new CredentialsStoreMock("", ""), url, true);
	}

	@Test(expected = RestClientException.class)
	public void checkAuthorizationFailed() throws RestClientException {
		restClient.connect(new CredentialsStoreMock("noname", ""), url, true);
	}

	@Test(expected = RestClientException.class)
	public void checkWithInvalidSharedspace() throws RestClientException {
		restClient.connect(new CredentialsStoreMock(), url, true);
		Workspace ws = restClient.getWorkspace(invalidId, workspaceId);
		FetchOptions options = new FetchOptions(entityType);
		ws.getEntities(options);
	}

	@Test(expected = RestClientException.class)
	public void checkWithInvalidWorkspace() throws RestClientException {
		restClient.connect(new CredentialsStoreMock(), url, true);
		Workspace ws = restClient.getWorkspace(sharespaceId, invalidId);
		FetchOptions options = new FetchOptions(entityType);
		ws.getEntities(options);
	}

	@Test(expected = RestClientException.class)
	public void checkSendRequestWithoutConnect() throws RestClientException {
		Workspace ws = restClient.getWorkspace(sharespaceId, workspaceId);
		FetchOptions options = new FetchOptions(entityType);
		ws.getEntities(options);
	}

	@Test
	public void checkAutoReconnect() throws RestClientException {
		RestClientMock restClientMock = new RestClientMock();
		restClientMock.connect(new CredentialsStoreMock(), url, true);
		Workspace ws = restClientMock.getWorkspace(sharespaceId, workspaceId);
		FetchOptions options = new FetchOptions(entityType);
		int sizeEntities = ws.getEntities(options).size();
		restClientMock.disconnect();
		int sizeEntitiesRecconect = ws.getEntities(options).size();
		assertEquals(sizeEntities, sizeEntitiesRecconect);
	}

	@Test(expected = RestClientException.class)
	public void checkAutoReconnectFalse() throws RestClientException {
		RestClientMock restClientMock = new RestClientMock();
		restClientMock.connect(new CredentialsStoreMock(), url, false);
		Workspace ws = restClientMock.getWorkspace(sharespaceId, workspaceId);
		FetchOptions options = new FetchOptions(entityType);
		ws.getEntities(options).size();
		restClientMock.disconnect();
		ws.getEntities(options).size();
	}

	@Test(expected = RestClientException.class)
	public void checkDisconnect() throws RestClientException {
		restClient.connect(new CredentialsStoreMock(), url, true);
		Workspace ws = restClient.getWorkspace(sharespaceId, workspaceId);
		FetchOptions options = new FetchOptions(entityType);
		restClient.disconnect();
		ws.getEntities(options);
	}

	@Test
	public void checkAddNewDefect() throws RestClientException {
		restClient.connect(new CredentialsStoreMock(), url, true);
		Workspace ws = restClient.getWorkspace(sharespaceId, workspaceId);
		FetchOptions options = new FetchOptions(entityType);
		int idNewDefect = ws.addEntity(entityType, newDefectJSON);
		options = options.setFilter(new Filter("id=" + idNewDefect));
		List<Entity> entities = ws.getEntities(options);
		boolean check = false;
		if (entities.get(0).getId() == idNewDefect && entities.get(0).getName().equals(nameNewDefect)) {
			check = true;
		}
		assertTrue(check);
	}
}
