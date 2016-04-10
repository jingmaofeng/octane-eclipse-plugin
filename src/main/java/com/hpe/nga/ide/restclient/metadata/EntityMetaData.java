package com.hpe.nga.ide.restclient.metadata;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class EntityMetaData {
    private String entityType;
    private HashMap<String, FieldMetaData> fieldToMetaDataMap; 

	@SuppressWarnings("unchecked")
	public EntityMetaData(String entityType, String fieldMetaDataJson) throws JsonParseException, JsonMappingException, IOException{
        this.entityType = entityType;       
        this.fieldToMetaDataMap = new HashMap<String, FieldMetaData>();
        ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(HashMap.class, new MetadataDeserializer());
		mapper.registerModule(module);
		fieldToMetaDataMap = mapper.readValue(fieldMetaDataJson, HashMap.class);
    }

    public FieldMetaData getFieldMetaData(String fieldName){
        return fieldToMetaDataMap.get(fieldName);
    }
    
    public String getType(){
    	return entityType;
    }
}
