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

package org.apache.lenya.cms.publication.templating;

import org.apache.excalibur.source.Source;

/**
 * Source visitor to obtain the first existing source.
 * 
 * @version $Id: ExistingSourceResolver.java 179568 2005-06-02 09:27:26Z jwkaltz $
 */
public class ExistingAncestorSourceResolver implements URIResolver {
    
    private String uri;

    /**
     * Ctor.
     */
    public ExistingAncestorSourceResolver() {
        super();
    }
    
    /**
     * Returns the URI of the first existing source.
     * @return The URI
     */
    public String getURI() {
        return this.uri;
    }

    private int matches = 0;

    /**
     * @see org.apache.lenya.cms.publication.templating.SourceVisitor#visit(org.apache.excalibur.source.Source)
     */
    public void visit(Source source) {
        if (source.exists()) {
            matches++;
            if (matches == 2) {
                this.uri = source.getURI();
            }
        }
    }

}
