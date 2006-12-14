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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.site.SiteNode;

/**
 * Root node.
 */
public class RootNode extends TreeNodeImpl {
    
    private SiteTreeImpl tree;

    /**
     * @param tree The tree.
     * @param logger The logger.
     */
    public RootNode(SiteTreeImpl tree, Logger logger) {
        super(null, "", false, logger);
        this.tree = tree;
    }

    public SiteTreeImpl getTree() {
        return this.tree;
    }

    public String getPath() {
        return "";
    }

    public SiteNode[] preOrder() {
        List preOrder = new ArrayList();
        SiteNode[] children = getChildren();
        for (int i = 0; i < children.length; i++) {
            TreeNode child = (TreeNode) children[i];
            preOrder.addAll(Arrays.asList(child.preOrder()));
        }
        return (SiteNode[]) preOrder.toArray(new SiteNode[preOrder.size()]);
    }

    protected void deleteIfEmpty() {
    }

}
