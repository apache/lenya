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
        }
        finally {
            if (linkManager != null) {
                getManager().release(linkManager);
            }
            if (resolver != null) {
                getManager().release(resolver);
            }
        }
        
    }

}
