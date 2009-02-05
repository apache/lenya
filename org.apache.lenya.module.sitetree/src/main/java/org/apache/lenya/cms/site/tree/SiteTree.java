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

package org.apache.lenya.cms.site.tree;

import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * A sitetree.
 * 
 * @version $Id: SiteTree.java 177923 2005-05-23 05:15:51Z gregor $
 */
public interface SiteTree extends SiteStructure {

    /**
     * The type of sitetree identifiable objects.
     */
    String IDENTIFIABLE_TYPE = "site";

    /**
     * Move up the node amongst its siblings.
     * 
     * @param path The document id of the node.
     * @throws SiteException if the moving failed.
     */
    void moveUp(String path) throws SiteException;

    /**
     * Move down the node amongst its siblings.
     * @param path The document id of the node.
     * @throws SiteException if the moving failed.
     */
    void moveDown(String path) throws SiteException;

    /**
     * @return The nodes in pre order.
     */
    SiteNode[] preOrder();

}
