/*
 * Copyright 1999-2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

/* $Id$ */

package org.apache.lenya.cms.cocoon.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.lenya.cms.cocoon.task.CocoonTaskWrapper;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.scheduler.LoadQuartzServlet;
import org.apache.lenya.cms.scheduler.ServletJob;
import org.apache.lenya.cms.task.TaskWrapper;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.util.ServletHelper;

/**
 * Scheduler helper
 */
public class SchedulerHelper {

    /**
     * Ctor.
     * @param _objectModel The Cocoon component object model.
     * @param _parameters The Cocoon component parameters.
     * @param _logger The logger to use.
     */
    public SchedulerHelper(Map _objectModel, Parameters _parameters, Logger _logger) {
        this.objectModel = _objectModel;
        this.parameters = _parameters;
        this.logger = _logger;
    }

    private Logger logger;
    private Parameters parameters;
    private Map objectModel;

    /**
     * Creates the scheduler parameters.
     * @return A map.
     * @throws ProcessingException when something went wrong.
     */
    public Map createParameters() throws ProcessingException {

        Map map = new HashMap();

        try {
            TaskWrapper wrapper = new CocoonTaskWrapper(this.objectModel, this.parameters, null);

            this.logger.debug("Adding task wrapper parameters");
            Map wrapperParameters = wrapper.getParameters();
            map.putAll(wrapperParameters);

            NamespaceMap schedulerParameters = new NamespaceMap(LoadQuartzServlet.PREFIX);

            DocumentIdentityMap identityMap = DocumentUtil.createDocumentIdentityMap(null, null);
            String url = ServletHelper.getWebappURI(ObjectModelHelper.getRequest(this.objectModel));
            Publication pub = null;

            schedulerParameters.put(ServletJob.PARAMETER_DOCUMENT_URL,identityMap.getFromURL(url)
                    .getCanonicalWebappURL());
            schedulerParameters.put(LoadQuartzServlet.PARAMETER_PUBLICATION_ID, pub.getId());
            map.putAll(schedulerParameters.getPrefixedMap());
        } catch (final Exception e) {
            throw new ProcessingException(e);
        }

        return map;
    }

}