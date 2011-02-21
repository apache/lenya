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
/*
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.TraversableSource;
import org.apache.lenya.cms.AbstractAccessControlTest;
*/
/**
 * Publication test.
 */
//public class PublicationTest extends AbstractAccessControlTest {

    /**
     * Tests the publication functionality.
     * @throws Exception
     */
/*    public void testPublication() throws Exception {
        String[] pubIds = getSession().getPublicationIds();
        for (String id : pubIds) {
            doTestPublication(getSession().getPublication(id));
        }
    }

    protected void doTestPublication(Publication pub) throws Exception {
        String contentUri = pub.getContentUri();
        assertNotNull(contentUri);

        assertTrue(pub.exists());

        SourceResolver sourceResolver = (SourceResolver) getBeanFactory().getBean(
                SourceResolver.ROLE);

        for (String areaName : pub.getAreaNames()) {
            Area area = pub.getArea(areaName);
            if (area.getDocuments().length > 0) {
                String areaContentUri = pub.getContentUri(areaName);

                TraversableSource source = null;
                try {
                    source = (TraversableSource) sourceResolver.resolveURI(areaContentUri);
                    assertTrue(source.isCollection());
                } finally {
                    if (source != null) {
                        sourceResolver.release(source);
                    }
                }

                // TODO: Resolve absolute vs. lenya:// URIs in content handling
                // assertEquals(contentUri + "/" + areaName, areaContentUri);
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
            Publication template = pub.getSession().getPublication(templateId);
            String[] templateTypes = template.getResourceTypeNames();
            for (int t = 0; t < templateTypes.length; t++) {
                assertTrue(typeSet.contains(templateTypes[t]));
            }
        }

    }

}
*/