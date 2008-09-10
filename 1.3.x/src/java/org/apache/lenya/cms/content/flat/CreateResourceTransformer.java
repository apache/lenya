package org.apache.lenya.cms.content.flat;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.transformation.AbstractDOMTransformer;
import org.apache.lenya.cms.content.ResourceException;
import org.apache.lenya.cms.content.ResourceTransformer;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
/**
 * @cocoon.sitemap.component.documentation This transformer creates a new Resource.
 * 
 * @author solprovider
 * @since 1.3
 */
// <?xml version="1.0" encoding="UTF-8"?>
// <resource defaultlanguage="en" doctype="xhtml" id="doctypes" type="xml" unid="0002" xmlns=""/>
public class CreateResourceTransformer extends AbstractDOMTransformer implements Configurable {
   private String type = ResourceTransformer.TYPE_CONTENT;
   public void configure(Configuration conf) throws ConfigurationException {
      Configuration child = conf.getChild("type");
      type = child.getValue(type);
   }
   protected org.w3c.dom.Document transform(org.w3c.dom.Document doc) {
      createResource(doc, type);
      return doc;
   }
   /**
    * @deprecated Request is not needed. Must update JS files in Modules before deleting.
    */
   static public org.w3c.dom.Document transformDocument(Request request, org.w3c.dom.Document doc, String type) throws ProcessingException {
      createResource(doc, type);
      return doc;
   }
   static public org.w3c.dom.Document transformDocument(org.w3c.dom.Document doc, String type) throws ProcessingException {
      createResource(doc, type);
      return doc;
   }
   static private void createResource(org.w3c.dom.Document doc, String resourceType) {
      // System.out.println("CreateResourceTransformer.createResource Type=" + resourceType);
      if(doc == null){
         System.out.println("CreateResource: Document is required.");
         // throw new ProcessingException("CreateResource: document is required.");
         return;
      }
      Element root = doc.getDocumentElement();
      NodeList nodes = root.getElementsByTagName("newresource");
      int length = nodes.getLength();
      for(int n = 0; n < length; n++){
         Element element = (Element) nodes.item(n);
         String unid = element.getAttribute("unid");
         String type = element.getAttribute("type");
         String id = element.getAttribute("id");
         FlatResource resource;
         try{
            if(ResourceTransformer.TYPE_STRUCTURE.equalsIgnoreCase(resourceType)){
               System.out.println("CreateResourceTransformer.createResource calling createStructure");
               resource = FlatResource.createStructure(unid, type, id);
            }else if(ResourceTransformer.TYPE_DESIGN.equalsIgnoreCase(resourceType)){
               resource = FlatResource.createDesign(unid, type, id);
            }else{
               resource = FlatResource.createContent(unid, type, id);
            }
            unid = resource.getUNID();
            element.setAttribute("unid", unid);
            element.setAttribute("status", "SUCCESS");
         }catch(ResourceException e){
            element.setAttribute("status", "FAILURE");
            Text text = doc.createTextNode(e.getMessage());
            element.appendChild(text);
         }
      }
   }
}
