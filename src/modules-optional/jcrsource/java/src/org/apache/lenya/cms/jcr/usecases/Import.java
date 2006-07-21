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
package org.apache.lenya.cms.jcr.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Import a publication into the JCR repository.
 */
public class Import extends AbstractUsecase {

    protected static final String PUBLICATIONS = "publications";
    protected static final String PUBLICATION = "publication";

    protected void initParameters() {
        super.initParameters();
        try {
            Publication[] pubs = PublicationUtil.getPublications(this.manager);
            List pubList = Arrays.asList(pubs);
            setParameter(PUBLICATIONS, pubList);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doExecute() throws Exception {
        super.doExecute();
        String pubId = getParameterAsString(PUBLICATION);
        DocumentFactory map = getDocumentIdentityMap();
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            Publication pub = PublicationUtil.getPublication(this.manager, pubId);
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(pub.getSiteManagerHint());

            List nodes = new ArrayList();
            Map uri2meta = new HashMap();

            String[] areas = { Publication.AUTHORING_AREA, Publication.LIVE_AREA,
                    Publication.TRASH_AREA, Publication.ARCHIVE_AREA };
            for (int i = 0; i < areas.length; i++) {
                Document[] docs = siteManager.getDocuments(map, pub, areas[i]);
                for (int j = 0; j < docs.length; j++) {
                    nodes.add(docs[j].getRepositoryNode());
                    final String lenyaUri = docs[j].getSourceURI();
                    final String sourcePath = lenyaUri.substring("lenya://".length());
                    final String contextUri = "context://" + sourcePath + ".meta";
                    /*
                    MetaDataManager meta = new MetaDataManager(contextUri,
                            this.manager,
                            getLogger());
                    uri2meta.put(docs[j].getSourceURI(), meta);
                    */
                }
                nodes.add(siteManager.getSiteStructure(map, pub, areas[i]).getRepositoryNode());
            }

            for (Iterator i = nodes.iterator(); i.hasNext();) {
                Node node = (Node) i.next();
                final String lenyaUri = node.getSourceURI();
                final String sourcePath = lenyaUri.substring("lenya://".length());
                final String contextUri = "context://" + sourcePath;
                final String jcrUri = "jcr://" + sourcePath;
                if (SourceUtil.exists(contextUri, this.manager)) {
                    SourceUtil.copy(resolver, contextUri, jcrUri);
/*
                    MetaDataManager sourceMgr = (MetaDataManager) uri2meta.get(lenyaUri);
                    if (sourceMgr != null) {
                        MetaDataManager jcrMgr = new JCRMetaDataManager(jcrUri,
                                this.manager,
                                getLogger());
                        jcrMgr.replaceMetaData(sourceMgr);
                    }
                    */
                } else {
                    addInfoMessage("The source [" + contextUri + "] does not exist.");
                }
            }

        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }

    }

}
