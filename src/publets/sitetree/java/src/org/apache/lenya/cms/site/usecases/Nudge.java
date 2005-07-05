/*
 * Copyright  1999-2005 The Apache Software Foundation
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

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.transaction.Transactionable;

/**
 * Nudge a document one position up or down.
 * 
 * @version $Id: Nudge.java 179568 2005-06-02 09:27:26Z jwkaltz $
 */
public class Nudge extends DocumentUsecase {

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

        Publication publication = getSourceDocument().getPublication();

        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            SiteStructure structure = siteManager.getSiteStructure(getSourceDocument()
                    .getIdentityMap(), publication, getSourceDocument().getArea());
            if (structure instanceof SiteTree) {

                SiteTree tree = (SiteTree) structure;
                SiteTreeNode node = tree.getNode(getSourceDocument().getId());
                SiteTreeNode[] siblings = null;

                String direction = getParameterAsString(DIRECTION);
                if (direction.equals(UP)) {
                    siblings = node.getPrecedingSiblings();
                } else if (direction.equals(DOWN)) {
                    siblings = node.getNextSiblings();
                } else {
                    addErrorMessage("nudge-error-direction-unknown", new String[] { direction });
                }

                if (siblings != null && siblings.length == 0) {
                    addErrorMessage("nudge-error-direction");
                }
            } else {
                addErrorMessage("nudge-error-area");
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
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getObjectsToLock()
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {
        try {
            Node node = SiteUtil.getSiteStructure(this.manager, getSourceDocument())
                    .getRepositoryNode();
            Node[] nodes = { node };
            return nodes;
        } catch (SiteException e) {
            throw new UsecaseException(e);
        }
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
            SiteStructure structure = siteManager.getSiteStructure(getSourceDocument()
                    .getIdentityMap(), publication, getSourceDocument().getArea());
            if (structure instanceof SiteTree) {

                SiteTree tree = (SiteTree) structure;
                String direction = getParameterAsString(DIRECTION);
                if (direction.equals(UP)) {
                    tree.moveUp(getSourceDocument().getId());
                } else if (direction.equals(DOWN)) {
                    tree.moveDown(getSourceDocument().getId());
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