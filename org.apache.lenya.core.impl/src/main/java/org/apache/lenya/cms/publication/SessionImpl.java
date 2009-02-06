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
package org.apache.lenya.cms.publication;

import org.springframework.util.Assert;

public class SessionImpl implements Session {

    public SessionImpl(RepositoryImpl repository,
            org.apache.lenya.cms.repository.Session repoSession) {
        Assert.notNull(repository, "repository");
        Assert.notNull(repoSession, "repository session");
        this.repository = repository;
        this.repositorySession = repoSession;
    }

    private RepositoryImpl repository;
    private org.apache.lenya.cms.repository.Session repositorySession;
    private DocumentFactory documentFactory;

    public Publication getPublication(String id) {
        try {
            return getDocumentFactory().getPublication(id);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
    }

    public Repository getRepository() {
        return this.repository;
    }

    public DocumentFactory getDocumentFactory() {
        if (this.documentFactory == null) {
            this.documentFactory = DocumentUtil.createDocumentFactory(this.repositorySession);
        }
        return this.documentFactory;
    }

    public boolean existsPublication(String id) {
        return getDocumentFactory().existsPublication(id);
    }

}
