/*
$Id: DefaultSiteTreeTest.java,v 1.8 2003/08/27 14:45:19 egli Exp $
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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

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
		siteTree = new DefaultSiteTree("testTree.xml");
		Label label = new Label("Foo", "en");
		Label[] fooLabels = { label };
		siteTree.addNode("/foo", fooLabels, null, null, false);
		label = new Label("Home", "en");
		Label[] homeLabels = { label };
		siteTree.addNode("/index", homeLabels, null, null, false);
		label = new Label("Bar", "en");
		Label label_de = new Label("Stab", "de");
		Label[] barLabels = { label, label_de };
		siteTree.addNode("/foo/bar", barLabels, "http://exact.biz", "suffix", true);
		label = new Label("Lala", "en");
		Label[] lalaLabels = { label };
		siteTree.addNode("/foo/lala", lalaLabels, null, null, false);
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

		siteTree.addNode("/foo", "tutorial", labels);
		SiteTreeNode node =  siteTree.getNode("/foo/tutorial");
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
     * 
     * @throws SiteTreeException if an error occurs
     */
    final public void testAddNodeStringLabelArrayStringStringboolean() throws SiteTreeException {
		Label label1 = new Label("Doh", "en");
		Label label2 = new Label("Ding", "en");
		Label[] labels = { label1, label2};

		siteTree.addNode("/foo/ding", labels, null, null, false);
		
		assertNotNull(siteTree.getNode("/foo/ding"));
		assertEquals(siteTree.getNode("/foo/ding").getId(), "ding");
    }

    /**
     * Test for void addNode(String, String, Label[], String, String, boolean)
     * 
     * @throws SiteTreeException if an error occurs
     */
    final public void testAddNodeStringStringLabelArrayStringStringboolean() throws SiteTreeException {
		Label label1 = new Label("Doh", "en");
		Label label2 = new Label("Ding", "en");
		Label[] labels = { label1, label2};

		siteTree.addNode("/foo", "baz", labels, null, null, false);
		
		assertNotNull(siteTree.getNode("/foo/baz"));
		assertEquals(siteTree.getNode("/foo/baz").getId(), "baz");		
    }

	/**
	 * Test addLabel
	 * 
	 * @throws SiteTreeException if an error occurs
	 */
    final public void testAddLabel() throws SiteTreeException {
		Label label = new Label("Tutorial", null);
		Label[] labels = null;

		siteTree.addLabel("/foo/bar", label);
		labels = siteTree.getNode("/foo/bar").getLabels();
		assertEquals(labels.length, 3);
		label = siteTree.getNode("/foo/bar").getLabel("");
		assertNotNull(label);
		assertEquals(label.getLabel(), "Tutorial");
    }

	/**
	 * Test removeLabel
	 *
	 */
    final public void testRemoveLabel() {
		assertEquals(siteTree.getNode("/foo/bar").getLabels().length, 2);
		
		Label label = new Label("Stab", "de");
		siteTree.removeLabel("/foo/bar", label);
		assertEquals(siteTree.getNode("/foo/bar").getLabels().length, 1);
		
		assertEquals(siteTree.getNode("/foo/bar").getLabels()[0], new Label("Bar", "en"));
		
		siteTree.addLabel("/foo/bar", label);
		assertEquals(siteTree.getNode("/foo/bar").getLabels().length, 2);
    }

	/**
	 * Test removeNode
	 * 
	 * @throws SiteTreeException if an error occurs
	 */
    final public void testRemoveNode() throws SiteTreeException {
    	Label label1 = new Label("Hi", "en");
    	Label label2 = new Label("Ho", "en");
    	Label[] labels1 = { label1, label2};
    	
    	siteTree.addNode("/hi", labels1, null, null, false);

		Label[] labels2 = { label1, label2};

		siteTree.addNode("/hi/ho", labels2, null, null, false);
		
		assertNotNull(siteTree.getNode("/hi/ho"));
		
		siteTree.removeNode("/hi");
		
		assertNull(siteTree.getNode("/hi/ho"));
		assertNull(siteTree.getNode("/hi"));
    }

	/**
	 * Test getNode
	 *
	 */
    final public void testGetNode() {
		assertNotNull(siteTree.getNode("/foo/bar"));
		
		assertNull(siteTree.getNode("/foo/baz"));
    }

	/**
	 * Test save
	 *
	 */
    final public void testSave() {
        //TODO Implement save().
    }
    
	/**
	 * Test moving a node up
	 * 
	 * @throws SiteTreeException if an error occurs
	 * @throws IOException if an error occurs
	 * @throws TransformerException if an error occurs
	 */
	final public void testMoveUp() throws SiteTreeException, IOException, TransformerException {
		siteTree.moveUp("/foo/lala");
		siteTree.save();
		assertNotNull(siteTree.getNode("/foo/lala"));
	}
	
	/**
	 * Test moving a node down
	 * 
	 * @throws SiteTreeException if an error occurs
	 * @throws IOException if an error occurs
	 * @throws TransformerException if an error occurs
	 */
	final public void testMoveDown() throws SiteTreeException, IOException, TransformerException {
		siteTree.moveDown("/foo");
		siteTree.save();
		assertNotNull(siteTree.getNode("/foo"));
	}
    
    /**
     * Test the import of a subtree
     * 
     * @throws ParserConfigurationException if an error occurs.
     * @throws SAXException if an error occurs.
     * @throws SiteTreeException if an error occurs.
     * @throws IOException if an error occurs.
     * @throws TransformerException if an error occurs.
     */
	final public void testImportSubtree() throws ParserConfigurationException, SAXException, SiteTreeException, IOException, TransformerException {
		DefaultSiteTree newSiteTree = new DefaultSiteTree("importedTree.xml");
		Label label = new Label("root", "en");
		Label[] rootLabels = { label };
		newSiteTree.addNode("/root", rootLabels, null, null, false);
		label = new Label("foo", "en");
		Label[] fooLabels = { label };
		newSiteTree.addNode("/root/foo", fooLabels, null, null, false);
		label = new Label("subtree", "en");
		Label[] subtreeLabels = { label };
		newSiteTree.addNode("/root/subtree", subtreeLabels, "http://exact.biz", "suffix", true);
		label = new Label("child", "en");
		Label[] childLabels = { label };
		newSiteTree.addNode("/root/subtree/child", childLabels, null, null, false);
		SiteTreeNode node=newSiteTree.getNode("/root/subtree");
		assertNotNull(node);
		SiteTreeNode parentNode=siteTree.getNode("/foo/lala");
		assertNotNull(parentNode);
        siteTree.importSubtree(parentNode,node, "subtree");
		siteTree.save();
		assertNotNull(siteTree.getNode("/foo/lala/subtree"));
		assertNotNull(siteTree.getNode("/foo/lala/subtree/child"));
	}
}
