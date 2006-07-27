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

import org.apache.lenya.cms.publication.Publication;

public final class SiteNode {

    private String path;
    private String area;
    private Publication publication;
    
    protected SiteNode(Publication publication, String area, String path) {
        super();
        this.publication = publication;
        this.area = area;
        this.path = path;
    }

    public String getArea() {
        return area;
    }

    public String getPath() {
        return path;
    }

    public Publication getPublication() {
        return publication;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SiteNode)) {
            return false;
        }
        String thisKey = getKey(getPublication(), getArea(), getPath());
        SiteNode node = (SiteNode) obj;
        String nodeKey = getKey(node.getPublication(), node.getArea(), node.getPath());
        return thisKey.equals(nodeKey);
    }

    public int hashCode() {
        return getKey(getPublication(), getArea(), getPath()).hashCode();
    }
    
    protected static String getKey(Publication pub, String area, String docId) {
        return pub.getId() + ":" + area + ":" + docId;
    }
    
    public SiteNode getParent() {
        String id = getPath().substring(1);
        String[] steps = id.split("/");
        if (steps.length == 1) {
            return null;
        }
        else {
            int lastIndex = id.lastIndexOf("/");
            String parentId = id.substring(0, lastIndex);
            return NodeFactory.getNode(getPublication(), getArea(), "/" + parentId);
        }
    }
    
}
