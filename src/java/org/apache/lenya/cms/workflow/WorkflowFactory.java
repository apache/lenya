/*
$Id: WorkflowFactory.java,v 1.15 2003/07/29 14:24:13 andreas Exp $
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

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.workflow.*;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.impl.WorkflowBuilder;

import java.io.File;

import java.util.List;
import java.util.Map;


/**
 *
 * @author andreas
 */
public class WorkflowFactory {
    public static final String WORKFLOW_DIRECTORY = "config/workflow".replace('/',
            File.separatorChar);

    /** Creates a new instance of WorkflowFactory */
    protected WorkflowFactory() {
    }

    /**
     * Returns an instance of the workflow factory.
     * @return A workflow factory.
     */
    public static WorkflowFactory newInstance() {
        return new WorkflowFactory();
    }

    /**
     * Creates a new workflow instance.
     * @param document The document to create the instance for.
     * @return A workflow instance.
     * @throws WorkflowException when something went wrong.
     */
    public WorkflowInstance buildInstance(Document document)
        throws WorkflowException {
        assert document != null;

        return new CMSHistory(document).getInstance();
    }

    /**
     * Checks if a workflow is assigned to the document.
     * This is done by looking for the workflow history file.
     * @param document The document to check.
     * @return <code>true</code> if the document has a workflow, <code>false</code> otherwise.
     */
    public boolean hasWorkflow(Document document) {
        return new CMSHistory(document).isInitialized();
    }

    /**
     * Builds a workflow for a given publication.
     * @param publication The publication.
     * @param workflowFileName The workflow definition filename.
     * @return A workflow object.
     * @throws WorkflowException when something went wrong.
     */
    protected static Workflow buildWorkflow(Publication publication, String workflowFileName)
        throws WorkflowException {
        assert publication != null;
        assert (workflowFileName != null) && !"".equals(workflowFileName);

        File workflowDirectory = new File(publication.getDirectory(), WORKFLOW_DIRECTORY);
        File workflowFile = new File(workflowDirectory, workflowFileName);
        Workflow workflow = WorkflowBuilder.buildWorkflow(workflowFile);

        return workflow;
    }

    /**
     * Creates a new workflow situation.
     * @param roles The roles of the situation.
     * @return A situation.
     * @throws WorkflowException when something went wrong.
     */
    public Situation buildSituation(Role[] roles) throws WorkflowException {
        return new CMSSituation(roles);
    }

    /**
     * Creates a situation for a Cocoon object model.
     * @param objectModel The object model.
     * @return A workflow situation.
     * @throws WorkflowException when something went wrong.
     */
    public Situation buildSituation(Map objectModel) throws WorkflowException {
        Request request = ObjectModelHelper.getRequest(objectModel);

        List roleList = (List) request.getAttribute(Role.class.getName());

        if (roleList == null) {
            throw new WorkflowException("Request does not contain roles!");
        }

        Role[] roles = (Role[]) roleList.toArray(new Role[roleList.size()]);

        return buildSituation(roles);
    }

    /**
     * Initializes the history of a document.
     * @param document The document object.
     * @param workflowId The ID of the workflow.
     * @throws WorkflowException When something goes wrong.
     */
    public static void initHistory(Document document, String workflowId)
        throws WorkflowException {
        new CMSHistory(document).initialize(workflowId);
    }
}
