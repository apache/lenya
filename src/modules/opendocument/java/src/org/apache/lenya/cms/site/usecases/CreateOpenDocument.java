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
package org.apache.lenya.cms.site.usecases;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.metadata.usecases.Metadata;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.SourceNode;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.cocoon.source.SourceUtil;

/**
 * Usecase to create a document.
 * 
 * @version $Id: CreateDocument.java 379098 2006-02-20 11:35:10Z andreas $
 */
public class CreateOpenDocument extends Create {

    protected static final String PARENT_ID = "parentId";

    protected static final String DOCUMENT_TYPE = "doctype";

    protected static final String RELATION = "relation";

    protected static final String RELATIONS = "relations";

    protected static final String RELATION_CHILD = "child";

    protected static final String RELATION_BEFORE = "sibling before";

    protected static final String RELATION_AFTER = "sibling after";

    protected static final String DOCUMENT_ID_PROVIDED = "documentIdProvided";

    protected static final String ODT_EXTENSION = "odt";

    protected static final String DEFAULT_INDEX = "index";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Document parent = getSourceDocument();
        if (parent == null) {
            setParameter(PARENT_ID, "");
        } else {
            setParameter(PARENT_ID, parent.getId());
            String[] languages = parent.getPublication().getLanguages();
            setParameter(LANGUAGES, languages);
        }

        String[] relations = { RELATION_CHILD, RELATION_AFTER };
        setParameter(RELATIONS, relations);
        setParameter(RELATION, RELATION_CHILD);

