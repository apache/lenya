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
 * Select the node identified by the <em>path</em> parameter, its siblings,
 * and its children recursively. If the path is "" or "/", the nodes of the
 * whole site are generated. Only the links for the currently selected language
 * are generated.
 */
public class SubtreeAndSiblingsSelector implements FragmentSelector {

    public void selectFragment(NodeGenerator generator, SiteStructure site, String path, String lang)
            throws SAXException, SiteException {
        if (path.equals("/") || path.equals("")) {
            SiteNode[] nodes = site.getTopLevelNodes();
            selectNodes(generator, nodes, lang);
        } else if (site.contains(path)) {
            SiteNode node = site.getNode(path);
            SiteNode[] siblings = getSiblings(node);
            for (int i = 0; i < siblings.length; i++) {
                if (siblings[i] == node) {
                    selectSubtree(generator, siblings[i], lang);
                } else {
                    selectNode(generator, siblings[i], lang);
                }
            }
        }
    }

    protected void selectNode(NodeGenerator generator, SiteNode node, String lang)
            throws SAXException {
        generator.startNode(node);
        generator.generateLink(node, lang);
        generator.endNode(node);
    }

    protected SiteNode[] getSiblings(SiteNode node) throws SiteException {
        return node.isTopLevel() ? node.getStructure().getTopLevelNodes() : node.getParent()
                .getChildren();
    }

    protected void selectSubtree(NodeGenerator generator, SiteNode node, String lang)
            throws SAXException {
        generator.startNode(node);
        generator.generateLink(node, lang);
        selectNodes(generator, node.getChildren(), lang);
        generator.endNode(node);
    }

    protected void selectNodes(NodeGenerator generator, SiteNode[] nodes, String lang)
            throws SAXException {
        for (int i = 0; i < nodes.length; i++) {
            selectSubtree(generator, nodes[i], lang);
        }
    }

}
