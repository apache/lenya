package org.apache.lenya.cms.content;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
public interface Content {
   public static final String TYPE_FLAT = "flat";
   public static final String TYPE_HIERARCHICAL = "hierarchical";
   public static final String TYPE_DEFAULT = "all";
   public String getURI(String unid, String language, String revision);
   public String getMetaURI(String unid, String language, String revision);
   public String getNewURI(String unid, String language);
   public String getUNID(String structure, String id);
   public String getIndexFilename(String indexName, String language);
   public String[] getLanguages();
   public Resource getResource(String unid);
}
