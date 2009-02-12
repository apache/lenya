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
package org.apache.lenya.cms.jcr.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Import a publication into the JCR repository.
 */
public class Import extends AbstractUsecase {

    protected static final String PUBLICATIONS = "publications";
    protected static final String PUBLICATION = "publication";

    private SourceResolver sourceResolver;

    protected void initParameters() {
        super.initParameters();
        String[] pubs = getSession().getPublicationIds();
        List pubList = Arrays.asList(pubs);
        setParameter(PUBLICATIONS, pubList);
    }

    protected void doExecute() throws Exception {
        super.doExecute();
        String pubId = getParameterAsString(PUBLICATION);
        Publication pub = getSession().getPublication(pubId);

        List nodes = new ArrayList();

        String[] areas = { Publication.AUTHORING_AREA, Publication.LIVE_AREA,
                Publication.TRASH_AREA, Publication.ARCHIVE_AREA };
        for (int i = 0; i < areas.length; i++) {
            Area area = pub.getArea(areas[i]);
            Document[] docs = area.getDocuments();
            for (int j = 0; j < docs.length; j++) {
                nodes.add(docs[j]);
                /*
                 * final String lenyaUri = docs[j].getSourceURI(); final String sourcePath =
                 * lenyaUri.substring("lenya://".length()); final String contextUri = "context://" +
                 * sourcePath + ".meta"; MetaDataManager meta = new MetaDataManager(contextUri,
                 * this.manager, getLogger()); uri2meta.put(docs[j].getSourceURI(), meta);
                 */
            }
            nodes.add(area.getSite());
        }

        for (Iterator i = nodes.iterator(); i.hasNext();) {
            Node node = (Node) i.next();
            final String lenyaUri = node.getSourceURI();
            final String sourcePath = lenyaUri.substring("lenya://".length());
            final String contextUri = "context://" + sourcePath;
            final String jcrUri = "jcr://" + sourcePath;
            SourceResolver resolver = getSourceResolver();
            if (SourceUtil.exists(contextUri, resolver)) {
                SourceUtil.copy(resolver, contextUri, jcrUri);
                /*
                 * MetaDataManager sourceMgr = (MetaDataManager) uri2meta.get(lenyaUri); if
                 * (sourceMgr != null) { MetaDataManager jcrMgr = new JCRMetaDataManager(jcrUri,
                 * this.manager, getLogger()); jcrMgr.replaceMetaData(sourceMgr); }
                 */
            } else {
                addInfoMessage("The source [" + contextUri + "] does not exist.");
            }
        }

    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    public SourceResolver getSourceResolver() {
        return sourceResolver;
    }

}
