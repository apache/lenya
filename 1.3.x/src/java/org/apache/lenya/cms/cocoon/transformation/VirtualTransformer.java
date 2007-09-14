package org.apache.lenya.cms.cocoon.transformation;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.cocoon.components.CocoonComponentManager;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.transformation.AbstractDOMTransformer;
import org.apache.excalibur.source.Source;
import org.apache.lenya.cms.cocoon.components.source.impl.StringSource;
import org.apache.lenya.cms.cocoon.components.source.impl.VirtualSourceFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This transformer creates a new VirtualDocument with the specified key.
 * 
 * @author <a href="mailto:solprovider@apache.org">Paul Ercolino</a>
 */
public class VirtualTransformer extends AbstractDOMTransformer{
   private static final String SERIALIZER_NAME = "xml";
   public static final String CONTENT_PREFIX = "content";

   protected org.w3c.dom.Document transform(org.w3c.dom.Document doc){
      Request request = ObjectModelHelper.getRequest(super.objectModel);
      createVirtual(request, this.source, doc);
      return doc;
   }
   static public org.w3c.dom.Document transformDocument(Request request, String key, org.w3c.dom.Document doc){
      createVirtual(request, key, doc);
      return doc;
   }
   /**
    *
    * @param request The request
    * @param doc The data to be inserted.
    */
   static private void createVirtual(Request request, String key, org.w3c.dom.Document doc){
      if (doc == null){
System.out.println("Virtual: Document is required.");
//         throw new ProcessingException("CreateRevision: document is required.");
      }
      ComponentManager manager = CocoonComponentManager.getSitemapComponentManager();
//      Element root = doc.getDocumentElement();
//      Source source = (Source) new StringSource(manager, root);
      Source source = (Source) new StringSource(manager, doc);
      VirtualSourceFactory.addSource(key, source);
   }
}
