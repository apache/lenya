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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.tree.DefaultSiteTree;

import junit.framework.TestCase;

/**
 * Tests the site tree
 */
public class SiteTreeNodeImplTest extends AbstractAccessControlTest {

    private SiteTreeNodeImpl node = null;
    private DefaultSiteTree siteTree = null;

    /**
     * @see TestCase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp();
        Publication pub = getPublication("test");
        this.siteTree = new DefaultSiteTree(getFactory(), pub, "testArea", getManager(), getLogger());
        ContainerUtil.enableLogging(siteTree, getLogger());
        
        siteTree.getRepositoryNode().lock();
        
        if (siteTree.contains("/foo")) {
            siteTree.removeNode("/foo");
        }
        
        siteTree.addNode("/foo", "foo-uuid", true, null, null, false);
        siteTree.addLabel("/foo", "en", "Foo");
        
        siteTree.addNode("/foo/bar", "foo-bar-uuid", true, "http://exact.biz", "suffix", true);
        siteTree.addLabel("/foo/bar", "en", "Bar");
        siteTree.addLabel("/foo/bar", "de", "Stab");

        this.node = (SiteTreeNodeImpl) siteTree.getNode("/foo/bar");
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
     * @throws SiteException 
     * 
     */
    final public void testGetLabels() throws SiteException {
        String[] languages = this.node.getLanguages();
        assertEquals(languages.length, 2);
    }

    /**
     * Test getLabel
     * @throws SiteException 
     * 
     */
    final public void testGetLabel() throws SiteException {
        Link label = this.node.getLink("en");
        assertNotNull(label);
        assertEquals(label.getLabel(), "Bar");
    }

    /**
     * Test addLabel
     * @throws SiteException 
     * 
     */
    final public void testAddLabel() throws SiteException {
        this.node.addLabel("it", "Barolo");
        Link label = this.node.getLink("it");
        assertNotNull(label);
        assertEquals(label.getLabel(), "Barolo");
        assertFalse(this.node.hasLink("ch"));
    }

    /**
     * Test removeLabel
     * @throws SiteException 
     * 
     */
    final public void testRemoveLabel() throws SiteException {
        assertNotNull(this.node.getLink("en"));
        this.node.removeLabel("en");
        assertFalse(this.node.hasLink("en"));
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
