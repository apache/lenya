package org.apache.lenya.cms.content.hierarchical;
import java.io.File;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.cms.content.Resource;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.util.Globals;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
public class HierarchicalContent implements Content {
   File directory;
   String[] languages = {"en"};
   public HierarchicalContent(File directory, String[] languages) {
      this.directory = directory;
      this.languages = languages;
   }
   /* Content API */
   public String getIndexFilename(String indexName, String language) {
      return new File(directory, indexName + File.separator + "sitetree.xml").getPath();
      // WORK? Filter by language and transform sitetree to index XML.
   }
   public String getUNID(String structure, String id) {
      return id;
   }
   public String[] getLanguages() {
      return languages;
   }
   public String getURI(String unid, String language, String revision) {
      String area = revision;
      if(area.equalsIgnoreCase("edit")){
         area = Publication.AUTHORING_AREA;
      }
      return new File(directory, area + unid + File.separator + "index_" + language + ".xml").getPath();
   }
   public String getMetaURI(String unid, String language, String revision) {
      String area = revision;
      if(area.equalsIgnoreCase("edit"))
         area = Publication.AUTHORING_AREA;
      return new File(directory, area + unid + File.separator + "index_" + language + ".xml").getPath();
   }
   public String getNewURI(String unid, String language) {
      return new File(directory, "live" + unid + File.separator + Globals.getDateString() + "_" + language + ".xml").getPath();
   }
   public Resource getResource(String unid) {
      // TODO: HierarchicalResource
      return (Resource) new HierarchicalResource(directory, unid);
   }
}
