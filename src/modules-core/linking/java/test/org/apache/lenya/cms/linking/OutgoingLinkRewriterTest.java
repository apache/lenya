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
import org.apache.lenya.cms.repository.Session;

public class OutgoingLinkRewriterTest extends AbstractAccessControlTest {

    public void testRelativeUrls() throws Exception {
        final Session session = login("lenya");
        final String url = "/aaa/bbb/ccc";

        boolean ssl = false;
        boolean considerSslPolicies = false;

        final OutgoingLinkRewriter relativeRewriter = new OutgoingLinkRewriter(getManager(),
                session, url, ssl, considerSslPolicies, true);

        assertEquals(relativeRewriter.rewrite("") + "/index", "../../index");
        assertEquals(relativeRewriter.rewrite("/aaa/bbb/foo"), "foo");
        assertEquals(relativeRewriter.rewrite("/aaa/bbb"), "../bbb");
        assertEquals(relativeRewriter.rewrite("/aaa/bbb/ccc/ddd"), "ccc/ddd");
        assertEquals(relativeRewriter.rewrite("/aaa/foo"), "../foo");
        assertEquals(relativeRewriter.rewrite("/aaa/foo/bar"), "../foo/bar");
        assertEquals(relativeRewriter.rewrite("/foo/bar"), "../../foo/bar");
        assertEquals(relativeRewriter.rewrite("/aaa/foo/bar/baz"), "../foo/bar/baz");
        assertEquals(relativeRewriter.rewrite("/aaa/bbb/?hello"), "?hello");
        assertEquals(relativeRewriter.rewrite("/aaa/?hello"), "../?hello");
        assertEquals(relativeRewriter.rewrite("/?hello"), "../../?hello");

        final OutgoingLinkRewriter absoluteRewriter = new OutgoingLinkRewriter(getManager(),
                session, url, ssl, considerSslPolicies, false);

        assertEquals(absoluteRewriter.rewrite("") + "/index", "/index");

    }

}
