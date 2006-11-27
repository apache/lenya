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

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataOwner;

/**
 * Provides access to a meta data source.
 */
public class MetaSourceWrapper extends SourceWrapper {

    protected static final String LENYA_META_SUFFIX = "meta";

    /**
     * Ctor.
     * @param node
     * @param sourceURI
     * @param manager
     * @param logger
     */
    public MetaSourceWrapper(SourceNode node, String sourceURI, ServiceManager manager, Logger logger) {
        super(node, sourceURI + "." + LENYA_META_SUFFIX, manager, logger);
    }

    private SourceNodeMetaDataHandler metaDataHandler = null;
    
    protected SourceNodeMetaDataHandler getMetaDataHandler() {
        if (this.metaDataHandler == null) {
            this.metaDataHandler = new SourceNodeMetaDataHandler(this.manager, getRealSourceUri());
        }
        return this.metaDataHandler;
    }

    /**
     * @return All supported meta data namespace URIs.
     * @throws MetaDataException if an error occurs.
     * @see MetaDataOwner#getMetaDataNamespaceUris()
     */
    public String[] getMetaDataNamespaceUris() throws MetaDataException {
        return getMetaDataHandler().getMetaDataNamespaceUris();
    }
    
}
