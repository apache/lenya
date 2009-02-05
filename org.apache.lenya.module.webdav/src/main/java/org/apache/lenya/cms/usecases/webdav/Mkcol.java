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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.site.usecases.Create;

/**
 * Supports WebDAV PUT.
 * 
 */
public class Mkcol extends Create {
    // default is xhtml and xml but you can override it with the config
    protected String TYPE = "xhtml";
    protected String EXTENSION = "xml";
    protected static final String ATTRIBUTE_TYPE = "resource-type";
    protected static final String ELEMENT_EXTENSION = "extension";

    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        Configuration typeConfig = config.getChild(ELEMENT_EXTENSION, false);
        if (typeConfig != null) {
            this.EXTENSION = typeConfig.getValue();
            this.TYPE = typeConfig.getAttribute(ATTRIBUTE_TYPE);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        // super.doExecute();
        SourceResolver resolver = null;
        ResourceType resourceType = null;

        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            Document doc = getSourceDocument();
            // sanity check
            if (doc == null)
                throw new IllegalArgumentException("illegal usage, source document may not be null");

            if (!doc.exists()) {
                DocumentManager documentManager = null;
                ServiceSelector selector = null;
                try {
                    selector = (ServiceSelector) this.manager
                            .lookup(ResourceType.ROLE + "Selector");

                    documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

                    DocumentFactory map = getDocumentFactory();
                    String path = doc.getPath();

                    resourceType = (ResourceType) selector.select(TYPE);
                    ResourceType.Sample sample = resourceType.getSample(resourceType.getSampleNames()[0]);
                    doc = documentManager.add(map, resourceType, sample.getUri(), getPublication(), doc
                            .getArea(), path, doc.getLanguage(), EXTENSION, doc.getName(), true);
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

        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
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

    protected String getNewDocumentName() {
        // TODO Auto-generated method stub
        return null;
    }

    protected String getNewDocumentPath() {
        // TODO Auto-generated method stub
        return null;
    }

    protected String getDocumentTypeName() {
        // TODO Auto-generated method stub
        return null;
    }

    protected String getSourceExtension() {
        return EXTENSION;
    }

    protected boolean createVersion() {
        return false;
    }

}
