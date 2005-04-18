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
package org.apache.lenya.cms.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.publication.Resource;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.usecase.UsecaseInvoker;

/**
 * Usecase to insert an image into a document.
 * 
 * @version $Id:$
 */
public class InsertAsset extends DocumentUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        loadResources();
    }

    protected void loadResources() {
        ResourcesManager resourcesManager = null;

        try {
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            Resource[] resources = resourcesManager.getResources(getSourceDocument());

            List selectedResources = new ArrayList();
            String mimeTypePrefix = getParameterAsString("mimeTypePrefix", "");
            for (int i = 0; i < resources.length; i++) {
                if (resources[i].getMimeType().startsWith(mimeTypePrefix)) {
                    selectedResources.add(resources[i]);
                }
            }

            setParameter("assets", selectedResources);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resourcesManager != null) {
                this.manager.release(resourcesManager);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() throws UsecaseException {
        super.advance();
        if (getParameter("file") != null) {
            UsecaseInvoker invoker = null;
            try {
                invoker = (UsecaseInvoker) this.manager.lookup(UsecaseInvoker.ROLE);
                String usecaseName = getParameterAsString("asset-usecase");
                invoker.invoke(getSourceURL(), usecaseName, getParameters());
                loadResources();
            }
            catch (Exception e) {
                throw new UsecaseException(e);
            } finally {
                if (invoker != null) {
                    this.manager.release(invoker);
                }
            }
        }
    }

}