/*
$Id: WorkflowMenuTransformer.java,v 1.20 2003/08/28 10:11:09 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.cocoon.transformation;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;

import java.util.Arrays;
import java.util.Map;

/**
 * This transformer disables menu items (by removing the href attribute)
 * which are not allowed with respect to the current workflow state.
 * 
 * @author andreas
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
            getLogger().debug("Event: [" + event + "]");

            // filter item if command not allowed 
            if (event != null) {
                passed = false;

                AttributesImpl attributes = new AttributesImpl(attr);
                int hrefIndex = attributes.getIndex("href");

                if (!containsEvent(event)) {
                    if (hrefIndex > -1) {
                        getLogger().debug("Removing href attribute");
                        attributes.removeAttribute(hrefIndex);
                    }
                } else {
                    getLogger().debug("Adding event to href attribute");
                    String href = attributes.getValue("href");
                    attributes.setValue(hrefIndex, href + "&lenya.event=" + event);
                }

                super.startElement(uri, localName, raw, attributes);
            }
        }

        if (passed) {
            super.startElement(uri, localName, raw, attr);
        }

        if (hasWorkflow() && localName.equals(MENU_ELEMENT)) {
            String prefix = "";

            if (raw.indexOf(":") != -1) {
                prefix = raw.substring(0, raw.indexOf(":")) + ":";
            }

            super.startElement(
                uri,
                "workflow-state",
                prefix + "workflow-state",
                new AttributesImpl());

            char[] characters = instance.getCurrentState().toString().toCharArray();
            super.characters(characters, 0, characters.length);
            super.endElement(uri, "workflow-state", prefix + "workflow-state");
            
            if (Arrays.asList(instance.getWorkflow().getVariableNames()).contains("is-live")) {
                super.startElement(
                    uri,
                    "is-live",
                    prefix + "is-live",
                    new AttributesImpl());

                try {
                    characters = Boolean.toString(instance.getValue("is_live")).toCharArray();
                } catch (WorkflowException e) {
                    throw new SAXException(e);
                }
                super.characters(characters, 0, characters.length);
                super.endElement(uri, "is-live", prefix + "is-live");
            }
            
        }
    }

    /** (non-Javadoc)
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
                setInstance(factory.buildInstance(document));
                situation = factory.buildSituation(objectModel);
            } catch (Exception e) {
                throw new ProcessingException(e);
            }

            this.events = getInstance().getExecutableEvents(situation);
        }
    }

    private boolean hasWorkflow;
    private WorkflowInstance instance;

    /**
     * Get the workflow instance.
     * 
     * @return a <code>WorkflowInstance</code>
     */
    protected WorkflowInstance getInstance() {
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
    public void setInstance(WorkflowInstance instance) {
        this.instance = instance;
    }
}
