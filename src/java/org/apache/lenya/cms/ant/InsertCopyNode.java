/*
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ant;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.xml.sax.SAXException;

/**
 * Ant task that copies a node of a tree and inserts it in the same tree 
 * @author edith
 *
 **/
public class InsertCopyNode extends TwoNodesTask {
	
	/**
	 * Creates a new instance of InsertCopyNode
	 */
	public InsertCopyNode() {
		super();
	}
	    
	/**
	 * copies a node of a tree and inserts it in the same tree 
	 * @param firstdocumentid : document-id of the copied document
	 * @param secdocumentid : document-id of the new document
	 * @param absolutetreepath : absolute path of the tree
 	 */
	public void manipulateTree(String firstdocumentid, String secdocumentid, String absolutetreepath)
	throws ParserConfigurationException, SAXException, IOException, SiteTreeException {	    

		DefaultSiteTree tree = null;
		try {
			tree = new DefaultSiteTree(absolutetreepath);
			
			SiteTreeNode node = tree.getNode(firstdocumentid);
             
			if (node != null) {
				tree.addNode(secdocumentid, node.getLabels(), node.getHref(), node.getSuffix(), node.hasLink());
			} else {
				throw new SiteTreeException("Node " + node + " couldn't be found");
			}
			tree.save();
		} catch (Exception e) {
			throw new SiteTreeException(e);
		}

	}

}
