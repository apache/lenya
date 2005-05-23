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

/* $Id$  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publishing.ParentNodeNotFoundException;
import org.apache.lenya.cms.publishing.PublishingException;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to publish the tree, adding a node for the new published document in the live tree
 */
public class TreePublisher extends PublicationTask {
    private String documentid;
    private String language;

    /**
     * Creates a new instance of TreePublisher
     */
    public TreePublisher() {
	    // do nothing
    }

    /**
     * Returns the document id
     * @return The document id
     */
    protected String getDocumentid() {
        return this.documentid;
    }

    /**
     * Sets the document id
     * @param _documentid The document id
     */
    public void setDocumentid(String _documentid) {
        this.documentid = _documentid;
    }

    /**
     * Get the language of the document to be published
     * @return a <code>String</code> containing the ISO string for this language, e.g. "de", "en"
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Set the language of the document to be published
     * @param string the ISO string for this language, e.g. "de", "en"
     */
    public void setLanguage(String string) {
        this.language = string;
    }

    /**
     * adds a node for the published document in the live tree
     * @param documentId The id of the published document
     * @param _language the language for which this document is to be published. Can be null if all
     *            languages are to be published.
     * @throws PublishingException if the publication failed.
     */
    public void publish(String documentId, String _language) throws PublishingException {
        SiteTree authoringTree = null;
        SiteTree liveTree = null;

        try {
            authoringTree = getSiteTree(Publication.AUTHORING_AREA);
            liveTree = getSiteTree(Publication.LIVE_AREA);

            SiteTreeNode authoringNode = authoringTree.getNode(documentId);
            SiteTreeNode[] siblings = authoringNode.getNextSiblings();
            String parentId = authoringNode.getParent().getAbsoluteId();
            SiteTreeNode sibling = null;
            String siblingDocId = null;
            for (int i = 0; i < siblings.length; i++) {
                String docId = parentId + "/" + siblings[i].getId();
                sibling = liveTree.getNode(docId);
                if (sibling != null) {
                    siblingDocId = docId;
                    break;
                }
            }

            if (_language == null) {
                // no language was specified. Simply publish the
                // node including all languages.
                try {
                    liveTree.addNode(authoringNode, siblingDocId);
                } catch (SiteException e1) {
                    throw new ParentNodeNotFoundException("Couldn't add document: " + documentId
                            + " to live tree.", e1);
                }
            } else {
                // a language was specified. Let's see if this
                // node even has an entry for the specified
                // language.
                Label label = authoringNode.getLabel(_language);
                if (label != null) {
                    // check if this node has already been
                    // published
                    SiteTreeNode liveNode = liveTree.getNode(documentId);
                    if (liveNode != null) {
                        // if the node already exists in the live
                        // tree simply insert the label in the
                        // live tree
                        liveTree.setLabel(documentId, label);
                    } else {
                        // if the node doesn't exist, add it and
                        // add the specified label to it.
                        Label[] labels = { label };
                        try {
                            liveTree.addNode(documentId, labels, authoringNode.getHref(),
                                    authoringNode.getSuffix(), authoringNode.hasLink(),
                                    siblingDocId);
                        } catch (SiteException e1) {
                            throw new ParentNodeNotFoundException("Couldn't add document: "
                                    + documentId + " to live tree.", e1);
                        }
                    }
                } else {
                    // the node that we're trying to publish
                    // doesn't have this language
                    throw new PublishingException("The node " + documentId
                            + " doesn't contain a label for language " + _language);
                }
            }
        } catch (final Exception e) {
            throw new PublishingException("Couldn't publish to live tree :", e);
        }
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("document id: " + getDocumentid());
            log("language: " + getLanguage());

            publish(getDocumentid(), getLanguage());
        } catch (final PublishingException e) {
            log("" +e.toString());
            throw new BuildException(e);
        }
    }
}