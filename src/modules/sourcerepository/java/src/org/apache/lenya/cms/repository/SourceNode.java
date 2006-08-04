/*
 * Copyright  1999-2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
package org.apache.lenya.cms.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.TraversableSource;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.ElementSet;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataRegistry;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.cms.rc.RCMLEntry;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A repository node.
 * 
 * @version $Id$
 */
public class SourceNode extends AbstractLogEnabled implements Node, Transactionable {

    private String sourceURI;
    protected ServiceManager manager;
    protected static final String FILE_PREFIX = "file:/";
    protected static final String CONTEXT_PREFIX = "context://";
    protected static final String LENYA_META_SUFFIX = "meta";

    /**
     * Ctor.
     * @param session
     * @param sourceURI
     * @param manager
     * @param logger
     */
    public SourceNode(Session session, String sourceURI, ServiceManager manager, Logger logger) {
        this.sourceURI = sourceURI;
        this.manager = manager;
        enableLogging(logger);
        this.session = session;
    }

    protected String getUserId() {
        String userId = null;
        Identity identity = getSession().getIdentity();
        if (identity != null) {
            User user = identity.getUser();
            if (user != null) {
                userId = user.getId();
            }
        }
        return userId;
    }

    /**
     * Returns the URI of the actual source which is used.
     * @return A string.
     */
    protected String getRealSourceURI() {
        String contentDir = null;
        String publicationId = null;
        try {
            String pubBase = Node.LENYA_PROTOCOL + Publication.PUBLICATION_PREFIX_URI + "/";
            String publicationsPath = this.sourceURI.substring(pubBase.length());
            publicationId = publicationsPath.split("/")[0];
            Publication pub = PublicationUtil.getPublication(this.manager, publicationId);
            contentDir = pub.getContentDir();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }

        String realSourceURI = null;
        String urlID = this.sourceURI.substring(Node.LENYA_PROTOCOL.length());

        if (contentDir == null) {
            // Default
            realSourceURI = CONTEXT_PREFIX + urlID;
        } else {
            // Substitute e.g. "lenya://lenya/pubs/PUB_ID/content" by "contentDir/content"
            String filePrefix = urlID.substring(0, urlID.indexOf(publicationId)) + publicationId;
            String tempString = urlID.substring(filePrefix.length() + 1);
            String fileMiddle = tempString.substring(0, tempString.indexOf("/"));
            String fileSuffix = tempString.substring(fileMiddle.length() + 1, tempString.length());
            if (new File(contentDir).isAbsolute()) {
                // Absolute
                realSourceURI = FILE_PREFIX + contentDir + File.separator + fileMiddle
                        + File.separator + fileSuffix;
            } else {
                // Relative
                realSourceURI = CONTEXT_PREFIX + contentDir + "/" + fileMiddle + "/" + fileSuffix;
            }
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Real Source URI: " + realSourceURI);
        }

        return realSourceURI;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkin()
     */
    public void checkin() throws RepositoryException {
        if (!isCheckedOut()) {
            throw new RepositoryException("Cannot check in node [" + this.sourceURI
                    + "]: not checked out!");
        }

        try {
            String userName = getUserId();
            boolean newVersion = getSession().isDirty(this);
            getRevisionController().reservedCheckIn(getRCPath(), userName, true, newVersion);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOut()
     */
    public boolean isCheckedOut() throws RepositoryException {
        try {
            return getRevisionController().isCheckedOut(getRCPath());
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isCheckedOutByUser()
     */
    public boolean isCheckedOutByUser() throws RepositoryException {
        try {
            RCMLEntry entry = getRevisionController().getRCML(getRCPath()).getLatestEntry();
            if (entry.getIdentity().equals(getUserId()) && isCheckedOut())
                return true;
            else
                return false;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#checkout()
     */
    public void checkout() throws RepositoryException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("SourceNode::checkout() called, sourceURI [" + sourceURI + "]");

        if (!isCheckedOut()) {
            try {
                getRevisionController().reservedCheckOut(getRCPath(), getUserId());
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
    }

    /**
     * @return The path to use for the revision controller.
     * @throws IOException if an error occurs.
     */
    protected String getRCPath() throws IOException {
        String pubBase = Node.LENYA_PROTOCOL + Publication.PUBLICATION_PREFIX_URI + "/";
        String publicationsPath = this.sourceURI.substring(pubBase.length());
        String publicationId = publicationsPath.split("/")[0];
        String path = pubBase + publicationId + "/";
        return this.sourceURI.substring(path.length());
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#deleteTransactionable()
     */
    public void deleteTransactionable() throws RepositoryException {
        try {
            if (!isCheckedOut()) {
                throw new RuntimeException("Cannot delete source [" + this.sourceURI
                        + "]: not checked out!");
            } else {
                SourceUtil.delete(getRealSourceURI(), this.manager);
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public void removed() {
        Node node;
        try {
            node = getDocumentNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Iterator i = this.listeners.iterator(); i.hasNext(); ) {
            NodeListener listener = (NodeListener) i.next();
            listener.nodeRemoved(node, getSession().getIdentity());
        }
    }

    private RevisionController revisionController;

    protected RevisionController getRevisionController() throws RepositoryException {
        if (this.revisionController == null) {
            try {
                String pubBase = Node.LENYA_PROTOCOL + Publication.PUBLICATION_PREFIX_URI + "/";
                String publicationsPath = this.sourceURI.substring(pubBase.length());
                String publicationId = publicationsPath.split("/")[0];

                Publication pub = PublicationUtil.getPublication(this.manager, publicationId);

                String publicationPath = pub.getDirectory().getCanonicalPath();
                RCEnvironment rcEnvironment = RCEnvironment.getInstance(pub.getServletContext()
                        .getCanonicalPath());
                String rcmlDirectory = publicationPath + File.separator
                        + rcEnvironment.getRCMLDirectory();
                String backupDirectory = publicationPath + File.separator
                        + rcEnvironment.getBackupDirectory();
                this.revisionController = new RevisionController(rcmlDirectory,
                        backupDirectory,
                        publicationPath);

            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
        return this.revisionController;
    }

    private Lock lock;

    /**
     * @see org.apache.lenya.transaction.Transactionable#hasChanged()
     */
    public boolean hasChanged() throws RepositoryException {
        try {
            int currentVersion = getRevisionController().getLatestVersion(getRCPath());
            int lockVersion = getLock().getVersion();
            return currentVersion > lockVersion;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#saveTransactionable()
     */
    public synchronized void saveTransactionable() throws RepositoryException {
        if (!isCheckedOut()) {
            throw new RepositoryException("Cannot save node [" + this.sourceURI
                    + "]: not checked out!");
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Saving [" + this + "] to source [" + getRealSourceURI() + "]");
        }

        if (this.data != null) {
            SourceResolver resolver = null;
            ModifiableSource source = null;
            InputStream in = null;
            OutputStream out = null;
            try {

                resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
                source = (ModifiableSource) resolver.resolveURI(getRealSourceURI());

                out = source.getOutputStream();

                byte[] buf = new byte[4096];
                in = new ByteArrayInputStream(this.data);
                int read = in.read(buf);

                while (read > 0) {
                    out.write(buf, 0, read);
                    read = in.read(buf);
                }
            } catch (Exception e) {
                throw new RepositoryException(e);
            } finally {

                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (Throwable t) {
                    throw new RuntimeException("Could not close streams: ", t);
                }

                if (resolver != null) {
                    if (source != null) {
                        resolver.release(source);
                    }
                    manager.release(resolver);
                }
            }
        }
    }

    public void changed() {
        Node node;
        try {
            node = getDocumentNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Iterator i = this.listeners.iterator(); i.hasNext(); ) {
            NodeListener listener = (NodeListener) i.next();
            listener.nodeChanged(node, getSession().getIdentity());
        }
    }

    /**
     * @return The document node, if this is a meta data node, or the node itself otherwise.
     * @throws ServiceException
     * @throws RepositoryException
     */
    protected Node getDocumentNode() throws ServiceException, RepositoryException {
        Node node;
        String sourceUri = getSourceURI();
        if (sourceUri.endsWith(".meta")) {
            String documentSourceUri = sourceUri.substring(0, sourceUri.length() - ".meta".length());
            NodeFactory factory = null;
            try {
                factory = (NodeFactory) this.manager.lookup(NodeFactory.ROLE);
                node = (Node) factory.buildItem(getSession(), documentSourceUri);
            }
            finally {
                if (factory != null) {
                    this.manager.release(factory);
                }
            }
        }
        else {
            node = this;
        }
        return node;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#createTransactionable()
     */
    public void createTransactionable() throws RepositoryException {
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#lock()
     */
    public void lock() throws RepositoryException {
        if (isCheckedOut() && !isCheckedOutByUser()) {
            throw new RepositoryException("Cannot lock [" + this + "]: node is checked out.");
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Locking [" + this + "]");
        }
        int currentVersion;
        try {
            currentVersion = getRevisionController().getLatestVersion(getRCPath());
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
        try {
            this.lock = getSession().createLock(this, currentVersion);
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }

        if (!getSourceURI().endsWith("." + LENYA_META_SUFFIX)) {
            lockMetaData();
        }
    }

    protected void lockMetaData() throws RepositoryException {
        SourceUtil.lock(getMetaSourceURI(), this.manager);
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#getLock()
     */
    public Lock getLock() {
        return this.lock;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#unlock()
     */
    public void unlock() throws RepositoryException {
        this.lock = null;
    }

    /**
     * @see org.apache.lenya.transaction.Transactionable#isLocked()
     */
    public boolean isLocked() throws RepositoryException {
        return this.lock != null;
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#exists()
     */
    public boolean exists() throws RepositoryException {
        loadData();
        return this.data != null;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "node " + this.sourceURI;
    }

    byte[] data = null;

    /**
     * @see org.apache.lenya.cms.repository.Node#getInputStream()
     */
    public synchronized InputStream getInputStream() throws RepositoryException {
        if (!exists()) {
            throw new RuntimeException(this + " does not exist!");
        }
        return new ByteArrayInputStream(this.data);
    }

    /**
     * 
     */
    public Collection getChildren() throws RepositoryException {
        SourceResolver resolver = null;
        TraversableSource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (TraversableSource) resolver.resolveURI(getRealSourceURI());
            Collection children = source.getChildren();
            java.util.Iterator iterator = children.iterator();
            java.util.Vector newChildren = new java.util.Vector();
            while (iterator.hasNext()) {
                TraversableSource child = (TraversableSource) iterator.next();
                newChildren.add(new SourceNode(getSession(),
                        sourceURI + "/" + child.getName(),
                        this.manager,
                        getLogger()));
            }
            return newChildren;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * 
     */
    public boolean isCollection() throws RepositoryException {
        SourceResolver resolver = null;
        TraversableSource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (TraversableSource) resolver.resolveURI(getRealSourceURI());
            return source.isCollection();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Loads the data from the real source.
     * @throws RepositoryException if an error occurs.
     */
    protected synchronized void loadData() throws RepositoryException {

        if (this.data != null) {
            return;
        }

        ByteArrayOutputStream out = null;
        InputStream in = null;
        SourceResolver resolver = null;
        TraversableSource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (TraversableSource) resolver.resolveURI(getRealSourceURI());

            if (source.exists() && !source.isCollection()) {
                byte[] buf = new byte[4096];
                out = new ByteArrayOutputStream();
                in = source.getInputStream();
                int read = in.read(buf);

                while (read > 0) {
                    out.write(buf, 0, read);
                    read = in.read(buf);
                }

                this.data = out.toByteArray();
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
                throw new RepositoryException(e);
            }

            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getOutputStream()
     */
    public synchronized OutputStream getOutputStream() throws RepositoryException {
        if (getLogger().isDebugEnabled())
            getLogger().debug("Get OutputStream for " + getSourceURI());
        try {
            registerDirty();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new NodeOutputStream();
    }

    /**
     * Output stream.
     */
    private class NodeOutputStream extends ByteArrayOutputStream {
        /**
         * @see java.io.OutputStream#close()
         */
        public synchronized void close() throws IOException {
            SourceNode.this.data = super.toByteArray();
            SourceNode.this.lastModified = new Date().getTime();
            super.close();
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getContentLength()
     */
    public long getContentLength() throws RepositoryException {
        loadData();
        return this.data.length;
    }

    private long lastModified = -1;

    /**
     * @see org.apache.lenya.cms.repository.Node#getLastModified()
     */
    public long getLastModified() throws RepositoryException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getRealSourceURI());

            long sourceLastModified;

            if (source.exists()) {
                sourceLastModified = source.getLastModified();
                if (sourceLastModified > this.lastModified) {
                    this.lastModified = sourceLastModified;
                }
            } else if (this.lastModified == -1) {
                throw new RepositoryException("The source [" + getRealSourceURI()
                        + "] does not exist!");
            }

            return this.lastModified;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getMimeType()
     */
    public String getMimeType() throws RepositoryException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getRealSourceURI());
            if (source.exists()) {
                return source.getMimeType();
            } else {
                throw new SourceNotFoundException("The source [" + getRealSourceURI()
                        + "] does not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Node#getSourceURI()
     */
    public String getSourceURI() {
        return sourceURI;
    }

    /**
     * @return The source URI of the meta data node. TODO: This is a hack and can be removed when
     *         UUIDs are used.
     */
    protected String getMetaSourceURI() {
        String sourceUri = getSourceURI();
        return sourceUri + "." + LENYA_META_SUFFIX;
    }

    private Session session;

    /**
     * @see org.apache.lenya.cms.repository.Node#getSession()
     */
    public Session getSession() {
        return this.session;
    }

    public void registerDirty() throws RepositoryException {
        try {
            getSession().registerDirty(this);
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }
    }

    public void registerRemoved() throws RepositoryException {
        try {
            getSession().registerRemoved(this);
            SourceUtil.delete(getMetaSourceURI(), this.manager);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    private Map namespace2metadata = new HashMap();

    public MetaData getMetaData(String namespaceUri) throws MetaDataException {

        MetaDataRegistry registry = null;
        try {
            registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
            if (!registry.isRegistered(namespaceUri)) {
                throw new MetaDataException("The namespace [" + namespaceUri
                        + "] is not registered!");
            }
        } catch (ServiceException e) {
            throw new MetaDataException(e);
        } finally {
            if (registry != null) {
                this.manager.release(registry);
            }
        }

        MetaData meta = (MetaData) this.namespace2metadata.get(namespaceUri);
        if (meta == null) {
            meta = new SourceNodeMetaData(namespaceUri, this, this.manager);
            this.namespace2metadata.put(namespaceUri, meta);
        }
        return meta;
    }

    private Map namespace2metamap = null;

    protected Map getMetaDataMap(String namespaceUri) throws MetaDataException {
        if (this.namespace2metamap == null) {
            loadMetaData();
        }
        Map map = (Map) this.namespace2metamap.get(namespaceUri);
        if (map == null) {
            map = new HashMap();
            this.namespace2metamap.put(namespaceUri, map);
        }
        return map;
    }

    protected static final String META_DATA_NAMESPACE = "http://apache.org/lenya/metadata/1.0";
    protected static final String ELEMENT_METADATA = "metadata";
    protected static final String ELEMENT_SET = "element-set";
    protected static final String ELEMENT_ELEMENT = "element";
    protected static final String ELEMENT_VALUE = "value";
    protected static final String ATTRIBUTE_NAMESPACE = "namespace";
    protected static final String ATTRIBUTE_KEY = "key";

    protected void loadMetaData() throws MetaDataException {

        if (this.namespace2metamap != null) {
            throw new IllegalStateException("The meta data have already been loaded!");
        }

        try {
            this.namespace2metamap = new HashMap();
            if (SourceUtil.exists(getMetaSourceURI(), this.manager)) {
                Document xml = SourceUtil.readDOM(getMetaSourceURI(), this.manager);
                if (!xml.getDocumentElement().getNamespaceURI().equals(META_DATA_NAMESPACE)) {
                    loadLegacyMetaData(xml);
                } else {
                    NamespaceHelper helper = new NamespaceHelper(META_DATA_NAMESPACE, "", xml);
                    Element[] setElements = helper.getChildren(xml.getDocumentElement(),
                            ELEMENT_SET);
                    for (int setIndex = 0; setIndex < setElements.length; setIndex++) {
                        String namespace = setElements[setIndex].getAttribute(ATTRIBUTE_NAMESPACE);
                        Element[] elementElements = helper.getChildren(setElements[setIndex],
                                ELEMENT_ELEMENT);
                        for (int elemIndex = 0; elemIndex < elementElements.length; elemIndex++) {
                            String key = elementElements[elemIndex].getAttribute(ATTRIBUTE_KEY);
                            Element[] valueElements = helper.getChildren(elementElements[elemIndex],
                                    ELEMENT_VALUE);
                            for (int valueIndex = 0; valueIndex < valueElements.length; valueIndex++) {
                                String value = DocumentHelper.getSimpleElementText(valueElements[valueIndex]);
                                List values = getValueList(namespace, key);
                                values.add(value);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new MetaDataException(e);
        }
    }

    protected void loadLegacyMetaData(Document xml) throws MetaDataException {
        NamespaceHelper helper = new NamespaceHelper(PageEnvelope.NAMESPACE, "", xml);

        Element metaElement = helper.getFirstChild(xml.getDocumentElement(), "meta");

        Element internalElement = helper.getFirstChild(metaElement, "internal");

        Element[] internalElements = helper.getChildren(internalElement);
        for (int i = 0; i < internalElements.length; i++) {
            String value = DocumentHelper.getSimpleElementText(internalElements[i]);
            String key = internalElements[i].getLocalName();

            if (key.equals("workflowVersion")) {
                List values = getValueList("http://apache.org/lenya/metadata/workflow/1.0", key);
                values.add(value);
            } else {
                List values = getValueList("http://apache.org/lenya/metadata/document/1.0", key);
                values.add(value);
            }
        }

        NamespaceHelper dcHelper = new NamespaceHelper(DublinCore.DC_NAMESPACE, "", xml);
        Element dcElement = helper.getFirstChild(metaElement, "dc");

        MetaDataRegistry registry = null;
        try {
            registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
            ElementSet dcElementSet = registry.getElementSet(DublinCore.DC_NAMESPACE);
            ElementSet dcTermSet = registry.getElementSet(DublinCore.DCTERMS_NAMESPACE);

            Element[] dcElements = dcHelper.getChildren(dcElement);
            for (int i = 0; i < dcElements.length; i++) {
                String value = DocumentHelper.getSimpleElementText(dcElements[i]);

                String key = dcElements[i].getLocalName();

                if (dcElementSet.containsElement(key)) {
                    List values = getValueList(DublinCore.DC_NAMESPACE, key);
                    values.add(value);
                } else if (dcTermSet.containsElement(key)) {
                    List values = getValueList(DublinCore.DCTERMS_NAMESPACE, key);
                    values.add(value);
                } else {
                    throw new RepositoryException("The dublin core key [" + key
                            + "] is not supported.");
                }
            }
        } catch (MetaDataException e) {
            throw e;
        } catch (Exception e) {
            throw new MetaDataException(e);
        } finally {
            if (registry != null) {
                this.manager.release(registry);
            }
        }

    }

    protected void saveMetaData() throws MetaDataException {
        try {
            NamespaceHelper helper = new NamespaceHelper(META_DATA_NAMESPACE, "", ELEMENT_METADATA);
            Collection namespaces = this.namespace2metamap.keySet();
            for (Iterator i = namespaces.iterator(); i.hasNext();) {
                String namespace = (String) i.next();

                Element setElement = helper.createElement(ELEMENT_SET);
                setElement.setAttribute(ATTRIBUTE_NAMESPACE, namespace);
                helper.getDocument().getDocumentElement().appendChild(setElement);

                Map map = getMetaDataMap(namespace);
                Collection keys = map.keySet();
                for (Iterator keyIterator = keys.iterator(); keyIterator.hasNext();) {
                    String key = (String) keyIterator.next();

                    Element elementElement = helper.createElement(ELEMENT_ELEMENT);
                    elementElement.setAttribute(ATTRIBUTE_KEY, key);

                    List values = (List) map.get(key);
                    for (Iterator valueIterator = values.iterator(); valueIterator.hasNext();) {
                        String value = (String) valueIterator.next();
                        if (!value.equals("")) {
                            Element valueElement = helper.createElement(ELEMENT_VALUE, value);
                            elementElement.appendChild(valueElement);
                        }
                    }
                    if (elementElement.hasChildNodes()) {
                        setElement.appendChild(elementElement);
                    }
                }
            }
            SourceUtil.writeDOM(helper.getDocument(), getMetaSourceURI(), this.manager);
        } catch (Exception e) {
            throw new MetaDataException(e);
        }
    }

    protected String[] getValues(String namespaceUri, String key) throws MetaDataException {
        List values = getValueList(namespaceUri, key);
        return (String[]) values.toArray(new String[values.size()]);
    }

    protected List getValueList(String namespaceUri, String key) throws MetaDataException {
        Map map = getMetaDataMap(namespaceUri);
        List values = (List) map.get(key);
        if (values == null) {
            values = new ArrayList();
            map.put(key, values);
        }
        return values;
    }

    protected void addValue(String namespaceUri, String key, String value) throws MetaDataException {
        List values = getValueList(namespaceUri, key);
        values.add(value);
        saveMetaData();
    }

    protected void removeAllValues(String namespaceUri, String key) throws MetaDataException {
        List values = getValueList(namespaceUri, key);
        values.clear();
        saveMetaData();
    }

    public String[] getMetaDataNamespaceUris() throws MetaDataException {
        if (this.namespace2metamap == null) {
            loadMetaData();
        }
        Set uris = this.namespace2metamap.keySet();
        return (String[]) uris.toArray(new String[uris.size()]);
    }

    private Set listeners = new HashSet();

    public void addListener(NodeListener listener) throws RepositoryException {
        if (this.listeners.contains(listener)) {
            throw new RepositoryException("The listener [" + listener
                    + "] is already registered for node [" + this + "]!");
        }
        this.listeners.add(listener);
    }

    public boolean isListenerRegistered(NodeListener listener) {
        return this.listeners.contains(listener);
    }

}
