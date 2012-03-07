package org.apache.lenya.versioning;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class RevisionTransformer extends AbstractSAXTransformer {

    private String webappUrl = null;
    private Integer revision = null;

    public RevisionTransformer() {
        this.defaultNamespaceURI = "http://apache.org/lenya/versioning";
    }

    @Override
    public void setup(SourceResolver resolver, @SuppressWarnings("rawtypes") Map objectModel,
            String src, Parameters params) throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, params);
        final Request request = ObjectModelHelper.getRequest(objectModel);
        this.webappUrl = ServletHelper.getWebappURI(request);
    }

    @Override
    public void startElement(String uri, String name, String raw, Attributes attr)
            throws SAXException {
        final String revAttr = attr.getValue(this.namespaceURI, "attr");
        if (revAttr == null) {
            super.startElement(uri, name, raw, attr);
        } else {
            final AttributesImpl attrs = new AttributesImpl(attr);
            final int pos = attrs.getIndex(this.namespaceURI, "attr");
            attrs.removeAttribute(pos);
            final String rev = Integer.toString(getRevision());
            attrs.setValue(attrs.getIndex(revAttr), rev);
            super.startElement(uri, name, raw, attrs);
        }
    }

    protected int getRevision() {
        if (this.revision == null) {
            try {
                final Session session = RepositoryUtil.getSession(this.manager, request);
                final DocumentFactory docFactory = DocumentUtil.createDocumentFactory(this.manager,
                        session);
                final Document document = docFactory.getFromURL(this.webappUrl);
                this.revision = document.getRepositoryNode().getHistory().getLatestRevision()
                        .getNumber();
            } catch (Exception e) {
                throw new RuntimeException("Error getting document from webapp URL ["
                        + this.webappUrl + "]: " + e.getMessage(), e);
            }
        }
        return this.revision;
    }

    @Override
    public void startTransformingElement(String uri, String name, String raw, Attributes attr)
            throws ProcessingException, IOException, SAXException {
        if ("revision".equals(name)) {
            final char[] rev = Integer.toString(getRevision()).toCharArray();
            characters(rev, 0, rev.length);
        } else {
            throw new ProcessingException("Unknown element <" + name + "> from namespace "
                    + this.namespaceURI);
        }
    }

}
