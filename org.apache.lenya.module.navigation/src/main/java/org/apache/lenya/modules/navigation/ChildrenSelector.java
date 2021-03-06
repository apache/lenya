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
package org.apache.lenya.modules.navigation;

import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.xml.sax.SAXException;

/**
 * Select the child nodes of the node identified by the <em>path</em>
 * parameter non-recursively. If the path is "" or "/", the top-level nodes are
 * generated.
 */
public class ChildrenSelector implements FragmentSelector {

    public void selectFragment(NodeGenerator generator, SiteStructure site, String path, String lang)
            throws SAXException, SiteException {
        SiteNode[] nodes;
        if (path.equals("/") || path.equals("")) {
            nodes = site.getTopLevelNodes();
        } else {
            if (site.contains(path)) {
                nodes = site.getNode(path).getChildren();
            } else {
                nodes = new SiteNode[0];
            }
        }
        for (int i = 0; i < nodes.length; i++) {
            generator.startNode(nodes[i]);
            generator.generateLink(nodes[i], lang);
            generator.endNode(nodes[i]);
        }
    }

}
