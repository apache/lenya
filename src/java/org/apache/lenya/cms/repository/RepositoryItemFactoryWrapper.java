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

import org.apache.lenya.transaction.IdentifiableFactory;
import org.apache.lenya.transaction.IdentityMap;

/**
 * Wraps a repository item factory to resemble an identifiable factory.
 */
public class RepositoryItemFactoryWrapper implements IdentifiableFactory {
    
    private RepositoryItemFactory delegate;
    private Session session;
    
    /**
     * Ctor.
     * @param delegate The factory to wrap.
     * @param session The session.
     */
    public RepositoryItemFactoryWrapper(RepositoryItemFactory delegate, Session session) {
        this.delegate = delegate;
        this.session = session;
    }

    public Object build(IdentityMap map, String key) throws Exception {
        return delegate.buildItem(this.session, key);
    }

    public String getType() {
        return this.delegate.getItemType();
    }

}
