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

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Nudge a document one position up or down.
 * 
 * @version $Id: Nudge.java 179568 2005-06-02 09:27:26Z jwkaltz $
 */
public class Nudge extends DocumentUsecase {

    protected static final String MESSAGE_AREA = "nudge-error-area";
    protected static final String MESSAGE_DIRECTION = "nudge-error-direction";
    protected static final String MESSAGE_DIRECTION_UNKNOWN = "nudge-error-direction-unknown";
    protected static final String MESSAGE_ISLIVE = "nudge-error-islive";
    protected static final String DIRECTION = "direction";
    protected static final String UP = "up";
    protected static final String DOWN = "down";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        if (hasErrors()) {
            return;
        }

        Document doc = getSourceDocument();
        SiteStructure liveSite = doc.getPublication().getArea(Publication.LIVE_AREA).getSite();
        if (liveSite.contains(doc.getPath())) {
            addErrorMessage(MESSAGE_ISLIVE);
        }

        Publication publication = doc.getPublication();

        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            SiteStructure structure = siteManager.getSiteStructure(doc.getFactory(), publication,
                    doc.getArea());
            if (structure instanceof SiteTree) {

                SiteTreeNode node = (SiteTreeNode) doc.getLink().getNode();
                SiteTreeNode[] siblings = null;

                String direction = getParameterAsString(DIRECTION);
                if (direction.equals(UP)) {
                    siblings = node.getPrecedingSiblings();
                } else if (direction.equals(DOWN)) {
                    siblings = node.getNextSiblings();
                } else {
                    addErrorMessage(MESSAGE_DIRECTION_UNKNOWN, new String[] { direction });
                }

                if (siblings != null && siblings.length == 0) {
                    addErrorMessage(MESSAGE_DIRECTION);
                }
            } else {
                addErrorMessage(MESSAGE_AREA);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        Node[] nodes = new Node[0];
        if(getSourceDocument() != null) {
            Node node = getSourceDocument().area().getSite().getRepositoryNode();
            nodes = new Node[] { node };
        }
        return nodes;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Publication publication = getSourceDocument().getPublication();
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            SiteStructure structure = siteManager.getSiteStructure(
                    getSourceDocument().getFactory(), publication, getSourceDocument().getArea());
            if (structure instanceof SiteTree) {

                SiteTree tree = (SiteTree) structure;
                String direction = getParameterAsString(DIRECTION);
                if (direction.equals(UP)) {
                    tree.moveUp(getSourceDocument().getPath());
                } else if (direction.equals(DOWN)) {
                    tree.moveDown(getSourceDocument().getPath());
                } else {
                    throw new IllegalArgumentException("The direction [" + direction
                            + "] is not supported.");
                }
            } else {
                throw new IllegalStateException("This operation is only supported for site trees.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }

    }
}
