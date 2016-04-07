package com.hpe.nga.ide.restclient;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class JUnitTestFetchOptions {
	FetchOptions options;
	String entityType = "defect";
	String restEntityName = entityType + "s";
	// default value of limit and offset in FetchOptions.class
	int default_limit = 100;
	int default_offset = 0;
	String default_line = "limit=" + default_limit + "&offset=" + default_offset;

	@Before
	public void setUp() {
		options = new FetchOptions(entityType);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyEntityType () {
		options = new FetchOptions("");
	}
	
	@Test
	public void testEmptyFieldsSet() {
		options.setFields(new String[] { "" });
		assertEquals(restEntityName + "?" + default_line, options.toString());
	}
		
	@Test
	public void testMinimalOptions() {
		assertEquals(restEntityName + "?" + default_line, options.toString());
	}

	@Test
	public void testOneFieldsSet() {
		options.setFields(new String[] { "id" });
		assertEquals(restEntityName + "?fields=id&" + default_line, options.toString());
	}

	@Test
	public void testTwoFieldsSet() {
		options.setFields(new String[] { "id","name" });
		assertEquals(restEntityName + "?fields=id,name&" + default_line, options.toString());
	}

	@Test
	public void testEmptyOrderbySet() {
		options.setFields(new String[] { "" });
		assertEquals(restEntityName + "?" + default_line, options.toString());
	}
	
	@Test
	public void testOneOrderbySet() {
		options.setOrder(new String[] { "id"});
		assertEquals(restEntityName + "?" + default_line + "&order_by=id", options.toString());
	}	
	
	@Test
	public void testTwoOrderbySet() {
		options.setOrder(new String[] {"id","name"});
		assertEquals(restEntityName + "?" + default_line + "&order_by=id,name", options.toString());
	}
	
	@Test
	public void testEmptyFilter() {
		options.setFilter(new Filter(""));
		assertEquals(restEntityName + "?" + default_line, options.toString());
	}
	
	@Test
	public void testOneFilter() {
		options.setFilter(new Filter("(id<1042)"));
		assertEquals(restEntityName + "?" + default_line + "&query=\"(id<1042)\"", options.toString());
	}
	
	@Test
	public void testTwoFilter() {
		options.setFilter(new Filter("(id<1042);(id>1001)"));
		assertEquals(restEntityName + "?" + default_line + "&query=\"(id<1042);(id>1001)\"", options.toString());
	}
	
	@Test
	public void testChangeLimitAndOffset(){
		int limit = 45;
		int offset = 5;
		options.setPageSize(limit);
		options.setOffset(offset);
		assertEquals(restEntityName + "?limit=" + limit + "&offset=" + offset, options.toString());		
	}
	
	@Test
	public void testSetAllParametrs(){
		options.setFields(new String[] {"id","name","type"})
				.setFilter(new Filter("(id<1042)"))
				.setOffset(2)
				.setPageSize(33)
				.setOrder(new String[] {"type"});
		assertEquals(restEntityName + "?fields=id,name,type&limit=33&offset=2&order_by=type&query=\"(id<1042)\"", options.toString());		
	}
}
