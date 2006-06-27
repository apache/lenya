package org.apache.lenya.cms.cocoon.components.source.impl;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.impl.AbstractSource;
import org.apache.excalibur.xml.sax.XMLizable;
import org.apache.cocoon.serialization.XMLSerializer;
import org.apache.cocoon.xml.dom.DOMStreamer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.cocoon.serialization.Serializer;


/**
 * Creates a Source from a String, Document, Node, or XMLizable.
 * 
 * If a String does not start with "<", it is wrapped with <text> tags.
 * If a String does not start with "<?xml", an XML header is added.
 */

public class StringSource extends AbstractSource {
    final static private String ENCODING = "ISO-8859-1";
    private Object inputObject;
    private boolean exists = true;
    private ComponentManager manager;

    public StringSource(ComponentManager mgr, Object object){
       manager = mgr;
       inputObject = object;
    }
    
    /**
     * Implement this method to obtain SAX events.
     */
    public void toSAX(ContentHandler handler) throws SAXException {
        if (inputObject instanceof Document ) {
            DOMStreamer domStreamer = new DOMStreamer( handler );
            domStreamer.stream( (Document)inputObject );
        } else if(inputObject instanceof Node ) {
            DOMStreamer domStreamer = new DOMStreamer( handler );
            handler.startDocument();
            domStreamer.stream( (Node)inputObject );
            handler.endDocument();
        } else if(inputObject instanceof XMLizable ) {
            ((XMLizable)inputObject).toSAX( handler );
        } else {
            exists = false;
            throw new SAXException("The " + inputObject.getClass() +" could not be serialized to XML.");
        }
    }

    /**
     * Return an <code>InputStream</code> object to read from the source.
     *
     * @throws IOException if I/O error occured.
     */
    public InputStream getInputStream() throws IOException, SourceException {
        ByteArrayInputStream inputStream = null;
        if (inputObject instanceof String){
           String temp = (String) inputObject;
           if(!temp.startsWith("<")) temp="<text>" + temp + "</text>";
           if(!temp.startsWith("<?xml")) temp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + temp;
           inputStream = new ByteArrayInputStream(temp.getBytes(ENCODING));
        }else{
           // Serialize the SAX events to the XMLSerializer:
           ComponentSelector serializerSelector = null;
           Serializer serializer = null;
           try{
              serializerSelector = (ComponentSelector) manager.lookup(Serializer.ROLE + "Selector");
              serializer = (Serializer)serializerSelector.select("xml");
              ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
              serializer.setOutputStream((OutputStream) outputStream );
              toSAX( serializer );
              inputStream = new ByteArrayInputStream( outputStream.toByteArray() );
           }catch(org.apache.avalon.framework.component.ComponentException ce){
              exists = false;
              throw new SourceException("StringSource: ComponentException", ce );
           }catch(SAXException se){
              exists = false;
              throw new SourceException("Could not serialize to a ByteArray.", se );
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
