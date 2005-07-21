/*
 * Copyright  1999-2005 The Apache Software Foundation
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

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.repository.SourceNode;
import org.apache.lenya.transaction.IdentityMap;

/**
 * JCR based source node.
 */
public class JCRSourceNode extends SourceNode {
    
    private String sourceUri;

    /**
     * Ctor.
     * @param map The identity map.
     * @param sourceURI The source URI.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public JCRSourceNode(IdentityMap map, String sourceURI, ServiceManager manager, Logger logger) {
        super(map, sourceURI, manager, logger);
        this.sourceUri = sourceURI;
    }

    /**
     * @see org.apache.lenya.cms.repository.SourceNode#getRealSourceURI()
     */
    protected String getRealSourceURI() {
        String path = this.sourceUri.substring(LENYA_PROTOCOL.length());
        return "jcr://" + path;
    }

}
