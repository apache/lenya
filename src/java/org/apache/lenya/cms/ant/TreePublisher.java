/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
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
                throw new PublishingException("No node found for the document " + documentid);
            }

            liveTree.save();
        } catch (Exception e) {
            throw new PublishingException("Couldn't publish the tree :" + absolutetreelivepath, e);
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
            log("Absolute Tree Authoring Path: " + getAbsolutetreeauthoringpath());
            log("Absolute Tree Live Path: " + getAbsolutetreelivepath());

            publish(getDocumentid(), getAbsolutetreeauthoringpath(), getAbsolutetreelivepath());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
