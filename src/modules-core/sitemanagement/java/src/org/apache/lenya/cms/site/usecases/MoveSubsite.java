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
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.cms.workflow.usecases.UsecaseWorkflowHelper;
import org.apache.lenya.util.Assert;
import org.apache.lenya.workflow.Workflowable;

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
                UsecaseWorkflowHelper.checkWorkflow(this.manager, this, getEvent(), docs[i],
                        getLogger());
            }
        }
    }

    /**
     * @return The workflow event.
     */
    protected abstract String getEvent();

    /**
     * Lock all source documents and the site structure repository nodes because
     * changes to the site structure would compromise the operation.
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        Set nodes = new HashSet();
  
        if(getSourceDocument() != null) {
            try {

                SiteStructure sourceSite = getSourceDocument().area().getSite();
                SiteStructure targetSite = getSourceDocument().getPublication()
                        .getArea(getTargetArea()).getSite();

                nodes.add(sourceSite.getRepositoryNode());
                nodes.add(targetSite.getRepositoryNode());

                Document[] docs = SiteUtil.getSubSite(this.manager,
                        getSourceDocument().getLink().getNode()).getDocuments();
                for (int i = 0; i < docs.length; i++) {
                    nodes.add(docs[i].getRepositoryNode());
                }

            } catch (PublicationException e) {
                throw new UsecaseException(e);
            }
        }
 
        return (Node[]) nodes.toArray(new Node[nodes.size()]);
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
            WorkflowUtil.invoke(this.manager, getSession(), getLogger(), sources[i], getEvent(),
                    true);
            
            if (this.getClass().getName().equals(Restore.class.getName())) {
                Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager, getSession(),
                        getLogger(), sources[i]);
                String state = workflowable.getLatestVersion().getState();
                if (!state.equals("authoring")) {
                    addErrorMessage("The state is [" + state + "] instead of [authoring]!");
                }
            }
            
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

    public String getTargetURL(boolean success) {
        if (getTargetArea().equals(Publication.AUTHORING_AREA)) {
            return super.getTargetURL(success);
        }

        String url;
        if (!success) {
            url = getSourceURL();
        } else {
            try {
                Document document = getTargetDocument(success);
                SiteStructure site = document.getPublication().getArea(Publication.AUTHORING_AREA).getSite();
                return getTargetUrl(site, document.getPath()) + getExitQueryString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return url + getExitQueryString();
    }

    protected static String getTargetUrl(SiteStructure site, String path) throws SiteException {
        if (path.length() == 0) {
            return "/" + site.getPublication().getId() + "/" + site.getArea() + "/";
        } else if (site.contains(path)) {
            SiteNode node = site.getNode(path);
            if (node.getLanguages().length > 0) {
                Link link;
                String defaultLanguage = site.getPublication().getDefaultLanguage();
                if (node.hasLink(defaultLanguage)) {
                    link = node.getLink(defaultLanguage);
                }
                else {
                    link = node.getLink(node.getLanguages()[0]);
                }
                return link.getDocument().getCanonicalWebappURL();
            }
            else {
                return getTargetUrl(site, getParentPath(path));
            }
        } else {
            return getTargetUrl(site, getParentPath(path));
        }
    }

    protected static String getParentPath(String path) {
        Assert.notNull("path", path);
        Assert.isTrue("not empty", path.length() > 0);
        return path.substring(0, path.lastIndexOf("/"));
    }

}
