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
package org.apache.lenya.cms.site.tree;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * Test for bug <a href="http://issues.apache.org/bugzilla/show_bug.cgi?id=42602">42602</a>.
 */
public class ConcurrentModificationTest extends AbstractAccessControlTest {
    
    public void testConcurrentModification() throws Exception {
        
        String pubId = "test";
        String area = "authoring";
        
        Session lenyaSession = login("lenya");
        DocumentFactory lenyaFactory = DocumentUtil.createDocumentFactory(getManager(), lenyaSession);
        Publication lenyaPub = lenyaFactory.getPublication(pubId);
        
        SiteStructure lenyaSite = lenyaPub.getArea(area).getSite();
        lenyaSite.getRepositoryNode().lock();
        
        Session aliceSession = login("alice");
        DocumentFactory aliceFactory = DocumentUtil.createDocumentFactory(getManager(), aliceSession);
        Publication alicePub = aliceFactory.getPublication(pubId);
        
        SiteStructure aliceSite = alicePub.getArea(area).getSite();
        aliceSite.getRepositoryNode().lock();
        
        SiteNode lenyaNode = lenyaSite.getNodes()[1];
        lenyaNode.setVisible(!lenyaNode.isVisible());
        lenyaSession.commit();
        
        SiteNode aliceNode = aliceSite.getNodes()[2];
        aliceNode.setVisible(!aliceNode.isVisible());
        try {
            aliceSession.commit();
            assertTrue("No exception raised", false);
        }
        catch (Exception ignore) {
        }
        
    }
    

}
