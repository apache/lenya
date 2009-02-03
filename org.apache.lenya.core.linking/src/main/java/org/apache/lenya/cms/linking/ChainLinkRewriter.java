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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lenya.util.Assert;

/**
 * A chain of link rewriters. The output of one rewriter serves as the input of the next one. If a
 * rewriter doesn't match a link, it is skipped.
 */
public class ChainLinkRewriter implements LinkRewriter {

    private List rewriters = new ArrayList();

    /**
     * Adds a rewriter to the end of the chain.
     * @param rewriter The rewriter.
     */
    public void add(LinkRewriter rewriter) {
        Assert.notNull("rewriter", rewriter);
        this.rewriters.add(rewriter);
    }

    public boolean matches(String url) {
        return true;
    }

    public String rewrite(String url) {
        String link = url;
        for (Iterator i = this.rewriters.iterator(); i.hasNext();) {
            LinkRewriter rewriter = (LinkRewriter) i.next();
            if (rewriter.matches(link)) {
                link = rewriter.rewrite(link);
            }
        }
        return link;
    }

}
