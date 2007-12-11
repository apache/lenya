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

/* $Id$  */

package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowManager;
import org.apache.lenya.workflow.Workflowable;
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

                    if (!this.executableEvents.contains(event)) {
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

        WorkflowManager workflowManager = null;

        try {
            Request request = ObjectModelHelper.getRequest(_objectModel);
            Session session = RepositoryUtil.getSession(this.manager, request);
            DocumentFactory map = DocumentUtil.createDocumentFactory(this.manager, session);

            String webappUrl = ServletHelper.getWebappURI(request);
            Document document = null;
            if (map.isDocument(webappUrl)) {
                document = map.getFromURL(webappUrl);
                ResourceType doctype = document.getResourceType();
                if (document.getPublication().getWorkflowSchema(doctype) != null) {
                    setHasWorkflow(true);
                    workflowManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
                } else {
                    setHasWorkflow(false);
                }
            } else {
                setHasWorkflow(false);
            }

            if (hasWorkflow()) {
                Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager,
                        session,
                        getLogger(),
                        document);
                Workflow workflow = workflowManager.getWorkflowSchema(workflowable);
                String[] events = workflow.getEvents();
                for (int i = 0; i < events.length; i++) {
                    if (workflowManager.canInvoke(workflowable, events[i])) {
                        this.executableEvents.add(events[i]);
                    }
                }

            }
        } catch (final Exception e) {
            throw new ProcessingException(e);
        } finally {
            if (workflowManager != null) {
                this.manager.release(workflowManager);
            }
        }

    }

    private boolean hasWorkflow;

    private Set executableEvents = new HashSet();

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

}