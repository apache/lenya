/*
<License>
 * =======================================================================
 * Copyright (c) 2000 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products
 *    derived from this software without prior written permission.
 *    For written permission , please contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona"
 *    nor may "wyona" appear in their names without prior written
 *    permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY
 * EXPRESS OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND
 * THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS
 * A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE FOR
 * ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN
 * IF wyona HAS BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE.
 * wyona WILL NOT BE LIABLE FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 * =======================================================================
</License>
 */
package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.publishing.PublishingException;

import org.apache.tools.ant.BuildException;


/**
 * Ant task to publish the tree, adding a node for  the new published document
 * in the live tree
 */
public class TreePublisher extends PublicationTask {
    private String documentid;
    private String absolutetreeauthoringpath;
    private String absolutetreelivepath;

    /**
     * Creates a new instance of TreePublisher
     */
    public TreePublisher() {
    }

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
     * Returns the absolute authoring path for the tree
     *
     * @return DOCUMENT ME!
     */
    protected String getAbsolutetreeauthoringpath() {
        return absolutetreeauthoringpath;
    }

    /**
     * Sets the absolute authoring path for the tree
     *
     * @param absolutetreeauthoringpath DOCUMENT ME!
     */
    public void setAbsolutetreeauthoringpath(String absolutetreeauthoringpath) {
        this.absolutetreeauthoringpath = absolutetreeauthoringpath;
    }

    /**
     * Returns the absolute live path for the tree
     *
     * @return DOCUMENT ME!
     */
    protected String getAbsolutetreelivepath() {
        return absolutetreelivepath;
    }

    /**
     * Sets the absolute live path for the tree
     *
     * @param absolutetreelivepath DOCUMENT ME!
     */
    public void setAbsolutetreelivepath(String absolutetreelivepath) {
        this.absolutetreelivepath = absolutetreelivepath;
    }

    /**
     * adds a node for the published document in the live tree
     *
     * @param documentid The id of the published document
     * @param absolutetreeauthoringpath The path for the tree in authoring
     * @param absolutetreelivepath The path for the tree in live
     *
     * @throws PublishingException DOCUMENT ME!
     */
    public void publish(String documentid, String absolutetreeauthoringpath,
        String absolutetreelivepath) throws PublishingException {
        DefaultSiteTree authoringTree = null;
        DefaultSiteTree liveTree = null;

        try {
            authoringTree = new DefaultSiteTree(absolutetreeauthoringpath);
            liveTree = new DefaultSiteTree(absolutetreelivepath);

            SiteTreeNode documentNode = authoringTree.getNode(documentid);

            if (documentNode != null) {
                liveTree.addNode(documentNode);
            } else {
                throw new PublishingException("No node found for the document " +
                    documentid);
            }

            liveTree.save();
        } catch (Exception e) {
            throw new PublishingException("Couldn't publish the tree :" +
                absolutetreelivepath, e);
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
            log("Absolute Tree Authoring Path: " +
                getAbsolutetreeauthoringpath());
            log("Absolute Tree Live Path: " + getAbsolutetreelivepath());

            publish(getDocumentid(), getAbsolutetreeauthoringpath(),
                getAbsolutetreelivepath());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
