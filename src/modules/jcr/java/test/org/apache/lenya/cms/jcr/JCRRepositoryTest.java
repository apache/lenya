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

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.apache.cocoon.components.validation.Validator;
import org.apache.lenya.cms.jcr.mock.TestElementSet;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Content;
import org.apache.lenya.cms.repo.ContentNode;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.DocumentType;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.RepositoryManager;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.Site;
import org.apache.lenya.cms.repo.SiteNode;
import org.apache.lenya.cms.repo.impl.DocumentTypeImpl;
import org.apache.lenya.cms.repo.impl.DocumentTypeRegistryImpl;
import org.apache.lenya.cms.repo.metadata.ElementSet;
import org.apache.lenya.cms.repo.metadata.MetaData;
import org.apache.lenya.cms.repo.metadata.impl.MetaDataRegistryImpl;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.lenya.xml.Schema;

/**
 * JCR repository test.
 */
public class JCRRepositoryTest extends TestCase {

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

        File webappDir = new File(getWebappDirectory());
        String path = "lenya/modules/xhtml/schemas/xhtml.rng".replace('/', File.separatorChar);
        File schemaFile = new File(webappDir, path);
        Schema schema = new Schema(Validator.GRAMMAR_RELAX_NG, schemaFile.toURI().toString());
        DocumentType doctype = new DocumentTypeImpl("xhtml", schema, true, "application/xhtml+xml");
        DocumentTypeRegistryImpl registry = (DocumentTypeRegistryImpl) repo.getDocumentTypeRegistry();
        registry.register(doctype);

        Publication pub = session.addPublication("test");
        assertSame(pub, session.getPublication("test"));

        assertFalse(pub.existsArea("authoring"));
        Area area = pub.addArea("authoring");

        Content content = area.getContent();
        Site site = area.getSite();

        ContentNode node1 = content.addNode("hello", doctype);
        assertNotNull(node1);
        ContentNode node2 = content.addNode("world", doctype);
        assertNotNull(node2);
        SiteNode parent = site.addChild("parent", node1);
        SiteNode child = parent.addChild("child", node2);
        assertSame(node2, child.getContentNode());

        doTestDocument(node1);

    }

    protected void doTestDocument(ContentNode node1) throws Exception {
        Document doc = node1.addDocument("de", "hello");

        String localName = "test";
        NamespaceHelper helper = new NamespaceHelper("http://foo.bar", "", localName);
        OutputStream out = doc.getOutputStream();
        Writer writer = new OutputStreamWriter(out);

        try {
            DocumentHelper.writeDocument(helper.getDocument(), writer);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        assertTrue(doc.getContentLength() > 0);

        org.w3c.dom.Document xmlDoc = DocumentHelper.readDocument(doc.getInputStream());
        assertEquals(xmlDoc.getDocumentElement().getLocalName(), localName);

        doTestMetaData(doc);
    }

    protected void doTestMetaData(Document doc) throws Exception {
        MetaDataRegistryImpl registry = (MetaDataRegistryImpl) this.repo.getMetaDataRegistry();
        ElementSet testSet = new TestElementSet();
        registry.registerElementSet(TestElementSet.NAME, testSet);

        MetaData meta = doc.getMetaData(TestElementSet.NAME);
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
