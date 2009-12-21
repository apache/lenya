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
package org.apache.lenya.modules.administration;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.linking.Link;
import org.apache.lenya.cms.linking.LinkManager;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.linking.LinkTarget;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.util.Assert;

/**
 * Various reports about a publication.
 */
public class Reports extends AbstractUsecase {

    protected static final String PARAM_REPORT = "report";
    protected static final Object REPORT_BROKEN_LINKS = "brokenLinks";
    protected static final String PARAM_BROKEN_LINKS = "brokenLinks";

    protected void prepareView() throws Exception {
        super.prepareView();
        
        final String report = getParameterAsString(PARAM_REPORT);
        if (report == null) {
            return;
        }
        
        if (report.equals(REPORT_BROKEN_LINKS)) {
            reportBrokenLinks();
        }
        
    }
    
    protected void reportBrokenLinks() throws Exception {
        List brokenLinks = new ArrayList();
        Publication pub = getPublication();
        String[] areaNames = pub.getAreaNames();
        LinkManager linkManager = null;
        LinkResolver linkResolver = null;
        try {
            linkManager = (LinkManager) this.manager.lookup(LinkManager.ROLE);
            linkResolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);
            for (int a = 0; a < areaNames.length; a++) {
                Area area = pub.getArea(areaNames[a]);
                Document[] docs = area.getDocuments();
                for (int d = 0; d < docs.length; d++) {
                    Link[] links = linkManager.getLinksFrom(docs[d]);
                    for (int l = 0; l < links.length; l++) {
                        String uri = links[l].getUri();
                        LinkTarget target = linkResolver.resolve(docs[d], uri);
                        if (!target.exists()) {
                            BrokenLink brokenLink = new BrokenLink(docs[d].getCanonicalWebappURL(), uri);
                            brokenLinks.add(brokenLink);
                        }
                    }
                }
            }
        }
        finally {
            if (linkManager != null) {
                this.manager.release(linkManager);
            }
            if (linkResolver != null) {
                this.manager.release(linkResolver);
            }
        }
        setParameter(PARAM_BROKEN_LINKS, brokenLinks);
    }
    
    public static class BrokenLink {
        
        private String sourceUrl;
        private String targetUrl;
        
        public BrokenLink(String sourceUrl, String targetUrl) {
            Assert.notNull("source URL", sourceUrl);
            Assert.notNull("target URL", targetUrl);
            this.sourceUrl = sourceUrl;
            this.targetUrl = targetUrl;
        }
        
        public String getSourceUrl() {
            return this.sourceUrl;
        }
        
        public String getTargetUrl() {
            return this.targetUrl;
        }
    }

    /**
     * @return The current publication.
     * @throws PublicationException if the usecase isn't invoked inside a publication.
     */
    protected Publication getPublication() throws PublicationException {
        URLInformation info = new URLInformation(getSourceURL());
        return getDocumentFactory().getPublication(info.getPublicationId());
    }
    
}
