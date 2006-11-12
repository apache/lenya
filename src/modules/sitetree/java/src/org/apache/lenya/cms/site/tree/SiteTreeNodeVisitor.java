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

/* $Id: SiteTreeNodeVisitor.java 123348 2004-12-25 22:49:57Z gregor $  */

package org.apache.lenya.cms.site.tree;

import org.apache.lenya.cms.publication.DocumentException;

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
