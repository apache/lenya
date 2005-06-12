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
package org.apache.lenya.defaultpub.cms.usecases.webdav;

import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.workflow.WorkflowManager;

/**
 * Supports WebDAV PUT.
 * 
 * @version $Id$
 */
public class Put extends DocumentUsecase {

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
            String path = mapper.getPath(doc.getId(), getSourceDocument().getLanguage());
            String sourceUri = doc.getSourceURI();
            String pubId = doc.getPublication().getId();
            String tempSourceUri = "cocoon:/request/PUT"; 

            SourceUtil.copy(resolver, tempSourceUri, sourceUri, true);


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
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getObjectsToLock()
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {
        return getSourceDocument().getRepositoryNodes();
    }

}
