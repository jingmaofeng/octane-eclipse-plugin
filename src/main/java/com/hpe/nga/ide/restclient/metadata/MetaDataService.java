package com.hpe.nga.ide.restclient.metadata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.hpe.nga.ide.restclient.RestClientException;

public class MetaDataService {
	private Map<String, EntityMetaData> entityToMetaDataMap;

	public MetaDataService() {
		entityToMetaDataMap = new HashMap<String, EntityMetaData>();
	}

	public void load(String entityType, String jsonResponse) throws JsonParseException, JsonMappingException, IOException, RestClientException {
		if (!entityToMetaDataMap.containsKey(entityType)) {
			entityToMetaDataMap.put(entityType, new EntityMetaData(entityType, jsonResponse));
		}
	}

	public EntityMetaData getEntityMetaData(String entitytype) {
		return entityToMetaDataMap.get(entitytype);
	}
}
