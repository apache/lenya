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
package org.apache.lenya.cms.site.usecases;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowManager;
import org.apache.lenya.workflow.Workflowable;

/**
 * Usecase to display the overview tab in the site area for a document.
 * 
 * @version $Id$
 */
public class Overview extends SiteUsecase {

    protected static final String LASTMODIFIED = "lastmodified";
    protected static final String LANGUAGES = "languages";
    protected static final String STATE = "state";
    protected static final String ISLIVE = "isLive";
    protected static final String VISIBLE_IN_NAVIGATION = "visibleInNav";
    protected static final String WORKFLOW_VARIABLE_ISLIVE = "is_live";
    protected static final String PARAM_NUMBER_OF_DOCUMENTS = "numberOfDocuments";
    protected static final String PARAM_NUMBER_OF_SITE_NODES = "numberOfSiteNodes";

    /**
     * Ctor.
     */
    public Overview() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        WorkflowManager resolver = null;
        try {
            Document doc = getSourceDocument();
            if (doc == null) {
                URLInformation info = new URLInformation(getSourceURL());
                Publication pub = getDocumentFactory().getPublication(info.getPublicationId());
                Area area = pub.getArea(info.getArea());
                setParameter(PARAM_NUMBER_OF_DOCUMENTS, new Integer(area.getDocuments().length));
                setParameter(PARAM_NUMBER_OF_SITE_NODES, new Integer(area.getSite().getNodes().length));
            }
            else {
                // read parameters from Dublin Core meta-data
                MetaData dc = doc.getMetaData(DublinCore.DC_NAMESPACE);
                setParameter(DublinCore.ELEMENT_TITLE, dc.getFirstValue(DublinCore.ELEMENT_TITLE));
                setParameter(DublinCore.ELEMENT_DESCRIPTION, dc
                        .getFirstValue(DublinCore.ELEMENT_DESCRIPTION));

                // read parameters from document attributes
                setParameter(LANGUAGES, doc.getLanguages());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                String lastModified = format
                        .format(new Date(getSourceDocument().getLastModified()));
                setParameter(LASTMODIFIED, lastModified);
                boolean visible = doc.getLink().getNode().isVisible();
                setParameter(VISIBLE_IN_NAVIGATION, Boolean.valueOf(visible));

                Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager,
                        getSession(), getLogger(), doc);
                resolver = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
                if (resolver.hasWorkflow(workflowable)) {
                    Workflow workflow = resolver.getWorkflowSchema(workflowable);
                    String[] variableNames = workflow.getVariableNames();
                    Version latestVersion = workflowable.getLatestVersion();
                    Boolean isLive = null;
                    if (latestVersion != null) {
                        setParameter(STATE, latestVersion.getState());
                        if (Arrays.asList(variableNames).contains(WORKFLOW_VARIABLE_ISLIVE)) {
                            isLive = Boolean.valueOf(latestVersion
                                    .getValue(WORKFLOW_VARIABLE_ISLIVE));
                        }
                    } else {
                        setParameter(STATE, workflow.getInitialState());
                        if (Arrays.asList(variableNames).contains(WORKFLOW_VARIABLE_ISLIVE)) {
                            isLive = Boolean.valueOf(workflow
                                    .getInitialValue(WORKFLOW_VARIABLE_ISLIVE));
                        }
                    }
                    setParameter(ISLIVE, isLive);
                } else {
                    setParameter(STATE, "");
                }
            }

        } catch (final Exception e) {
            addErrorMessage("Could not read a value. See log files for details.");
            getLogger().error("Could not read value for Overview usecase. ", e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        // don't complain if document is null
    }

}