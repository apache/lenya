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

/* $Id: MoveSiteTreeNodeTask.java,v 1.2 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.Publication;
import org.apache.tools.ant.BuildException;

/**
 * Task to move a node amongst the siblings
 */
public class MoveSiteTreeNodeTask extends PublicationTask {

    public static final String UP = "up";
    public static final String DOWN = "down";

    /**
     * Returns the document ID.
     * @return A string.
     */
    protected String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the document ID.
     * @param documentId A string.
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Returns the direction.
     * @return A string.
     */
    protected String getDirection() {
        return direction;
    }

    /**
     * Sets the direction.
     * @param direction A string.
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        
        log("Moving sitetree node:");
        log("    Document ID: [" + getDocumentId() + "]");
        log("    Direction:   [" + getDirection() + "]");

        try {
            DefaultSiteTree tree = getPublication().getSiteTree(Publication.AUTHORING_AREA);
            if (getDirection().equals(UP)) {
                tree.moveUp(getDocumentId());
            } else if (getDirection().equals(DOWN)) {
                tree.moveDown(getDocumentId());
            } else {
                throw new BuildException(
                    "The direction in which the node should be moved isn't specified.");
            }
            tree.save();
        } catch (BuildException e) {
            throw e;
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
    
    private String direction;
    private String documentId;

}