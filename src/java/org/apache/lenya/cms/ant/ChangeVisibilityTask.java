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

/* $Id: ChangeVisibilityTask.java 42616 2004-09-22 12:56:33Z jaf $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.publication.SiteTreeNodeImpl;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to change the visbility of a node in the navigation.
 */
public class ChangeVisibilityTask extends PublicationTask {
    private String documentid;
    private String area;

    /**
     * Creates a new instance of InsertLabelTask
     */
    public ChangeVisibilityTask() {
        super();
    }

    /**
     * Get the area of the site tree.
     * 
     * @return  the area of the tree.
     */
    protected String getArea() {
        return area;
    }

    /**
     * Set the area.
     * 
     * @param area the area.
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * Return the document-id corresponding to the node to delete.
     * 
     * @return string The document-id.
     */
    protected String getDocumentid() {
        return documentid;
    }

    /**
     * Set the value of the document-id corresponding to the node to delete.
     * 
     * @param string The document-id.
     */
    public void setDocumentid(String string) {
        documentid = string;
    }

    /**
     * Change the visibility of an existing node in the tree.
     * 
     * @param documentid the document-id of the document.
     * @param area determines in which sitetree the label is to be renamed
     * 
     * @throws SiteTreeException if an error occurs.
     */
    public void changeVisibility(
        String documentid,
        String area)
        throws SiteTreeException, DocumentException {

        DefaultSiteTree tree = null;
        tree = getPublication().getSiteTree(area);
        SiteTreeNode node = tree.getNode(documentid);

        if (node == null) {
            throw new DocumentException(
                "Document-id " + documentid + " not found.");
        }
 
        //if node is visible change to fale and vice versa
        String visibility = "false";
        if (!node.visibleInNav()) visibility = "true";
        node.setNodeAttribute(SiteTreeNodeImpl.VISIBLEINNAV_ATTRIBUTE_NAME, visibility);        

        tree.save();
    }

    /** (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("document-id corresponding to the node: " + getDocumentid());
            log("area: " + getArea());
            changeVisibility(
                getDocumentid(),
                getArea());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
