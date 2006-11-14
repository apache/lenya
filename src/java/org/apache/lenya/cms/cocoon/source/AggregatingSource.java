package org.apache.lenya.cms.cocoon.source;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.AggregatedValidity;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public class AggregatingSource implements Source {

    private String uri;
    private String[] sourceUris;
    private ServiceManager manager;

    /**
     * @param uri
     * @param uris
     * @param manager
     */
    public AggregatingSource(String uri, String[] uris, ServiceManager manager) {
        this.manager = manager;
        this.sourceUris = uris;
        this.uri = uri;
    }

    protected void loadDom() {
        try {
            for (int i = 0; i < sourceUris.length; i++) {
                Document sourceDom = SourceUtil.readDOM(sourceUris[i], manager);

                if (sourceDom == null) {
                    throw new RuntimeException("The source [" + sourceUris[i] + "] doesn't contain XML.");
                }

                Element docElement = sourceDom.getDocumentElement();
                if (this.dom == null) {
                    String namespaceUri = docElement.getNamespaceURI();
                    String prefix = docElement.getPrefix();
                    String localName = docElement.getLocalName();

                    if (namespaceUri == null || prefix == null) {
                        this.dom = DocumentHelper.createDocument(null, localName, null);
                    }
                    else {
                        NamespaceHelper helper = new NamespaceHelper(namespaceUri, prefix, localName);
                        this.dom = helper.getDocument();
                    }
                }

                Element[] elements = DocumentHelper.getChildren(docElement);
                for (int e = 0; e < elements.length; e++) {
                    Element clone = (Element) this.dom.importNode(elements[e], true);
                    this.dom.getDocumentElement().appendChild(clone);
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Document dom;
    private byte[] data;

    protected Document getDom() {
        if (this.dom == null) {
            loadDom();
        }
        return this.dom;
    }

    protected byte[] getData() {
        if (this.data == null) {
            Document dom = getDom();
            if (dom != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    DocumentHelper.writeDocument(dom, out);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                this.data = out.toByteArray();
            }
        }
        return this.data;
    }

    public boolean exists() {
        return getData() != null;
    }

    public long getContentLength() {
        return getData().length;
    }

    public InputStream getInputStream() throws IOException, SourceNotFoundException {
        if (!exists()) {
            throw new RuntimeException(this + " does not exist!");
        }
        return new ByteArrayInputStream(this.data);
    }

    public long getLastModified() {
        long lastModified = 0;
        for (int i = 0; i < this.sourceUris.length; i++) {
            try {
                lastModified = Math
                        .max(lastModified, SourceUtil.getLastModified(sourceUris[i], this.manager));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return lastModified;
    }

    public String getMimeType() {
        return "application/xml";
    }

    public String getScheme() {
        return "aggregate-template";
    }

    public String getURI() {
        return this.uri;
    }
    
    private SourceValidity validity;

    public SourceValidity getValidity() {
        if (this.validity == null) {
            AggregatedValidity aggregatedValidity = new AggregatedValidity();
            for (int i = 0; i < this.sourceUris.length; i++) {
                SourceResolver resolver = null;
                Source source = null;
                try {
                    resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                    source = resolver.resolveURI(this.sourceUris[i]);
                    aggregatedValidity.add(source.getValidity());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                finally {
                    if (resolver != null) {
                        if (source != null) {
                            resolver.release(source);
                        }
                        this.manager.release(resolver);
                    }
                }
            }
            this.validity = aggregatedValidity;
        }
        return this.validity;
    }

    public void refresh() {
        this.dom = null;
        this.data = null;
    }

}
