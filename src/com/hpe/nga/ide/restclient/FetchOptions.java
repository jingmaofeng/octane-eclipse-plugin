package com.hpe.nga.ide.restclient;

public class FetchOptions {
	private String entityType;
	private Filter filter;
	private String[] fields;
	private String[] order;
	private int pageSize = 100;
	private int offset = 0;
	private char delimiter = ',';

	public FetchOptions(String entityType) {
		if(entityType == null || entityType.isEmpty())
			throw new IllegalArgumentException("undefined entity type");
		this.entityType = entityType;
	}

	public FetchOptions(String entityType, Filter filter, String[] fields, String[] order, int pageSize, int page) {
		this.entityType = entityType;
		this.filter = filter;
		this.fields = fields;
		this.order = order;
		this.pageSize = pageSize;
		this.offset = page;
	};

	// build url
	@Override 
	public String toString() {				
		StringBuffer result = new StringBuffer(entityType);
		result.append("s?");
	
		String fieldsStr = arrayToString(fields,delimiter);
		if (fieldsStr != null && !fieldsStr.isEmpty()) {
			result.append("fields=" + fieldsStr);
			result.append("&");
		}
				
		result.append("limit=" + pageSize);
		result.append("&");
		result.append("offset=" + offset);
		
		String orderLine = arrayToString(order,delimiter);
		if (orderLine != null && !orderLine.isEmpty()) {
			result.append("&");
			result.append("order_by=" + orderLine);	
		}
		
		if (filter != null && filter.getFilter() != null && !filter.getFilter().isEmpty()) {
			result.append("&");
			result.append("query=\"" + filter.getFilter() + "\"");
		}
		
		return result.toString();
	}

	private String arrayToString(String[] array, char delimiter) {
		StringBuffer line = new StringBuffer();
		boolean firstStep = true;
		if (array != null) {
			for (String field : array) {
				if (firstStep) {
					firstStep = false;
				} else {
					line.append(delimiter);					
				}									
				line.append(field.replaceAll(" ", ""));
			}
			return line.toString();
		}
		return null;
	};

	public Filter getFilter() {
		return filter;
	}

	public FetchOptions setFilter(Filter filter) {
		this.filter = filter;
		return this;
	}

	public String[] getFields() {
		return fields;
	}

	public FetchOptions setFields(String[] fields) {
		this.fields = fields;
		return this;
	}

	public String[] getOrder() {
		return order;
	}

	public FetchOptions setOrder(String[] order) {
		this.order = order;
		return this;
	}

	public int getPageSize() {
		return pageSize;
	}

	public FetchOptions setPageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public int getOffset() {
		return offset;
	}

	public FetchOptions setOffset(int offset) {
		this.offset = offset;
		return this;
	}
}
