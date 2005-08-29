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
package org.apache.lenya.cms.publication.usecases;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.templating.Instantiator;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Create a new publication based on a template publication.
 * 
 * @version $Id$
 */
public class CreatePublicationFromTemplate extends AbstractUsecase {

    protected static final String AVAILABLE_TEMPLATES = "availableTemplates";
    protected static final String PUBLICATION_ID = "publicationId";
    protected static final String PUBLICATION_NAME = "publicationName";
    protected static final String TEMPLATE = "template";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        try {
            Publication[] pubs = PublicationUtil.getPublications(this.manager);
            List templates = new ArrayList();
            for (int i = 0; i < pubs.length; i++) {
                if (pubs[i].getInstantiatorHint() != null) {
                    templates.add(pubs[i].getId());
                }
            }
            setParameter(AVAILABLE_TEMPLATES, templates);

            setParameter(PUBLICATION_NAME, "New Publication");

        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        String publicationId = getParameterAsString(PUBLICATION_ID);

        if (publicationId.trim().equals("")) {
            addErrorMessage("Please enter a publication ID!");
        } else {
            Publication publication = PublicationUtil.getPublication(this.manager, publicationId);
            if (publication.exists()) {
                addErrorMessage("A publication with this ID already exists.");
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String templateId = getParameterAsString(TEMPLATE);

        ServiceSelector selector = null;
        Instantiator instantiator = null;

        try {

            Publication template = PublicationUtil.getPublication(this.manager, templateId);
            String name = getParameterAsString(PUBLICATION_NAME);

            selector = (ServiceSelector) this.manager.lookup(Instantiator.ROLE + "Selector");
            instantiator = (Instantiator) selector.select(template.getInstantiatorHint());

            instantiator.instantiate(template, getParameterAsString(PUBLICATION_ID), name);

        } finally {
            if (selector != null) {
                if (instantiator != null) {
                    selector.release(instantiator);
                }
                this.manager.release(selector);
            }
        }

    }

}