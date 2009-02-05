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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.util.Assert;
import org.apache.lenya.util.StringUtil;

/**
 * Site tree node.
 */
public class TreeNodeImpl extends AbstractLogEnabled implements TreeNode {

    private TreeNode parent;
    private String name;
    private String path;

    /**
     * A top level node.
     * @param parent The parent.
     * @param name The name.
     * @param visible The navigation visibility.
     * @param logger The logger.
     */
    public TreeNodeImpl(TreeNode parent, String name, boolean visible, Logger logger) {
        ContainerUtil.enableLogging(this, logger);
        Assert.notNull("name", name);
        this.name = name;
        this.parent = parent;
        this.isVisible = visible;
    }

    /**
     * Sets the UUID.
     * @param uuid The UUID.
     */
    protected void setUuid(String uuid) {
        Assert.notNull("uuid", uuid);
        if (this.language2link.keySet().size() > 0) {
            throw new RuntimeException("Can't set the UUID if the node has links.");
        }
        
        if (this.uuid != null) {
            String[] languages = getLanguages();
            for (int i = 0; i < languages.length; i++) {
                getTree().linkRemoved(this.uuid, languages[i]);
            }
        }
        
        this.uuid = uuid;
        
        String[] languages = getLanguages();
        for (int i = 0; i < languages.length; i++) {
            try {
                getTree().linkAdded(getLink(languages[i]));
            } catch (SiteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void delete() {
        deleteInternal();
        changed();
    }

    protected void deleteInternal() {
        String[] languages = getLanguages();
        for (int i = 0; i < languages.length; i++) {
            removeLinkInternal(languages[i]);
        }
        SiteNode[] children = getChildren();
        for (int i = 0; i < children.length; i++) {
            ((TreeNodeImpl) children[i]).deleteInternal();
        }
        ((TreeNodeImpl) this.parent).removeChild(getName());
    }

    private Map language2link = new HashMap();
    private String uuid;
    private boolean isVisible;

    public String[] getLanguages() {
        Set languages = this.language2link.keySet();
        return (String[]) languages.toArray(new String[languages.size()]);
    }

    public Link getLink(String language) throws SiteException {
        Assert.notNull("language", language);
        if (!this.language2link.containsKey(language)) {
            throw new SiteException("No link contained for language [" + language + "]");
        }
        return (Link) this.language2link.get(language);
    }

    public String getName() {
        return this.name;
    }

    public SiteNode getParent() throws SiteException {
        if (isTopLevel()) {
            throw new SiteException("This is a top level node.");
        }
        return (SiteNode) this.parent;
    }

    public String getPath() { 
        if(path != null) {
            return path;
        }
        String getPath = this.parent.getPath() + "/" + getName();
        path = getPath;
        return path;
    }

    public SiteStructure getStructure() {
        return getTree();
    }

    public String getUuid() {
        return this.uuid;
    }

    public boolean hasLink(String language) {
        Assert.notNull("language", language);
        return this.language2link.containsKey(language);
    }

    public boolean isTopLevel() {
        return this.parent instanceof RootNode;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visibleInNav) {
        this.isVisible = visibleInNav;
        changed();
    }

    protected void changed() {
        getTree().changed();
        this.path = null;
    }

    public SiteTreeNode[] getPrecedingSiblings() {
        SiteNode[] children = this.parent.getChildren();
        int pos = Arrays.asList(children).indexOf(this);
        List siblings = new ArrayList();
        for (int i = 0; i < pos ; i++) {
            siblings.add(children[i]);
        }
        return (SiteTreeNode[]) siblings.toArray(new TreeNodeImpl[siblings.size()]);
    }

    public SiteTreeNode[] getNextSiblings() {
        SiteNode[] children = this.parent.getChildren();
        int pos = Arrays.asList(children).indexOf(this);
        List siblings = new ArrayList();
        for (int i = pos + 1; i < children.length; i++) {
            siblings.add(children[i]);
        }
        return (SiteTreeNode[]) siblings.toArray(new TreeNodeImpl[siblings.size()]);
    }

    public SiteTreeImpl getTree() {
        return this.parent.getTree();
    }

    protected Link addLink(String lang, String label) {
        Assert.notNull("language", lang);
        Assert.notNull("label", label);
        Link link = addLinkInternal(lang, label);
        changed();
        return link;
    }

    protected Link addLinkInternal(String lang, String label) {
        Assert.notNull("language", lang);
        Assert.notNull("label", label);
        if (this.language2link.containsKey(lang)) {
            throw new RuntimeException("The language [" + lang + "] is already contained.");
        }
        DocumentFactory factory = getTree().getPublication().getFactory();
        Link link = new SiteTreeLink(factory, this, label, lang);
        this.language2link.put(lang, link);
        getTree().linkAdded(link);
        return link;
    }

    protected void removeLink(String language) {
        removeLinkInternal(language);
        deleteIfEmpty();
        changed();
    }

    protected void removeLinkInternal(String language) {
        Assert.notNull("language", language);
        this.language2link.remove(language);
        getTree().linkRemoved(getUuid(), language);
    }

    protected void deleteIfEmpty() {
        if (isEmpty()) {
            deleteInternal();
        }
    }

    protected boolean isEmpty() {
        return this.language2link.isEmpty() && this.name2child.isEmpty();
    }

    public String toString() {
        return getPath() + "[" + StringUtil.join(getLanguages(), ",") + "]";
    }

    private List children = new ArrayList();

    public SiteNode[] getChildren() {
        return (SiteNode[]) this.children.toArray(new SiteNode[this.children.size()]);
    }

    public SiteNode[] preOrder() {
        List preOrder = new ArrayList();
        preOrder.add(this);
        SiteNode[] children = getChildren();
        for (int i = 0; i < children.length; i++) {
            TreeNode child = (TreeNode) children[i];
            preOrder.addAll(Arrays.asList(child.preOrder()));
        }
        return (SiteNode[]) preOrder.toArray(new SiteNode[preOrder.size()]);
    }

    protected void removeChild(String name) {
        Assert.notNull("name", name);
        if (!this.name2child.containsKey(name)) {
            throw new RuntimeException("The node [" + name + "] is not contained!");
        }
        SiteNode node = (SiteNode) this.name2child.get(name);
        this.name2child.remove(node.getName());
        this.children.remove(node);
        getTree().nodeRemoved(getPath() + "/" + name);
        deleteIfEmpty();
    }

    private Map name2child = new HashMap();

    public SiteNode addChild(String name, boolean visible) {
        Assert.notNull("name", name);
        return addChild(name, this.children.size(), visible);
    }

    public SiteNode addChild(String name, String followingNodeName, boolean visible) {
        Assert.notNull("name", name);
        Assert.notNull("following node name", followingNodeName);
        SiteNode followingSibling = getChild(followingNodeName);
        int pos = this.children.indexOf(followingSibling);
        return addChild(name, pos, visible);
    }

    protected SiteNode addChild(String name, int pos, boolean visible) {
        Assert.notNull("name", name);

        if (this.name2child.containsKey(name)) {
            throw new RuntimeException("The child [" + name + "] is already contained.");
        }

        SiteNode node = new TreeNodeImpl(this, name, visible, getLogger());
        this.children.add(pos, node);
        this.name2child.put(name, node);
        getTree().nodeAdded(node);
        getTree().changed();
        return node;
    }

    protected SiteNode getChild(String name) {
        Assert.notNull("name", name);
        if (this.name2child.containsKey(name)) {
            return (SiteNode) this.name2child.get(name);
        } else {
            throw new RuntimeException("No such child [" + name + "]");
        }
    }

    protected int getPosition(SiteNode child) {
        Assert.notNull("child", child);
        Assert.isTrue("contains", this.children.contains(child));
        return this.children.indexOf(child);
    }

    public void moveDown(String name) {
        SiteNode child = getChild(name);
        int pos = getPosition(child);
        Assert.isTrue("not last", pos < this.children.size() - 1);
        this.children.remove(child);
        this.children.add(pos + 1, child);
        changed();
    }

    public void moveUp(String name) {
        SiteNode child = getChild(name);
        int pos = getPosition(child);
        Assert.isTrue("not first", pos > 0);
        this.children.remove(child);
        this.children.add(pos - 1, child);
        changed();
    }

    public String getHref() {
        return null;
    }

    public String getSuffix() {
        return null;
    }

    public boolean hasLink() {
        return false;
    }
    
}
