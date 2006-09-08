package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;;

public class MetaDataTransformer extends AbstractSAXTransformer implements
        Disposable {
    /**
     * The namespace for the meta data is http://apache.org/lenya/meta/1.0
     */
    static public final String NAMESPACE_URI = "http://apache.org/lenya/meta/1.0";

    /**
     * The namespace prefix for this namespace.
     */
    static public final String PREFIX = "meta";

    /**
     * The value element is getting the value for a specific ns and key. It is
     * the only method implemented so far.
     */
    static public final String VALUE_ELEMENT = "value";

    /**
     * ELEMENT_ATT - which meta data key do we want to look up
     */
    static public final String ELEMENT_ATT = "element";

    /**
     * NS_ATT - in which namespace should we look
     */
    static public final String NS_ATT = "ns";

    /**
     * UUID_ATT - for which uuid?
     */
    static public final String UUID_ATT = "uuid";

    /**
     * LANG_ATT - in which language this is optional (when not found use
     * publication default)
     */
    static public final String LANG_ATT = "lang";

    /** Helper for lenya document retrival */
    protected String publicationId = null;

    protected String area = null;

    protected String language = null;

    protected String uuid = null;

    protected Publication pub;

    private DocumentFactory factory;

    /**
     * Setup the MetaDataTransformer.
     */
    public void setup(SourceResolver resolver, Map objectModel, String src,
            Parameters par) throws ProcessingException, SAXException,
            IOException {
        super.setup(resolver, objectModel, src, par);
        this.publicationId = par.getParameter("pubid", null);
        if (this.publicationId == null) {
            throw new ProcessingException(
                    "The pubid is not set! Please set like e.g. <map:parameter name='pubid' value='{request-param:pubid}'/>");
        }

        this.area = par.getParameter("area", null);
        if (this.area == null) {
            throw new ProcessingException(
                    "The area is not set! Please set like e.g. <map:parameter name='area' value='{request-param:area}'/>");
        }
        Request request = ObjectModelHelper.getRequest(objectModel);
        factory = DocumentUtil.getDocumentFactory(this.manager, request);
        try {
            pub = factory.getPublication(this.publicationId);
        } catch (PublicationException e) {
            throw new ProcessingException(
                    "Error geting publication id / area from page envelope", e);
        }
    }

    public void startElement(String uri, String name, String raw,
            Attributes attr) throws SAXException {
        if (NAMESPACE_URI.equals(uri)) {
            if (VALUE_ELEMENT.equals(name)) {
                String lang = null, uuid = null, ns = null, key = null;
                for (int i = 0; i < attr.getLength(); i++) {
                    String localName = attr.getLocalName(i);
                    String value = attr.getValue(i);
                    if (ELEMENT_ATT.equals(localName))
                        key = value;
                    else if (NS_ATT.equals(localName))
                        ns = key;
                    else if (UUID_ATT.equals(localName))
                        uuid = value;
                    else if (LANG_ATT.equals(localName))
                        lang = value;
                }//end for
                if(uuid==null||ns==null||key==null)
                    throw new SAXException("Error by setting up the transformation. Please fix the calling code.");
                if (lang==null)
                    lang=pub.getDefaultLanguage();
                try {
                    Document document = pub.getArea(area).getDocument(uuid, lang);
                    MetaData metaData = document.getMetaData(ns);
                    String [] returnValue=metaData.getValues(key);
                } catch (PublicationException e) {
                    throw new SAXException("Error by getting document for [ "+lang+"/"+uuid+" ]");
                } catch (MetaDataException e) {
                    throw new SAXException("Error by getting meta data with ns [ "+ns+" ] for document for [ "+lang+"/"+uuid+" ]");
                }
                
            } else {
                String warn = "Could not find method for " + name
                        + ". Ignoring.";
                getLogger().warn(warn);
            }
        } else {
            super.startElement(uri, name, raw, attr);
        }
    }

    public void endElement(String uri, String name, String raw)
            throws SAXException {
        if (!NAMESPACE_URI.equals(uri)) {
            super.endElement(uri, name, raw);
        }
    }
}
