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

/* $Id$  */

package org.apache.lenya.cms.site.tree;

import junit.framework.TestCase;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.tree.DefaultSiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;

/**
 * Test class for the default site tree
 */
public class DefaultSiteTreeTest extends AbstractAccessControlTest {

    private DefaultSiteTree siteTree = null;

    /**
     * @see TestCase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp();
        Publication pub = getPublication("test");
        this.siteTree = new DefaultSiteTree(getFactory(),
                pub,
                "test",
                getManager(),
                getLogger());
        ContainerUtil.enableLogging(this.siteTree, getLogger());

        this.siteTree.getRepositoryNode().lock();

        this.siteTree.addNode("/foo", "foo-uuid", true, null, null, false);
        this.siteTree.addNode("/index", "index-uuid", true, null, null, false);
        this.siteTree.addNode("/foo/bar", "foo-bar-uuid", true, "http://exact.biz", "suffix", true);
        this.siteTree.addNode("/foo/lala", "foo-lala-uuid", true, null, null, false);
    }

    /**
     * @see TestCase#tearDown()
     */
    public void tearDown() throws Exception {
        super.tearDown();
        this.siteTree.getRepositoryNode().unlock();
    }

    /**
     * Test for void DefaultSiteTree(String)
     */
    final public void testDefaultSiteTreeString() {
        // TODO Implement DefaultSiteTree().
    }

    /**
     * Test for void DefaultSiteTree(File)
     */
    final public void testDefaultSiteTreeFile() {
        // TODO Implement DefaultSiteTree().
    }

    /**
     * Test for void addNode(String, String, Label[])
     * 
     * @throws SiteException if an error occurs
     */
    final public void testAddNodeStringStringLabelArray() throws SiteException {
        String uuid = "12345";
        this.siteTree.addNode("/foo", "tutorial", uuid, true);
        this.siteTree.addLabel("/foo", "en", "Tutorial");
        SiteNode node = this.siteTree.getNode("/foo/tutorial");
        assertNotNull(node);
        assertEquals(node.getName(), "tutorial");
        assertEquals(node.getUuid(), uuid);
    }

    /**
     * Test for void addNode(SiteTreeNode)
     */
    final public void testAddNodeSiteTreeNode() {
        // TODO Implement addNode().
    }

    /**
     * Test for void addNode(String, Label[], String, String, boolean)
     * 
     * @throws SiteException if an error occurs
     */
    final public void testAddNodeStringLabelArrayStringStringboolean() throws SiteException {
        this.siteTree.addNode("/foo/ding", "foo-ding-uuid", true, null, null, false);
        this.siteTree.addLabel("/foo/ding", "en", "Doh");
        this.siteTree.addLabel("/foo/ding", "de", "Ding");

        assertNotNull(this.siteTree.getNode("/foo/ding"));
        assertEquals(this.siteTree.getNode("/foo/ding").getName(), "ding");
    }

    /**
     * Test for void addNode(String, String, Label[], String, String, boolean)
     * @throws SiteException if an error occurs
     */
    final public void testAddNodeStringStringLabelArrayStringStringboolean() throws SiteException {

        String uuid = "123";

        this.siteTree.addNode("/foo", "baz", uuid, true, null, null, false);

        assertNotNull(this.siteTree.getNode("/foo/baz"));
        assertEquals(this.siteTree.getNode("/foo/baz").getName(), "baz");
    }

    /**
     * Test addLabel
     * @throws SiteException
     */
    final public void testAddLabel() throws SiteException {
        this.siteTree.addLabel("/foo/bar", "en", "Tutorial");
        String[] labels = ((SiteTreeNode) this.siteTree.getNode("/foo/bar")).getLanguages();
        assertEquals(labels.length, 1);
        Link label = ((SiteTreeNode) this.siteTree.getNode("/foo/bar")).getLink("en");
        assertNotNull(label);
        assertEquals(label.getLabel(), "Tutorial");
    }

