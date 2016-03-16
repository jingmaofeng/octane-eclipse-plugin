package test;

import org.junit.Before;
import org.junit.Test;

import controller.RestClient;
import junit.framework.Assert;

public class JUnitTestConnect extends Assert {
	public String login = "edi@hpe.com";
	public String pass = "";
	public String url = "http://myd-vm21085.hpswlabs.adapps.hp.com:8081";
	public int sharespace = 1021;	
	public int workspace = 1002;
	RestClient ht;
	
	@Before
	  public void setUp() {
		ht = RestClient.getInstance();
		ht.Authenticate(url, login, pass, sharespace, workspace);
		ht.Authorization();
	  }
	
	@Test
	public void TestConnect(){
		assertEquals(ht.getResponseCode(), 200);
		assertEquals(ht.is_auth(), true);
	}
	
	@Test
	public void TestGetWorkspace(){
		String json = ht.getEntity("");
		String id = json.substring(json.indexOf("id")+4);
		assertEquals(Integer.parseInt(id.substring(0, id.indexOf(','))), workspace);
	}
	
	/// ????
	@Test
	public void TestGetDefects(){
		String json = ht.getEntity("defects");
		System.out.println(json);
		assertTrue(json.contains("defect"));
	}
}
