package com.hpe.nga.ide.restclient;

public class CredentialsStoreMock implements CredentialsStore{
	// for test only
	String username  = "default_user";
	String password = "default_password";
    public CredentialsStoreMock(String user, String pass){
    	username = user;
    	password = pass;
    }
    // for test only - end
    
    public CredentialsStoreMock(){}
    
    public String getUserName(){
        return username;
    }
    public String getPassword(){
        return password;
    }
}
