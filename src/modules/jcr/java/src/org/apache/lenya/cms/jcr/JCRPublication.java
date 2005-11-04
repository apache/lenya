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
package org.apache.lenya.cms.jcr;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

public class JCRPublication {

    private String pubId;
    private RepositorySession session;

    public JCRPublication(RepositorySession session, String pubId) {
        this.pubId = pubId;
        this.session = session;
    }

    protected String getPublicationId() {
        return this.pubId;
    }

    protected RepositorySession getSession() {
        return this.session;
    }
    
    private Map areas = new HashMap();
    
    public JCRArea getArea(String area) {
        JCRArea jcrArea = (JCRArea) this.areas.get(area);
        if (jcrArea == null) {
            jcrArea = new JCRArea(this, area);
            this.areas.put(area, jcrArea);
        }
        return jcrArea;
    }

}
