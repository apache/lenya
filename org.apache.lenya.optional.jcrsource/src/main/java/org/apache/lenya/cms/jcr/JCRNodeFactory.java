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
package org.apache.lenya.cms.jcr;

import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.Session;

/**
 * JCR node factory.
 */
public class JCRNodeFactory extends AbstractLogEnabled implements NodeFactory {
    
    private SourceResolver sourceResolver;

    public RepositoryItem buildItem(Session session, String key) throws RepositoryException {
        return new JCRSourceNode(this.session, key, getSourceResolver(), getLogger());
    }

    public String getItemType() {
        return Node.IDENTIFIABLE_TYPE;
    }
    
    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean isSharable() {
        return false;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    public SourceResolver getSourceResolver() {
        return sourceResolver;
    }

}