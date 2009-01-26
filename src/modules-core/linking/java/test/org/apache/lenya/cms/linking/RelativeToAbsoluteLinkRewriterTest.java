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

import org.apache.lenya.ac.impl.AbstractAccessControlTest;

/**
 * Test case for relative-to-absolute link rewriting.
 */
public class RelativeToAbsoluteLinkRewriterTest extends AbstractAccessControlTest {

    private static final String SOURCE_URI = "/foo/bar";

    /**
     * Test case for relative-to-absolute link rewriting.
     */
    public void testLinks() {
        RelativeToAbsoluteLinkRewriter rewriter = new RelativeToAbsoluteLinkRewriter(SOURCE_URI);
        assertEquals(rewriter.rewrite("baz"), "/foo/baz");
        assertEquals(rewriter.rewrite("bar/baz"), "/foo/bar/baz");
        assertEquals(rewriter.rewrite("../baz"), "/baz");
        assertEquals(rewriter.rewrite(".."), "/");
    }

}
