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

/* $Id: TreePublisher.java,v 1.13 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.publishing.ParentNodeNotFoundException;
import org.apache.lenya.cms.publishing.PublishingException;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to publish the tree, adding a node for the new published
 * document in the live tree
 */
public class TreePublisher extends PublicationTask {
    private String documentid;
    private String language;
    /**
     * Creates a new instance of TreePublisher
     */
    public TreePublisher() {}

    /**
     * Returns the document id
     *
     * @return DOCUMENT ME!
     */
    protected String getDocumentid() {
        return documentid;
    }

    /**
     * Sets the document id
     *
     * @param documentid DOCUMENT ME!
     */
    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }

    /**
     * Get the language of the document to be published
     * 
     * @return a <code>String</code> containing the ISO string for
     * this language, e.g. "de", "en"
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language of the document to be published
     * 
     * @param string the ISO string for this language, e.g. "de", "en"
     */
    public void setLanguage(String string) {
        language = string;
    }

    /**
     * adds a node for the published document in the live tree
     *
     * @param documentId The id of the published document
     * @param language the language for which this document is to be published. 
     * 	Can be null if all languages are to be published.
     *
     * @throws PublishingException if the publication failed.
     */
    public void publish(String documentId, String language)
        throws PublishingException {
        SiteTree authoringTree = null;
        SiteTree liveTree = null;

        try {
            authoringTree =
                getPublication().getSiteTree(Publication.AUTHORING_AREA);
            liveTree = getPublication().getSiteTree(Publication.LIVE_AREA);

            SiteTreeNode authoringNode = authoringTree.getNode(documentId);
			SiteTreeNode[] siblings = authoringNode.getNextSiblings();
            String parentId = authoringNode.getAbsoluteParentId();
			SiteTreeNode sibling = null;
			String siblingDocId = null;
			for (int i = 0; i < siblings.length; i++){
				String docId=parentId+"/"+siblings[i].getId();
				sibling =liveTree.getNode(docId);
				if (sibling != null) {
					siblingDocId = docId;
                	break;  
				}
			}

            if (authoringNode != null) {
                if (language == null) {
                    // no language was specified. Simply publish the
                    // node including all languages.
                    try {
                        liveTree.addNode(authoringNode,siblingDocId);
                    } catch (SiteTreeException e1) {
                        throw new ParentNodeNotFoundException(
                            "Couldn't add document: "
                                + documentId
                                + " to live tree.",
                            e1);
                    }
                } else {
                    // a language was specified. Let's see if this
                    // node even has an entry for the specified
                    // language.
                    Label label = authoringNode.getLabel(language);
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
                                liveTree.addNode(
                                    documentId,
                                    labels,
                                    authoringNode.getHref(),
                                    authoringNode.getSuffix(),
                                    authoringNode.hasLink(),
								    siblingDocId);
                            } catch (SiteTreeException e1) {
                                throw new ParentNodeNotFoundException(
                                    "Couldn't add document: "
                                        + documentId
                                        + " to live tree.",
                                    e1);
                            }
                        }
                    } else {
                        // the node that we're trying to publish
                        // doesn't have this language
                        throw new PublishingException(
                            "The node "
                                + documentId
                                + " doesn't contain a label for language "
                                + language);
                    }
                }
            } else {
                throw new PublishingException(
                    "No node found for the document " + documentId);
            }

            liveTree.save();
        } catch (PublishingException e) {
            throw e;
        } catch (Exception e) {
            throw new PublishingException("Couldn't publish to live tree :", e);
        }
    }

    /**
     * Executes the task
     *
     * @throws BuildException DOCUMENT ME!
     */
    public void execute() throws BuildException {
        try {
            log("document id: " + getDocumentid());
            log("language: " + getLanguage());

            publish(
                getDocumentid(),
                getLanguage());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
