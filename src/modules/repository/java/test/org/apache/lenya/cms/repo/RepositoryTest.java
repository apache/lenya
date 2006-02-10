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
package org.apache.lenya.cms.repo;

import java.net.URL;

import org.apache.cocoon.components.validation.Validator;
import org.apache.lenya.cms.repo.impl.AssetTypeImpl;
import org.apache.lenya.cms.repo.mock.AssetTypeResolverImpl;
import org.apache.lenya.xml.Schema;

import junit.framework.TestCase;

/**
 * JCR repository test.
 */
public class RepositoryTest extends TestCase {

    protected static final String AREA_ID = "authoring";
    protected static final String PUBLICATION_ID = "test";
    protected static final String LANGUAGE_DE = "de";
    protected static final String ASSET_TYPYE = "xhtml";
    

    /**
     * Ctor.
     */
    public RepositoryTest() {
        setWebappDirectory(System.getProperty("test.repo.webappDirectory"));
        setRepositoryFactory(System.getProperty("test.repo.repositoryFactory"));
    }

    private String webappDirectory;
    private String repositoryFactory;

    /**
     * @param webappDirectory The web application directory.
     */
    public void setWebappDirectory(String webappDirectory) {
        this.webappDirectory = webappDirectory;
    }

    /**
     * @return The web application directory.
     */
    public String getWebappDirectory() {
        return this.webappDirectory;
    }

    /**
     * @return The repository factory classname.
     */
    public String getRepositoryFactory() {
        return repositoryFactory;
    }

    /**
     * @param repositoryFactory The repository factory classname.
     */
    public void setRepositoryFactory(String repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    private Session session;

    protected Session getSession() throws RepositoryException {
        if (this.session == null) {
            Repository repo = RepositoryManager.getRepository(getWebappDirectory(),
                    getRepositoryFactory());
            this.session = repo.login("john");

            URL schemaUrl = getClass().getResource("schema.xml");
            Schema schema = new Schema(Validator.GRAMMAR_RELAX_NG, schemaUrl.toString());
            AssetType doctype = new AssetTypeImpl(ASSET_TYPYE, schema, true);
            AssetTypeResolverImpl resolver = new AssetTypeResolverImpl();
            resolver.register(doctype);
            repo.setAssetTypeResolver(resolver);

            if (session.existsPublication(PUBLICATION_ID)) {
                Publication pub = session.getPublication(PUBLICATION_ID);
                pub.remove();
            }
        }
        return this.session;
    }

}
