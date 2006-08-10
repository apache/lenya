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

import java.util.Arrays;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.util.Assert;

/**
 * Abstract site node implementation.
 */
public abstract class AbstractSiteNode implements SiteNode {

    private String path;
    private SiteStructure structure;
    private String uuid;

    protected AbstractSiteNode(Publication publication, SiteStructure structure, String path, String uuid) {
        super();
        
        Assert.notNull("structure", structure);
        this.structure = structure;
        
        Assert.notNull("path", path);
        Assert.isTrue("path starts with /", path.startsWith("/"));
        this.path = path;
        
        Assert.notNull("uuid", uuid);
        this.uuid = uuid;
    }

    public String getPath() {
        return path;
    }

    public SiteStructure getStructure() {
        return this.structure;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SiteNode)) {
            return false;
        }
        String thisKey = getKey(getStructure().getPublication(),
                getStructure().getArea(),
                getPath());
        SiteNode node = (SiteNode) obj;
        String nodeKey = getKey(node.getStructure().getPublication(),
                node.getStructure().getArea(),
                node.getPath());
        return thisKey.equals(nodeKey);
    }

    public int hashCode() {
        return getKey(getStructure().getPublication(), getStructure().getArea(), getPath()).hashCode();
    }

    protected static String getKey(Publication pub, String area, String docId) {
        return pub.getId() + ":" + area + ":" + docId;
    }

    public SiteNode getParent() throws SiteException {
        String id = getPath().substring(1);
        String[] steps = id.split("/");
        if (steps.length == 1) {
            return null;
        } else {
            int lastIndex = id.lastIndexOf("/");
            String parentId = id.substring(0, lastIndex);
            return getStructure().getNode("/" + parentId);
        }
    }

    public String getUuid() {
        return this.uuid;
    }

    public boolean hasLink(String language) {
        return Arrays.asList(getLanguages()).contains(language);
    }

}
