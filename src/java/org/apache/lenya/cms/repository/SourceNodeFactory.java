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
package org.apache.lenya.cms.repository;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.transaction.Identifiable;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.IdentifiableFactory;

/**
 * Factory to create source nodes.
 *
 * @version $Id$
 */
public class SourceNodeFactory extends AbstractLogEnabled implements IdentifiableFactory {

    private ServiceManager manager;
    
    /**
     * Ctor.
     * @param manager
     * @param logger
     */
    public SourceNodeFactory(ServiceManager manager, Logger logger) {
        this.manager = manager;
        enableLogging(logger);
    }
    
    /**
     * @see org.apache.lenya.transaction.IdentifiableFactory#build(org.apache.lenya.transaction.IdentityMap, java.lang.String)
     */
    public Identifiable build(IdentityMap map, String key) throws Exception {
        return new SourceNode(map, key, this.manager, getLogger());
    }

}
