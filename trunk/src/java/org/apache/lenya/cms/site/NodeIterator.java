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
package org.apache.lenya.cms.site;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.lenya.util.Assert;

/**
 * Node iterator.
 */
public class NodeIterator {

    private Iterator delegate;
    
    /**
     * @param collection The collection to iterate over.
     */
    public NodeIterator(Collection collection) {
        Assert.notNull("collection", collection);
        this.delegate = collection.iterator();
    }
    
    /**
     * @param nodes The nodes to iterate over.
     */
    public NodeIterator(SiteNode[] nodes) {
        Assert.notNull("nodes", nodes);
        this.delegate = Arrays.asList(nodes).iterator();
    }
    
    /**
     * @return A site node.
     * @see Iterator#next()
     */
    public SiteNode next() {
        return (SiteNode) this.delegate.next();
    }
    
    /**
     * @return A boolean value.
     * @see Iterator#hasNext()
     */
    public boolean hasNext() {
        return this.delegate.hasNext();
    }
    
}
