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
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowBuildException;
import org.apache.lenya.workflow.impl.WorkflowBuilder;
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

    protected WorkflowDocument(Document document) throws WorkflowBuildException {
        assert document != null;
        this.document = document;

        DocumentType type;
        org.w3c.dom.Document xmlDocument;
        try {
            File historyFile = getHistoryFile(document);
            xmlDocument = DocumentHelper.readDocument(historyFile);
            String documentTypeName =
                xmlDocument.getDocumentElement().getAttribute(DOCTYPE_ATTRIBUTE);
            assert documentTypeName != null;
            type = new DocumentType(documentTypeName);
        } catch (Exception e) {
            throw new WorkflowBuildException(e);
        }

        this.workflow = WorkflowFactoryImpl.buildWorkflow(document.getPublication(), type);

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
            State state = getState(stateId);
            setCurrentState(state);
        }
                
    }

    private Document document;

    public static final String HISTORY_PATH = "history".replace('/', File.separatorChar);

    protected static File getHistoryFile(Document document) {
        
        String documentPath = document.getId().replace('/', File.separatorChar) + ".xml";
        
        File workflowDirectory = new File(document.getPublication().getDirectory(), WorkflowFactoryImpl.WORKFLOW_DIRECTORY);
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

    private Workflow workflow;

    public static final String DOCTYPE_ATTRIBUTE = "doctype";
    public static final String VERSION_ELEMENT = "version";
    public static final String STATE_ATTRIBUTE = "state";

}
