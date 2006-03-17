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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.workflow.WorkflowManager;
import org.apache.lenya.xml.Schema;

/**
 * Supports WebDAV PUT.
 * 
 */
public class Put extends DocumentUsecase {
    // registeredExtensions contain all known extension matching to a certain resource-type.
    private HashMap registeredExtensions = new HashMap();
//  default is xhtml and xml but you can override it with the config
    protected String TYPE = "xhtml";
    protected String EXTENSION = "*";
    protected static final String ATTRIBUTE_TYPE = "resource-type";
    protected static final String ELEMENT_ROOT = "extensions";
    protected static final String ELEMENT_EXTENSION = "extension";
    
    private boolean fallback = false;
    
    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        Configuration extensionsConfig = config.getChild(ELEMENT_ROOT, false);
        if (extensionsConfig != null) {
            Configuration [] extensions=extensionsConfig.getChildren(ELEMENT_EXTENSION);
            for (int i = 0; i < extensions.length; i++) {
                Configuration extension = extensions[i];
//              add extension to register (key: extension,value: resource-type)
                if (extension != null)
                  registeredExtensions.put(extension.getValue(),extension.getAttribute(ATTRIBUTE_TYPE));
            }
        }else{
            registeredExtensions.put(this.EXTENSION,this.TYPE);
        }
      }
    

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        SourceResolver resolver = null;
        WorkflowManager wfManager = null;

        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            Document doc = getSourceDocument();
            String destinationUri = getParameterAsString(SOURCE_URL);
            String extension = destinationUri.substring(destinationUri.lastIndexOf(".")+1,destinationUri.length());
            // sanity check
            if (doc == null)
                throw new IllegalArgumentException("illegal usage, source document may not be null");

            // create new doc from PUT input
            if (!doc.exists()) {
                DocumentManager documentManager = null;
                ServiceSelector selector = null;
                ResourceType resourceType = null;
                try {
                    selector = (ServiceSelector) this.manager.lookup(ResourceType.ROLE + "Selector");

                    documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

                    DocumentIdentityMap map = getDocumentIdentityMap();
                    Document document = map.get(getPublication(),
                            doc.getArea(),
                            doc.getId(),
                            doc.getLanguage());
                    //lookupResourceType(extension)
                    resourceType = lookUpExtension(extension, selector);
                    documentManager.add(document, resourceType, extension, doc.getName(), true, null);
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

            String sourceUri = "cocoon:/request/PUT/" + extension;
            
            // validate if a schema is provided and we are not using any fallback
            if (doc.getResourceType().getSchema() != null & fallback==false){
              validateDoc(resolver, sourceUri, doc);
            }

            if (!hasErrors()) {
              try {
                SourceUtil.copy(resolver, sourceUri, doc.getSourceURI(), true);
              } catch (Exception e) {
                addErrorMessage("invalid source xml. Full exception: "+ e);
              }
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


    private ResourceType lookUpExtension(String extension, ServiceSelector selector) throws ServiceException {
        ResourceType resourceType;
        String resourceTypeName=(String) registeredExtensions.get(extension);
        if (resourceTypeName==null||resourceTypeName.equals("")){
            resourceTypeName=(String) registeredExtensions.get(this.EXTENSION);
            this.fallback=true;
        }
        if (selector.isSelectable(resourceTypeName)){
            resourceType = (ResourceType) selector.select(resourceTypeName);
        }else{
            //using a fallback resource type
            // FIXME this resource tye should be a more generic one like "media-assets" or "bin"
            resourceType = (ResourceType) selector.select(this.TYPE);
            this.fallback=true;
        }
        return resourceType;
    }    

    private void validateDoc(SourceResolver resolver, String uploadSourceUri, Document doc) throws Exception {
          Source uploadSource = resolver.resolveURI(uploadSourceUri);
          if (!uploadSource.exists()) {
              throw new IllegalArgumentException("The upload file [" + uploadSource.getURI()
                      + "] does not exist.");
          }

          ResourceType resourceType = doc.getResourceType();
          Schema schema = resourceType.getSchema();

          // FIXME not working yet, dunno why
          //org.w3c.dom.Document xmlDoc = DocumentHelper.readDocument(uploadSource.getInputStream());
          //ValidationUtil.validate(this.manager, xmlDoc, schema, new UsecaseErrorHandler(this));
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        try {
            Document doc = getSourceDocument();
            List nodes = new ArrayList();

            DocumentSet set = SiteUtil.getSubSite(this.manager, doc);
            Document[] documents = set.getDocuments();
            for (int i = 0; i < documents.length; i++) {
                nodes.add(documents[i].getRepositoryNode());
            }

            SiteStructure structure = SiteUtil.getSiteStructure(this.manager, getSourceDocument());
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
                        getSourceURL());
            } catch (PublicationException e) {
                throw new RuntimeException(e);
            }
        }
        return this.publication;
    }

}
