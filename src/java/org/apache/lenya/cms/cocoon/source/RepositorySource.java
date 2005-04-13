/*
 * Created on 03.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.cocoon.source;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.impl.AbstractSource;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.SourceNodeFactory;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;

/**
 * Repository source.
 * 
 * @version $Id:$
 */
public class RepositorySource extends AbstractSource implements ModifiableSource,
        TransactionableSource {

    private ServiceManager manager;
    private Node node;
    private IdentityMap identityMap;
    private Logger logger;
    protected static final String SCHEME = "lenya";

    /**
     * @param manager The service manager.
     * @param uri The source URI.
     * @param map The identity map.
     * @param logger The logger.
     * @throws SourceException if an error occurs.
     * @throws MalformedURLException if an error occurs.
     */
    public RepositorySource(ServiceManager manager, String uri, IdentityMap map, Logger logger)
            throws SourceException, MalformedURLException {
        this.manager = manager;
        this.logger = logger;
        this.identityMap = map;

        if (uri == null) {
            throw new MalformedURLException("The source URI must not be null!");
        }

        setSystemId(uri);

        // Scheme
        int start = 0;
        int end = uri.indexOf(':');
        if (end == -1)
            throw new MalformedURLException(
                    "Malformed uri for xmodule source (cannot find scheme) : " + uri);

        String scheme = uri.substring(start, end);
        if (!SCHEME.equals(scheme))
            throw new MalformedURLException("Malformed uri for a xmodule source : " + uri);

        setScheme(scheme);

        if (map.getFactory(Node.IDENTIFIABLE_TYPE) == null) {
            map.setFactory(Node.IDENTIFIABLE_TYPE, new SourceNodeFactory(this.manager, logger));
        }

        this.node = (Node) map.get(Node.IDENTIFIABLE_TYPE, uri);
    }
    
    /**
     * @return The repository node which is accessed by this source.
     */
    public Node getNode() {
        return this.node;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    /**
     * @see org.apache.excalibur.source.ModifiableSource#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        try {
            if (!this.node.isLocked()) {
                throw new RuntimeException("Cannot write to source [" + getURI() + "]: not locked!");
            }
            this.identityMap.getUnitOfWork().registerDirty(this.node);
            return new DOMOutputStream();
        } catch (TransactionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.excalibur.source.ModifiableSource#delete()
     */
    public void delete() {
        try {
            if (!this.node.isLocked()) {
                throw new RuntimeException("Cannot delete source [" + getURI() + "]: not locked!");
            } else {
                this.identityMap.getUnitOfWork().registerRemoved(this.node);
            }
        } catch (TransactionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.excalibur.source.ModifiableSource#canCancel(java.io.OutputStream)
     */
    public boolean canCancel(OutputStream arg0) {
        return false;
    }

    /**
     * @see org.apache.excalibur.source.ModifiableSource#cancel(java.io.OutputStream)
     */
    public void cancel(OutputStream arg0) throws IOException {
    }

    /**
     * @see org.apache.excalibur.source.Source#exists()
     */
    public boolean exists() {
        try {
            return this.node.exists();
        } catch (TransactionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.excalibur.source.Source#getInputStream()
     */
    public InputStream getInputStream() throws IOException, SourceNotFoundException {
        if (!exists()) {
            throw new SourceNotFoundException("The source [" + getURI() + "] does not exist!");
        }
        try {
            Document doc = this.node.getDocument();
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.setOutputProperty("indent", "yes");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(doc), new StreamResult(bos));
            return new ByteArrayInputStream(bos.toByteArray());
            
            /*
            return convert(doc);
            */
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected InputStream convert(org.w3c.dom.Document edoc) throws IOException {

        final org.w3c.dom.Document doc = edoc;
        final PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream();
        pis.connect(pos);

        (new Thread(new Runnable() {

            public void run() {
                try {
                    transform(doc, pos);
                } catch (TransformerException e) {
                    throw new RuntimeException(
                            "Failed to tranform org.w3c.dom.Document to PipedOutputStream", e);
                } finally {
                    try {
                        pos.close();
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    }
                }
            }
        }, getClass().getName() + ".convert(org.w3c.dom.Document edoc)")).start();

        return pis;
    }

    private void transform(org.w3c.dom.Document edoc, PipedOutputStream pos)
            throws TransformerException {

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        transformer.setOutputProperty("encoding", "UTF-8");
        transformer.setOutputProperty("indent", "yes");

        transformer.transform(new DOMSource(edoc), new StreamResult(pos));

    }

    /**
     * DOM output stream.
     */
    private class DOMOutputStream extends ByteArrayOutputStream {
        /**
         * @see java.io.OutputStream#close()
         */
        public void close() throws IOException {
            byte[] content = super.toByteArray();
            InputStream in = new ByteArrayInputStream(content);
            Document document;
            try {
                document = DocumentHelper.readDocument(in);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            RepositorySource.this.node.setDocument(document);
            super.close();
        }
    }

    /**
     * @see org.apache.lenya.cms.cocoon.source.TransactionableSource#checkout()
     */
    public void checkout() throws TransactionException {
    }

    /**
     * @see org.apache.lenya.cms.cocoon.source.TransactionableSource#checkin()
     */
    public void checkin() throws TransactionException {
    }

    /**
     * @see org.apache.lenya.cms.cocoon.source.TransactionableSource#lock()
     */
    public void lock() throws TransactionException {
        if (this.identityMap.getUnitOfWork() != null) {
            try {
                this.node.lock();
            } catch (TransactionException e) {
                throw new RuntimeException("Locking source [" + getURI() + "] failed.");
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.cocoon.source.TransactionableSource#unlock()
     */
    public void unlock() throws TransactionException {
        if (this.identityMap.getUnitOfWork() != null) {
            try {
                this.node.unlock();
            } catch (TransactionException e) {
                throw new RuntimeException("Locking source [" + getURI() + "] failed.");
            }
        }
    }

    protected boolean isLocked() throws TransactionException {
        if (this.identityMap.getUnitOfWork() != null) {
            try {
                return this.node.isLocked();
            } catch (TransactionException e) {
                throw new RuntimeException("Locking source [" + getURI() + "] failed.");
            }
        }
        return false;
    }

    public Document getDocument() {
        return this.node.getDocument();
    }

    public void setDocument(Document document) {
        this.node.setDocument(document);
        try {
            registerDirty();
        } catch (TransactionException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerDirty() throws TransactionException {
        this.identityMap.getUnitOfWork().registerDirty(this.node);
    }

}