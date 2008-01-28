package org.apache.lenya.cms.cocoon.components.modules.input;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.cms.content.Resource;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;
/**
 * Retrieves Content Variables from the appropriate Resource
 * 
 * @author solprovider
 * @deprecated Use org.apache.lenya.cms.content.ContentModule
 */
public class ContentModule extends AbstractPageEnvelopeModule implements ThreadSafe {
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
    *      org.apache.avalon.framework.configuration.Configuration,
    *      java.util.Map)
    */
   public Object getAttribute(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
      if(getLogger().isDebugEnabled()){
         getLogger().debug("Resolving [" + name + "]");
      }
      PageEnvelope pe = getEnvelope(objectModel);
      Publication pub = pe.getPublication();
      Content content = pub.getContent();
      String unid = "";
      int pos = name.indexOf(":");
      if(pos < 1)
         return "error";
      unid = name.substring(pos + 1);
      name = name.substring(0, pos);
      Resource resource = content.getResource(unid);
      if(name.equalsIgnoreCase("type"))
         return resource.getType();
      if(name.equalsIgnoreCase("doctype") || name.equalsIgnoreCase("documenttype"))
         return resource.getDocumentType();
      if(name.equalsIgnoreCase("defaultlanguage"))
         return resource.getDefaultLanguage();
      return "";
   }
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration,
    *      java.util.Map)
    */
   public Iterator getAttributeNames(Configuration modeConf, Map objectModel) throws ConfigurationException {
      return Collections.EMPTY_SET.iterator();
   }
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
    *      org.apache.avalon.framework.configuration.Configuration,
    *      java.util.Map)
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