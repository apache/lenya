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
package org.apache.lenya.cms.usecase;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.cms.publication.DocumentIdentityMap;

/**
 * Default implementation of a unit of work.
 * 
 * @version $Id$
 */
public class UnitOfWorkImpl extends AbstractLogEnabled implements UnitOfWork {

    /**
     * Ctor.
     */
    public UnitOfWorkImpl() {
        // do nothing
    }

    private DocumentIdentityMap identityMap;

    /**
     * Returns the document identity map.
     * @return A document identity map.
     */
    public DocumentIdentityMap getIdentityMap() {
        if (this.identityMap == null) {
            this.identityMap = new DocumentIdentityMap();
        }
        return this.identityMap;
    }


}