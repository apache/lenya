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
package org.apache.lenya.cms.cocoon.components.modules.input;

import javax.servlet.http.HttpServletRequest;

import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentFactoryBuilder;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryManager;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * Super class for operation-based input modules.
 * 
 * @version $Id$
 */
public class OperationModule extends AbstractInputModule {

    private RepositoryManager repositoryManager;
    private DocumentFactory documentFactory;
    private DocumentFactoryBuilder documentFactoryBuilder;

    protected DocumentFactory getDocumentFactory() {
        ProcessInfoProvider processInfo = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        if (this.documentFactory == null) {
            HttpServletRequest request = processInfo.getRequest();
            try {
                Session session = RepositoryUtil.getSession(getRepositoryManager(), request);
                this.documentFactory = getDocumentFactoryBuilder().createDocumentFactory(
                        session);
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
        return this.documentFactory;
    }

    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public DocumentFactoryBuilder getDocumentFactoryBuilder() {
        return documentFactoryBuilder;
    }

    public void setDocumentFactoryBuilder(DocumentFactoryBuilder documentFactoryBuilder) {
        this.documentFactoryBuilder = documentFactoryBuilder;
    }

}
