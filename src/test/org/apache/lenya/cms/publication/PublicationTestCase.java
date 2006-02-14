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
package org.apache.lenya.cms.publication;

import org.apache.cocoon.core.container.ContainerTestCase;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.IdentityMapImpl;

public class PublicationTestCase extends ContainerTestCase {

    private DocumentIdentityMap identityMap;
    
    protected DocumentIdentityMap getIdentityMap() {
        if (this.identityMap == null) {
            IdentityMap map = new IdentityMapImpl(getLogger());
            this.identityMap = new DocumentIdentityMap(map, getManager(), getLogger());
        }
        return this.identityMap;
    }
    
}
