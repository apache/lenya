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

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.workflow.DocumentWorkflowable;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowManager;

/**
 * Module for workflow access.
 * 
 * @version $Id$
 */
public class WorkflowModule extends AbstractPageEnvelopeModule {

    /**
     * <code>STATE</code> The state
     */
    public static final String STATE = "state";
    /**
     * <code>VARIABLE_PREFIX</code> The variable prefix
     */
    public static final String VARIABLE_PREFIX = "variable.";
    /**
     * <code>HISTORY_PATH</code> The history path
     * @deprecated Exposes implementation details.
     */
    public static final String HISTORY_PATH = "history-path";

    static final String[] PARAMETER_NAMES = { STATE, HISTORY_PATH };

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        Object value = null;
        WorkflowManager wfManager = null;

        try {
            PageEnvelope envelope = getEnvelope(objectModel, name);
            Document document = envelope.getDocument();
            if (document != null && document.exists()) {
                wfManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
                Session session = RepositoryUtil.getSession(ObjectModelHelper.getRequest(objectModel),
                        getLogger());
                DocumentWorkflowable workflowable = new DocumentWorkflowable(this.manager,
                        session,
                        document,
                        getLogger());
                if (wfManager.hasWorkflow(workflowable)) {

                    Version latestVersion = workflowable.getLatestVersion();

                    if (name.equals(STATE)) {
                        if (latestVersion == null) {
                            Workflow workflow = wfManager.getWorkflowSchema(workflowable);
                            value = workflow.getInitialState();
                        } else {
                            value = latestVersion.getState();
                        }
                    } else if (name.startsWith(VARIABLE_PREFIX)) {
                        String variableName = name.substring(VARIABLE_PREFIX.length());
                        Workflow workflow = wfManager.getWorkflowSchema(workflowable);
                        String[] variableNames = workflow.getVariableNames();
                        if (Arrays.asList(variableNames).contains(variableName)) {
                            if (latestVersion == null) {
                                value = Boolean.valueOf(workflow.getInitialValue(variableName));
                            } else {
                                value = Boolean.valueOf(latestVersion.getValue(variableName));
                            }
                        }
                    } else if (name.equals(HISTORY_PATH)) {
                        final String path = document.getPublication()
                                .getPathMapper()
                                .getPath(document.getId(), document.getLanguage());
                        final String uri = "content/workflow/history/" + path;
                        final File pubDir = document.getPublication().getDirectory();
                        final File workflowFile = new File(pubDir, uri.replace('/',
                                File.separatorChar));
                        value = workflowFile.getAbsolutePath();
                    } else {
                        throw new ConfigurationException("The attribute [" + name
                                + "] is not supported!");
                    }
                }
            }
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Resolving attribute failed: ", e);
        } finally {
            if (wfManager != null) {
                this.manager.release(wfManager);
            }
        }
        return value;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        return Arrays.asList(PARAMETER_NAMES).iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };

        return objects;
    }

}