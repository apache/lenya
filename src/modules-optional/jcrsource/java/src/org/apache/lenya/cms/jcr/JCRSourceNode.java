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

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.repository.SourceNode;

/**
 * JCR based source node.
 */
public class JCRSourceNode extends SourceNode {
    
    private String sourceUri;

    /**
     * Ctor.
     * @param session The session.
     * @param sourceURI The source URI.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public JCRSourceNode(Session session, String sourceURI, ServiceManager manager, Logger logger) {
        super(session, sourceURI, manager, logger);
        this.sourceUri = sourceURI;
    }

    /**
     * @see org.apache.lenya.cms.repository.SourceNode#getRealSourceUri()
     */
    protected String getRealSourceURI() {
        String path = this.sourceUri.substring(LENYA_PROTOCOL.length());
        return "jcr://" + path;
    }
    
    /**
     * No separate meta data node, so no locking is required.
     * @see org.apache.lenya.cms.repository.SourceNode#lockMetaData()
     */
    protected void lockMetaData() throws RepositoryException {
    }

}
