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
package org.apache.lenya.cms.site.tree2;

import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.tree.SiteTreeNode;

/**
 * A parent for nodes.
 */
public interface TreeNode extends SiteNode, SiteTreeNode {

    /**
     * @return The children.
     */
    SiteNode[] getChildren();
    
    /**
     * @return Preorder enumeration.
     */
    SiteNode[] preOrder();

    /**
     * @param name The name.
     * @param visible The navigation visibility.
     * @return A site node.
     */
    SiteNode addChild(String name, boolean visible);

    /**
     * @param nodeName
     * @param followingNodeName
     * @param visible The navigation visibility.
     * @return A site node.
     */
    SiteNode addChild(String nodeName, String followingNodeName, boolean visible);
    
    /**
     * @return The tree.
     */
    SiteTreeImpl getTree();

    /**
     * @return The path.
     */
    String getPath();

    /**
     * @param name The name.
     */
    void moveDown(String name);

    /**
     * @param name The name.
     */
    void moveUp(String name);

}
