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
package org.apache.lenya.cms.export;

import java.io.File;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.linking.Link;
import org.apache.lenya.cms.linking.LinkManager;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * Import example content into test publication.
 */
public class ImportTest extends AbstractAccessControlTest {

    /**
     * @throws Exception if an error occurs.
     */
    public void testImport() throws Exception {
        
        Session session = login("lenya");
        
        Publication pub = getPublication(session, "test");
        Area area = pub.getArea("authoring");
        
        if (area.getDocuments().length == 0) {
            Publication defaultPub = getPublication(session, "default");
            Area defaultArea = defaultPub.getArea("authoring");
            String pubPath = defaultArea.getPublication().getDirectory().getAbsolutePath();
            String path = pubPath.replace(File.separatorChar, '/') + "/example-content";
            Importer importer = new Importer(getManager(), getLogger());
            importer.importContent(defaultPub, area, path);
            
            assertTrue(area.getSite().contains("/tutorial"));
            checkLinks(area);
            
            session.commit();
        }
        
        Session aliceSession = login("alice");
        Publication alicePub = getPublication(aliceSession, "test");
        assertTrue(alicePub.getArea("authoring").getSite().contains("/tutorial"));
        
    }

    protected void checkLinks(Area area) throws PublicationException, ServiceException {
        SiteStructure site = area.getSite();
        Document source = site.getNode("/index").getLink("en").getDocument();
        
        LinkManager linkManager = null;
        try {
            linkManager = (LinkManager) getManager().lookup(LinkManager.ROLE);
            Link[] links = linkManager.getLinksFrom(source);
            assertTrue(links.length > 0);
        }
        finally {
            if (linkManager != null) {
                getManager().release(linkManager);
            }
        }
        
    }
    
}
