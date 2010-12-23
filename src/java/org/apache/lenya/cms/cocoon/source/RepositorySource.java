/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.lenya.cms.cocoon.source;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.util.Collection;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.ModifiableTraversableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.AbstractSource;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.ContentHolder;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.Query;
import org.apache.lenya.util.ServletHelper;

/**
 * Repository source.
 * 
 * @version $Id$
 */
public class RepositorySource extends AbstractSource implements ModifiableTraversableSource {

    private ServiceManager manager;
    private ContentHolder content;
    private Session session;
    private Logger logger;
    protected static final String SCHEME = "lenya";

    /**
     * @param manager The service manager.
     * @param uri The source URI.
     * @param session The repository session.
     * @param logger The logger.
     * @throws SourceException if an error occurs.
     * @throws MalformedURLException if an error occurs.
     */
    public RepositorySource(ServiceManager manager, String uri, Session session, Logger logger)
            throws SourceException, MalformedURLException {
        this.manager = manager;
        this.logger = logger;

        if (getLogger().isDebugEnabled())
            getLogger().debug("Init RepositorySource: " + uri);

        if (session == null) {
            throw new IllegalArgumentException("The repository session must not be null!");
        }
        this.session = session;

        if (uri == null) {
            throw new MalformedURLException("The source URI must not be null!");
        }

        setSystemId(uri);

        // Scheme
        int start = 0;
        int end = uri.indexOf(':');
        if (end == -1)
            throw new MalformedURLException("Malformed uri for lenya source (cannot find scheme) : "
                    + uri);

        String scheme = uri.substring(start, end);
        if (!SCHEME.equals(scheme))
            throw new MalformedURLException("Malformed uri for a lenya source : " + uri);

        setScheme(scheme);

        NodeFactory factory = null;
        try {
            factory = (NodeFactory) this.manager.lookup(NodeFactory.ROLE);
            
            String sourceUri;
            int revisionNumber = -1;
            
            int questionMarkIndex = uri.indexOf("?");
            if (questionMarkIndex > -1) {
                sourceUri = uri.substring(0, questionMarkIndex);
                Query query = new Query(uri.substring(questionMarkIndex + 1));
                String revisionString = query.getValue("rev", null);
                if (revisionString != null) {
                    
                    ContextUtility util = null;
                    try {
                        util = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
                        Request request = util.getRequest();
                        String webappUrl = ServletHelper.getWebappURI(request);
                        
                        Publication pub = PublicationUtil.getPublication(this.manager, request);
                        Document currentDoc = pub.getFactory().getFromURL(webappUrl);
                        if (currentDoc.getSourceURI().equals(sourceUri)) {
                            revisionNumber = Integer.valueOf(revisionString).intValue();
                        }
                    }
                    finally {
                        if (util != null) {
                            this.manager.release(util);
                        }
                    }
                }
            }
            else {
                sourceUri = uri;
            }
            
            if (revisionNumber == -1) {
                this.content = (ContentHolder) session.getRepositoryItem(factory, sourceUri);
            }
            else {
                Node node = (Node) session.getRepositoryItem(factory, sourceUri);
                this.content = node.getHistory().getRevision(revisionNumber);
            }
            
        } catch (Exception e) {
            throw new SourceException("Creating repository node failed: ", e);
        } finally {
            if (factory != null) {
                this.manager.release(factory);
            }
        }
    }

    /**
     * @return The repository node which is accessed by this source.
     */
    public Node getNode() {
        
        if (!(this.content instanceof Node)) {
            throw new RuntimeException("This operation can only be invoked on nodes, not on revisions.");
        }
        
        return (Node) this.content;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    /**
     * @see org.apache.excalibur.source.ModifiableSource#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        try {
            return getNode().getOutputStream();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.excalibur.source.ModifiableSource#delete()
     */
    public void delete() {
        try {
            getNode().delete();
        } catch (RepositoryException e) {
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
            if (getContent().exists()) {
                return true;
            } else {
                return isCollection();
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.excalibur.source.Source#getInputStream()
     */
    public InputStream getInputStream() throws IOException, SourceNotFoundException {
        if (getLogger().isDebugEnabled())
            getLogger().debug("Get InputStream for " + getURI());
        if (!exists()) {
            throw new SourceNotFoundException("The source [" + getURI() + "] does not exist!");
        }
        try {
            return getContent().getInputStream();
        } catch (RepositoryException e) {
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
                    throw new RuntimeException("Failed to tranform org.w3c.dom.Document to PipedOutputStream",
                            e);
                } finally {
                    try {
                        pos.close();
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    }
                }
            }
        },
                getClass().getName() + ".convert(org.w3c.dom.Document edoc)")).start();

        return pis;
    }

    void transform(org.w3c.dom.Document edoc, PipedOutputStream pos) throws TransformerException {

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        transformer.setOutputProperty("encoding", "UTF-8");
        transformer.setOutputProperty("indent", "yes");

        transformer.transform(new DOMSource(edoc), new StreamResult(pos));

    }
    
    /**
     * @return The content of this source.
     */
    public ContentHolder getContent() {
        return this.content;
    }

    /**
     * @see org.apache.excalibur.source.Source#getContentLength()
     */
    public long getContentLength() {
        try {
            return getContent().getContentLength();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.excalibur.source.Source#getLastModified()
     */
    public long getLastModified() {
        try {
            return getContent().getLastModified();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The source URI.
     */
    public String getSourceURI() {
        return getContent().getSourceURI();
    }

    /**
     * @see org.apache.excalibur.source.Source#getMimeType()
     */
    public String getMimeType() {
        try {
            return getContent().getMimeType();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     */
    public Source getParent() {
        getLogger().warn("getParent() not implemented yet!");
        return null;
    }

    /**
     * 
     */
    public void makeCollection() {
        getLogger().warn("RepositorySource().makeCollection() not implemented yet!");
    }

    /**
     * 
     */
    public String getName() {
        // Quick and dirty
        String name = new java.io.File(getURI()).getName();
        if (getLogger().isDebugEnabled())
            getLogger().debug("getName(): URI: " + name);
        return name;
    }

    /**
     * 
     */
    public Source getChild(String name) {
        getLogger().warn("getChild() not implemented yet!");
        return null;
    }

    /**
     * 
     */
    public Collection getChildren() {
        try {
            Collection children = getNode().getChildren();
            java.util.Iterator iterator = children.iterator();
            java.util.Vector newChildren = new java.util.Vector();
            while (iterator.hasNext()) {
                Node child = (Node) iterator.next();
                newChildren.add(new RepositorySource(this.manager,
                        child.getSourceURI(),
                        this.session,
                        getLogger()));
            }
            return newChildren;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     */
    public boolean isCollection() {
        try {
            return getNode().isCollection();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    
    private SourceValidity validity;

    public SourceValidity getValidity() {
        if (this.validity == null) {
            this.validity = new RepositorySourceValidity(this);
        }
        return this.validity;
    }
    
    
}
