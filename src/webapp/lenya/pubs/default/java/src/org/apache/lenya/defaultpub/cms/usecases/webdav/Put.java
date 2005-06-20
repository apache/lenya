/*
 * Copyright  1999-2004 The Apache Software Foundation
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
package org.apache.lenya.defaultpub.cms.usecases.webdav;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.workflow.WorkflowManager;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.excalibur.source.Source;
import org.xml.sax.InputSource;
import org.apache.lenya.xml.RelaxNG;

/**
 * Supports WebDAV PUT.
 * 
 * @version $Id$
 */
public class Put extends DocumentUsecase {
	
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
            //sanity check
            if (doc == null)
                throw new IllegalArgumentException("illegal usage, source document may not be null");

            if (!doc.exists()) {
                DocumentManager documentManager = null;
                DocumentTypeBuilder documentTypeBuilder = null;
                try {

                    documentTypeBuilder = (DocumentTypeBuilder) this.manager
                            .lookup(DocumentTypeBuilder.ROLE);

                    documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
     
                    DocumentIdentityMap map = (DocumentIdentityMap) getUnitOfWork().getIdentityMap();
                    Document document = map.get(getPublication(),
                            doc.getArea(),
                            doc.getId(),
                            doc.getLanguage());
                    
                    DocumentType documentType = documentTypeBuilder
                            .buildDocumentType("xhtml", getPublication());
                    documentManager.add(document,
                            documentType,
                            doc.getName(),
                            null);

                    setMetaData(document);
                    doc = document;
                } finally {
                    if (documentManager != null) {
                        this.manager.release(documentManager);
                    }
                    if (documentTypeBuilder != null) {
                        this.manager.release(documentTypeBuilder);
                    }
                }
            }

            DocumentIdToPathMapper mapper = doc.getPublication().getPathMapper();
            String path = mapper.getPath(doc.getId(), getSourceDocument().getLanguage());
            String sourceUri = doc.getSourceURI();
            String pubId = doc.getPublication().getId();
            String uploadSourceUri = "cocoon:/request/PUT"; 

            //lets copy the source to temp work area
            String tempSourceUri = "context://lenya/pubs/" + pubId + "/work/webdav/content/"
                    + doc.getArea() + "/" + path + ".tmp";
            tempSourceUri = tempSourceUri.substring("lenya://".length());
            tempSourceUri = "context://" + tempSourceUri;

            SourceUtil.copy(resolver, uploadSourceUri, tempSourceUri, true);
            
            Source tempSource = resolver.resolveURI(tempSourceUri);
            if (!tempSource.exists()) {
                throw new IllegalArgumentException("The temp file [" + tempSource.getURI()
                        + "] does not exist.");
            }
            
            //validity check
            DocumentType resourceType = doc.getResourceType();
            String schemaUri = resourceType.getSchemaDefinitionSourceURI();
            Source schemaSource = resolver.resolveURI(schemaUri);
            if (!schemaSource.exists()) {
                throw new IllegalArgumentException("The schema [" + schemaSource.getURI()
                        + "] does not exist.");
            }

            InputSource schemaInputSource = org.apache.cocoon.components.source.SourceUtil.getInputSource(schemaSource);
            InputSource xmlInputSource = org.apache.cocoon.components.source.SourceUtil.getInputSource(tempSource);

            String message = RelaxNG.validate(schemaInputSource, xmlInputSource);
            if (message != null) {
                addErrorMessage("error-validation", new String[]{message});
            }            
            
            if (SourceUtil.exists(tempSourceUri, this.manager)) {
                SourceUtil.copy(resolver, tempSourceUri, sourceUri, true);
                SourceUtil.delete(tempSourceUri, this.manager);
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
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getObjectsToLock()
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {
        try {
            List nodes = new ArrayList();
            Document doc = getSourceDocument();
            Document liveVersion = doc.getIdentityMap().getAreaVersion(doc, Publication.LIVE_AREA);
            
            DocumentSet set = SiteUtil.getSubSite(this.manager, doc);
            Document[] documents = set.getDocuments();
            for (int i = 0; i < documents.length; i++) {
                nodes.addAll(Arrays.asList(documents[i].getRepositoryNodes()));
            }

            nodes.add(SiteUtil.getSiteStructure(this.manager,
                    doc.getIdentityMap(),
                    doc.getPublication(),
                    Publication.LIVE_AREA).getRepositoryNode());
            return (Transactionable[]) nodes.toArray(new Transactionable[nodes.size()]);

        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }    
    
    /**
     * Sets the meta data of the created document.
     * @param document The document.
     * @throws DocumentException if an error occurs.
     */
    protected void setMetaData(Document document) throws DocumentException {

        if (document == null)
            throw new IllegalArgumentException("parameter document may not be null");

        Map dcMetaData = new HashMap();
        dcMetaData.put(DublinCore.ELEMENT_TITLE, document.getName());
        dcMetaData.put(DublinCore.ELEMENT_CREATOR, "");
        dcMetaData.put(DublinCore.ELEMENT_PUBLISHER,"");
        dcMetaData
                .put(DublinCore.ELEMENT_SUBJECT, "");
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
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            try {
                this.publication = factory.getPublication(this.manager, getSourceURL());
            } catch (PublicationException e) {
                throw new RuntimeException(e);
            }
        }
        return this.publication;
    }


    
}
