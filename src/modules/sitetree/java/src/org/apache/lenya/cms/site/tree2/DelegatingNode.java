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
import java.util.List;

import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.tree.SiteTreeNode;

/**
 * Site tree node which delegates all operations to a shared tree node.
 */
public class DelegatingNode implements TreeNode {
    
    private SiteNode node;
    private DelegatingSiteTree tree;

    /**
     * @param tree The tree.
     * @param delegate The delegate node.
     */
    public DelegatingNode(DelegatingSiteTree tree, SiteNode delegate) {
        this.node = delegate;
        this.tree = tree;
    }

    public void delete() {
        throw new UnsupportedOperationException();
    }
    
    private List children;
    private List preOrder;
    
    public SiteNode[] getChildren() {
        if (this.children == null) {
            SiteNode[] delegateChildren = this.node.getChildren();
            this.children = new ArrayList();
            for (int i = 0; i < delegateChildren.length; i++) {
                this.children.add(this.tree.getNode(delegateChildren[i]));
            }
        }
        return (SiteNode[]) this.children.toArray(new SiteNode[this.children.size()]);
    }

    public String getHref() {
        return this.node.getHref();
    }

    public String[] getLanguages() {
        return this.node.getLanguages();
    }

    public Link getLink(String language) throws SiteException {
        return this.tree.getLink(this.node.getLink(language));
    }

    public String getName() {
        return this.node.getName();
    }

    public SiteNode getParent() throws SiteException {
        return this.tree.getNode(this.node.getParent());
    }

    public String getPath() {
        return this.node.getPath();
    }

    public SiteStructure getStructure() {
        return this.tree;
    }

    public String getSuffix() {
        return this.node.getSuffix();
    }

    public String getUuid() {
        return this.node.getUuid();
    }

    public boolean hasLink(String language) {
        return this.node.hasLink(language);
    }

    public boolean hasLink() {
        return this.node.hasLink();
    }

    public boolean isTopLevel() {
        return this.node.isTopLevel();
    }

    public boolean isVisible() {
        return this.node.isVisible();
    }

    public void setVisible(boolean visibleInNav) {
        throw new UnsupportedOperationException();
    }

    public SiteNode addChild(String name, boolean visible) {
        throw new UnsupportedOperationException();
    }

    public SiteNode addChild(String nodeName, String followingNodeName, boolean visible) {
        throw new UnsupportedOperationException();
    }

    public SiteTreeImpl getTree() {
        throw new UnsupportedOperationException();
    }

    public void moveDown(String name) {
        throw new UnsupportedOperationException();
    }

    public void moveUp(String name) {
        throw new UnsupportedOperationException();
    }

    public SiteNode[] preOrder() {
        if (this.preOrder == null) {
            SiteNode[] delegates = ((TreeNode) this.node).preOrder();
            this.preOrder = new ArrayList();
            for (int i = 0; i < delegates.length; i++) {
                this.preOrder.add(this.tree.getNode(delegates[i]));
            }
        }
        return (SiteNode[]) this.preOrder.toArray(new SiteNode[this.preOrder.size()]);
    }

    public SiteTreeNode[] getNextSiblings() {
        SiteNode[] delegates = ((TreeNode) this.node).getNextSiblings();
        SiteTreeNode[] nodes = new SiteTreeNode[delegates.length];
        for (int i = 0; i < delegates.length; i++) {
            nodes[i] = this.tree.getNode(delegates[i]);
        }
        return nodes;
    }

    public SiteTreeNode[] getPrecedingSiblings() {
        SiteNode[] delegates = ((TreeNode) this.node).getPrecedingSiblings();
        SiteTreeNode[] nodes = new SiteTreeNode[delegates.length];
        for (int i = 0; i < delegates.length; i++) {
            nodes[i] = this.tree.getNode(delegates[i]);
        }
        return nodes;
    }

}
