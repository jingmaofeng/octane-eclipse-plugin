/**
 * 
 */
package org.hp.qc.web.restapi.docexamples.docexamples.infrastructure;

/**
 * @author nathan
 * 
 * 
 * these constants are used through out the code to determine the server to work against. if you
 * wish to execute this code, and not just learn from it, you must change these settings to fit
 * those of your server.
 */
public class Constants {
    
    private Constants() {}
    
    /*
    public static final String HOST = "vbronstein01";
    public static final String PORT = "8080";
    
    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin";
    
    public static final String DOMAIN = "DEFAULT";
    public static final String PROJECT = "DEFAULT";
    
    //*/
    /*
    
    public static final String USERNAME = "alex_qa";
    public static final String PASSWORD = "";
    public static final String PROJECT = "ALM_Demo";
      
    //*/
    //*
    public static final String HOST = "localhost";
    public static final String PORT = "8080";
    
    public static final String USERNAME = "admin";
    public static final String PASSWORD = "";
    
    public static final String DOMAIN = "DEFAULT";
    public static final String PROJECT = "version";
    
    //*/
    
    /**
     * @return true if entities of entityType support versioning
     * 
     * this is a more advanced use case, using the customization resource. this is not an example,
     * just something needed in order to run the tests correctly on both versioned and non-versioned
     * projects.
     */
    public static boolean isVersioned(String entityType, final String domain, final String project) throws Exception {
        
        RestConnector con = RestConnector.getInstance();
        String descriptorUrl =
                con.buildUrl("rest/domains/"
                             + domain
                             + "/projects/"
                             + project
                             + "/customization/entities/"
                             + entityType);
        
        String descriptorXml = con.httpGet(descriptorUrl, null, null).toString();
        EntityDescriptor descriptor =
                EntityMarshallingUtils.marshal(EntityDescriptor.class, descriptorXml);
        
        boolean isVersioned = descriptor.getSupportsVC().getValue();
        
        return isVersioned;
    }
    
    /**
     * @param field
     * @param value
     * @return
     */
    public static String generateFieldXml(String field, String value) {
        
        return "<Field Name=\"" + field + "\"><Value>" + value + "</Value></Field>";
    }
    
    /**
     * we'll use this string to create new "requirement" type entities.
     */
    public static final String entityToPostName = "req" + Double.toHexString(Math.random());
    public static final String entityToPostFieldName = "type-id";
    public static final String entityToPostFieldValue = "1";
    public static final String entityToPostFormat =
            "<Entity Type=\"requirement\">"
                    + "<Fields>"
                    + Constants.generateFieldXml("%s", "%s")
                    + Constants.generateFieldXml("%s", "%s")
                    + "</Fields>"
                    + "</Entity>";
    
    public static final String entityToPostXml =
            String.format(
                    entityToPostFormat,
                    "name",
                    entityToPostName,
                    entityToPostFieldName,
                    entityToPostFieldValue);
    
    public static final CharSequence entityToPostFieldXml =
            generateFieldXml(Constants.entityToPostFieldName, Constants.entityToPostFieldValue);

}