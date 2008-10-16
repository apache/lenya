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

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.linking.UuidToUrlRewriter;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.util.ServletHelper;

/**
 * Transform lenya-document: URLs to web application URLs.
 * @see UuidToUrlRewriter
 */
public class UuidToUrlModule extends AbstractInputModule implements Serviceable {
    
    private ServiceManager manager;

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        try {
            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            LinkResolver linkResolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);
            String currentUrl = ServletHelper.getWebappURI(request);
            
            UuidToUrlRewriter rewriter = new UuidToUrlRewriter(currentUrl, linkResolver, factory);
            if (factory.isDocument(currentUrl)) {
                rewriter.setCurrentDocument(factory.getFromURL(currentUrl));
            }
            
            return rewriter.rewrite(name);
            
        } catch (final Exception e) {
            throw new ConfigurationException("Resolving link " + name + " failed: ", e);
        }
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
