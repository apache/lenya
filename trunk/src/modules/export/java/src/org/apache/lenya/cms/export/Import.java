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
package org.apache.lenya.cms.export;

import java.io.File;

import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Import content.
 */
public class Import extends AbstractUsecase {

    protected void initParameters() {
        super.initParameters();

        Publication publication;
        try {
            publication = PublicationUtil.getPublicationFromUrl(this.manager, getDocumentFactory(),
                    getSourceURL());
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
        String path = getExampleContentPath(publication);
        if (!new File(path).exists()) {
            path = getExampleContentPath(getDefaultPub());
        }
        setParameter("path", path);
    }

    protected String getExampleContentPath(Publication publication) {
        return publication.getDirectory().getAbsolutePath().replace(File.separatorChar, '/')
                + "/example-content";
    }

    protected Publication getDefaultPub() {
        Publication defaultPub;
        try {
            defaultPub = getDocumentFactory().getPublication("default");
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
        return defaultPub;
    }

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        Area area = getArea();
        if (area.getDocuments().length > 0) {
            addErrorMessage("You can't import anything because this publication already contains content.");
        }
    }

    protected Area getArea() {
        String url = getSourceURL();
        URLInformation info = new URLInformation(url);
        String pubId = info.getPublicationId();
        String areaName = info.getArea();
        Area area;
        try {
            area = getDocumentFactory().getPublication(pubId).getArea(areaName);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
        return area;
    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        String path = getParameterAsString("path");
        String baseUri = "file://" + path;
        String sitetreeUri = baseUri + "/sitetree.xml";
        if (!SourceUtil.exists(sitetreeUri, this.manager)) {
            addErrorMessage("The sitetree file does not exist in this directory.");
        }
    }

    protected void doExecute() throws Exception {
        super.doExecute();
        String path = getParameterAsString("path");
        Importer importer = new Importer(this.manager, getLogger());
        importer.importContent(getDefaultPub(), getArea(), path);
    }

}
