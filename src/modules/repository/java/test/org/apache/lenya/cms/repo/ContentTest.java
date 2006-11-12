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
package org.apache.lenya.cms.repo;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.lenya.cms.repo.metadata.MetaData;
import org.apache.lenya.cms.repo.metadata.MetaDataOwner;
import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;
import org.apache.lenya.cms.repo.mock.TestElementSet;
import org.apache.lenya.xml.DocumentHelper;

public class ContentTest extends AbstractRepositoryTest {

    /**
     * Test.
     * @throws Exception if an error occurs.
     */
    public void testContent() throws Exception {

        Session session = getSession();

        Publication pub = session.addPublication(PUBLICATION_ID);
        assertSame(pub, session.getPublication(PUBLICATION_ID));

        Exception ex = null;
        try {
            pub = session.addPublication(PUBLICATION_ID);
        } catch (Exception e) {
            ex = e;
        }
        assertNotNull(ex);

        doTestMetaData(pub);

        assertFalse(pub.existsArea(AREA_ID));
        Area area = pub.addArea(AREA_ID);

        Content content = area.getContent();

        AssetType doctype = session.getRepository().getAssetTypeResolver().resolve(ASSET_TYPYE);

        Asset asset1 = content.addAsset(doctype);
        assertNotNull(asset1);
        doTestMetaData(asset1);

        Asset asset2 = content.addAsset(doctype);
        assertNotNull(asset2);

        doTestTranslation(asset1);
        doTestTranslation(asset2);

        session.logout();
        session.getRepository().shutdown();

    }

    protected void doTestTranslation(Asset asset) throws Exception {
        Translation trans = asset.addTranslation(LANGUAGE_DE, "hello", "application/xml");
        assertTrue(asset.getTranslations().length == 1);

        trans.remove();
        assertTrue(asset.getTranslations().length == 0);

        trans = asset.addTranslation(LANGUAGE_DE, "hello", "application/xml");

        String validXmlResource = "valid.xml";
        String invalidXmlResource = "invalid.xml";
        String localName = PUBLICATION_ID;

        org.w3c.dom.Document validXml = DocumentHelper.readDocument(getClass().getResourceAsStream(validXmlResource));
        org.w3c.dom.Document invalidXml = DocumentHelper.readDocument(getClass().getResourceAsStream(invalidXmlResource));

        OutputStream out = trans.getOutputStream();
        Writer writer = new OutputStreamWriter(out);

        try {
            DocumentHelper.writeDocument(invalidXml, writer);
        } finally {
            if (out != null) {
                Exception ex = null;
                try {
                    out.close();
                } catch (Exception e) {
                    ex = e;
                }
                assertNotNull(ex);
            }
        }

        out = trans.getOutputStream();
        writer = new OutputStreamWriter(out);

        try {
            DocumentHelper.writeDocument(validXml, writer);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        assertTrue(trans.getContentLength() > 0);

        org.w3c.dom.Document xmlDoc = DocumentHelper.readDocument(trans.getInputStream());
        assertEquals(xmlDoc.getDocumentElement().getLocalName(), localName);

        doTestMetaData(trans);
    }

    protected void doTestMetaData(MetaDataOwner owner) throws Exception {
        MetaDataRegistry registry = getSession().getRepository().getMetaDataRegistry();
        if (!registry.isRegistered(TestElementSet.NAME)) {
            registry.register(TestElementSet.NAME, TestElementSet.ELEMENTS);
        }

        MetaData meta = owner.getMetaData(TestElementSet.NAME);
        RepositoryException ex = null;
        try {
            meta.setValue(TestElementSet.MULTIPLE_ELEMENT, "hello");
        } catch (RepositoryException e) {
            ex = e;
        }
        assertNotNull(ex);
        meta.addValue(TestElementSet.MULTIPLE_ELEMENT, "hello");
        meta.addValue(TestElementSet.MULTIPLE_ELEMENT, "world");
        assertTrue(meta.getValues(TestElementSet.MULTIPLE_ELEMENT).length == 2);

        try {
            meta.addValue(TestElementSet.SINGLE_ELEMENT, "hello");
        } catch (RepositoryException e) {
            ex = e;
        }
        assertNotNull(ex);
        meta.setValue(TestElementSet.SINGLE_ELEMENT, "hello");
        assertEquals(meta.getValue(TestElementSet.SINGLE_ELEMENT), "hello");

    }

}
