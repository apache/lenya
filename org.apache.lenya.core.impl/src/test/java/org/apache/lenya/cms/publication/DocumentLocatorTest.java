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

import junit.framework.TestCase;

/**
 *
 */
public class DocumentLocatorTest extends TestCase {

    /**
     * 
     */
    public void testDocumentLocator() {
        
        String pubId = "pub";
        String area = "area";
        String area2 = "area2";
        String languageDe = "de";
        String languageEn = "en";
        
        DocumentLocator root = DocumentLocator.getLocator(pubId, area, "", languageDe);
        DocumentLocator foo = DocumentLocator.getLocator(pubId, area, "/foo", languageDe);
        DocumentLocator fooBar = DocumentLocator.getLocator(pubId, area, "/foo/bar", languageDe);
        DocumentLocator fooBarBaz = DocumentLocator.getLocator(pubId, area, "/foo/bar/baz", languageDe);

        DocumentLocator fooEn = DocumentLocator.getLocator(pubId, area, "/foo", languageEn);
        DocumentLocator foo2 = DocumentLocator.getLocator(pubId, area2, "/foo", languageDe);

        assertEquals(foo.getParent(), root);
        assertEquals(fooBar.getParent(), foo);
        assertEquals(fooBarBaz.getParent(), fooBar);
        
        assertEquals(root.getDescendant("foo"), foo);
        assertEquals(foo.getDescendant("bar/baz"), fooBarBaz);
        
        assertEquals(foo.getChild("bar"), fooBar);
        
        assertEquals(foo.getLanguageVersion(languageEn), fooEn);
        
        assertEquals(foo.getAreaVersion(area2), foo2);
        
    }
    
}
