package org.apache.lenya.cms.content;

import org.apache.cocoon.transformation.AbstractDOMTransformer;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.cms.content.Resource;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;
import org.w3c.dom.Document;

/**
 * This transformer modifies the structural information of a Resource.
 * 
 * It accepts a UNID as the src parameter.
 * It can:
 * - change the id and defaultlanguage of the Resource,
 * - delete Translations and Revisions, 
 * - change the live and edit Revisions of Translations.
 * 
 * <resource [id="newid"] [defaultlanguage="aa"]>
 *    <translation language="xx" [action="delete"] [live="1137958604000"] [edit="1137958604000"]>
 *       <revision revision="1137958604000" [action="delete"]/>
 *    </translation>
 * </resource>
 * 
 * action="delete" has top priority.
 * Any settings not included will not be affected.
 * 
 * @author <a href="mailto:solprovider@apache.org">Paul Ercolino</a>
 */
public class ResourceTransformer extends AbstractDOMTransformer{
    protected org.w3c.dom.Document transform(org.w3c.dom.Document doc){
System.out.println("ResourceTransformer - BEGIN");
       String unid = this.source;
       Request request = ObjectModelHelper.getRequest(super.objectModel);
       return transformDocument(request, unid, doc);
    }
    static public org.w3c.dom.Document transformDocument(Request request, String unid, org.w3c.dom.Document doc){
       PageEnvelope envelope = (PageEnvelope) request.getAttribute(PageEnvelope.class.getName());
       Publication pub = envelope.getPublication();
       Content content = pub.getContent();
       Resource resource = content.getResource(unid);
       resource.update(doc);
System.out.println("ResourceTransformer - RETURN");
       return doc;
    }

}
