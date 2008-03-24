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
package org.apache.lenya.cms.publication;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;

/**
 * Publication test.
 */
public class PublicationTest extends AbstractAccessControlTest {

    /**
     * Tests the publication functionality.
     * @throws Exception
     */
    public void testPublication() throws Exception {

        PublicationManager pubMgr = null;
        try {
            pubMgr = (PublicationManager) getManager().lookup(PublicationManager.ROLE);

            Publication[] pubs = pubMgr.getPublications(getFactory());
            for (int i = 0; i < pubs.length; i++) {
                doTestPublication(pubs[i]);
            }

        } finally {
            if (pubMgr != null) {
                getManager().release(pubMgr);
            }
        }

    }

    protected void doTestPublication(Publication pub) throws PublicationException {
        String contentDirPath = pub.getContentDir();
        assertNotNull(contentDirPath);

        File contentDir = new File(contentDirPath);

        assertTrue(pub.exists());

        String[] areaNames = pub.getAreaNames();
        for (int i = 0; i < areaNames.length; i++) {
            Area area = pub.getArea(areaNames[i]);
            if (area.getDocuments().length > 0) {
                File areaContentDir = pub.getContentDirectory(areaNames[i]);
                assertTrue(areaContentDir.isDirectory());
                assertEquals(new File(contentDir, areaNames[i]), areaContentDir);
            }
        }

        String[] languages = pub.getLanguages();
        assertTrue(languages.length > 0);

        assertNotNull(pub.getDefaultLanguage());
        assertTrue(Arrays.asList(languages).contains(pub.getDefaultLanguage()));

        String[] types = pub.getResourceTypeNames();
        assertTrue(types.length > 0);

        Set typeSet = new HashSet(Arrays.asList(types));

        String templateId = pub.getTemplateId();
        if (templateId != null) {
            Publication template = pub.getFactory().getPublication(templateId);
            String[] templateTypes = template.getResourceTypeNames();
            for (int t = 0; t < templateTypes.length; t++) {
                assertTrue(typeSet.contains(templateTypes[t]));
            }
        }

    }

}
