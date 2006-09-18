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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * <p>
 * Abstract superclass for usecases to create a document.
 * </p>
 * <p>
 * You can pass the following parameters into the usecase:
 * </p>
 * <ul>
 * <li><strong>path</strong> - the path of the document to create (optional)</li>
 * </ul>
 * 
 * @version $Id$
 */
public abstract class Create extends AbstractUsecase {

    protected static final String RESOURCE_TYPES = "resourceTypes";
    protected static final String LANGUAGE = "language";
    protected static final String LANGUAGES = "languages";
    protected static final String PATH = "path";
    protected static final String NODE_NAME = "nodeName";
    protected static final String VISIBLEINNAV = "visibleInNav";
    protected static final String SAMPLE = "sample";
    protected static final String SAMPLES = "samples";

    /**
     * Ctor.
     */
    public Create() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        if (!getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        try {
            SiteStructure structure = getPublication().getArea(getArea()).getSite();
            Node[] nodes = { structure.getRepositoryNode() };
            return nodes;
        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        String navigationTitle = getDublinCoreParameter(DublinCore.ELEMENT_TITLE);
        if (navigationTitle.equals("")) {
            addErrorMessage("The navigation title is required.");
        }

        if (getInitialDocument() == null) {
            String[] samples = (String[]) getParameter(SAMPLES);
            String sample = getParameterAsString(SAMPLE);
            if (samples != null && samples.length > 1 && (sample == null || sample.equals(""))) {
                addErrorMessage("Please select a page layout.");
            }
        }

        String path = getNewDocumentPath();
        SiteStructure site = getPublication().getArea(getArea()).getSite();
        if (!createVersion() && site.contains(path)) {
            addErrorMessage("The path [" + path + "] already exists!");
        }

        String doctypeName = getDocumentTypeName();
        if (doctypeName != null) {
            initSampleParameters();
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        // create new document
        DocumentManager documentManager = null;
        ServiceSelector selector = null;
        ResourceType resourceType = null;
        try {

            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

            String language = getParameterAsString(LANGUAGE);
            Document initialDocument = getInitialDocument();

            Document document;

            String title = getDublinCoreParameter(DublinCore.ELEMENT_TITLE);

            if (createVersion()) {
                document = documentManager.addVersion(initialDocument, getArea(), language, true);
                document.getLink().setLabel(title);
            } else {
                if (initialDocument == null) {
                    selector = (ServiceSelector) this.manager
                            .lookup(ResourceType.ROLE + "Selector");
                    resourceType = (ResourceType) selector.select(getDocumentTypeName());
                    String sample = getParameterAsString(SAMPLE, resourceType.getSampleNames()[0]);
                    String sampleUri = resourceType.getSampleURI(sample);
                    document = documentManager.add(getDocumentFactory(), resourceType, sampleUri,
                            getPublication(), getArea(), getNewDocumentPath(), language,
                            getSourceExtension(), title, getVisibleInNav());
                } else {
                    document = documentManager.add(initialDocument, getArea(),
                            getNewDocumentPath(), language, getSourceExtension(), title,
                            getVisibleInNav());
                }
            }

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

    protected String getDublinCoreParameter(String name) {
        return getParameterAsString(DUBLIN_CORE_PREFIX + name, null);
    }

    protected void setDublinCoreParameter(String name, String value) {
        setParameter(DUBLIN_CORE_PREFIX + name, value);
    }

    protected abstract boolean createVersion();

    /**
     * @return the extension to use for the document source.
     */
    protected abstract String getSourceExtension();

    /**
     * @return the name of the document being created in the usecase
     */
    protected abstract String getNewDocumentName();

    /**
     * @return the id of the new document being created in the usecase
     */
    protected abstract String getNewDocumentPath();

    /**
     * If the document created in the usecase shall have initial contents copied from an existing
     * document, construct that document in this method.
     * 
     * @return A document.
     */
    protected Document getInitialDocument() {
        return null;
    }

    /**
     * @return The type of the created document.
     */
    protected abstract String getDocumentTypeName();

    protected static final String DUBLIN_CORE_PREFIX = "dublincore.";

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
        String[] dcKeys = dcMetaData.getAvailableKeys();

        for (int i = 0; i < dcKeys.length; i++) {
            String param = getDublinCoreParameter(dcKeys[i]);
            if (param != null) {
                dcMetaData.setValue(dcKeys[i], param);
            }
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Map objectModel = ContextHelper.getObjectModel(getContext());
        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession(false);
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        User user = identity.getUser();
        if (user != null) {
            setDublinCoreParameter(DublinCore.ELEMENT_CREATOR, user.getId());
        } else {
            setDublinCoreParameter(DublinCore.ELEMENT_CREATOR, "");
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setDublinCoreParameter(DublinCore.ELEMENT_DATE, format.format(new GregorianCalendar()
                .getTime()));

        String doctypeName = getDocumentTypeName();
        if (doctypeName != null) {
            initSampleParameters();
            setParameter(RESOURCE_TYPES, Collections.EMPTY_LIST);
        } else {
            String[] resourceTypes = getPublication().getResourceTypeNames();
            setParameter(RESOURCE_TYPES, Arrays.asList(resourceTypes));
        }
    }

    protected void initSampleParameters() {
        ServiceSelector selector = null;
        ResourceType resourceType = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(ResourceType.ROLE + "Selector");
            resourceType = (ResourceType) selector.select(getDocumentTypeName());
            String[] samples = resourceType.getSampleNames();
            if (samples.length == 0) {
                addErrorMessage("The resource type [" + resourceType.getName()
                        + "] doesn't provide any samples!");
            }
            setParameter(SAMPLES, samples);
            setParameter(SAMPLE, samples[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (resourceType != null) {
                    selector.release(resourceType);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * @return The source document or <code>null</code> if the usecase was not invoked on a
     *         document.
     */
    protected Document getSourceDocument() {
        Document document = null;
        String url = getSourceURL();
        try {
            if (getDocumentFactory().isDocument(url)) {
                document = getDocumentFactory().getFromURL(url);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return document;
    }

    /**
     * @return The new document.
     */
    protected Document getNewDocument() {
        try {
            String path = getNewDocumentPath();
            String language = getParameterAsString(LANGUAGE);
            return getPublication().getArea(getArea()).getSite().getNode(path).getLink(language)
                    .getDocument();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The area without the "info-" prefix.
     */
    public String getArea() {
        URLInformation info = new URLInformation(getSourceURL());
        return info.getArea();
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

    /**
     * @return the visibleInNav Attribute of the document being created in the usecase
     */
    protected boolean getVisibleInNav() {
        if (getParameterAsString(VISIBLEINNAV).equals("false")) {
            return false;
        }
        return true;
    }

}
