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
package org.apache.lenya.cms.workflow;

import java.io.File;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.EventImpl;
import org.apache.lenya.workflow.impl.StateImpl;
import org.apache.lenya.workflow.impl.WorkflowBuilder;
import org.apache.lenya.workflow.impl.WorkflowImpl;
import org.apache.lenya.workflow.impl.WorkflowInstanceImpl;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkflowDocument extends WorkflowInstanceImpl {

    protected WorkflowDocument(Document document) throws WorkflowException {
        assert document != null;
        this.document = document;

        DocumentType type;
        org.w3c.dom.Document xmlDocument;
        File historyFile = getHistoryFile(document);
        if (!historyFile.exists()) {
            throw new WorkflowException(
                "There is no workflow assigned to this document " +
                "(history file does not exist)!");
        }
        
        try {
            xmlDocument = DocumentHelper.readDocument(historyFile);
            String documentTypeName =
                xmlDocument.getDocumentElement().getAttribute(DOCTYPE_ATTRIBUTE);
            assert documentTypeName != null;
            type = new DocumentType(documentTypeName);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }

        WorkflowImpl workflow
            = (WorkflowImpl) WorkflowFactory.buildWorkflow(document.getPublication(), type);
        setWorkflow(workflow);

        // initialize instance state from last version element
        NamespaceHelper helper =
            new NamespaceHelper(
                WorkflowBuilder.NAMESPACE,
                WorkflowBuilder.DEFAULT_PREFIX,
                xmlDocument);
                
        Element versionElements[] = helper.getChildren(xmlDocument.getDocumentElement(), VERSION_ELEMENT);
        if (versionElements.length > 0) {
            Element lastElement = versionElements[versionElements.length - 1]; 
            String stateId = lastElement.getAttribute(STATE_ATTRIBUTE);
            State state;
            try {
                state = getState(stateId);
            } catch (WorkflowException e) {
                throw new WorkflowException(e);
            }
            setCurrentState(state);
        }
                
    }

    private Document document;

    public static final String HISTORY_PATH = "history".replace('/', File.separatorChar);

    protected static File getHistoryFile(Document document) {
        
        String documentPath = document.getId().replace('/', File.separatorChar) + ".xml";
        
        File workflowDirectory = new File(document.getPublication().getDirectory(), WorkflowFactory.WORKFLOW_DIRECTORY);
        File historyDirectory = new File(workflowDirectory, HISTORY_PATH);
        File historyFile = new File(historyDirectory, documentPath);
        return historyFile;
    }

    /**
     * Returns the document of this WorkflowDocument object.
     * @return A document object.
     */
    protected Document getDocument() {
        return document;
    }

    public static final String DOCTYPE_ATTRIBUTE = "doctype";
    public static final String VERSION_ELEMENT = "version";
    public static final String STATE_ATTRIBUTE = "state";
    public static final String USER_ATTRIBUTE = "user";
    public static final String EVENT_ATTRIBUTE = "event";

    /* (non-Javadoc)
     * @see org.apache.lenya.workflow.WorkflowInstance#invoke(org.apache.lenya.workflow.Situation, org.apache.lenya.workflow.Event)
     */
    public void invoke(Situation situation, Event event)
        throws WorkflowException {
        super.invoke(situation, event);
        
        File file = getHistoryFile(getDocument());

        try {
            org.w3c.dom.Document xmlDocument = DocumentHelper.readDocument(file);
            Element root = xmlDocument.getDocumentElement();

            NamespaceHelper helper =
                new NamespaceHelper(
                    WorkflowBuilder.NAMESPACE,
                    WorkflowBuilder.DEFAULT_PREFIX,
                    xmlDocument);
                    
            CMSSituation cmsSituation = (CMSSituation) situation;
                    
            Element versionElement = helper.createElement(VERSION_ELEMENT);
            versionElement.setAttribute(STATE_ATTRIBUTE, ((StateImpl) getCurrentState()).getId());
            versionElement.setAttribute(USER_ATTRIBUTE, cmsSituation.getUser().getId());
            versionElement.setAttribute(EVENT_ATTRIBUTE, ((EventImpl) event).getName());
            
            root.appendChild(versionElement);
            DocumentHelper.writeDocument(xmlDocument, file);
            
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }
    
    public void invoke(Situation situation, String eventName) throws WorkflowException {
        assert eventName != null;
        Event event = ((WorkflowImpl) getWorkflow()).getEvent(eventName);
        invoke (situation, event);
    }
    
}
