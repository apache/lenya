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
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.linking.UuidToUrlRewriter;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
//import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.utils.ServletHelper;
import org.apache.lenya.utils.URLInformation;
/**
 * Transform lenya-document: URLs to web application URLs.
 * @see UuidToUrlRewriter
 */
public class UuidToUrlModule extends AbstractInputModule {
    
    protected Repository repository;
    protected LinkResolver linkResolver;
    
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        try {
            Session session = this.repository.getSession(request);
            //String currentUrl = ServletHelper.getWebappURI(request);
            String currentUrl = new URLInformation().getWebappUrl();
            
            UuidToUrlRewriter rewriter = new UuidToUrlRewriter(currentUrl, linkResolver, session);
            if (session.getUriHandler().isDocument(currentUrl)) {
                rewriter.setCurrentDocument(session.getUriHandler().getDocument(currentUrl));
            }
            
            return rewriter.rewrite(name);
            
        } catch (final Exception e) {
            throw new ConfigurationException("Resolving link " + name + " failed: ", e);
        }
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setLinkResolver(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

}
