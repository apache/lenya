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
package org.apache.lenya.cms.site.usecases;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * Abstract superclass for usecases to create a resource.
 * 
 * @version $Id: Create.java 123982 2005-01-03 15:01:19Z andreas $
 */
public abstract class Create extends DocumentUsecase {

    protected static final String LANGUAGE = "language";
    protected static final String LANGUAGES = "languages";
    protected static final String DOCUMENT_ID = "documentId";

    /**
     * Ctor.
     */
    public Create() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        String navigationTitle = getParameterAsString(DublinCore.ELEMENT_TITLE);

        if (navigationTitle.equals("")) {
            addErrorMessage("The navigation title is required.");
        }

        /*
         * DocumentIdentityMap map = getSourceDocument().getIdentityMap();
         * SiteManager manager = null; try { manager =
         * getSourceDocument().getPublication().getSiteManager(map); } catch
         * (SiteException e) { throw new UsecaseException(e); }
         */
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Document document = createDocument();
        Publication publication = document.getPublication();

        SiteManager _manager = publication.getSiteManager(document.getIdentityMap());
        _manager.add(document);
        _manager.setLabel(document, getParameterAsString(DublinCore.ELEMENT_TITLE));

        WorkflowInstance instance = getWorkflowInstance(document);
        instance.getHistory().initialize(getSituation());

        setMetaData(document);
        setTargetDocument(document);
    }

    /**
     * Creates a document.
     * @return A document.
     * @throws Exception if an error occurs.
     */
    protected abstract Document createDocument() throws Exception;

    /**
     * @return The type of the created document.
     */
    protected abstract String getDocumentTypeName();

    /**
     * Sets the meta data of the created document.
     * @param document The document.
     * @throws DocumentException if an error occurs.
     */
    protected void setMetaData(Document document) throws DocumentException {
        DublinCore dublinCore = document.getDublinCore();
        dublinCore.setValue(DublinCore.ELEMENT_TITLE,
                getParameterAsString(DublinCore.ELEMENT_TITLE));
        dublinCore.setValue(DublinCore.ELEMENT_CREATOR,
                getParameterAsString(DublinCore.ELEMENT_CREATOR));
        dublinCore.setValue(DublinCore.ELEMENT_PUBLISHER,
                getParameterAsString(DublinCore.ELEMENT_PUBLISHER));
        dublinCore.setValue(DublinCore.ELEMENT_SUBJECT,
                getParameterAsString(DublinCore.ELEMENT_SUBJECT));
        dublinCore.setValue(DublinCore.ELEMENT_DATE, getParameterAsString(DublinCore.ELEMENT_DATE));
        dublinCore.setValue(DublinCore.ELEMENT_RIGHTS,
                getParameterAsString(DublinCore.ELEMENT_RIGHTS));
        dublinCore.setValue(DublinCore.ELEMENT_LANGUAGE, getParameterAsString(LANGUAGE));
        dublinCore.save();
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
        setParameter(DublinCore.ELEMENT_CREATOR, user.getId());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        setParameter(DublinCore.ELEMENT_DATE, format.format(new GregorianCalendar().getTime()));

    }
}