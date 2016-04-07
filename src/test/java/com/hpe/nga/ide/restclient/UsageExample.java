package com.hpe.nga.ide.restclient;

import java.util.List;

public class UsageExample {
	public static String url = "http://my-server:8081"; // your url
	public static int sharedspaceID = 1001; // your shared space ID
	public static int workspaceID = 1002; // your workspace ID

	public static void main(String[] args) throws RestClientException {
		RestClient restClient = RestClientProvider.getRestClient();
			restClient.connect(new CredentialsStoreMock(), url, true);
			Workspace ws = restClient.getWorkspace(sharedspaceID, workspaceID);
			FetchOptions options = new FetchOptions("defect")
					.setFilter(new Filter("(team={name='Team1'})||(team={name='Team3'})||(release={name='Release1'})"))
					.setFields(new String[] {"id","name","parent","severity","owner"})
					.setOrder(new String[] {"name","id"})
					.setPageSize(100)
					.setOffset(0);
			List<Entity> entities = ws.getEntities(options);
			if (entities != null) {
				for (Entity entity : entities) {
					System.out.println(entity.fields.keySet());
					for(String field: entity.fields.keySet())
					{
						System.out.println(field + " : " + entity.fields.get(field).toString());
					}
				}
			}			
			restClient.disconnect();
	}
}
