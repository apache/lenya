/*
$Id: CMSHistory.java,v 1.5 2003/07/09 13:44:52 egli Exp $
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
package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.History;
import org.apache.lenya.workflow.impl.StateImpl;
import org.apache.lenya.workflow.impl.WorkflowInstanceImpl;
import org.apache.lenya.xml.NamespaceHelper;

import org.w3c.dom.Element;

import java.io.File;


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
     * 
     * @param document the document to which the CMSHistory is attached
     */
    protected CMSHistory(Document document) {
        setDocument(document);
    }

    private Document document;

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.workflow.History#createVersionElement(org.apache.lenya.xml.NamespaceHelper, org.apache.lenya.workflow.impl.StateImpl, org.apache.lenya.workflow.Situation, org.apache.lenya.workflow.Event)
     */
    protected Element createVersionElement(NamespaceHelper helper, StateImpl state,
        Situation situation, Event event) {
        Element element = super.createVersionElement(helper, state, situation, event);

        // TODO: add identity

        /*
        User user = ((CMSSituation) situation).getUser();
        element.setAttribute(USER_ATTRIBUTE, user.getId());
        */
        return element;
    }
	
	/**
	 *  (non-Javadoc)
	 * @see org.apache.lenya.workflow.impl.History#getHistoryFile()
	 */
    protected File getHistoryFile() {
        String language = getDocument().getLanguage();
        String languageSuffix = "".equals(language) ? "" : ("_" + language);

        String documentPath = getDocument().getId().replace('/', File.separatorChar) +
            languageSuffix + ".xml";

        File workflowDirectory = new File(document.getPublication().getDirectory(),
                WorkflowFactory.WORKFLOW_DIRECTORY);
        File historyDirectory = new File(workflowDirectory, HISTORY_PATH);
        File historyFile = new File(historyDirectory, documentPath);

        return historyFile;
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.workflow.impl.History#createInstance()
     */
    protected WorkflowInstanceImpl createInstance() throws WorkflowException {
        return new WorkflowDocument(getDocument());
    }

    /**
     * Get the document
     * 
     * @return the Document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Set the document
     * 
     * @param document the document
     */
    public void setDocument(Document document) {
        this.document = document;
    }
}
