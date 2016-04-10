package com.hpe.nga.ide.restclient.metadata;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class MetadataDeserializer extends JsonDeserializer<HashMap<String, FieldMetaData>> {
	 
    @Override
    public HashMap<String, FieldMetaData> deserialize(JsonParser jsonParser, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        JsonNode allFieldsMetaData = (JsonNode) jsonParser.getCodec().readTree(jsonParser).get("data");
        HashMap<String, FieldMetaData> mapItem = new HashMap<String, FieldMetaData>();
        for(JsonNode fieldMetaData: allFieldsMetaData){
        	String label =  fieldMetaData.get("label").asText();
        	boolean sortable = fieldMetaData.get("sortable").asBoolean();
            String name = fieldMetaData.get("name").asText();
            boolean visibleInUi = fieldMetaData.get("visible_in_ui").asBoolean();
            String fieldType =  fieldMetaData.get("field_type").asText();
            boolean multiple = false;
            if(fieldMetaData.has("field_type_data")&&fieldMetaData.get("field_type_data").has("multiple")) {
            	multiple = fieldMetaData.get("field_type_data").get("multiple").asBoolean();
            }
            mapItem.put(name, new FieldMetaData(label, sortable, name, visibleInUi, fieldType, multiple));
        } 
         return mapItem;
    }
}