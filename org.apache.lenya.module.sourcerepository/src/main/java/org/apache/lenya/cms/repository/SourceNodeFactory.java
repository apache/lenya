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
package org.apache.lenya.cms.repository;

import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.excalibur.source.SourceResolver;

/**
 * Factory to create source nodes.
 * 
 * @version $Id$
 */
public class SourceNodeFactory extends AbstractLogEnabled implements NodeFactory {

    private SourceResolver sourceResolver;
    private SourceNodeRcmlFactory rcmlFactory;

    /**
     * Ctor.
     */
    public SourceNodeFactory() {
    }

    public RepositoryItem buildItem(Session session, String key) throws RepositoryException {
        SourceNode node = new SourceNode(session, key, getSourceResolver(), getLogger());
        node.setRcmlFactory(this.rcmlFactory);
        return node;
    }

    public String getItemType() {
        return Node.IDENTIFIABLE_TYPE;
    }

    public SourceResolver getSourceResolver() {
        return sourceResolver;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    public void setRcmlFactory(SourceNodeRcmlFactory rcmlFactory) {
        this.rcmlFactory = rcmlFactory;
    }

}
