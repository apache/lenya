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
package org.apache.lenya.cms.site;

import java.util.Arrays;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.lenya.cms.publication.Publication;

/**
 * Abstract site node implementation.
 */
public abstract class AbstractSiteNode extends AbstractLogEnabled implements SiteNode {

    private String path;
    private SiteStructure structure;
    private String uuid;

    protected AbstractSiteNode(Publication publication, SiteStructure structure, String path,
            String uuid, Log logger)
    {
    	Validate.notNull(structure);
    	Validate.notNull(path);
        Validate.isTrue(path.startsWith("/"), "Path must start with /");
        Validate.notNull(uuid);
        this.structure = structure;
        this.path = path;
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
            throw new SiteException("The node [" + getPath() + "] is a top-level node.");
        } else {
            int lastIndex = id.lastIndexOf("/");
            String parentId = id.substring(0, lastIndex);
            return getStructure().getNode("/" + parentId);
        }
    }
    
    public boolean isTopLevel() {
        return getPath().lastIndexOf("/") == 0;
    }

    public String getUuid() {
        return this.uuid;
    }

    public boolean hasLink(String language) {
        return Arrays.asList(getLanguages()).contains(language);
    }

}
