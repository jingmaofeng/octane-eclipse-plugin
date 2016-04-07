package com.hpe.nga.ide.restclient;

public class Constants {
	public static final String URL_AUTHENTICATION = "authentication/sign_in/";
	public static final String URL_LOGOUT = "authentication/sign_out/";
	public static final String URL_SHARED_SPACE = "api/shared_spaces/%d/";
	public static final String URL_WORKSPACE = "workspaces/%d/";
	public static final String URL_SHARED_SPACE_AND_WORKSPACE = URL_SHARED_SPACE + URL_WORKSPACE;	
	public static final String URL_METADATA = "metadata/fields?query=\"(entity_name='%s')\"";
	
	// Text exceptions
	public static final String EXCEPTION_PREFIX = "Failed to connect to server: ";
	public static final String EXCEPTION_NULL_OR_EMPTY = Constants.EXCEPTION_PREFIX + "%s is null or empty.";
	public static final String EXCEPTION_ERROR_SEND_REQUEST = Constants.EXCEPTION_PREFIX +  "error in send request: %s.";
	public static final String EXCEPTION_ERROR_STATUS = Constants.EXCEPTION_PREFIX +  "the sign in operation failed, status code = %d.";
	public static final String EXCEPTION_ERROR_URL = Constants.EXCEPTION_PREFIX + "URL not found %s.";
	public static final String EXCEPTION_AUTH_FAILED = Constants.EXCEPTION_PREFIX + "authorization failed for username %s at %s";
	public static final String EXCEPTION_ERROR_NO_CONNECTION = "You are not connected to the server. You must first use connect().";
}