        String documentId = getParameterAsString(DOCUMENT_ID);
        boolean provided = documentId != null && !documentId.equals("");
        setParameter(DOCUMENT_ID_PROVIDED, Boolean.valueOf(provided));
    }

    protected String[] getSupportedRelations() {
        return new String[] { RELATION_CHILD, RELATION_AFTER };
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        String documentName = getParameterAsString(DOCUMENT_ID);
        String language = getParameterAsString(LANGUAGE);
        String relation = getParameterAsString(RELATION);

        if (!Arrays.asList(getSupportedRelations()).contains(relation)) {
            addErrorMessage("The relation '" + relation + "' is not supported.");
        }

        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            String hint = getSourceDocument().getPublication().getDocumentBuilderHint();
            builder = (DocumentBuilder) selector.select(hint);

            boolean provided = getParameterAsBoolean(DOCUMENT_ID_PROVIDED, false);
            if (!provided && !builder.isValidDocumentName(documentName)) {
                addErrorMessage("The document ID may not contain any special characters.");
            } else {
                Publication publication = getSourceDocument().getPublication();
                String newDocumentId = getNewDocumentId();
                Document document = getSourceDocument().getIdentityMap().get(publication,
                        getSourceDocument().getArea(),
                        newDocumentId,
                        language);
                if (document.exists()) {
                    addErrorMessage("The document with ID " + newDocumentId + " already exists.");
                }
            }
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {

        // create new document
        DocumentManager documentManager = null;
        ServiceSelector selector = null;
        ResourceType resourceType = null;
        try {

            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

            DocumentFactory map = getDocumentIdentityMap();
            Document document = map.get(getPublication(),
                    getArea(),
                    getNewDocumentId(),
                    getParameterAsString(LANGUAGE));
            selector = (ServiceSelector) this.manager.lookup(ResourceType.ROLE + "Selector");
            resourceType = (ResourceType) selector.select(getDocumentTypeName());
            if (getParameterAsString(SAMPLE) != null && getParameterAsString(SAMPLE).length() > 0)
                resourceType.setSampleURI(getParameterAsString(SAMPLE));
            // now that the source is determined, lock involved nodes
            Node node = document.getRepositoryNode();
            node.lock();
            addODT(document, resourceType);

            Map lenyaMetaData = new HashMap(2);
            lenyaMetaData.put(LenyaMetaData.ELEMENT_RESOURCE_TYPE, resourceType.getName());
            lenyaMetaData.put(LenyaMetaData.ELEMENT_CONTENT_TYPE, "xml");
            document.getMetaDataManager().setLenyaMetaData(lenyaMetaData);
            setMetaData(document);

            // the location to navigate to after completion of usecase
            setTargetURL(document.getCanonicalWebappURL());

        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
            if (selector != null) {
                if (resourceType != null) {
                    selector.release(resourceType);
                }
                this.manager.release(selector);
            }
        }

    }

    protected void addODT(Document document, ResourceType resourceType) throws Exception {
        SourceResolver resolver = null;
        String publicationId = null;
        String contentDir = null;
        String destination = null;
        String sourceUri = resourceType.getSampleURI();
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            String pubBase = Node.LENYA_PROTOCOL + Publication.PUBLICATION_PREFIX_URI + "/";
            String publicationsPath = document.getPublication()
                    .getSourceURI()
                    .substring(pubBase.length());
            publicationId = publicationsPath.split("/")[0];
            Publication pub = PublicationUtil.getPublication(this.manager, publicationId);
            contentDir = pub.getContentDir();
            String urlID = "content/" + document.getArea() + document.getId() + "/" + DEFAULT_INDEX
                    + "_" + document.getLanguage();
            if (contentDir == null) {
                destination = SourceNode.CONTEXT_PREFIX + Publication.PUBLICATION_PREFIX_URI + "/"
                        + publicationId + "/" + urlID;
            } else {
                if (new File(contentDir).isAbsolute()) {
                    // Absolute
                    destination = SourceNode.FILE_PREFIX + contentDir + File.separator + urlID;
                } else {
                    // Relative
                    destination = SourceNode.CONTEXT_PREFIX + contentDir + File.separator + urlID;
                }
            }
            SourceUtil.copy(resolver, sourceUri, destination);
            siteManager = (SiteManager) selector.select(document.getPublication()
                    .getSiteManagerHint());
            if (siteManager.contains(document)) {
                throw new PublicationException("The document [" + document
                        + "] is already contained in this publication!");
            }

            siteManager.add(document);
            siteManager.setLabel(document, getParameterAsString(DublinCore.ELEMENT_TITLE));
            siteManager.setVisibleInNav(document, getVisibleInNav());

            document.getMetaDataManager()
                    .getLenyaMetaData()
                    .setValue(LenyaMetaData.ELEMENT_EXTENSION, ODT_EXTENSION);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
            }
            this.manager.release(selector);
            this.manager.release(resolver);
        }

    }

    /**
     * @see Create#getNewDocumentName()
     */
    protected String getNewDocumentName() {
        final String documentId = getParameterAsString(DOCUMENT_ID);
        String documentName;
        if (getParameterAsBoolean(DOCUMENT_ID_PROVIDED, false)) {
            documentName = documentId.substring(documentId.lastIndexOf("/") + 1);
        } else {
            documentName = documentId;
        }
        return documentName;
    }

    /**
     * @return The relation between the source document and the created document.
     */
    protected String getRelation() {
        return getParameterAsString(RELATION);
    }

    /**
     * @see Create#getNewDocumentId()
     */
    protected String getNewDocumentId() {
        if (getParameterAsBoolean(DOCUMENT_ID_PROVIDED, false)) {
            return getParameterAsString(DOCUMENT_ID);
        } else {
            String relation = getRelation();
            Document sourceDoc = getSourceDocument();
            if (relation.equals(RELATION_CHILD)) {
                return sourceDoc.getId() + "/" + getNewDocumentName();
            } else if (relation.equals(RELATION_BEFORE)) {
                return sourceDoc.getId().substring(0,
                        sourceDoc.getId().lastIndexOf(sourceDoc.getName()))
                        + getNewDocumentName();
            } else if (relation.equals(RELATION_AFTER)) {
                return sourceDoc.getId().substring(0,
                        sourceDoc.getId().lastIndexOf(sourceDoc.getName()))
                        + getNewDocumentName();
            } else {
                return getSourceDocument().getId() + "/" + getNewDocumentName();
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.Create#getDocumentTypeName()
     */
    protected String getDocumentTypeName() {
        return getParameterAsString(DOCUMENT_TYPE);
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.Create#setMetaData(org.apache.lenya.cms.publication.Document)
     */
    protected void setMetaData(Document document) throws DocumentException {
        super.setMetaData(document);

        MetaData customMeta = document.getMetaDataManager().getCustomMetaData();
        String[] paramNames = getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].startsWith(Metadata.CUSTOM_FORM_PREFIX)) {
                String key = paramNames[i].substring(Metadata.CUSTOM_FORM_PREFIX.length());
                String value = getParameterAsString(paramNames[i]);
                customMeta.addValue(key, value);
            }
        }
    }

    protected String getSourceExtension() {
        return ODT_EXTENSION;
    }
}
