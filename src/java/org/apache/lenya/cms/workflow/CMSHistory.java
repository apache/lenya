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

import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.History;
import org.apache.lenya.workflow.impl.StateImpl;
import org.apache.lenya.workflow.impl.WorkflowInstanceImpl;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CMSHistory extends History {

    public static final String HISTORY_PATH = "history".replace('/', File.separatorChar);

    /**
     * Creates a new CMSHistory object.
     */
    protected CMSHistory(Document document) {
        setDocument(document);
    }
    
    private Document document;

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.workflow.History#createVersionElement(org.apache.lenya.xml.NamespaceHelper, org.apache.lenya.workflow.impl.StateImpl, org.apache.lenya.workflow.Situation, org.apache.lenya.workflow.Event)
     */
    protected Element createVersionElement(
        NamespaceHelper helper,
        StateImpl state,
        Situation situation,
        Event event) {
        
        Element element = super.createVersionElement(helper, state, situation, event);
        User user = ((CMSSituation) situation).getUser();
        element.setAttribute(USER_ATTRIBUTE, user.getId());
        return element;
    }
    
    protected File getHistoryFile() {
        String documentPath = getDocument().getId().replace('/', File.separatorChar) + ".xml";

        File workflowDirectory =
            new File(document.getPublication().getDirectory(), WorkflowFactory.WORKFLOW_DIRECTORY);
        File historyDirectory = new File(workflowDirectory, HISTORY_PATH);
        File historyFile = new File(historyDirectory, documentPath);
        
        return historyFile;
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.workflow.impl.History#createInstance()
     */
    protected WorkflowInstanceImpl createInstance() throws WorkflowException {
        return new WorkflowDocument(getDocument());
    }

    /**
     * @return
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @param document
     */
    public void setDocument(Document document) {
        this.document = document;
    }

}
