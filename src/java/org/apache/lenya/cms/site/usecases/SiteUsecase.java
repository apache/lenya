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

import java.util.Arrays;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * Super class for site related usecases.
 * 
 * @version $Id$
 */
public class SiteUsecase extends DocumentUsecase {

    protected Document doc = null;
    protected WorkflowInstance instance = null;
    protected static final String AREA = "area";
    protected static final String DOCUMENTID = "documentid";
    protected static final String LANGUAGEEXISTS = "languageexists";
    protected static final String STATE = "state";
    protected static final String ISLIVE = "is_live";

    /**
     * Ctor.
     */
    public SiteUsecase() {
        super();
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
    /*TODO make common parameters available to site usecases: area, documentid, languageexists etc
     * may need to take special areas into acccount, such as info-authoring */
    protected void doInitialize() {
        super.doInitialize();
        doc = getSourceDocument();
        try {
            WorkflowFactory factory = WorkflowFactory.newInstance();
            if (factory.hasWorkflow(getSourceDocument())) {
                instance = factory.buildInstance(getSourceDocument());
                setParameter(STATE, instance.getCurrentState().toString());
                String[] variableNames = instance.getWorkflow().getVariableNames();
                if (Arrays.asList(variableNames).contains(ISLIVE)) {
                    setParameter("islive", Boolean.valueOf(instance.getValue(ISLIVE)));
                }
            } else {
                setParameter("state", "");
            }
        } catch (Exception e) {
        	getLogger().error("Could not get workflow state.");
        	addErrorMessage("Could not get workflow state.");
        }
        setParameter(AREA, this.doc.getArea());
        setParameter(DOCUMENTID, this.doc.getId());
        setParameter(LANGUAGEEXISTS, "true");
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
    }

    
}
