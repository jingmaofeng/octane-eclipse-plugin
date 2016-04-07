package com.hpe.nga.ide.restclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONparseResult {
	
	public static Map<String, Object> parseJSON(String jsonLine) {
		JSONObject jObj = new JSONObject(jsonLine);
		JSONArray names = jObj.names();
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < names.length(); i++) {
			String key = names.getString(i);
			Object value = jObj.get(key);
			if (value instanceof JSONArray) {
				List<Object> listObject = new ArrayList<Object>();
				JSONArray arrayElements = jObj.getJSONArray(key);
				for (int j = 0; j < arrayElements.length(); j++) {
					if(isPrimitive(arrayElements.get(j))) {
						listObject.add(arrayElements.get(j));
					}
					else if (isEntity(arrayElements.get(j))) {
						listObject.add(new Entity(parseJSON(arrayElements.get(j).toString())));
					} else {
						listObject.add(parseJSON(arrayElements.get(j).toString()));
					}
				}
				value = listObject;
			} else if (value instanceof JSONObject) {
				if (isEntity(value)) {
					value = new Entity(parseJSON(value.toString()));
				} else
					value = parseJSON(value.toString());
			}
			map.put(key, value);
		}
		return map;
	}

	private static boolean isPrimitive(Object object) {
		return object instanceof String || object instanceof Number || object instanceof Boolean;
	}

	private static boolean isEntity(Object obj) {
		JSONObject jsonObject = new JSONObject(obj.toString());
		return !jsonObject.isNull("type") && !jsonObject.isNull("name") && !jsonObject.isNull("id");
	}
}
