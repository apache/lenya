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

/* $Id: SiteTreeNodeVisitor.java,v 1.3 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;

/**
 * Visitor interface to define operations performed on SiteTreeNode
 * (Visitor pattern)
 *
 */
public interface SiteTreeNodeVisitor {

	/**
	 * Operation performed on a SiteTreeNode
	 * @param node The SiteTreeNode.
     * 
     * @throws DocumentException if an error occurs
	 */
	void visitSiteTreeNode(SiteTreeNode node) throws DocumentException;

}
