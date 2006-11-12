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

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * Document factory test.
 */
public class DocumentFactoryTest extends AbstractAccessControlTest {

    /**
     * Tests the meta data.
     * @throws Exception
     */
    public void testDocumentFactory() throws Exception {
        Session session = RepositoryUtil.getSession(getManager(), getRequest());
        DocumentFactory factoryA = DocumentUtil.createDocumentFactory(getManager(), session);

        Publication publication = getPublication("test");
        
        Document docA1 = factoryA.get(publication, Publication.AUTHORING_AREA, "/index", "en");
        Document docA2 = factoryA.get(publication, Publication.AUTHORING_AREA, "/index", "en");
        assertSame(docA1, docA2);
        
        DocumentFactory factoryB = DocumentUtil.createDocumentFactory(getManager(), session);

        Document docB1 = factoryB.get(publication, Publication.AUTHORING_AREA, "/index", "en");
        
        assertSame(docA1, docB1);
    }

}
