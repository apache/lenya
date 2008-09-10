package org.apache.lenya.cms.content;
import java.io.IOException;
import java.util.Map;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractDOMTransformer;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.Publication;
import org.xml.sax.SAXException;
/**
 * This transformer modifies the structural information of a Resource. It accepts a UNID as the src parameter. It can: - change the id and defaultlanguage of the Resource, - delete Translations and Revisions, - change the live and edit Revisions of Translations. <resource [id="newid"] [defaultlanguage="aa"]> <translation language="xx" [action="delete"] [live="1137958604000"] [edit="1137958604000"]> <revision revision="1137958604000" [action="delete"]/> </translation> </resource> action="delete" has top priority. Any settings not included will not be affected.
 * 
 * @author solprovider
 * @since 1.3
 */
public class ResourceTransformer extends AbstractDOMTransformer {
   public static final String TYPE_CONTENT = "content";
   public static final String TYPE_DESIGN = "design";
   public static final String TYPE_STRUCTURE = "structure";
   private String type = TYPE_CONTENT;
   public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par) throws ProcessingException, SAXException, IOException {
      super.setup(resolver, objectModel, src, par);
      type = parameters.getParameter("type", type);
   }
   protected org.w3c.dom.Document transform(org.w3c.dom.Document doc) {
      System.out.println("ResourceTransformer - BEGIN");
      String unid = this.source;
      Request request = ObjectModelHelper.getRequest(super.objectModel);
      return transformDocument(request, unid, doc, type);
   }
   static public org.w3c.dom.Document transformDocument(Request request, String unid, org.w3c.dom.Document doc, String type) {
      PageEnvelope envelope;
      try{
         envelope = PageEnvelope.getCurrent();
      }catch(PageEnvelopeException e){
         return null;
      }
      Publication publication = envelope.getPublication();
      Resource resource;
      if(TYPE_DESIGN.equalsIgnoreCase(type)){
         resource = publication.getDesign().getDesign(unid);
      }else if(TYPE_STRUCTURE.equalsIgnoreCase(type)){
         resource = publication.getDesign().getStructure(unid);
      }else{ // TYPE_CONTENT
         resource = publication.getContent().getResource(unid);
      }
      resource.update(doc);
      return doc;
   }
}
