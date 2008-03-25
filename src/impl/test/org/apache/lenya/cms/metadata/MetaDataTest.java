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
package org.apache.lenya.cms.metadata;

import java.util.Map;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;

/**
 * Meta data test.
 */
public class MetaDataTest extends AbstractAccessControlTest {

    /**
     * Tests the meta data.
     * @throws Exception
     */
    public void testMetaData() throws Exception {

        Publication publication = getPublication("test");
        Document doc = publication.getArea("authoring").getSite().getNode("/index").getLink("en").getDocument(); 

        String namespaceUri = "foobar";
        Exception e = null;
        try {
            doc.getMetaData(namespaceUri);
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull(e);

        namespaceUri = DublinCore.DC_NAMESPACE;
        MetaData dc = doc.getMetaData(namespaceUri);

        doc.getRepositoryNode().lock();

        checkSetTitle(dc);
        checkRemoveAllValues(dc);

    }

    protected void checkSetTitle(MetaData dc) throws MetaDataException {
        Exception e = null;
        try {
            dc.setValue("foo", "bar");
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull(e);
        dc.setValue("title", "This is the title");

        e = null;
        // addValue() should throw an exception because a value is already set
        try {
            dc.addValue("title", "bar");
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull(e);

    }

    String NAMESPACE = "http://apache.org/lenya/test/metadata";

    protected void checkOnCopy(Publication pub) throws Exception {
        MetaDataRegistry registry = null;
        try {
            registry = (MetaDataRegistry) getManager().lookup(MetaDataRegistry.ROLE);
            ElementSet set = new TestElementSet();
            registry.register(NAMESPACE, set);
        }
        finally {
            getManager().release(registry);
        }
        
        DocumentFactory factory = getFactory();
        Document source = factory.get(pub, Publication.AUTHORING_AREA, "/index", "en");
        Document target = factory.get(pub, Publication.AUTHORING_AREA, "/index", "en");
        
        MetaData sourceMeta = source.getMetaData(NAMESPACE);
        sourceMeta.setValue("copy", "sourceCopy");
        sourceMeta.setValue("ignore", "sourceIgnore");
        sourceMeta.setValue("delete", "sourceDelete");
        
        MetaData targetMeta = target.getMetaData(NAMESPACE);
        targetMeta.setValue("ignore", "targetIgnore");
        targetMeta.setValue("delete", "targetDelete");
        
        targetMeta.replaceBy(sourceMeta);
        
        assertTrue(targetMeta.getValues("copy").length == 1);
        assertEquals(sourceMeta.getValues("copy"), targetMeta.getValues("copy"));
        
        assertTrue(targetMeta.getValues("ignore").length == 1);
        assertEquals(targetMeta.getFirstValue("ignore"), "targetIgnore");
        
        assertTrue(targetMeta.getValues("delete").length == 0);
    }

    protected void checkRemoveAllValues(MetaData dc) throws MetaDataException {
        dc.removeAllValues("title");
        assertTrue(dc.getValues("title").length == 0);
    }

    protected class TestElement implements Element {

        private String name;
        private int actionOnCopy;

        protected TestElement(String name, int actionOnCopy) {
            this.name = name;
            this.actionOnCopy = actionOnCopy;
        }

        public int getActionOnCopy() {
            return actionOnCopy;
        }

        public String getDescription() {
            return "";
        }

        public String getName() {
            return name;
        }

        public boolean isEditable() {
            return false;
        }

        public boolean isMultiple() {
            return false;
        }

        public boolean isSearchable() {
            return false;
        }

    }

    protected class TestElementSet implements ElementSet {

        private Element[] elements = { new TestElement("copy", Element.ONCOPY_COPY),
                new TestElement("ignore", Element.ONCOPY_IGNORE),
                new TestElement("delete", Element.ONCOPY_DELETE) };
        
        private Map name2element;

        protected TestElementSet() {
            for (int i = 0; i < elements.length; i++) {
                this.name2element.put(elements[i].getName(), elements[i]);
            }
        }

        public boolean containsElement(String name) {
            return true;
        }

        public Element getElement(String name) throws MetaDataException {
            return (Element) this.name2element.get(name);
        }

        public Element[] getElements() {
            return elements;
        }

        public String getNamespaceUri() {
            return NAMESPACE;
        }

    }

}
