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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.tree.DefaultSiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;

import junit.framework.TestCase;

/**
 * Tests the site tree
 */
public class SiteTreeNodeImplTest extends AbstractAccessControlTest {

    private SiteTreeNode node = null;
    private DefaultSiteTree siteTree = null;

    /**
     * @see TestCase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp();
        Publication pub = PublicationUtil.getPublication(getManager(), "test");
        this.siteTree = new DefaultSiteTree(getIdentityMap(), pub, "testArea", getManager(), getLogger());
        ContainerUtil.enableLogging(siteTree, getLogger());
        
        siteTree.getRepositoryNode().lock();
        
        siteTree.addNode("/foo", "foo-uuid", true, null, null, false);
        siteTree.addLabel("/foo", "en", "Foo");
        
        siteTree.addNode("/foo/bar", "foo-bar-uuid", true, "http://exact.biz", "suffix", true);
        siteTree.addLabel("/foo/bar", "en", "Bar");
        siteTree.addLabel("/foo/bar", "de", "Stab");

        this.node = (SiteTreeNode) siteTree.getNode("/foo/bar");
    }

    /**
     * @see TestCase#tearDown()
     */
    public void tearDown() throws Exception {
        super.tearDown();
        this.siteTree.getRepositoryNode().unlock();
    }

    /**
     * Test getAbsoluteId
     * 
     */
    final public void testGetAbsoluteId() {
        assertEquals(this.node.getPath(), "/foo/bar");
    }

    /**
     * Test getId
     * 
     */
    final public void testGetId() {
        assertEquals(this.node.getName(), "bar");
    }

    /**
     * Test getLabels
     * 
     */
    final public void testGetLabels() {
        assertEquals(this.node.getLabels().length, 2);
        for (int i = 0; i < this.node.getLabels().length; i++) {
            Label label = this.node.getLabels()[i];
            Label label1 = new Label(getIdentityMap(), node, "Bar", "en");
            Label label2 = new Label(getIdentityMap(), node, "Stab", "de");
            assertTrue(label.equals(label1) || label.equals(label2));
        }
    }

    /**
     * Test getLabel
     * 
     */
    final public void testGetLabel() {
        Label label = this.node.getLabel("en");
        assertNotNull(label);
        assertEquals(label.getLabel(), "Bar");
    }

    /**
     * Test addLabel
     * 
     */
    final public void testAddLabel() {
        Label label = new Label(getIdentityMap(), this.node, "Barolo", "it");
        this.node.addLabel(label);
        label = this.node.getLabel("it");
        assertNotNull(label);
        assertEquals(label.getLabel(), "Barolo");
        label = this.node.getLabel("ch");
        assertNull(label);
    }

    /**
     * Test removeLabel
     * 
     */
    final public void testRemoveLabel() {
        assertNotNull(this.node.getLabel("en"));
        this.node.removeLabel("en");
        assertNull(this.node.getLabel("en"));
    }

    /**
     * Test getHref
     * 
     */
    final public void testGetHref() {
        assertEquals(this.node.getHref(), "http://exact.biz");
    }

    /**
     * Test getSuffix
     * 
     * 
     */
    final public void testGetSuffix() {
        assertEquals(this.node.getSuffix(), "suffix");
    }

    /**
     * Test hasLink
     * 
     */
    final public void testHasLink() {
        assertTrue(this.node.hasLink());
    }

}
