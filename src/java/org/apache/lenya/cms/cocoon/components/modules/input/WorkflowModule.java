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

/* $Id: WorkflowModule.java,v 1.5 2004/03/01 16:18:24 gregor Exp $  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.workflow.CMSHistory;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.WorkflowInstance;

public class WorkflowModule extends AbstractInputModule {

    public static final String STATE = "state";
    public static final String VARIABLE_PREFIX = "variable.";
    public static final String HISTORY_PATH = "history-path";

    protected static final String[] PARAMETER_NAMES = { STATE, HISTORY_PATH };

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {

        Object value = null;

		
        try {
            PageEnvelope envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
            Document document = envelope.getDocument();

            WorkflowFactory factory = WorkflowFactory.newInstance();
            if (factory.hasWorkflow(document)) {
                WorkflowInstance instance = factory.buildInstance(document);
                if (name.equals(STATE)) {
                    value = instance.getCurrentState().toString();
                }
                else if (name.startsWith(VARIABLE_PREFIX)) {
                    String variableName = name.substring(VARIABLE_PREFIX.length());
                    String[] variableNames = instance.getWorkflow().getVariableNames();
                    if (Arrays.asList(variableNames).contains(variableName)) {
                        value = new Boolean(instance.getValue(variableName));
                    }
                }
                else if (name.equals(HISTORY_PATH)) {
                    value = ((CMSHistory) WorkflowFactory.getHistory(document)).getHistoryPath();
                }
				else {
					throw new ConfigurationException("The attribute [" + name + "] is not supported!");
				}
            }
		} catch (ConfigurationException e) {
			throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Resolving attribute failed: ", e);
        }
        return value;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        return Arrays.asList(PARAMETER_NAMES).iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel)};

        return objects;
    }
    
}
