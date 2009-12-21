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
package org.apache.lenya.cms.lucene;

import java.util.Arrays;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Index all documents of the current publication in the current area.
 */
public class IndexSite extends AbstractUsecase {

    public static final String PARAM_AREA = "area";
    public static final String PARAM_INDEX_AREA = "indexArea";
    
    public void prepareView() {
        String url = getSourceURL();
        URLInformation info = new URLInformation(url);
        String area = info.getArea();
        setParameter(PARAM_AREA, area);
        setParameter(PARAM_INDEX_AREA, "all");
    }
    
    public void doExecute() throws Exception {
        String url = getSourceURL();
        URLInformation info = new URLInformation(url);
        String pubId = info.getPublicationId();

        DocumentFactory factory = getDocumentFactory();
        Publication pub = factory.getPublication(pubId);

        String areaName = getParameterAsString(PARAM_INDEX_AREA);

        String[] areaNames = pub.getAreaNames();
        if (areaName != null && Arrays.asList(areaNames).contains(areaName)) {
            Area area = pub.getArea(areaName);
            indexArea(area);
        } else {
            for (int a = 0; a < areaNames.length; a++) {
                Area area = pub.getArea(areaNames[a]);
                indexArea(area);
            }
        }

    }

    protected void indexArea(Area area) throws ServiceException, DocumentException {
        Document[] docs = area.getDocuments();

        IndexUpdater updater = null;
        try {
            updater = (IndexUpdater) this.manager.lookup(IndexUpdater.ROLE);
            for (int i = 0; i < docs.length; i++) {
                try {
                    updater.index(getSession(), docs[i].getResourceType(), area.getPublication()
                            .getId(), area.getName(), docs[i].getUUID(), docs[i].getLanguage());
                } catch (Exception e) {
                    String message = "Error indexing document [" + docs[i].getPath() + ":"
                            + docs[i].getLanguage() + "], UUID=" + docs[i].getUUID();
                    addErrorMessage(e + ", see logfiles for more information.");
                    getLogger().error(message, e);
                }
            }
        } finally {
            if (updater != null) {
                this.manager.release(updater);
            }
        }
    }
}
