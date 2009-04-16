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
package org.apache.lenya.cms.usecases.webdav;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.site.usecases.CreateDocument;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.usecase.xml.UsecaseErrorHandler;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.cms.workflow.usecases.UsecaseWorkflowHelper;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.Schema;
import org.apache.lenya.xml.ValidationUtil;

/**
 * Supports WebDAV PUT.
 * @version $Id: $
 */
public class Put extends CreateDocument {
    
    // registeredExtensions contain all known extension matching to a certain resource-type.
    private HashMap registeredExtensions = new HashMap();
    // default is xhtml and xml but you can override it with the config
    protected String TYPE = "xhtml";
    protected String EXTENSION = "*";
    protected static final String ATTRIBUTE_TYPE = "resource-type";
    protected static final String ELEMENT_ROOT = "extensions";
    protected static final String ELEMENT_EXTENSION = "extension";
    protected static final String EVENT = "lenya.event";
    
    private boolean fallback = false;

    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        Configuration extensionsConfig = config.getChild(ELEMENT_ROOT, false);
        if (extensionsConfig != null) {
            Configuration[] extensions = extensionsConfig.getChildren(ELEMENT_EXTENSION);
            for (int i = 0; i < extensions.length; i++) {
                Configuration extension = extensions[i];
                // add extension to register (key: extension,value: resource-type)
                if (extension != null)
                    registeredExtensions.put(extension.getValue(), extension
                            .getAttribute(ATTRIBUTE_TYPE));
            }
        } else {
            registeredExtensions.put(this.EXTENSION, this.TYPE);
        }
    }

    protected void doCheckExecutionConditions() throws Exception {
        String event = getParameterAsString(EVENT);
        if (event != null) {
            Document doc = getSourceDocument();
            UsecaseWorkflowHelper.checkWorkflow(this.manager, this, event, doc,
                    getLogger());
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        SourceResolver resolver = null;

        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            Document doc = getSourceDocument();
            String extension = getSourceExtension();
            // sanity check
            if (doc == null)
                throw new IllegalArgumentException("illegal usage, source document may not be null");

            // create new doc from PUT input
            if (!doc.exists()) {
                DocumentManager documentManager = null;
                ServiceSelector selector = null;
                ResourceType resourceType = null;
                try {
                    selector = (ServiceSelector) this.manager
                            .lookup(ResourceType.ROLE + "Selector");

                    documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

                    DocumentFactory map = getDocumentFactory();
                    String path = doc.getPath();
                    // lookupResourceType(extension)
                    resourceType = lookUpExtension(extension, selector);
                    ResourceType.Sample sample = resourceType.getSample(resourceType.getSampleNames()[0]);
                    doc = documentManager.add(map, resourceType, sample.getUri(), getPublication(), doc
                            .getArea(), path, doc.getLanguage(), extension, doc.getName(), true);
                    doc.setMimeType(sample.getMimeType());
                    setMetaData(doc);
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

            String sourceUri = "cocoon:/request/PUT/" + extension;
            org.w3c.dom.Document xmlDoc = SourceUtil.readDOM(sourceUri, manager);

            // validate if a schema is provided and we are not using any fallback
            if (doc.getResourceType().getSchema() != null & fallback == false) {
                validateDoc(resolver, xmlDoc, doc);
            }

            if (!hasErrors()) {
                DocumentHelper.writeDocument(xmlDoc, doc.getOutputStream());
                String event = getParameterAsString(EVENT);
                if (event != null) {
                    WorkflowUtil.invoke(this.manager, getSession(), getLogger(), doc, event);
                }
            }


        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    private ResourceType lookUpExtension(String extension, ServiceSelector selector)
            throws ServiceException {
        ResourceType resourceType;
        String resourceTypeName = (String) registeredExtensions.get(extension);
        if (resourceTypeName == null || resourceTypeName.equals("")) {
            resourceTypeName = (String) registeredExtensions.get(this.EXTENSION);
            this.fallback = true;
        }
        if (selector.isSelectable(resourceTypeName)) {
            resourceType = (ResourceType) selector.select(resourceTypeName);
        } else {
            // using a fallback resource type
            // FIXME this resource tye should be a more generic one like "media-assets" or "bin"
            resourceType = (ResourceType) selector.select(this.TYPE);
            this.fallback = true;
        }
        return resourceType;
    }

    private void validateDoc(SourceResolver resolver, org.w3c.dom.Document xmlDoc, Document doc)
            throws Exception {
        ResourceType resourceType = doc.getResourceType();
        Schema schema = resourceType.getSchema();
        ValidationUtil.validate(this.manager, xmlDoc, schema, new UsecaseErrorHandler(this));
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        try {
            Document doc = getSourceDocument();
            List nodes = new ArrayList();

            NodeSet set = SiteUtil.getSubSite(this.manager, doc.getLink().getNode());
            Document[] documents = set.getDocuments();
            for (int i = 0; i < documents.length; i++) {
                nodes.add(documents[i].getRepositoryNode());
            }

            SiteStructure structure = getSourceDocument().area().getSite();
            nodes.add(structure.getRepositoryNode());
            return (Node[]) nodes.toArray(new Node[nodes.size()]);

        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * Sets the meta data of the created document.
     * 
     * @param document The document.
     * @throws MetaDataException if an error occurs.
     */
    protected void setMetaData(Document document) throws MetaDataException {

        if (document == null)
            throw new IllegalArgumentException("parameter document may not be null");

        MetaData dcMetaData = document.getMetaData(DublinCore.DC_NAMESPACE);
        dcMetaData.setValue(DublinCore.ELEMENT_TITLE, document.getName());
        dcMetaData.setValue(DublinCore.ELEMENT_CREATOR, "");
        dcMetaData.setValue(DublinCore.ELEMENT_PUBLISHER, "");
        dcMetaData.setValue(DublinCore.ELEMENT_SUBJECT, "");
        dcMetaData.setValue(DublinCore.ELEMENT_DATE, "");
        dcMetaData.setValue(DublinCore.ELEMENT_RIGHTS, "");
        dcMetaData.setValue(DublinCore.ELEMENT_LANGUAGE, document.getLanguage());
    }

    private Publication publication;

    /**
     * Access to the current publication. Use this when the publication is not yet known in the
     * usecase: e.g. when creating a global asset. When adding a resource or a child to a document,
     * access the publication via that document's interface instead.
     * 
     * @return the publication in which the use-case is being executed
     */
    protected Publication getPublication() {
        if (this.publication == null) {
            try {
                this.publication = PublicationUtil.getPublicationFromUrl(this.manager,
                        getDocumentFactory(), getSourceURL());
            } catch (PublicationException e) {
                throw new RuntimeException(e);
            }
        }
        return this.publication;
    }

    protected String getSourceExtension() {
        String destinationUri = getParameterAsString(SOURCE_URL);
        String extension = null;
        if (destinationUri.indexOf(".") > 0) {
            extension = destinationUri.substring(destinationUri.lastIndexOf(".") + 1,
                    destinationUri.length());
        } else {
            extension = EXTENSION;
        }
        return extension;
    }

    protected String getNewDocumentName() {
        // TODO Auto-generated method stub
        return null;
    }

    protected String getNewDocumentPath() {
        Document doc = getSourceDocument();
        return doc.getUUID();
    }

    protected boolean getVisibleInNav() {
        return true;
    }

    protected String getDocumentTypeName() {
        ResourceType resourceType = null;
        ServiceSelector selector = null;
        String docType = "";
        try {
            selector = (ServiceSelector) this.manager.lookup(ResourceType.ROLE + "Selector");
            resourceType = lookUpExtension(getSourceExtension(), selector);
            docType = resourceType.getName();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                this.manager.release(selector);
            }
        }
        return docType;
    }

}
