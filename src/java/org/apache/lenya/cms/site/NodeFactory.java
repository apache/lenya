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
package org.apache.lenya.cms.site;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;

public class NodeFactory {

    private static Map nodes = new WeakHashMap();
    
    public static SiteNode getNode(Publication pub, String area, String docId) {
        String key = SiteNode.getKey(pub, area, docId);
        SiteNode node = null;
        if (nodes.containsKey(key)) {
            node = (SiteNode) nodes.get(key);
        }
        else {
            node = new SiteNode(pub, area, docId);
            nodes.put(key, node);
        }
        return node;
    }
    
    public static SiteNode getNode(Document document) {
        return getNode(document.getPublication(), document.getArea(), document.getId());
    }
    
}
