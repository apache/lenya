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
package org.apache.lenya.cms.jcr;

import java.io.File;

import javax.jcr.Repository;

import org.apache.lenya.cms.repo.DocumentTypeRegistry;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.impl.DocumentTypeRegistryImpl;
import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;
import org.apache.lenya.cms.repo.metadata.impl.MetaDataRegistryImpl;

/**
 * Facade to the JCR repository, providing Lenya-specific access.
 */
public class JCRRepository implements org.apache.lenya.cms.repo.Repository {

    private File webappDirectory;
    
    /**
     * Ctor.
     * @param repository The repository.
     * @param webappDirectory The web application directory.
     */
    public JCRRepository(Repository repository, File webappDirectory) {
        this.repository = repository;
        this.webappDirectory = webappDirectory;
    }

    private Repository repository;

    protected Repository getRepository() {
        return this.repository;
    }

    /**
     * @return The repository session.
     */
    public org.apache.lenya.cms.repo.Session createSession() {
        return new JCRSession(this);
    }

    private DocumentTypeRegistry documentTypeRegistry;

    public DocumentTypeRegistry getDocumentTypeRegistry() throws org.apache.lenya.cms.repo.RepositoryException {
        if (this.documentTypeRegistry == null) {
            this.documentTypeRegistry = new DocumentTypeRegistryImpl();
        }
        return this.documentTypeRegistry;
    }
    
    private MetaDataRegistry metaDataRegistry;

    public MetaDataRegistry getMetaDataRegistry() throws RepositoryException {
        if (this.metaDataRegistry == null) {
            this.metaDataRegistry = new MetaDataRegistryImpl();
        }
        return this.metaDataRegistry;
    }
    
    protected File getWebappDirectory() {
        return this.webappDirectory;
    }

}
