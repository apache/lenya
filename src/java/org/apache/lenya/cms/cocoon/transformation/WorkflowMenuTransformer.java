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

/* $Id: WorkflowMenuTransformer.java,v 1.30 2004/03/01 16:18:20 gregor Exp $  */

package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.cocoon.workflow.WorkflowHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This transformer disables menu items (by removing the href attribute)
 * which are not allowed with respect to the current workflow state.
 */
public class WorkflowMenuTransformer extends AbstractSAXTransformer {
    public static final String MENU_ELEMENT = "menu";
    public static final String ITEM_ELEMENT = "item";
    public static final String EVENT_ATTRIBUTE = "event";

    /** (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String raw, Attributes attr)
        throws SAXException {
        boolean passed = true;

        if (hasWorkflow() && localName.equals(ITEM_ELEMENT)) {
            String event = attr.getValue(Workflow.NAMESPACE, EVENT_ATTRIBUTE);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Event: [" + event + "]");
            }

            // filter item if command not allowed 
            if (event != null) {
                passed = false;

                AttributesImpl attributes = new AttributesImpl(attr);

                int hrefIndex = attributes.getIndex("href");
                if (hrefIndex > -1) {

                    if (!containsEvent(event)) {
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("Removing href attribute");
                        }
                        attributes.removeAttribute(hrefIndex);
                    } else {
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("Adding event to href attribute");
                        }
                        String href = attributes.getValue("href");
                        attributes.setValue(hrefIndex, href + "&lenya.event=" + event);
                    }

                }

                super.startElement(uri, localName, raw, attributes);
            }
        }

        if (passed) {
            super.startElement(uri, localName, raw, attr);
        }

    }

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters)
        throws ProcessingException, SAXException, IOException {

        PageEnvelope envelope = null;

        try {
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (Exception e) {
            throw new ProcessingException(e);
        }

        Document document = envelope.getDocument();
        WorkflowFactory factory = WorkflowFactory.newInstance();

        setHasWorkflow(factory.hasWorkflow(document));

        if (hasWorkflow()) {
            Situation situation = null;

            try {
                setInstance(factory.buildSynchronizedInstance(document));
                situation = WorkflowHelper.buildSituation(objectModel);
            } catch (Exception e) {
                throw new ProcessingException(e);
            }

            try {
                this.events = getInstance().getExecutableEvents(situation);

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Executable events: ");
                    for (int i = 0; i < events.length; i++) {
                        getLogger().debug("    [" + events[i] + "]");
                    }
                }

            } catch (WorkflowException e) {
                throw new ProcessingException(e);
            }
        }
    }

    private boolean hasWorkflow;
    private SynchronizedWorkflowInstances instance;

    /**
     * Get the workflow instance.
     * 
     * @return a <code>WorkflowInstance</code>
     */
    protected SynchronizedWorkflowInstances getInstance() {
        return instance;
    }

    private Event[] events;

    /**
     * Returns if the events contain a specific event.
     * @param eventName The name of the event to check for.
     * @return A boolean value.
     */
    protected boolean containsEvent(String eventName) {
        boolean result = false;

        for (int i = 0; i < events.length; i++) {
            if (events[i].getName().equals(eventName)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Returns if the current document has a workflow.
     * @return A boolean value.
     */
    protected boolean hasWorkflow() {
        return hasWorkflow;
    }

    /**
     * Sets if the current document has a workflow.
     * @param hasWorkflow A boolean value.
     */
    public void setHasWorkflow(boolean hasWorkflow) {
        this.hasWorkflow = hasWorkflow;
    }

    /**
     * Sets the workflow instance for the current request.
     * @param instance A workflow instance.
     */
    public void setInstance(SynchronizedWorkflowInstances instance) {
        this.instance = instance;
    }
}
