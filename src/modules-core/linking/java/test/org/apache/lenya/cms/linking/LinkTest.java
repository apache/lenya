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
package org.apache.lenya.cms.linking;

import java.util.Arrays;

import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * Test for link functionality.
 */
public class LinkTest extends AbstractAccessControlTest {

    /**
     * Link test.
     * @throws Exception
     */
    public void testLinks() throws Exception {

        Publication pub = getPublication("test");
        Area area = pub.getArea("authoring");
        SiteStructure site = area.getSite();

        Document source = site.getNode("/index").getLink("en").getDocument();
        Document target = site.getNode("/tutorial").getLink("en").getDocument();

        LinkManager linkManager = null;
        LinkResolver resolver = null;
        try {
            linkManager = (LinkManager) getManager().lookup(LinkManager.ROLE);
            resolver = (LinkResolver) getManager().lookup(LinkResolver.ROLE);

            Link[] links = linkManager.getLinksFrom(source);
	    assertTrue(links.length > 0);
	    
            boolean matched = false;
            for (int i = 0; i < links.length; i++) {
                LinkTarget linkTarget = resolver.resolve(source, links[i].getUri());
                if (linkTarget.exists() && linkTarget.getDocument().equals(target)) {
                    matched = true;
                }
            }

            assertTrue(matched);

            Document[] references = linkManager.getReferencingDocuments(target);
            assertTrue(Arrays.asList(references).contains(source));
        } finally {
            if (linkManager != null) {
                getManager().release(linkManager);
            }
            if (resolver != null) {
                getManager().release(resolver);
            }
        }

    }

    protected String getWebappUrl() {
        return "/default/authoring/index.html";
    }

    /**
     * Test links across publications.
     * @throws Exception
     */
    public void testInterPublicationLinks() throws Exception {

        final Publication defaultPub = getPublication("default");
        final Document[] docs = defaultPub.getArea(Publication.AUTHORING_AREA).getDocuments();
        if (docs.length == 0) {
            getLogger().warn("To run this test, the default publication has to contain documents.");
            return;
        }

        final Document source = docs[0];

        final Publication pub = getPublication("test");
        final Area area = pub.getArea("authoring");
        final SiteStructure site = area.getSite();

        final Document target = site.getNode("/index").getLink("en").getDocument();
        final String queryString = "?format=xhtml";
        final String baseLink = "lenya-document:" + target.getUUID() + ",lang="
                + target.getLanguage();
        final String relativeLink = baseLink;
        final String absoluteLink = relativeLink + ",pub=test";

        SourceResolver sourceResolver = null;
        LinkResolver resolver = null;
        try {
            resolver = (LinkResolver) getManager().lookup(LinkResolver.ROLE);

            assertFalse(resolver.resolve(source, relativeLink).exists());
            assertTrue(resolver.resolve(source, absoluteLink).exists());

            sourceResolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);

            String relativeUri = relativeLink + queryString;
            String absoluteUri = absoluteLink + queryString;

            Exception e = null;
            try {
                sourceResolver.resolveURI(relativeUri);
            } catch (SourceNotFoundException ex) {
                e = ex;
            }
            assertNotNull("SourceNotFoundException thrown", e);
            
            sourceResolver.resolveURI(absoluteUri);

        } finally {
            if (resolver != null) {
                getManager().release(resolver);
            }
            if (sourceResolver != null) {
                getManager().release(sourceResolver);
            }
        }

    }

}
