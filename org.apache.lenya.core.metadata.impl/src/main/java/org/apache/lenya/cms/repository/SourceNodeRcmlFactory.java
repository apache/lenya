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

import java.util.HashMap;
import java.util.Map;

import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.rc.RCML;

/**
 * Factory for source node RCML objects.
 */
public class SourceNodeRcmlFactory {

    private SourceResolver sourceResolver;

    private Map uri2rcml = new HashMap();

    /**
     * @param node The node.
     * @param manager The service manager.
     * @return An RCML object.
     */
    public synchronized RCML getRcml(SourceNode node) {
        String uri = node.getSourceURI();
        RCML rcml = (RCML) this.uri2rcml.get(uri);
        if (rcml == null) {
            rcml = new SourceNodeRCML(node.getContentSource().getRealSourceUri(), node
                    .getMetaSource().getRealSourceUri(), getSourceResolver());
            this.uri2rcml.put(uri, rcml);
        }
        return rcml;
    }

    public SourceResolver getSourceResolver() {
        return sourceResolver;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }
    
    
}
