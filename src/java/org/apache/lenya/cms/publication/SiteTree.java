/*
 * $Id: SiteTree.java,v 1.6 2003/05/28 12:56:24 egli Exp $
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
package org.apache.lenya.cms.publication;

public interface SiteTree {

    /**
     * Add a node.
	 * @param parentid e.g. "/tutorial"
	 * @param id e.g. "concepts"
	 * @param labels
	 * @throws SiteTreeException
	 */
	void addNode(String parentid, String id, Label[] labels)
	throws SiteTreeException;
    
    /**
     * Add a node.
	 * @param parentid
	 * @param id
	 * @param labels
	 * @param href
	 * @param suffix
	 * @param link
	 * @throws SiteTreeException
	 */
	void addNode(String parentid, String id, Label[] labels,
		 String href, String suffix, boolean link)
	throws SiteTreeException;
    
    /**
     * Add a node. This method is typically used when publishing,
     * i.e. when copying a node from the authoring tree to the live
     * tree. The existing node already has a parent node (in the
     * authoring tree). The node that is added will be a copy of
     * the original node and will be inserted at the same parentid
     * as the original node.
	 * @param node
	 * @throws SiteTreeException
	 */
	void addNode(SiteTreeNode node)
	throws SiteTreeException;
    
    /**
     * Delete the node with the given document-id
	 * @param id
	 */
	void deleteNode(String id);
    
    /**
     * Return the Node for a given document-id
	 * @param documentId
	 * @return
	 */
	SiteTreeNode getNode(String documentId);
}
