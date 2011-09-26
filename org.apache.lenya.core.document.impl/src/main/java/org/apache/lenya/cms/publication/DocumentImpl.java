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

package org.apache.lenya.cms.publication;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.cocoon.ResourceNotFoundException;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataCache;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataWrapper;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.repository.ContentHolder;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.Persistable;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.repository.SessionHolder;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.History;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * A typical CMS document.
 */
public class DocumentImpl implements Document, RepositoryItem {

    private static final Log logger = LogFactory.getLog(DocumentImpl.class);

    private DocumentIdentifier identifier;
    private org.apache.lenya.cms.publication.Session session;
    private NodeFactory nodeFactory;
    private ResourceTypeResolver resourceTypeResolver;
    private int revision = -1;

    //florent : extract propertie from document impl to document (api)
    /**
     * The meta data namespace.
     */
   // public static final String METADATA_NAMESPACE = "http://apache.org/lenya/metadata/document/1.0";

    /**
     * The name of the resource type attribute. A resource has a resource type; this information can
     * be used e.g. for different rendering of different types.
     */
    //protected static final String METADATA_RESOURCE_TYPE = "resourceType";

    /**
     * The name of the mime type attribute.
     */
    //protected static final String METADATA_MIME_TYPE = "mimeType";

    /**
     * The name of the content type attribute. Any content managed by Lenya has a type; this
     * information can be used e.g. to provide an appropriate management interface.
     */
    //protected static final String METADATA_CONTENT_TYPE = "contentType";

    /**
     * The number of seconds from the request that a document can be cached before it expires
     */
    //protected static final String METADATA_EXPIRES = "expires";

    /**
     * The extension to use for the document source.
     */
    //protected static final String METADATA_EXTENSION = "extension";

