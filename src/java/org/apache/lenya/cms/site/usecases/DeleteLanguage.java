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
package org.apache.lenya.cms.site.usecases;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.util.DocumentHelper;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.usecase.DocumentUsecase;

/**
 * Delete a language version.
 * 
 * @version $Id:$
 */
public class DeleteLanguage extends DocumentUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        
        if (getSourceDocument().getLanguages().length == 1) {
            addErrorMessage("The last language version cannot be removed.");
        }
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Document document = getSourceDocument();
        SiteManager _manager = document.getPublication().getSiteManager(document.getIdentityMap());
        _manager.delete(document);
        
        document.getFile().delete();
        
        if (hasWorkflow(document)) {
            getWorkflowInstance(document).getHistory().delete();
        }
        
        setTargetDocument(DocumentHelper.getExistingLanguageVersion(document));
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        setParameter("documentId", getSourceDocument().getId());
        setParameter("language", getSourceDocument().getLanguage());
    }
}