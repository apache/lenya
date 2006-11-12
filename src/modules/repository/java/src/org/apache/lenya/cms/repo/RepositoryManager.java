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
package org.apache.lenya.cms.repo;

import java.io.File;

/**
 * Repository manager.
 */
public class RepositoryManager {

    /**
     * @param webappPath The directory of the web application.
     * @param repositoryFactoryClass The class of the repository factory.
     * @return A repository.
     * @throws RepositoryException if the repository object could not be generated.
     */
    public static Repository getRepository(String webappPath, String repositoryFactoryClass)
            throws RepositoryException {
        try {
            Class repoFactoryClass = Class.forName(repositoryFactoryClass);
            RepositoryFactory repoFactory = (RepositoryFactory) repoFactoryClass.newInstance();

            File webappDirectory = new File(webappPath);

            String jaasPath = "lenya/modules/jackrabbit/repository/jaas.config";
            System.setProperty("java.security.auth.login.config", new File(webappDirectory,
                    jaasPath).getAbsolutePath());

            Repository repository = repoFactory.getRepository(webappPath);
            return repository;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }
    
}
