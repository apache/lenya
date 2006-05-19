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

/* $Id$  */

package org.apache.lenya.cms.site.tree;

import junit.framework.TestCase;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.lenya.ac.impl.AccessControlTest;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.tree.DefaultSiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;

/**
 * Test class for the default site tree
 */
public class DefaultSiteTreeTest extends AccessControlTest {

    private DefaultSiteTree siteTree = null;

    /**
     * @see TestCase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp();
        Publication pub = PublicationUtil.getPublication(getManager(), "test");
        this.siteTree = new DefaultSiteTree(pub, "test", getManager(), getLogger());
        ContainerUtil.enableLogging(this.siteTree, getLogger());

        this.siteTree.getRepositoryNode().lock();

        Label label = new Label("Foo", "en");
        Label[] fooLabels = { label };
        this.siteTree.addNode("/foo", fooLabels, true, null, null, false);
        label = new Label("Home", "en");
        Label[] homeLabels = { label };
        this.siteTree.addNode("/index", homeLabels, true, null, null, false);
        label = new Label("Bar", "en");
        Label label_de = new Label("Stab", "de");
        Label[] barLabels = { label, label_de };
        this.siteTree.addNode("/foo/bar", barLabels, true, "http://exact.biz", "suffix", true);
        label = new Label("Lala", "en");
        Label[] lalaLabels = { label };
        this.siteTree.addNode("/foo/lala", lalaLabels, true, null, null, false);
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
        Label label = new Label("Tutorial", null);
        Label[] labels = { label };

        this.siteTree.addNode("/foo", "tutorial", labels, true);
        SiteTreeNode node = this.siteTree.getNode("/foo/tutorial");
        assertNotNull(node);
        assertEquals(node.getId(), "tutorial");
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
        Label label1 = new Label("Doh", "en");
        Label label2 = new Label("Ding", "en");
        Label[] labels = { label1, label2 };

        this.siteTree.addNode("/foo/ding", labels, true, null, null, false);

        assertNotNull(this.siteTree.getNode("/foo/ding"));
        assertEquals(this.siteTree.getNode("/foo/ding").getId(), "ding");
    }

    /**
     * Test for void addNode(String, String, Label[], String, String, boolean)
     * @throws SiteException if an error occurs
     */
    final public void testAddNodeStringStringLabelArrayStringStringboolean() throws SiteException {
        Label label1 = new Label("Doh", "en");
        Label label2 = new Label("Ding", "en");
        Label[] labels = { label1, label2 };

        this.siteTree.addNode("/foo", "baz", labels, true, null, null, false);

        assertNotNull(this.siteTree.getNode("/foo/baz"));
        assertEquals(this.siteTree.getNode("/foo/baz").getId(), "baz");
    }

    /**
     * Test addLabel
     */
    final public void testAddLabel() {
        Label label = new Label("Tutorial", null);
        Label[] labels = null;

        this.siteTree.addLabel("/foo/bar", label);
        labels = this.siteTree.getNode("/foo/bar").getLabels();
        assertEquals(labels.length, 3);
        label = this.siteTree.getNode("/foo/bar").getLabel("");
        assertNotNull(label);
        assertEquals(label.getLabel(), "Tutorial");
    }

    /**
     * Test removeLabel
     */
    final public void testRemoveLabel() {
        assertEquals(this.siteTree.getNode("/foo/bar").getLabels().length, 2);

        Label label = new Label("Stab", "de");
        this.siteTree.removeLabel("/foo/bar", label);
        assertEquals(this.siteTree.getNode("/foo/bar").getLabels().length, 1);

        assertEquals(this.siteTree.getNode("/foo/bar").getLabels()[0], new Label("Bar", "en"));

        this.siteTree.addLabel("/foo/bar", label);
        assertEquals(this.siteTree.getNode("/foo/bar").getLabels().length, 2);
    }

    /**
     * Test removeNode
     * 
     * @throws SiteException if an error occurs
     */
    final public void testRemoveNode() throws SiteException {
        Label label1 = new Label("Hi", "en");
        Label label2 = new Label("Ho", "en");
        Label[] labels1 = { label1, label2 };

        this.siteTree.addNode("/hi", labels1, true, null, null, false);

        Label[] labels2 = { label1, label2 };

        this.siteTree.addNode("/hi/ho", labels2, true, null, null, false);

        assertNotNull(this.siteTree.getNode("/hi/ho"));

        this.siteTree.removeNode("/hi");

        assertNull(this.siteTree.getNode("/hi/ho"));
        assertNull(this.siteTree.getNode("/hi"));
    }

    /**
     * Test getNode
     * 
     */
    final public void testGetNode() {
        assertNotNull(this.siteTree.getNode("/foo/bar"));

        assertNull(this.siteTree.getNode("/foo/baz"));
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
     * @throws SiteException if an error occurs
     */
    final public void testMoveUp() throws SiteException {
        this.siteTree.moveUp("/foo/lala");
        this.siteTree.save();
        assertNotNull(this.siteTree.getNode("/foo/lala"));
    }

    /**
     * Test moving a node down
     * @throws SiteException if an error occurs
     */
    final public void testMoveDown() throws SiteException {
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
        Publication pub = PublicationUtil.getPublication(getManager(), "test");
        DefaultSiteTree newSiteTree = new DefaultSiteTree(pub, "test1", getManager(), getLogger());
        ContainerUtil.enableLogging(newSiteTree, getLogger());

        newSiteTree.getRepositoryNode().lock();

        Label label = new Label("root", "en");
        Label[] rootLabels = { label };
        newSiteTree.addNode("/root", rootLabels, true, null, null, false);
        label = new Label("foo", "en");
        Label[] fooLabels = { label };
        newSiteTree.addNode("/root/foo", fooLabels, true, null, null, false);
        label = new Label("subtree", "en");
        Label[] subtreeLabels = { label };
        newSiteTree.addNode("/root/subtree",
                subtreeLabels,
                true,
                "http://exact.biz",
                "suffix",
                true);
        label = new Label("child", "en");
        Label[] childLabels = { label };
        newSiteTree.addNode("/root/subtree/child", childLabels, true, null, null, false);
        SiteTreeNode node = newSiteTree.getNode("/root/subtree");
        assertNotNull(node);
        SiteTreeNode parentNode = this.siteTree.getNode("/foo/lala");
        assertNotNull(parentNode);
        this.siteTree.importSubtree(parentNode, node, "subtree", null);
        this.siteTree.save();
        assertNotNull(this.siteTree.getNode("/foo/lala/subtree"));
        assertNotNull(this.siteTree.getNode("/foo/lala/subtree/child"));

        newSiteTree.getRepositoryNode().unlock();
    }
}
