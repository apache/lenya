/*
$Id: DefaultSiteTreeTest.java,v 1.1 2003/07/08 09:31:24 egli Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
 
package org.apache.lenya.cms.publication;

import junit.framework.TestCase;

/**
 * 
 * @author egli
 * 
 */
public class DefaultSiteTreeTest extends TestCase {
	
	private DefaultSiteTree siteTree = null;

	/**
	 * Constructor.
	 * @param test The test.
	 */
	public DefaultSiteTreeTest(String test) {
        super(test);
    }

	/**
	 * The main program.
	 * The parameters are set from the command line arguments.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
        junit.textui.TestRunner.run(DefaultSiteTreeTest.class);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
		siteTree = new DefaultSiteTree("testTree");
		Label label = new Label("Foo", "en");
		Label[] fooLabels = { label };
		siteTree.addNode("/foo", fooLabels, null, null, false);
		label = new Label("Bar", "en");
		Label label_de = new Label("Stab", "de");
		Label[] barLabels = { label, label_de };
		siteTree.addNode("/foo/bar", barLabels, "http://exact.biz", "suffix", true);
	}

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test for void DefaultSiteTree(String)
     */
    final public void testDefaultSiteTreeString() {
        //TODO Implement DefaultSiteTree().
    }

    /**
     * Test for void DefaultSiteTree(File)
     */
    final public void testDefaultSiteTreeFile() {
        //TODO Implement DefaultSiteTree().
    }

    /**
     * Test for void addNode(String, String, Label[])
     * 
     * @throws SiteTreeException if an error occurs
     */
    final public void testAddNodeStringStringLabelArray() throws SiteTreeException {
		Label label = new Label("Tutorial", null);
		Label[] labels = { label };

		siteTree.addNode("/tutorial", labels, null, null, false);
		SiteTreeNode node =  siteTree.getNode("/tutorial");
		assertNotNull(node);
		assertEquals(node.getId(), "tutorial");
    }

    /**
     * Test for void addNode(SiteTreeNode)
     */
    final public void testAddNodeSiteTreeNode() {
        //TODO Implement addNode().
    }

    /**
     * Test for void addNode(String, Label[], String, String, boolean)
     */
    final public void testAddNodeStringLabelArrayStringStringboolean() {
        //TODO Implement addNode().
    }

    /**
     * Test for void addNode(String, String, Label[], String, String, boolean)
     */
    final public void testAddNodeStringStringLabelArrayStringStringboolean() {
        //TODO Implement addNode().
    }

	/**
	 * Test addLabel
	 * 
	 * @throws SiteTreeException
	 */
    final public void testAddLabel() throws SiteTreeException {
		Label label = new Label("Tutorial", null);
		Label[] labels = null;

		siteTree.addLabel("/foo/bar", label);
		labels = siteTree.getNode("foo/bar").getLabels();
		assertEquals(labels.length, 3);
		label = siteTree.getNode("/foo/bar").getLabel("");
		assertNotNull(label);
		assertEquals(label.getLabel(), "Tutorial");
    }

    final public void testRemoveLabel() {
        //TODO Implement removeLabel().
    }

    final public void testRemoveNode() {
        //TODO Implement removeNode().
    }

    final public void testGetNode() {
        //TODO Implement getNode().
    }

    final public void testSave() {
        //TODO Implement save().
    }

}
