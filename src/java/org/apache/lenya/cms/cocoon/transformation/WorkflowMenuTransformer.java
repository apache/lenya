/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.ac.FileUser;
import org.apache.lenya.cms.ac.Group;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.publication.DefaultDocument;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkflowMenuTransformer
    extends AbstractSAXTransformer {
    
    public static final String ITEM_ELEMENT = "item";
    public static final String EVENT_ATTRIBUTE = "event";

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String raw, Attributes attr)
        throws SAXException {
        
        boolean passed = true;
        if (localName.equals(ITEM_ELEMENT)) {
            String event = attr.getValue(EVENT_ATTRIBUTE);
            
            // filter item if command not allowed 
            if (event != null) {
                passed = false;
                AttributesImpl attributes = new AttributesImpl(attr);
                int hrefIndex = attributes.getIndex("href");
                if (!containsEvent(event)) {
                    if (hrefIndex > -1) {
                        attributes.removeAttribute(hrefIndex);
                    }
                }
                else {
                    if (hrefIndex > -1) {
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
    
    /* (non-Javadoc)
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters)
        throws ProcessingException, SAXException, IOException {
            
        Publication publication = PublicationFactory.getPublication(objectModel);
        
        PageEnvelope envelope = null;
      
        try {
            envelope = new PageEnvelope(publication, ObjectModelHelper.getRequest(objectModel));
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
      
        Group group = new Group("test-group");
        group.addRole(new Role("editor"));
      
        User user = new FileUser("testuser");
        user.addGroup(group);
        
        Document document = new DefaultDocument(publication, envelope.getDocumentId());
        
        WorkflowFactory factory = WorkflowFactory.newInstance();
        WorkflowInstance instance = null;
        Situation situation = null;
      
        try {
            instance = factory.buildInstance(document);
            situation = factory.buildSituation(user);
        }
        catch (Exception e) {
            throw new ProcessingException(e);
        }
      
        this.events = instance.getExecutableEvents(situation);
      
    }
    
    private Event events[];
    
    protected boolean containsEvent(String eventName) {
        boolean result = false;
        for (int i = 0; i < events.length; i++) {
            if (events[i].getName().equals(eventName)) {
                result = true;
            }
        }
        return result;
    }

}
