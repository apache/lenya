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
package org.apache.lenya.cms.jcr.jackrabbit;

import java.io.File;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.lenya.cms.jcr.RepositoryFactory;

/**
 * Jackrabbit-based repository factory.
 */
public class JackrabbitRepositoryFactory implements RepositoryFactory {

    protected static final String homePath = "lenya/modules/jackrabbit/repository".replace('/',
            File.separatorChar);
    protected static final String configFilePath = "repository.xml";

    public Repository getRepository(String webappPath) throws RepositoryException {
        File webappDirectory = new File(webappPath);
        File repoDirectory = new File(webappDirectory, homePath);
        File configFile = new File(repoDirectory, configFilePath);
        RepositoryConfig repoConfig = RepositoryConfig.create(configFile.getAbsolutePath(),
                repoDirectory.getAbsolutePath());
        Repository repo = RepositoryImpl.create(repoConfig);
        
        return repo;
    }

}