    /**
     * Test removeLabel
     * @throws SiteException
     */
    final public void testRemoveLabel() throws SiteException {
        this.siteTree.addLabel("/foo/bar", "de", "Hello");
        if (!this.siteTree.getNode("/foo/bar").hasLink("en")) {
            this.siteTree.addLabel("/foo/bar", "en", "World");
        }
        assertEquals(((SiteTreeNode) this.siteTree.getNode("/foo/bar")).getLanguages().length, 2);

        this.siteTree.removeLabel("/foo/bar", "de");
        assertEquals(((SiteTreeNode) this.siteTree.getNode("/foo/bar")).getLanguages().length, 1);

        assertEquals(((SiteTreeNode) this.siteTree.getNode("/foo/bar")).getLanguages()[0],
                "en");

        this.siteTree.addLabel("/foo/bar", "de", "Foo");
        assertEquals(((SiteTreeNode) this.siteTree.getNode("/foo/bar")).getLanguages().length, 2);
    }

    /**
     * Test removeNode
     * 
     * @throws SiteException if an error occurs
     */
    final public void testRemoveNode() throws SiteException {

        this.siteTree.addNode("/hi", "hi-uuid", true, null, null, false);


        this.siteTree.addNode("/hi/ho", "hi-ho-uuid", true, null, null, false);

        assertNotNull(this.siteTree.getNode("/hi/ho"));

        this.siteTree.removeNode("/hi");

        assertFalse(this.siteTree.contains("/hi/ho"));
        assertFalse(this.siteTree.contains("/hi"));
    }

    /**
     * Test getNode
     * @throws SiteException
     * 
     */
    final public void testGetNode() throws SiteException {
        assertNotNull(this.siteTree.getNode("/foo/bar"));
        assertFalse(this.siteTree.contains("/foo/bar/baz"));
    }

    /**
     * Test save
     * 
     */
    final public void testSave() {
        // TODO Implement save().
    }

    /**
     * Test moving a node up
     * @throws RepositoryException 
     */
    final public void testMoveUp() throws RepositoryException {
        this.siteTree.moveUp("/foo/lala");
        this.siteTree.save();
        assertNotNull(this.siteTree.getNode("/foo/lala"));
    }

    /**
     * Test moving a node down
     * @throws RepositoryException 
     */
    final public void testMoveDown() throws RepositoryException {
        this.siteTree.moveDown("/foo");
        this.siteTree.save();
        assertNotNull(this.siteTree.getNode("/foo"));
    }

    /**
     * Test the import of a subtree
     * @throws PublicationException
     * @throws RepositoryException
     */
    final public void testImportSubtree() throws PublicationException, RepositoryException {
        Publication pub = getPublication("test");
        DefaultSiteTree newSiteTree = new DefaultSiteTree(getFactory(),
                pub,
                "test1",
                getManager(),
                getLogger());
        ContainerUtil.enableLogging(newSiteTree, getLogger());

        newSiteTree.getRepositoryNode().lock();

        newSiteTree.addNode("/root", "root-uuid", true, null, null, false);
        newSiteTree.addLabel("/root", "en", "root");
        newSiteTree.addNode("/root/foo", "root-foo-uuid", true, null, null, false);
        newSiteTree.addLabel("/root/foo", "en", "foo");
        newSiteTree.addNode("/root/subtree",
                "root-subtree-uuid",
                true,
                "http://exact.biz",
                "suffix",
                true);
        newSiteTree.addLabel("/root/subtree", "en", "subtree");
        newSiteTree.addNode("/root/subtree/child",
                "root-subtree-child-uuid",
                true,
                null,
                null,
                false);
        newSiteTree.addLabel("/root/subtree/child", "en", "child");
        SiteTreeNode node = (SiteTreeNode) newSiteTree.getNode("/root/subtree");
        assertNotNull(node);
        SiteTreeNode parentNode = (SiteTreeNode) this.siteTree.getNode("/foo/lala");
        assertNotNull(parentNode);

        newSiteTree.getRepositoryNode().unlock();
    }
}
