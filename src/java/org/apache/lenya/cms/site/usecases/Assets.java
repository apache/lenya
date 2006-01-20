/*
 * Copyright  1999-2005 The Apache Software Foundation
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.cocoon.servlet.multipart.RejectedPart;

import org.apache.lenya.cms.publication.Resource;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to add Assets to a resource.
 * 
 * @version $Id$
 */
public class Assets extends SiteUsecase {

    /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {
        String title = getParameterAsString("title");

        if (title.length() == 0) {
            addErrorMessage("Please enter a title.");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        if (getParameter("delete") == null) {
            validate();
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        ResourcesManager resourcesManager = null;

        try {
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            Resource[] resources = resourcesManager.getResources(getSourceDocument());
            setParameter("assets", Arrays.asList(resources));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resourcesManager != null) {
                this.manager.release(resourcesManager);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        if (getParameter("delete") == null) {
            addAsset();
        } else {
            deleteAsset();
        }
    }

    /**
     * Deletes an asset.
     * @throws Exception if an error occurs.
     */
    protected void deleteAsset() throws Exception {
        String assetName = getParameterAsString("delete");
        ResourcesManager resourcesManager = null;
        try {

            // Retrieve the resource instance via the ResourcesManager
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            Resource theResource = resourcesManager.getResource(getSourceDocument(), assetName);
            if (theResource == null)
                throw new Exception("no such resource [" + assetName + "] exists for document [ "
                        + getSourceDocument().getId() + "]");

            // Lock the resource nodes
            List nodes = new ArrayList();
            nodes.addAll(Arrays.asList(theResource.getRepositoryNodes()));
            lockInvolvedObjects((Node[]) nodes.toArray(new Node[nodes.size()]));

            // Delete the resource
            resourcesManager.deleteResource(theResource);

        } catch (final Exception e) {
            getLogger().error("The resource could not be deleted: ", e);
            addErrorMessage("The resource could not be deleted (see log files for details).");
        } finally {
            if (resourcesManager != null) {
                this.manager.release(resourcesManager);
            }
        }
    }

    /**
     * Adds an asset. If asset upload is not enabled, an error message is added.
     */
    protected void addAsset() {

        if (getLogger().isDebugEnabled())
            getLogger().debug("Assets::addAsset() called");

        Part file = getPart("file");

        if (file.isRejected()) {
            String[] params = { Integer.toString(file.getSize()) };
            addErrorMessage("upload-size-exceeded", params);
        } else {
            addAsset(file);
        }
    }

    /**
     * Adds an asset.
     * @param file The part.
     */
    protected void addAsset(Part file) {
        String title = getParameterAsString("title");
        String creator = getParameterAsString("creator");
        String rights = getParameterAsString("rights");

        Map metadata = new HashMap();
        metadata.put("title", title);
        metadata.put("creator", creator);
        metadata.put("rights", rights);
        ResourcesManager resourcesManager = null;
        try {
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            resourcesManager.addResource(getSourceDocument(), file, metadata);
        } catch (final Exception e) {
            getLogger().error("The resource could not be added: ", e);
            addErrorMessage("The resource could not be added (see log files for details).");
        } finally {
            if (resourcesManager != null) {
                this.manager.release(resourcesManager);
            }
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug("Assets::addAsset() done.");
    }

}
