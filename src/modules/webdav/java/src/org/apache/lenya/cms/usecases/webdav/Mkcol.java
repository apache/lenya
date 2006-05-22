/*
 * Copyright  1999-2006 The Apache Software Foundation
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
package org.apache.lenya.cms.usecases.webdav;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.site.usecases.Create;
import org.apache.lenya.workflow.WorkflowManager;

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
        //super.doExecute();
        SourceResolver resolver = null;
        WorkflowManager wfManager = null;
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
                    selector = (ServiceSelector) this.manager.lookup(ResourceType.ROLE + "Selector");

                    documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

                    DocumentFactory map = getDocumentIdentityMap();
                    Document document = map.get(getPublication(),
                            doc.getArea(),
                            doc.getId(),
                            doc.getLanguage());

                    resourceType = (ResourceType) selector.select(TYPE);
                    documentManager.add(document, resourceType, EXTENSION, doc.getName(), true);

                    setMetaData(document);
                    doc = document;
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

            String sourceUri = resourceType.getSampleURI();
            String destination = doc.getSourceURI();

            try {
                SourceUtil.copy(resolver, sourceUri, destination);
            } catch (Exception e) {
                addErrorMessage("invalid source xml");
            }


        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
            if (wfManager != null) {
                this.manager.release(wfManager);
            }
        }
    }

    /**
     * Sets the meta data of the created document.
     * 
     * @param document The document.
     * @throws DocumentException if an error occurs.
     */
    protected void setMetaData(Document document) throws DocumentException {

        if (document == null)
            throw new IllegalArgumentException("parameter document may not be null");

        Map dcMetaData = new HashMap();
        dcMetaData.put(DublinCore.ELEMENT_TITLE, document.getName());
        dcMetaData.put(DublinCore.ELEMENT_CREATOR, "");
        dcMetaData.put(DublinCore.ELEMENT_PUBLISHER, "");
        dcMetaData.put(DublinCore.ELEMENT_SUBJECT, "");
        dcMetaData.put(DublinCore.ELEMENT_DATE, "");
        dcMetaData.put(DublinCore.ELEMENT_RIGHTS, "");
        dcMetaData.put(DublinCore.ELEMENT_LANGUAGE, document.getLanguage());

        document.getMetaDataManager().setDublinCoreMetaData(dcMetaData);
    }

    protected String getNewDocumentName() {
        // TODO Auto-generated method stub
        return null;
    }

    protected String getNewDocumentId() {
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

}
