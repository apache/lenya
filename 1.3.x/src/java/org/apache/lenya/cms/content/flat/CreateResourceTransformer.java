package org.apache.lenya.cms.content.flat;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.transformation.AbstractDOMTransformer;
import org.apache.lenya.cms.content.ResourceException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/**
 * @cocoon.sitemap.component.documentation This transformer creates a new Resource.
 * 
 * @author solprovider
 * @since 1.3
 */
// <?xml version="1.0" encoding="UTF-8"?>
// <resource defaultlanguage="en" doctype="xhtml" id="doctypes" type="xml" unid="0002" xmlns=""/>
public class CreateResourceTransformer extends AbstractDOMTransformer {
   public static final String CONTENT_PREFIX = "content";
   public static final String FILE_NAME_REGEXP = "[-a-zA-Z0-9_. ]+";
   protected org.w3c.dom.Document transform(org.w3c.dom.Document doc) {
      Request request = ObjectModelHelper.getRequest(super.objectModel);
      createResource(request, doc);
      return doc;
   }
   static public org.w3c.dom.Document transformDocument(Request request, org.w3c.dom.Document doc) throws ProcessingException {
      createResource(request, doc);
      return doc;
   }
   /**
    * @param request
    *           The request
    * @param doc
    *           The data to be inserted.
    * @throws ProcessingException
    * @throws ResourceException
    */
   static private void createResource(Request request, org.w3c.dom.Document doc) {
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
            resource = FlatResource.create(unid, type, id);
            unid = resource.getUNID();
            // Attempts to make UNID accessible to XMAP.
            // request.setAttribute("unid", unid);
            // Globals.getRequest().setAttribute("unid", unid);
            // ContextMap contextMap = ContextMap.getCurrentContext();
            // contextMap.set("unid", unid);
            element.setAttribute("unid", unid);
            element.setAttribute("status", "SUCCESS");
         }catch(ResourceException e){
            element.setAttribute("status", "FAILURE");
            element.setNodeValue(e.getLocalizedMessage());
         }
      }
   }
}
