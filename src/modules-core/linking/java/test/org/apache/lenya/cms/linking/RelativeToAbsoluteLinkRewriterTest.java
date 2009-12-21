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
package org.apache.lenya.cms.linking;

import junit.framework.TestCase;

/**
 * Test case for relative-to-absolute link rewriting.
 */
public class RelativeToAbsoluteLinkRewriterTest extends TestCase {

    private static final String SOURCE_URI = "/foo/bar";
    private final LinkRewriter rewriter = new RelativeToAbsoluteLinkRewriter(SOURCE_URI);

    /**
     * Test case for relative-to-absolute link rewriting.
     */
    public void testLinks() {
        assertEquals(rewrite("baz"), "/foo/baz");
        assertEquals(rewrite("bar/baz"), "/foo/bar/baz");
        assertEquals(rewrite("../baz"), "/baz");
        assertEquals(rewrite(".."), "/");
        
        assertEquals(rewrite("/foo/bar"), "/foo/bar");
        assertEquals(rewrite("lenya-document:123"), "lenya-document:123");
        assertEquals(rewrite("http://apache.org"), "http://apache.org");
    }
    
    protected String rewrite(String input) {
        return this.rewriter.matches(input) ? this.rewriter.rewrite(input) : input;
    }

}
