package model;

public final class ConnectionClientInfo {
	public static final String URL_AUTH = "authentication/sign_in/";
	public static final String URL_SHARED_SPACE = "api/shared_spaces/";
	public static final String URL_WORKSPACE = "/workspaces/";

	public ConnectionClientInfo(String serverUrl, String username, String password, int sharedSpaceId,
			int workSpaceId) {
		this.serverUrl = serverUrl;
		this.username = username;
		this.password = password;
		this.sharedSpaceId = sharedSpaceId;
		this.workSpaceId = workSpaceId;
		urlAuth = makeSlashInEndURL(serverUrl) + URL_AUTH;
		urlWithSharespaceAndWorkspace = makeSlashInEndURL(serverUrl) + URL_SHARED_SPACE + Integer.toString(sharedSpaceId) + URL_WORKSPACE 
				+Integer.toString(workSpaceId) + "/";
	}

	public String serverUrl;
	public String username;
	public String password;
	public int sharedSpaceId;
	public int workSpaceId;
	public String urlWithSharespaceAndWorkspace;
	public String urlAuth;
	
	private String makeSlashInEndURL(String url) {
		return url.charAt(url.length() - 1) != '/' ? url + "/" : url;
	}
}
