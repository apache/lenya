package org.apache.lenya.cms.modules;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.lenya.cms.content.flat.FlatContent;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.util.Globals;
/**
 * Retrieves Publication variables {publication:variablename} where case-insensitive variablename =<br>
 * contenttype - flat or hierarchical<br>
 * defaultlanguage<br>
 * BreadcrumbPrefix<br>
 * SSLPrefix<br>
 * ServletContext<br>
 * Directory - Discouraged, use content: and module: protocols<br>
 * ContentDirectory - Discouraged, use content: protocol<br>
 * The following three return semicolon-separated lists: languages<br>
 * revisions<br>
 * structures<br>
 * <br>
 * Anything else defaults to the publication Id. Best practice suggests using {publication:publication}.
 * 
 * @author solprovider
 * @since 1.3
 */
public class PublicationInputModule extends AbstractInputModule implements ThreadSafe {
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
    */
   public Object getAttribute(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
      // TODO: Add "name"
      if(getLogger().isDebugEnabled()){
         getLogger().debug("Resolving [" + name + "]");
      }
      Publication publication = Globals.getPublication();
      if(name.equalsIgnoreCase("contenttype")) return publication.getContentType();
      if(name.equalsIgnoreCase("defaultlanguage")) return publication.getDefaultLanguage();
      if(name.equalsIgnoreCase("BreadcrumbPrefix")) return publication.getBreadcrumbPrefix();
      if(name.equalsIgnoreCase("SSLPrefix")) return publication.getSSLPrefix();
      if(name.equalsIgnoreCase("revisions")){
         Iterator iterator = ((FlatContent) publication.getContent()).getRevisions().iterator();
         StringBuffer buffer = new StringBuffer("");
         if(iterator.hasNext()) buffer.append((String) iterator.next());
         while(iterator.hasNext())
            buffer.append(";" + (String) iterator.next());
         return buffer.toString();
      }
      if(name.equalsIgnoreCase("structures")){
         Iterator iterator = ((FlatContent) publication.getContent()).getStructures().iterator();
         StringBuffer buffer = new StringBuffer("");
         if(iterator.hasNext()) buffer.append((String) iterator.next());
         while(iterator.hasNext())
            buffer.append(";" + (String) iterator.next());
         return buffer.toString();
      }
      if(name.equalsIgnoreCase("languages")){
         String[] languagesArray = publication.getLanguages();
         StringBuffer buffer = new StringBuffer(languagesArray[0]);
         for(int l = 1; l < languagesArray.length; l++)
            buffer.append(";" + languagesArray[l]);
         return buffer.toString();
      }
      try{
         if(name.equalsIgnoreCase("ServletContext")) return publication.getServletContext().getCanonicalPath();
         if(name.equalsIgnoreCase("Directory")) return publication.getDirectory().getCanonicalPath();
         if(name.equalsIgnoreCase("ContentDirectory")) return publication.getContentDirectory().getCanonicalPath();
      }catch(java.io.IOException ioe){
         return "error";
      }
      return publication.getId();
   }
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration, java.util.Map)
    */
   public Iterator getAttributeNames(Configuration modeConf, Map objectModel) throws ConfigurationException {
      return Collections.EMPTY_SET.iterator();
   }
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
    */
   public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
      Object[] objects = {getAttribute(name, modeConf, objectModel)};
      return objects;
   }
   /**
    * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
    */
   public void configure(Configuration conf) throws ConfigurationException {
      super.configure(conf);
   }
}