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

/* $Id: SchedulerHelper.java,v 1.3 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.cms.cocoon.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.cocoon.task.CocoonTaskWrapper;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.scheduler.LoadQuartzServlet;
import org.apache.lenya.cms.scheduler.ServletJob;
import org.apache.lenya.cms.task.TaskWrapper;
import org.apache.lenya.util.NamespaceMap;

public class SchedulerHelper {

    /**
     * Ctor.
     * @param objectModel The Cocoon component object model.
     * @param parameters The Cocoon component parameters.
     * @param logger The logger to use.
     */
    public SchedulerHelper(Map objectModel, Parameters parameters, Logger logger) {
        this.objectModel = objectModel;
        this.parameters = parameters;
        this.logger = logger;
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
            TaskWrapper wrapper = new CocoonTaskWrapper(objectModel, parameters);

            logger.debug("Adding task wrapper parameters");
            Map wrapperParameters = wrapper.getParameters();
            map.putAll(wrapperParameters);

            NamespaceMap schedulerParameters = new NamespaceMap(LoadQuartzServlet.PREFIX);

            PageEnvelope envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);

            schedulerParameters.put(
                ServletJob.PARAMETER_DOCUMENT_URL,
                envelope.getDocument().getCompleteURL());
            schedulerParameters.put(
                LoadQuartzServlet.PARAMETER_PUBLICATION_ID,
                envelope.getPublication().getId());
            map.putAll(schedulerParameters.getPrefixedMap());

        } catch (Exception e) {
            throw new ProcessingException(e);
        }

        return map;
    }

}
