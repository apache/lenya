/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
package org.apache.lenya.cms.jcr;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import javax.jcr.ItemExistsException;

import junit.framework.TestCase;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.cocoon.components.validation.Validator;
import org.apache.lenya.cms.jcr.mock.TestElementSet;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Asset;
import org.apache.lenya.cms.repo.AssetType;
import org.apache.lenya.cms.repo.Content;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.RepositoryManager;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.Site;
import org.apache.lenya.cms.repo.SiteNode;
import org.apache.lenya.cms.repo.Translation;
import org.apache.lenya.cms.repo.impl.AssetTypeImpl;
import org.apache.lenya.cms.repo.impl.AssetTypeRegistryImpl;
import org.apache.lenya.cms.repo.metadata.ElementSet;
import org.apache.lenya.cms.repo.metadata.MetaData;
import org.apache.lenya.cms.repo.metadata.MetaDataOwner;
import org.apache.lenya.cms.repo.metadata.impl.MetaDataRegistryImpl;
import org.apache.lenya.cms.url.URLUtil;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.Schema;

/**
 * JCR repository test.
 */
public class JCRRepositoryTest extends TestCase {

    protected static final String AREA_ID = "authoring";
    protected static final String PUBLICATION_ID = "test";
    protected static final String LANGUAGE_DE = "de";

    /**
     * Ctor.
     */
    public JCRRepositoryTest() {
        setWebappDirectory(System.getProperty("test.repo.webappDirectory"));
        setRepositoryFactory(System.getProperty("test.repo.repositoryFactory"));
    }

    private String webappDirectory;
    private String repositoryFactory;

    /**
     * @param webappDirectory The web application directory.
     */
    public void setWebappDirectory(String webappDirectory) {
        this.webappDirectory = webappDirectory;
    }

    /**
     * @return The web application directory.
     */
    public String getWebappDirectory() {
        return this.webappDirectory;
    }

    /**
     * @return The repository factory classname.
     */
    public String getRepositoryFactory() {
        return repositoryFactory;
    }

    /**
     * @param repositoryFactory The repository factory classname.
     */
    public void setRepositoryFactory(String repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    private Repository repo;

    /**
     * Test.
     * @throws Exception if an error occurs.
     */
    public void testRepository() throws Exception {

        System.out.println("Starting test");

        this.repo = RepositoryManager.getRepository(getWebappDirectory(), getRepositoryFactory());
        Session session = repo.createSession();

        URL schemaUrl = getClass().getResource("schema.xml");
        Schema schema = new Schema(Validator.GRAMMAR_RELAX_NG, schemaUrl.toString());
        AssetType doctype = new AssetTypeImpl("xhtml", schema, true);
        AssetTypeRegistryImpl registry = (AssetTypeRegistryImpl) repo.getDocumentTypeRegistry();
        registry.register(doctype);

        Publication pub = session.addPublication(PUBLICATION_ID);
        assertSame(pub, session.getPublication(PUBLICATION_ID));

        doTestMetaData(pub);

        assertFalse(pub.existsArea(AREA_ID));
        Area area = pub.addArea(AREA_ID);

        Content content = area.getContent();
        Site site = area.getSite();

        Asset asset1 = content.addAsset(doctype);
        assertNotNull(asset1);
        doTestMetaData(asset1);

        Asset asset2 = content.addAsset(doctype);
        assertNotNull(asset2);
        SiteNode parent = site.addChild("parent", asset1);
        SiteNode child = parent.addChild("child", asset2);
        assertSame(asset2.getAssetId(), child.getAsset().getAssetId());

        doTestSite(site, asset1);

        doTestTranslation(asset1);
        doTestTranslation(asset2);

        doTestUrlMapping(child);

    }

    protected void doTestSite(Site site, Asset asset) throws RepositoryException {
        SiteNode foo = site.addChild("foo", asset);
        SiteNode bar = site.addChild("bar", asset);

        RepositoryException ex = null;
        try {
            site.move(foo.getPath(), bar.getPath());
        } catch (RepositoryException e) {
            ex = e;
        }
        assertTrue(ex.getCause() instanceof ItemExistsException);
        site.move("/foo", "/bar/baz");

        SiteNode barBaz = bar.getChild("baz");
        assertSame(foo.getAsset().getAssetId(), barBaz.getAsset().getAssetId());

    }

    protected void doTestTranslation(Asset asset) throws Exception {
        Translation trans = asset.addTranslation(LANGUAGE_DE, "hello", "application/xml");

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
        MetaDataRegistryImpl registry = (MetaDataRegistryImpl) this.repo.getMetaDataRegistry();
        if (!registry.isRegistered(TestElementSet.NAME)) {
            ElementSet testSet = new TestElementSet();
            registry.registerElementSet(TestElementSet.NAME, testSet);
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

    /**
     * Test the URL mapping.
     * @param child The child site node.
     * @throws RepositoryException if an error occurs.
     */
    public void doTestUrlMapping(SiteNode child) throws RepositoryException {

        SiteNode parent = child.getParent();

        Area area = parent.getAsset().getContent().getArea();
        Publication pub = area.getPublication();

        String webappUrl = "/" + pub.getPublicationId() + "/" + area.getAreaID() + "/"
                + parent.getName() + "/" + child.getName() + "_" + LANGUAGE_DE;

        Translation childTrans = child.getAsset().getTranslation(LANGUAGE_DE);
        Translation trans = URLUtil.getTranslation(pub, webappUrl, new ConsoleLogger());

        assertSame(trans.getAsset().getAssetId(), childTrans.getAsset().getAssetId());
        assertSame(trans.getLanguage(), childTrans.getLanguage());

        String derivedUrl = URLUtil.getWebappURL(pub, trans, new ConsoleLogger());
        Translation derivedTrans = URLUtil.getTranslation(pub, derivedUrl, new ConsoleLogger());

        assertSame(trans.getAsset().getAssetId(), derivedTrans.getAsset().getAssetId());
        assertSame(trans.getLanguage(), derivedTrans.getLanguage());

    }
}
