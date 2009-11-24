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
package org.apache.lenya.cms.site.usecases;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.cms.workflow.usecases.UsecaseWorkflowHelper;

/**
 * Switch the navigation visibility of a document.
 */
public class ChangeVisibility extends DocumentUsecase {

    protected String getEvent() {
        return "edit";
    }

    public Object getParameter(String name) {
        if (name.equals(Usecase.PARAMETER_ITEM_STATE)) {
            Document doc = getSourceDocument();
            try {
                return doc != null ? Boolean.valueOf(doc.getLink().getNode().isVisible()) : null;
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            return super.getParameter(name);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (hasErrors()) {
            return;
        }

        Document doc = getSourceDocument();
        if (!getSourceDocument().getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        }

        String[] languages = doc.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            Document version = doc.getTranslation(languages[i]);
            UsecaseWorkflowHelper.checkWorkflow(this.manager, this, getEvent(), version,
                    getLogger());
        }
    }

    protected void doExecute() throws Exception {
        super.doExecute();
        Document doc = getSourceDocument();
        SiteNode node = doc.getLink().getNode();
        node.setVisible(!node.isVisible());

        String[] languages = doc.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            Document version = doc.getTranslation(languages[i]);
            WorkflowUtil.invoke(this.manager, getSession(), getLogger(), version, getEvent());
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        List nodes = new ArrayList();
				try {				
					  SiteNode node = getSourceDocument().getLink().getNode();
					  String[] languages = node.getLanguages();
					  for (int l = 0; l < languages.length; l++) {
						    Document doc = node.getLink(languages[l]).getDocument();
                nodes.add(doc.getRepositoryNode());
					  }
				} catch (Exception e) {
            throw new UsecaseException(e);
        }
        return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }

}
