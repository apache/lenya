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

/* $Id: SiteTreeNode.java 155270 2005-02-24 22:43:34Z andreas $  */

package org.apache.lenya.cms.site.tree;

import org.apache.lenya.cms.site.SiteNode;

/**
 * This interface is a wrapper around the more general w3c.Node which
 * hides some details which are irrelevant for site tree nodes. It basically
 * delegates everything to the Node.
 */
public interface SiteTreeNode extends SiteNode {

    /**
     * Get the sitetreenodes, which are the siblings preceding this node
     * 
     * @return the children.
     */
    SiteTreeNode[] getPrecedingSiblings();

	/**
	 * Get the sitetreenodes, which are the siblings following this node
	 * 
	 * @return the children.
	 */
	SiteTreeNode[] getNextSiblings();

}
