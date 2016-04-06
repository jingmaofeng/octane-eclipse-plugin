package com.example.testplugin.dao;

import java.util.List;
import java.util.NoSuchElementException;

import com.hpe.nga.ide.restclient.Entity;
import com.hpe.nga.ide.restclient.FetchOptions;
import com.hpe.nga.ide.restclient.Filter;
import com.hpe.nga.ide.restclient.RestClient;
import com.hpe.nga.ide.restclient.RestClientException;
import com.hpe.nga.ide.restclient.RestClientProvider;
import com.hpe.nga.ide.restclient.Workspace;

public class EntityList {
    RestClient restClient = RestClientProvider.getRestClient();
    private List<Entity> entityList;
    private SessionInfoSnglt sInfo = SessionInfoSnglt.getInstance();

    public EntityList() {
        try {
        	
        	
        	
        	
            Workspace workSpace = restClient.getWorkspace(Integer.parseInt(sInfo.getSharedSpace()), Integer.parseInt(sInfo.getWorkSpace()));
            FetchOptions options = new FetchOptions("defect");
            this.entityList = workSpace.getEntities(options);
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
        }
    }

    public EntityList(String filter) {
        try {
            Workspace ws = restClient.getWorkspace(Integer.parseInt(sInfo.getSharedSpace()), Integer.parseInt(sInfo.getWorkSpace()));
            FetchOptions options = new FetchOptions("defect")
                    .setFilter(new Filter(filter));
            this.entityList = ws.getEntities(options);
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public Entity getDefectById(String id) throws NoSuchElementException{
        for(Entity entity : entityList){
            if(entity.fields.get("id").toString().equals(id)){
                return entity;
            }
        }
        throw new NoSuchElementException();
    }

    public List<Entity> getEntityList() {
        return entityList;
    }
}
