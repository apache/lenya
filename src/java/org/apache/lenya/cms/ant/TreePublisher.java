/*
 * $Id: TreePublisher.java,v 1.2 2003/05/14 13:53:57 edith Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publishing.PublishingException;
import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;

import org.apache.tools.ant.BuildException;

/**
 * Ant task to publish the tree, adding a node for 
 * the new published document in the live tree
 *
 */
public class TreePublisher
    extends PublicationTask {
    
    /** Creates a new instance of TreePublisher */
    public TreePublisher() {
    }
    
    private String documentid;

    /**
     * Returns the document id
     */
    protected String getDocumentid() {
        return documentid;
    }

    /**
     * Sets the document id
     */
    public void setDocumentid(String documentid) {
        this.documentid = documentid;
    }
    
    private String absolutetreeauthoringpath;
    
    /**
     * Returns the absolute authoring path for the tree
     */
    protected String getAbsolutetreeauthoringpath() {
        return absolutetreeauthoringpath;
    }
    
    /**
     * Sets the absolute authoring path for the tree
     */
    public void setAbsolutetreeauthoringpath(String absolutetreeauthoringpath) {
        this.absolutetreeauthoringpath = absolutetreeauthoringpath;
    }
    
    private String absolutetreelivepath;

    /**
     * Returns the absolute live path for the tree
     */
    protected String getAbsolutetreelivepath() {
        return absolutetreelivepath;
    }

    /**
     * Sets the absolute live path for the tree
     */
    public void setAbsolutetreelivepath(String absolutetreelivepath) {
        this.absolutetreelivepath= absolutetreelivepath;
    }

    /**
     * adds a node for the published document in the live tree 
     * @param documentid The id of the published document
     * @param absolutetreeauthoringpath The path for the tree in authoring
     * @param absolutetreelivepath The path for the tree in live
     */
    public void publish(String documentid, String absolutetreeauthoringpath, String absolutetreelivepath)
        throws PublishingException {

      DefaultSiteTree authoringTree = null;
      DefaultSiteTree liveTree = null;

      try {
        authoringTree = new DefaultSiteTree(absolutetreeauthoringpath);
        liveTree = new DefaultSiteTree(absolutetreelivepath);
        SiteTreeNode documentNode = authoringTree.getNode(documentid);
        if (documentNode!= null) {
          liveTree.addNode(documentNode); 
        } else {
          throw new PublishingException("No node found for the document "+documentid);
        } 
        liveTree.serialize(); 
      } catch (Exception e) {
        throw new PublishingException("Couldn't publish the tree :"+ absolutetreelivepath , e);
      }
    }
    
    /**
     * Executes the task
     */
    public void execute()
        throws BuildException {
            
        try {
            log("document id: " + getDocumentid());
            log("Absolute Tree Authoring Path: " + getAbsolutetreeauthoringpath());
            log("Absolute Tree Live Path: " + getAbsolutetreelivepath());

            publish (
                getDocumentid(), 
                getAbsolutetreeauthoringpath(),
                getAbsolutetreelivepath());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

}