    /**
     * Creates a new instance of DefaultDocument.
     * @param session The session the document belongs to.
     * @param identifier The identifier.
     * @param revision The revision number or -1 if the latest revision should be used.
     */
    protected DocumentImpl(org.apache.lenya.cms.publication.Session session,
            DocumentIdentifier identifier, int revision) {
    	
        if (logger.isDebugEnabled()) {
            logger.debug("DefaultDocument() creating new instance with id [" + identifier.getUUID()
                    + "], language [" + identifier.getLanguage() + "]");
        }

        if (identifier.getUUID() == null) {
            throw new IllegalArgumentException("The UUID must not be null!");
        }

        this.identifier = identifier;
        this.session = session;
        this.revision = revision;

        if (logger.isDebugEnabled()) {
            logger.debug("DefaultDocument() done building instance with _id ["
                    + identifier.getUUID() + "], _language [" + identifier.getLanguage() + "]");
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getExpires()
     */
    public Date getExpires() throws DocumentException {
        Date expires = null;
        long secs = 0;

        MetaData metaData = null;
        String expiresMeta = null;
        try {
            metaData = this.getMetaData(METADATA_NAMESPACE);
            expiresMeta = metaData.getFirstValue("expires");
        } catch (MetaDataException e) {
            throw new DocumentException(e);
        }
        if (expiresMeta != null) {
            secs = Long.parseLong(expiresMeta);
        } else {
            secs = -1;
        }

        if (secs != -1) {
            Date date = new Date();
            date.setTime(date.getTime() + secs * 1000l);
            expires = date;
        } else {
            expires = this.getResourceType().getExpires();
        }

        return expires;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getName()
     */
    public String getName() {
        try {
            return getLink().getNode().getName();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private Publication publication;

    /**
     * @see org.apache.lenya.cms.publication.Document#getPublication()
     */
    public Publication getPublication() {
        if (this.publication == null) {
            this.publication = getSession().getPublication(getIdentifier().getPublicationId());
        }
        return this.publication;
    }
    
    public String getPublicationId(){
    	if (this.publication == null) {
        this.publication = getSession().getPublication(getIdentifier().getPublicationId());
    }
    return this.publication.getId();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getLastModified()
     */
    public long getLastModified() throws DocumentException {
        try {
            return getRepositoryNode().getLastModified();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    public String getLanguage() {
        return this.identifier.getLanguage();
    }
    
    public String[] getLanguages() {

        List documentLanguages = new ArrayList();
        String[] allLanguages = getPublication().getLanguages();

        if (logger.isDebugEnabled()) {
            logger.debug("Number of languages of this publication: " + allLanguages.length);
        }

        for (int i = 0; i < allLanguages.length; i++) {
            if (existsTranslation(allLanguages[i])) {
                documentLanguages.add(allLanguages[i]);
            }
        }

        return (String[]) documentLanguages.toArray(new String[documentLanguages.size()]);
    }

    public String getArea() {
        return this.identifier.getArea();
    }

    private String extension = null;
    private String defaultExtension = "html";

    /**
     * @see org.apache.lenya.cms.publication.Document#getExtension()
     */
    public String getExtension() {
        if (extension == null) {
            String sourceExtension = getSourceExtension();
            if (sourceExtension.equals("xml") || sourceExtension.equals("")) {
                logger.info("Default extension will be used: " + defaultExtension);
                return defaultExtension;
            } else {
                return sourceExtension;
            }

        }
        return this.extension;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getUUID()
     */
    public String getUUID() {
        return getIdentifier().getUUID();
    }

    private String defaultSourceExtension = "xml";

    /**
     * @see org.apache.lenya.cms.publication.Document#getSourceExtension()
     */
    public String getSourceExtension() {
        String sourceExtension;
        try {
            sourceExtension = getMetaData(METADATA_NAMESPACE).getFirstValue(METADATA_EXTENSION);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (sourceExtension == null) {
            logger.warn("No source extension for document [" + this + "]. The extension \""
                    + defaultSourceExtension + "\" will be used as default!");
            sourceExtension = defaultSourceExtension;
        }
        return sourceExtension;
    }

    /**
     * Sets the extension of the file in the URL.
     * @param _extension A string.
     */
    protected void setExtension(String _extension) {
        Validate.notNull(_extension);
        Validate.isTrue(!_extension.startsWith("."), "Extension must start with a dot");
        checkWritability();
        this.extension = _extension;
    }

    public boolean exists()throws DocumentException{
        try {
            return getRepositoryNode().exists();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
        	throw new DocumentException(e);
        }
    }

    public boolean existsInAnyLanguage() {
        String[] languages = getLanguages();

        if (languages.length > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Document (" + this + ") exists in at least one language: "
                        + languages.length);
            }
            String[] allLanguages = getPublication().getLanguages();
            if (languages.length == allLanguages.length)
                // TODO: This is not entirely true, because the publication
                // could assume the
                // languages EN and DE, but the document could exist for the
                // languages DE and FR!
                if (logger.isDebugEnabled()) {
                    logger.debug("Document (" + this
                            + ") exists even in all languages of this publication");
                }
            return true;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Document (" + this + ") does NOT exist in any language");
            }
            return false;
        }

    }

    public DocumentIdentifier getIdentifier() {
        return this.identifier;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        if (getClass().isInstance(object)) {
            DocumentImpl document = (DocumentImpl) object;
            return document.getIdentifier().equals(getIdentifier());
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {

        String key = getPublication().getId() + ":" + getPublication().getPubBaseUri() + ":"
                + getArea() + ":" + getUUID() + ":" + getLanguage();

        return key.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getIdentifier().toString();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getCanonicalWebappURL()
     */
    public String getCanonicalWebappURL() {
        return "/" + getPublication().getId() + "/" + getArea() + getCanonicalDocumentURL();
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getCanonicalDocumentURL()
     */
    public String getCanonicalDocumentURL() {
        try {
            DocumentBuilder builder = getPublication().getDocumentBuilder();
            String webappUrl = builder.buildCanonicalUrl(getSession(), getLocator());
            String prefix = "/" + getPublication().getId() + "/" + getArea();
            return webappUrl.substring(prefix.length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public org.apache.lenya.cms.publication.Session getSession() {
        return this.session;
    }

    public void accept(DocumentVisitor visitor) throws Exception {
        visitor.visitDocument(this);
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#delete()
     */
    public void delete() throws DocumentException {
        if (hasLink()) {
            throw new DocumentException("Can't delete document [" + this
                    + "], it's still referenced in the site structure.");
        }
        try {
            getRepositoryNode().delete();
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    protected static final String IDENTIFIABLE_TYPE = "document";

    private ResourceType resourceType;
    private MetaDataCache metaDataCache;
    private SourceResolver resolver;

    /**
     * Convenience method to read the document's resource type from the meta-data.
     * @see Document#getResourceType()
     */
    public ResourceType getResourceType() throws DocumentException {
        if (this.resourceType == null) {
            String name;
            try {
                name = getMetaData(METADATA_NAMESPACE).getFirstValue(METADATA_RESOURCE_TYPE);
            } catch (MetaDataException e) {
                throw new DocumentException(e);
            }
            if (name == null) {
                throw new DocumentException("No resource type defined for document [" + this + "]!");
            }
            this.resourceType = this.resourceTypeResolver.getResourceType(name);
        }
        return this.resourceType;
    }

    public MetaData getMetaData(String namespaceUri) throws MetaDataException {
        MetaData meta;
        try {
            meta = new MetaDataWrapper(getContentHolder().getMetaData(namespaceUri));
        } catch (org.apache.lenya.cms.metadata.MetaDataException e) {
            throw new MetaDataException(e);
        }
        if (getRepositorySession().isModifiable()) {
            return meta;
        } else {
            String cacheKey = getPublication().getId() + ":" + getArea() + ":" + getUUID() + ":"
                    + getLanguage();
            return getMetaDataCache().getMetaData(cacheKey, meta, namespaceUri);
        }
    }

    protected MetaDataCache getMetaDataCache() {
        return this.metaDataCache;
    }

    public String[] getMetaDataNamespaceUris() throws MetaDataException {
        try {
            return getContentHolder().getMetaDataNamespaceUris();
        } catch (org.apache.lenya.cms.metadata.MetaDataException e) {
            throw new MetaDataException(e);
        }
    }

    public String getMimeType() throws DocumentException {
        try {
            String mimeType = getMetaData(METADATA_NAMESPACE).getFirstValue(METADATA_MIME_TYPE);
            if (mimeType == null) {
                mimeType = "";
            }
            return mimeType;
        } catch (MetaDataException e) {
            throw new DocumentException(e);
        }
    }

    public long getContentLength() {
        try {
            return getContentHolder().getContentLength();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMimeType(String mimeType) {
        checkWritability();
        try {
            getMetaData(METADATA_NAMESPACE).setValue(METADATA_MIME_TYPE, mimeType);
        } catch (MetaDataException e) {
            throw new RuntimeException(e);
        }
    }

    public DocumentLocator getLocator() {
        SiteStructure structure = area().getSite();
        if (!structure.containsByUuid(getUUID(), getLanguage())) {
            throw new RuntimeException("The document [" + this
                    + "] is not referenced in the site structure.");
        }
        try {
            return DocumentLocatorImpl.getLocator(getPublication().getId(), getArea(), structure
                    .getByUuid(getUUID(), getLanguage()).getNode().getPath(), getLanguage());
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPath() throws DocumentException {
        return getLink().getNode().getPath();
    }
    
    public boolean existsAreaVersion(String area) {
        String sourceUri = getSourceURI(getPublication(), area, getUUID(), getLanguage());
        try {
            return SourceUtil.exists(sourceUri, this.resolver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsTranslation(String language) {
        return area().contains(getUUID(), language);
    }

    public Document getAreaVersion(String area) throws ResourceNotFoundException {
        return getPublication().getArea(area).getDocument(getUUID(), getLanguage());
    }

    public Document getTranslation(String language) throws ResourceNotFoundException {
        return area().getDocument(getUUID(), language);
    }

    private Node repositoryNode;

    public Node getRepositoryNode() {
        if (this.repositoryNode == null) {
            SessionHolder holder = (SessionHolder) getSession();
            this.repositoryNode = getRepositoryNode(getNodeFactory(),
                    holder.getRepositorySession(), getSourceURI());
        }
        return this.repositoryNode;
    }

    protected ContentHolder getContentHolder() {
        Node node = getRepositoryNode();
        if (isRevisionSpecified()) {
            try {
                return node.getHistory().getRevision(revision);
            } catch (org.apache.lenya.cms.repository.RepositoryException e) {
                throw new RuntimeException(e);
            }
        } else {
            return node;
        }
    }

    protected static Node getRepositoryNode(NodeFactory nodeFactory, Session session,
            String sourceUri) {
        try {
            return (Node) session.getRepositoryItem(nodeFactory, sourceUri);
        } catch (Exception e) {
            throw new RuntimeException("Creating repository node failed: ", e);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getSourceURI()
     */
    public String getSourceURI() {
        return getSourceURI(getPublication(), getArea(), getUUID(), getLanguage());
    }

    protected static String getSourceURI(Publication pub, String area, String uuid, String language) {
        String path = pub.getPathMapper().getPath(uuid, language);
        return pub.getContentUri(area) + "/" + path;
    }

    
    public boolean existsVersion(String area, String language) {
        String sourceUri = getSourceURI(getPublication(), area, getUUID(), language);
        try {
            return SourceUtil.exists(sourceUri, getSourceResolver());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Document getVersion(String area, String language) throws ResourceNotFoundException {
        return getPublication().getArea(area).getDocument(getUUID(), language);
    }

    public Link getLink() throws DocumentException {
        SiteStructure structure = area().getSite();
        try {
            if (structure.containsByUuid(getUUID(), getLanguage())) {
                return structure.getByUuid(getUUID(), getLanguage());
            } else {
                throw new DocumentException("The document [" + this
                        + "] is not referenced in the site structure [" + structure + "].");
            }
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    public boolean hasLink() {
        return area().getSite().containsByUuid(getUUID(), getLanguage());
    }

    public Area area() {
        return getPublication().getArea(getArea());
    }

    public void setResourceType(ResourceType resourceType) {
        Validate.notNull(resourceType);
        checkWritability();
        try {
            MetaData meta = getMetaData(DocumentImpl.METADATA_NAMESPACE);
            meta.setValue(DocumentImpl.METADATA_RESOURCE_TYPE, resourceType.getName());
        } catch (MetaDataException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSourceExtension(String extension) {
        Validate.notNull(extension);
        Validate.isTrue(!extension.startsWith("."), "Extension must start with a dot");
        checkWritability();
        try {
            MetaData meta = getMetaData(DocumentImpl.METADATA_NAMESPACE);
            meta.setValue(DocumentImpl.METADATA_EXTENSION, extension);
        } catch (MetaDataException e) {
            throw new RuntimeException(e);
        }
    }

    public OutputStream getOutputStream() {
        checkWritability();
        try {
            return getRepositoryNode().getOutputStream();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    protected void checkWritability() {
        if (isRevisionSpecified()) {
            throw new UnsupportedOperationException();
        }
    }

    protected boolean isRevisionSpecified() {
        return this.revision != -1;
    }

    public InputStream getInputStream() {
        try {
            return getRepositoryNode().getInputStream();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Session getRepositorySession() {
        return ((SessionHolder) getSession()).getRepositorySession();
    }

    public int getRevisionNumber() {
        if (!isRevisionSpecified()) {
            throw new UnsupportedOperationException(
                    "This is not a particular revision of the document [" + this + "].");
        }
        return this.revision;
    }

    public void setMetaDataCache(MetaDataCache metaDataCache) {
        this.metaDataCache = metaDataCache;
    }

    public SourceResolver getSourceResolver() {
        return resolver;
    }

    public void setSourceResolver(SourceResolver resolver) {
        this.resolver = resolver;
    }

    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public void checkin() throws RepositoryException {
        try {
            getRepositoryNode().checkin();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public boolean isCheckedOutBySession(String sessionId, String userId)
            throws RepositoryException {
        try {
            return getRepositoryNode().isCheckedOutBySession(sessionId, userId);
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void checkout() throws RepositoryException {
        try {
            getRepositoryNode().checkout();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public String getCheckoutUserId() throws RepositoryException {
        try {
            return getRepositoryNode().getCheckoutUserId();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public boolean isCheckedOut() throws RepositoryException {
        try {
            return getRepositoryNode().isCheckedOut();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void lock() throws RepositoryException {
        try {
            getRepositoryNode().lock();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void registerDirty() throws RepositoryException {
        try {
            getRepositoryNode().registerDirty();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void unlock() throws RepositoryException {
        try {
            getRepositoryNode().unlock();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    private History history;

    public History getHistory() {
        if (this.history == null) {
            //florent : wrapper not still usefull this.history = new HistoryWrapper(getRepositoryNode().getHistory());
        	this.history = getRepositoryNode().getHistory();
        }
        return this.history;
    }

    public boolean isLocked() {
        // TODO Auto-generated method stub
        return false;
    }

    public Document getRevision(int i) throws RepositoryException{
    	try{
        return area().getDocument(getUUID(), getLanguage(), i);
    	}
    	catch (ResourceNotFoundException rnfe){
    		throw new RepositoryException(rnfe);
    	}
    }

    public void forceCheckIn() throws RepositoryException {
        try {
            getRepositoryNode().forceCheckIn();
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void rollback(int revision) throws RepositoryException {
        try {
            getRepositoryNode().rollback(revision);
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void checkout(boolean checkoutRestrictedToSession) throws RepositoryException {
        try {
            getRepositoryNode().checkout(checkoutRestrictedToSession);
        } catch (org.apache.lenya.cms.repository.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void setResourceTypeResolver(ResourceTypeResolver resourceTypeResolver) {
        this.resourceTypeResolver = resourceTypeResolver;
    }

    /*** BEGIN unimplemented methods **/
    //Florent : 
    //This methods come from the remove of o.a.l.cms.publication.Node and the use of o.a.l.cms.repository.Node
    
		public boolean isCollection() throws RepositoryException {
			// TODO Auto-generated method stub
			return false;
		}

		public Collection getChildren() throws RepositoryException {
			// TODO Auto-generated method stub
			return null;
		}

		public void copyRevisionsFrom(Node source) throws RepositoryException {
			// TODO Auto-generated method stub
			
		}

		public void registerRemoved() throws RepositoryException {
			// TODO Auto-generated method stub
			
		}

		public void setPersistable(Persistable persistable)
				throws RepositoryException {
			// TODO Auto-generated method stub
			
		}

		public Persistable getPersistable() {
			// TODO Auto-generated method stub
			return null;
		}
		/*** END unimplemented methods **/
}