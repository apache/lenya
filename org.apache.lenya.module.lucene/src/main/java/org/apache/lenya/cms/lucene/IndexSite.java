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

import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Index all documents of the current publication in the current area.
 */
public class IndexSite extends AbstractUsecase {

    private IndexUpdater indexUpdater;

    public void doExecute() throws Exception {
        String url = getSourceURL();
        URLInformation info = new URLInformation(url);
        String pubId = info.getPublicationId();

        Publication pub = getSession().getPublication(pubId);
        Area area = pub.getArea(info.getArea());

        Document[] docs = area.getDocuments();

        for (int i = 0; i < docs.length; i++) {
            try {
                getIndexUpdater().index((Session) getSession(),
                        docs[i].getResourceType(), pubId, area.getName(), docs[i].getUUID(),
                        docs[i].getLanguage());
            } catch (Exception e) {
                String message = "Error indexing document [" + docs[i].getPath() + ":"
                        + docs[i].getLanguage() + "], UUID=" + docs[i].getUUID();
                addErrorMessage(e + ", see logfiles for more information.");
                getLogger().error(message, e);
            }
        }
    }

    /**
     * TODO: Bean wiring
     */
    public void setIndexUpdater(IndexUpdater indexUpdater) {
        this.indexUpdater = indexUpdater;
    }

    public IndexUpdater getIndexUpdater() {
        return indexUpdater;
    }
}
