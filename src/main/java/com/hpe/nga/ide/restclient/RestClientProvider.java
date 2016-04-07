package com.hpe.nga.ide.restclient;

public class RestClientProvider {

    private static RestClient restClient = null;

    private RestClientProvider(){}

    public static RestClient getRestClient(){
        if(restClient==null){
            restClient = new RestClient();
        }
        return restClient;
    }
}

