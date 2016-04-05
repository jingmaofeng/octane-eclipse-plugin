package com.hpe.nga.ide.restclient;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	private String type;
	public Map<String, Object> fields;

	public Entity(String type) {
		this.type = type;
		fields = new HashMap<String, Object>();		
	}
	
	public Entity(Map<String, Object> fields) {
		this.fields = fields;
		if(fields.containsKey("type")) {
			this.type = getFieldValue("type");
		}
	}

	private String getFieldValue(String field) {
		Object value = fields.get(field);
		if (value == null) {
			return "";
		} else {
			return value.toString();
		}
	}

	public String getType() {
		return type;
	}

	public int getId() {
		try{
			int result = Integer.parseInt(getFieldValue("id"));
			return result;
		}
		catch(Exception e) {
			return -1;		
		}
	}
	
	public String getName() {
		return getFieldValue("name");
	}

	public Entity getParent(){
		return (Entity) fields.get("parent");
	}
	
	public Entity getRelease(){
		return (Entity) fields.get("release");
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof Entity)) {
			return false;
		} else {
			Entity other = (Entity) obj;
			return other.type.equals(type) && other.getId() == getId();
		}
	}

	public int hashCode() {
		return type.hashCode() * 31 + getId();
	}
		
	@Override
	public String toString() {
		return getName();
	}
}
