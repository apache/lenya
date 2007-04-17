/*
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
package org.apache.lenya.cms.editors.cforms;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.workflow.usecases.InvokeWorkflow;
import org.apache.lenya.xml.DocumentHelper;

/**
 * Dummy CForms usecase.
 */
public class CForms extends InvokeWorkflow {
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        try {
            doPreparation(this.manager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        org.w3c.dom.Document xml = (org.w3c.dom.Document) getParameter("xml");
        DocumentHelper.writeDocument(xml, getSourceDocument().getOutputStream());
    }
    
    protected Node[] getNodesToLock() {
        Node[] nodes = { getSourceDocument().getRepositoryNode() };
        return nodes;
    }
    
    private void doPreparation(ServiceManager manager) {
        Request request = ContextHelper.getRequest(this.context);
        Document doc = getSourceDocument();
        String sourceUri = doc.getSourceURI();
        setParameter("sourceUri", sourceUri);
        String pubId = doc.getPublication().getId();
        setParameter("pubId", pubId);
        String host="http://"+request.getServerName()+":"+request.getServerPort()  ;
        setParameter("host", host);
    }

}
