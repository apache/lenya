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
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataOwner;

/**
 * Provides access to a meta data source.
 */
public class MetaSourceWrapper extends SourceWrapper implements MetaDataOwner {

    protected static final String LENYA_META_SUFFIX = "meta";

    /**
     * Ctor.
     * @param node
     * @param sourceURI
     * @param manager
     * @param logger
     */
    public MetaSourceWrapper(SourceNode node, String sourceURI, ServiceManager manager,
            Logger logger) {
        super(node, sourceURI + "." + LENYA_META_SUFFIX, manager, logger);
        this.handler = new ModifiableMetaDataHandler(manager, this);
    }
    
    private ModifiableMetaDataHandler handler;

    public MetaData getMetaData(String namespaceUri) throws MetaDataException {
        return this.handler.getMetaData(namespaceUri);
    }

    public String[] getMetaDataNamespaceUris() throws MetaDataException {
        return this.handler.getMetaDataNamespaceUris();
    }

}
