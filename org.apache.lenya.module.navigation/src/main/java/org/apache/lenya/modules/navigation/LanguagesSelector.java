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
 * Select the node which is identified by the <em>path</em> parameter and its
 * links. The currently selected link will have the attribute <em>current="true"</em>.
 */
public class LanguagesSelector implements FragmentSelector {

    public void selectFragment(NodeGenerator generator, SiteStructure site, String path,
            String language) throws SAXException, SiteException {
        if (site.contains(path)) {
            SiteNode node = site.getNode(path);
            generator.startNode(node);
            String[] languages = node.getLanguages();
            for (int i = 0; i < languages.length; i++) {
                generator.generateLink(node, languages[i]);
            }
            generator.endNode(node);
        }
    }

}
