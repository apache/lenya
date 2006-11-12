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
package org.apache.lenya.cms.site.usecases;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Usecase to move a subsite to another area.
 * 
 * @version $Id:$
 */
public abstract class MoveSubsite extends DocumentUsecase {

    /**
     * @return The possible source areas.
     */
    protected abstract String[] getSourceAreas();

    /**
     * @return The target area.
     */
    protected abstract String getTargetArea();

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (hasErrors()) {
            return;
        }

        List sourceAreas = Arrays.asList(getSourceAreas());
        if (!sourceAreas.contains(getSourceDocument().getArea())) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        } else {

            Document document = getSourceDocument();

            NodeSet subsite = SiteUtil.getSubSite(this.manager, document.getLink().getNode());
            Document[] docs = subsite.getDocuments();
            for (int i = 0; i < docs.length; i++) {
                if (docs[i].existsAreaVersion(Publication.LIVE_AREA)) {
                    Document liveVersion = docs[i].getAreaVersion(Publication.LIVE_AREA);
                    addErrorMessage("delete-doc-live", new String[] { liveVersion.toString() });
                }
                if (!WorkflowUtil.canInvoke(this.manager,
                        getSession(),
                        getLogger(),
                        docs[i],
                        getEvent())) {
                    addErrorMessage("The workflow event [" + getEvent() + "] cannot be invoked on document [" + docs[i]
                            + "].");
                }
            }
        }
    }

    /**
     * @return The workflow event.
     */
    protected abstract String getEvent();

    /**
     * Lock all source documents and the site structure repository nodes because changes to the site
     * structure would compromise the operation.
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        try {

            Set nodes = new HashSet();

            SiteStructure sourceSite = getSourceDocument().area().getSite();
            SiteStructure targetSite = getSourceDocument().getPublication()
                    .getArea(getTargetArea())
                    .getSite();

            nodes.add(sourceSite.getRepositoryNode());
            nodes.add(targetSite.getRepositoryNode());

            Document[] docs = SiteUtil.getSubSite(this.manager,
                    getSourceDocument().getLink().getNode()).getDocuments();
            for (int i = 0; i < docs.length; i++) {
                nodes.add(docs[i].getRepositoryNode());
            }

            return (Node[]) nodes.toArray(new Node[nodes.size()]);
        } catch (PublicationException e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {

        String targetAreaName = getTargetArea();
        Document doc = getSourceDocument();
        Document[] sources = SiteUtil.getSubSite(this.manager, doc.getLink().getNode())
                .getDocuments();
        Area targetArea = doc.getPublication().getArea(targetAreaName);

        DocumentLocator targetLoc = doc.getLocator().getAreaVersion(targetAreaName);
        targetLoc = SiteUtil.getAvailableLocator(this.manager, getDocumentFactory(), targetLoc);

        for (int i = 0; i < sources.length; i++) {
            WorkflowUtil.invoke(this.manager,
                    getSession(),
                    getLogger(),
                    sources[i],
                    getEvent(),
                    true);
        }

        DocumentManager docManager = null;
        try {
            docManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            docManager.moveAll(doc.area(), doc.getPath(), targetArea, targetLoc.getPath());

        } finally {
            if (docManager != null) {
                this.manager.release(docManager);
            }
        }

        setTargetDocument(doc.getAreaVersion(targetAreaName));

    }

}