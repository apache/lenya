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

import java.util.StringTokenizer;

import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.xml.sax.SAXException;

/**
 * Generate the nodes from the top-level node down to the node identified by the
 * <em>path</em> parameter. Only the links of the currently selected language
 * are included.
 */
public class BreadcrumbSelector implements FragmentSelector {

    public void selectFragment(NodeGenerator generator, SiteStructure site, String path, String lang)
            throws SAXException, SiteException {
        StringTokenizer tokens = new StringTokenizer(path, "/");
        StringBuffer currentPath = new StringBuffer();
        while (tokens.hasMoreTokens()) {
            String name = tokens.nextToken();
            currentPath.append("/").append(name);
            String pathString = currentPath.toString();
            if (site.contains(pathString)) {
                SiteNode node = site.getNode(pathString);
                generator.startNode(node);
                generator.generateLink(node, lang);
                generator.endNode(node);
            } else {
                break;
            }
        }
    }

}
