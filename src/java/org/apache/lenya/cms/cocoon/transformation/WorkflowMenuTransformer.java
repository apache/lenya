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

/* $Id$  */

package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.workflow.WorkflowResolver;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This transformer disables menu items (by removing the href attribute) which are not allowed with
 * respect to the current workflow state.
 */
public class WorkflowMenuTransformer extends AbstractSAXTransformer {
    /**
     * <code>MENU_ELEMENT</code> The menu element
     */
    public static final String MENU_ELEMENT = "menu";
    /**
     * <code>ITEM_ELEMENT</code> The item element
     */
    public static final String ITEM_ELEMENT = "item";
    /**
     * <code>EVENT_ATTRIBUTE</code> The event attribute
     */
    public static final String EVENT_ATTRIBUTE = "event";

    /**
     * (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String,
     *      java.lang.String, org.xml.sax.Attributes)
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
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver,
     *      java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver _resolver, Map _objectModel, String src, Parameters _parameters)
            throws ProcessingException, SAXException, IOException {

        super.setup(_resolver, _objectModel, src, _parameters);
        
        PageEnvelope envelope = null;
        WorkflowResolver workflowResolver = null;

        try {
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            Publication pub = factory.getPublication(_objectModel);
            DocumentIdentityMap map = new DocumentIdentityMap(pub);
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(map, _objectModel);

            Document document = envelope.getDocument();
            workflowResolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);

            setHasWorkflow(workflowResolver.hasWorkflow(document));

            if (hasWorkflow()) {
                Situation situation = null;

                try {
                    setInstance(workflowResolver.getSynchronizedInstance(document));
                    situation = workflowResolver.getSituation();
                } catch (Exception e) {
                    throw new ProcessingException(e);
                }

                try {
                    this.events = getInstance().getExecutableEvents(situation);

                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Executable events: ");
                        for (int i = 0; i < this.events.length; i++) {
                            getLogger().debug("    [" + this.events[i] + "]");
                        }
                    }

                } catch (final WorkflowException e) {
                    throw new ProcessingException(e);
                }
            }
        } catch (final ServiceException e) {
            throw new ProcessingException(e);
        } catch (final ProcessingException e) {
            throw new ProcessingException(e);
        } catch (final PublicationException e) {
            throw new ProcessingException(e);
        } catch (final PageEnvelopeException e) {
            throw new ProcessingException(e);
        }
        finally {
            if (workflowResolver != null) {
                this.manager.release(workflowResolver);
            }
        }

    }

    private boolean hasWorkflow;
    private SynchronizedWorkflowInstances instance;

    /**
     * Get the workflow instance.
     * @return a <code>WorkflowInstance</code>
     */
    protected SynchronizedWorkflowInstances getInstance() {
        return this.instance;
    }

    private String[] events;

    /**
     * Returns if the events contain a specific event.
     * @param eventName The name of the event to check for.
     * @return A boolean value.
     */
    protected boolean containsEvent(String eventName) {
        boolean result = false;

        for (int i = 0; i < this.events.length; i++) {
            if (this.events[i].equals(eventName)) {
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
        return this.hasWorkflow;
    }

    /**
     * Sets if the current document has a workflow.
     * @param _hasWorkflow A boolean value.
     */
    public void setHasWorkflow(boolean _hasWorkflow) {
        this.hasWorkflow = _hasWorkflow;
    }

    /**
     * Sets the workflow instance for the current request.
     * @param _instance A workflow instance.
     */
    public void setInstance(SynchronizedWorkflowInstances _instance) {
        this.instance = _instance;
    }
}