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

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.cocoon.servlet.multipart.Part;

import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.xml.DocumentHelper;

/**
 * Usecase to add Assets to a resource.
 * 
 * @version $Id: Assets.java 123984 2005-01-03 15:02:18Z andreas $
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
        ResourcesManager resourcesManager = getSourceDocument().getResourcesManager();
        File[] resources = resourcesManager.getResources();

        if (resources != null) {
            Map[] assets = new Map[resources.length];

            for (int i = 0; i < resources.length; i++) {
                Map asset = new HashMap();
                String title = "";
                String format = "";
                org.w3c.dom.Document metaDoc;
                try {
                    metaDoc = DocumentHelper.readDocument(resourcesManager
                            .getMetaFile(resources[i]));
                    title = metaDoc.getElementsByTagNameNS("http://purl.org/dc/elements/1.1/",
                            "title").item(0).getChildNodes().item(0).getNodeValue();
                    format = metaDoc.getElementsByTagNameNS("http://purl.org/dc/elements/1.1/",
                            "format").item(0).getChildNodes().item(0).getNodeValue();
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
                asset.put("source", resources[i].getName());
                asset.put("title", title);
                asset.put("date", DateFormat.getDateInstance().format(new Date(resources[i]
                        .lastModified())));
                asset.put("format", format);
                asset.put("extent", new Long(resources[i].length() / 1024));
                assets[i] = asset;
            }
            setParameter("assets", assets);
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
        getSourceDocument().getResourcesManager().deleteResource(assetName);
    }

    /**
     * Adds an asset.
     */
    protected void addAsset() {
        String title = getParameterAsString("title");
        String creator = getParameterAsString("creator");
        String rights = getParameterAsString("rights");
        Part file = getPart("file");

        Map metadata = new HashMap();
        metadata.put("title", title);
        metadata.put("creator", creator);
        metadata.put("rights", rights);
        try {
            getSourceDocument().getResourcesManager().addResource(file, metadata);
        } catch (final Exception e) {
            getLogger().error("The resource could not be added: ", e);
            addErrorMessage("The resource could not be added (see log files for details).");
        }
    }

}