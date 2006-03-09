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
package org.apache.lenya.cms.editors.bxe;

import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.WorkflowManager;

/**
 * BXE usecase handler.
 * 
 * @version $Id: BXE.java 356755 2005-12-14 09:00:59Z edith $
 */
public class BXE extends DocumentUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        SourceResolver resolver = null;
        WorkflowManager wfManager = null;

        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            Document doc = getSourceDocument();
            DocumentIdToPathMapper mapper = doc.getPublication().getPathMapper();
            String path = mapper.getPath(doc.getId(), doc.getLanguage(), doc.getSourceExtension());
            String sourceUri = doc.getSourceURI();
            String pubId = doc.getPublication().getId();
            String tempSourceUri = "context://lenya/pubs/" + pubId + "/work/bxe/content/"
                    + doc.getArea() + "/" + path + ".tmp";
            tempSourceUri = tempSourceUri.substring("lenya://".length());
            tempSourceUri = "context://" + tempSourceUri;

            if (SourceUtil.exists(tempSourceUri, this.manager)) {
                SourceUtil.copy(resolver, tempSourceUri, sourceUri, true);
                SourceUtil.delete(tempSourceUri, this.manager);
                WorkflowUtil.invoke(this.manager,
                        getSession(),
                        getLogger(),
                        getSourceDocument(),
                        getEvent());
            }

        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
            if (wfManager != null) {
                this.manager.release(wfManager);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!WorkflowUtil.canInvoke(this.manager,
                getSession(),
                getLogger(),
                getSourceDocument(),
                getEvent())) {
            addErrorMessage("error-workflow-document", new String[] { getEvent(),
                    getSourceDocument().getId() });
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        Node[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

    protected String getEvent() {
        return "edit";
    }

}