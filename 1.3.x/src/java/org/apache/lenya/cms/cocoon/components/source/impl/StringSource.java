package org.apache.lenya.cms.cocoon.components.source.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.cocoon.xml.dom.DOMStreamer;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.impl.AbstractSource;
import org.apache.excalibur.xml.sax.XMLizable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Creates a Source from a String, Document, Node, or XMLizable. If a String does not start with "<", it is wrapped with <text> tags. If a String does not start with "<?xml", an XML header is added.
 */
public class StringSource extends AbstractSource {
    final static private String ENCODING = "ISO-8859-1";
    private Object inputObject;
    private boolean exists = true;
    // private ComponentManager manager;
    public StringSource(ComponentManager mgr, Object object) {
        // manager = mgr;
        inputObject = object;
    }
    /**
     * Implement this method to obtain SAX events.
     */
    public void toSAX(ContentHandler handler) throws SAXException {
        /* 
         * domStreamer.setNormalizeNamespaces(false) is necessary because the default true will "normalize" namespaces.
         * The effect is to remove:
         *    xmlns:xhtml="http://www.w3.org/1999/xhtml"
         * It does not remove:
         *    xhtml:dummy="FIXME:keepNamespace"
         * causing the error:
         *   The prefix "xhtml" for attribute "xhtml:dummy" associated with an element type "html" is not bound.
         * which some people may find annoying.
         */
        if (inputObject instanceof Document) {
            DOMStreamer domStreamer = new DOMStreamer(handler);
            domStreamer.setNormalizeNamespaces(false);
            domStreamer.stream((Document) inputObject);
        } else if (inputObject instanceof Node) {
            DOMStreamer domStreamer = new DOMStreamer(handler);
            domStreamer.setNormalizeNamespaces(false);
            handler.startDocument();
            domStreamer.stream((Node) inputObject);
            handler.endDocument();
        } else if (inputObject instanceof XMLizable) {
            ((XMLizable) inputObject).toSAX(handler);
        } else {
            exists = false;
            throw new SAXException("The " + inputObject.getClass() + " could not be serialized to XML.");
        }
    }
    /**
     * Return an <code>InputStream</code> object to read from the source.
     * @throws IOException if I/O error occured.
     */
    public InputStream getInputStream() throws IOException, SourceException {
        ByteArrayInputStream inputStream = null;
        if (inputObject instanceof String) {
            String temp = (String) inputObject;
            if (!temp.startsWith("<"))
                temp = "<text>" + temp + "</text>";
            if (!temp.startsWith("<?xml"))
                temp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + temp;
            inputStream = new ByteArrayInputStream(temp.getBytes(ENCODING));
        } else {
            // Serialize the SAX events to the XMLSerializer:
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat("xml", "UTF-8", true);
                org.apache.xml.serialize.XMLSerializer serializer = new org.apache.xml.serialize.XMLSerializer(outputStream, format);
                toSAX(serializer);
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            } catch (SAXException se) {
                exists = false;
                throw new SourceException("Could not serialize to a ByteArray.", se);
            }
        }
        return inputStream;
    }
    /**
     * @return true if the resource was created properly.
     */
    public boolean exists() {
        return exists;
    }
}
