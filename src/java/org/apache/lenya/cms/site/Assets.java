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
package org.apache.lenya.cms.site;

import java.util.Map;
import org.apache.cocoon.servlet.multipart.Part;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.site.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to Assets a resource.
 * 
 * @version $Id$
 */
public class Assets extends SiteUsecase {
	
	private ResourcesManager resourcesManager = null;

	/**
     * Ctor.
     */
    public Assets() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
     */
    protected void doInitialize() {
        super.doInitialize();
        resourcesManager = getSourceDocument().getResourcesManager();
    }
            
     /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {
        String title = getParameterAsString("title");
        String creator = getParameterAsString("creator");
        String rights = getParameterAsString("rights");

        if (title.length() == 0) {
            addErrorMessage("Please enter a title.");
        }

   }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        String title = getParameterAsString("title");
        String creator = getParameterAsString("creator");
        String rights = getParameterAsString("rights");
        Part file = (Part)getParameter("file");

        Map metadata = null;
        metadata.put("title",title);
        metadata.put("cretor",creator);
        metadata.put("rights",rights);
        try {
        	resourcesManager.addResource(file, metadata);
        } catch (Exception e) {
            addErrorMessage("The resource could not be added.");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
    }
}