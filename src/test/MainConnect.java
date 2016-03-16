package test;

import java.io.IOException;
import java.net.URISyntaxException;

import controller.RestClient;
public class MainConnect {

	public static String user = "edi@hpe.com";
	public static String pass = "";
	public static String urlAuth = "http://myd-vm21085.hpswlabs.adapps.hp.com:8081";

	public static void main(String[] args) throws IOException, URISyntaxException {
		RestClient client = RestClient.getInstance();
		System.out.println(client.Authorization());

		/*if(client.is_auth()){
			String defects = client.getEntity(urlAuth, "1005","1002","defects");
			System.out.println(defects);
		}
		else{
			System.out.println("not connect. Status : " + client.responseCode);
		}*/
	}
}
