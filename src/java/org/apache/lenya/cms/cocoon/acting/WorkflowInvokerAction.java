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

/* $Id: WorkflowInvokerAction.java,v 1.10 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.cocoon.workflow.WorkflowHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;

/**
 * Action to invoke a workflow transition independently from the request document URL.
 * Parameters:
 * <ul>
 *   <li><strong>area:</strong> The area.</li>
 *   <li><strong>document-id:</strong> The document id.</li>
 *   <li><strong>language:</strong> The language.</li>
 *   <li><strong>event:</strong> The event to invoke.</li>
 * </ul>
 */
public class WorkflowInvokerAction extends AbstractAction {

    public static final String AREA = "area";
    public static final String DOCUMENT_ID = "document-id";
    public static final String LANGUAGE = "language";
    public static final String EVENT = "event";

    /**
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws Exception {

        String area = parameters.getParameter(AREA);
        String documentId = parameters.getParameter(DOCUMENT_ID);
        String language = parameters.getParameter(LANGUAGE);
        String eventName = parameters.getParameter(EVENT);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(getClass().getName() + " invoked.");
            getLogger().debug("    Area:        [" + area + "]");
            getLogger().debug("    Document ID: [" + documentId + "]");
            getLogger().debug("    Language:    [" + language + "]");
            getLogger().debug("    Event:       [" + eventName + "]");
        }

        PageEnvelope envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        Publication publication = envelope.getPublication();
        DocumentBuilder builder = publication.getDocumentBuilder();
        String url = builder.buildCanonicalUrl(publication, area, documentId, language);
        Document document = builder.buildDocument(publication, url);

        WorkflowFactory factory = WorkflowFactory.newInstance();

        if (factory.hasWorkflow(document)) {

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Invoking workflow event");
            }

            SynchronizedWorkflowInstances instance = factory.buildSynchronizedInstance(document);
            Situation situation = WorkflowHelper.buildSituation(objectModel);
            Event[] events = instance.getExecutableEvents(situation);
            Event event = null;

            for (int i = 0; i < events.length; i++) {
                if (events[i].getName().equals(eventName)) {
                    event = events[i];
                }
            }

            assert event != null;
            instance.invoke(situation, event);
        } else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Document has no workflow.");
            }
        }

        return Collections.EMPTY_MAP;
    }

}
