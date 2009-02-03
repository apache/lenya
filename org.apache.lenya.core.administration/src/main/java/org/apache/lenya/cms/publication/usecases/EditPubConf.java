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
package org.apache.lenya.cms.publication.usecases;

import java.io.File;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Edit the settings of the current publication.
 */
public class EditPubConf extends AbstractUsecase {

    private static final String PUB_NAME = "name";
    private static final String PUB = "publication";
    private static final String CONTENT_DIR = "contentDir";

    protected void prepareView() throws Exception {
        super.prepareView();
        Publication pub = getPublication();
        setParameter(PUB, pub);
        setParameter(PUB_NAME, pub.getName());
        setParameter(CONTENT_DIR, pub.getContentDir());
        Boolean[] booleans = { Boolean.FALSE, Boolean.TRUE };
        String[] areas = pub.getAreaNames();
        for (int b = 0; b < booleans.length; b++) {
            for (int a = 0; a < areas.length; a++) {
                boolean ssl = booleans[b].booleanValue();
                String name = getProxyParameterName(areas[a], ssl);
                setParameter(name, pub.getProxy(areas[a], ssl).getUrl());
            }
        }
    }

    protected String getProxyParameterName(String area, boolean ssl) {
        return "proxy-" + area + "-" + ssl;
    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        Publication pub = getPublication();
        String name = getParameterAsString(PUB_NAME);
        if (name == null || name.trim().equals("")) {
            addErrorMessage("invalid-publication-name");
        }
        String contentPath = getParameterAsString(CONTENT_DIR).trim();
        if (contentPath.equals("")) {
            addErrorMessage("content-dir-missing");
        } else {
            File contentDir;
            if (contentPath.startsWith("/")) {
                contentDir = new File(contentPath);
            } else {
                contentDir = new File(pub.getDirectory(), contentPath);
            }
            if (!contentDir.isDirectory()) {
                addErrorMessage("content-dir-does-not-exist");
            }
        }
    }

    protected void doExecute() throws Exception {
        super.doExecute();
        Publication pub = getPublication();

        String name = getParameterAsString(PUB_NAME);
        pub.setName(name);

        Boolean[] booleans = { Boolean.FALSE, Boolean.TRUE };
        String[] areas = pub.getAreaNames();
        for (int b = 0; b < booleans.length; b++) {
            for (int a = 0; a < areas.length; a++) {
                boolean ssl = booleans[b].booleanValue();
                String paramName = getProxyParameterName(areas[a], ssl);
                String url = getParameterAsString(paramName);
                pub.getProxy(areas[a], ssl).setUrl(url);
            }
        }
        pub.saveConfiguration();
    }

    protected Publication getPublication() throws PublicationException {
        URLInformation info = new URLInformation(getSourceURL());
        String pubId = info.getPublicationId();
        Publication pub = getDocumentFactory().getPublication(pubId);
        return pub;
    }

}
