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

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.tools.ant.BuildException;


/**
 * Ant task, which implements the SiteTreeNodeVisitor for the operation init the workflow history files
 * when copying documents
 * (Visitor pattern)
 */
public class InitCopyWorkflowTask extends TwoDocumentsOperationTask {

	private String userId = "";
	private String machineIp = "";

	/**
	 * 
	 */
	public InitCopyWorkflowTask() {
		super();
	}

	/**
	 * Returns the machine IP address from which the history was initialized.
	 * @return A string.
	 */
	public String getMachineIp() {
		return this.machineIp;
	}

	/**
	 * Sets the machine IP address from which the history was initialized.
	 * @param _machineIp A string.
	 */
	public void setMachineIp(String _machineIp) {
		this.machineIp = _machineIp;
	}

	/**
	 * Returns the ID of the user who initialized the history.
	 * @return A string.
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * Sets the ID of the user who initialized the history.
	 * @param _userId A string.
	 */
	public void setUserId(String _userId) {
		this.userId = _userId;
	}

	/**
	 * @param node The node to visit
	 * @see org.apache.lenya.cms.site.tree.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.site.tree.SiteTreeNode)
	 */
	public void visitSiteTreeNode(SiteTreeNode node) {
		Label[] labels = node.getLabels(); 
		for (int i = 0 ; i < labels.length; i++){
			String language = labels[i].getLanguage();

			String srcDocumentid = node.getAbsoluteId();
			String destDocumentid = srcDocumentid.replaceFirst(this.getFirstdocumentid(),this.getSecdocumentid());

			Document document;
			Document newdocument;
			WorkflowFactory factory = WorkflowFactory.newInstance();
			
			log("init workflow history");
			try {
				document = getIdentityMap().getFactory().get(getFirstarea(), srcDocumentid, language);
				newdocument = getIdentityMap().getFactory().get(getSecarea(), destDocumentid, language);
			} catch (DocumentBuildException e) {
				throw new BuildException(e);
			}
			try {
				if (factory.hasWorkflow(document)) {
					String[] roles = new String[0];
					Situation situation =
						factory.buildSituation(roles, getUserId(), getMachineIp());
                    
                    WorkflowInstance sourceInstance = factory.buildExistingInstance(document);
                    String workflowName = sourceInstance.getWorkflow().getName();
                    WorkflowInstance destInstance = factory.buildNewInstance(newdocument, workflowName);
                    destInstance.getHistory().initialize(situation);
				}
			} catch (WorkflowException e) {
				throw new BuildException(e);
			}
		}
	}

}
