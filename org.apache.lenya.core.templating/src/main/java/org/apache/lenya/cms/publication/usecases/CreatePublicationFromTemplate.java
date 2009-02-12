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
package org.apache.lenya.cms.publication.usecases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.lenya.cms.publication.Publication;
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

        String[] pubIds = getSession().getPublicationIds();
        List templates = new ArrayList();
        for (String pubId : pubIds) {
            if (getSession().getPublication(pubId).getInstantiatorHint() != null) {
                templates.add(pubId);
            }
        }
        Collections.sort(templates);
        setParameter(AVAILABLE_TEMPLATES, templates);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        String publicationId = getParameterAsString(PUBLICATION_ID).trim();

        if (!PublicationUtil.isValidPublicationID(publicationId)) {
            addErrorMessage("invalid-publication-id");
        } else {
            if (getSession().existsPublication(publicationId)) {
                addErrorMessage("publication-already-exists");
            }
        }
        String name = getParameterAsString(PUBLICATION_NAME);
        if (name == null || name.trim().equals("")) {
            addErrorMessage("invalid-publication-name");
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String templateId = getParameterAsString(TEMPLATE);

        Instantiator instantiator = null;

        Publication template = getSession().getPublication(templateId);
        String name = getParameterAsString(PUBLICATION_NAME);

        instantiator = (Instantiator) WebAppContextUtils.getCurrentWebApplicationContext().getBean(
                Instantiator.ROLE + "/" + template.getInstantiatorHint());

        instantiator.instantiate(template, getParameterAsString(PUBLICATION_ID), name);
    }
    
    /*
     * TODO: enable once we can change configuration options in publication via web interface public
     * String getTargetURL(boolean success) { if (success) { return "/" +
     * getParameterAsString(PUBLICATION_ID) + "/introduction.html"; } return
     * super.getTargetURL(success); }
     */
}
