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

package org.apache.lenya.cms.publication.templating;

import org.apache.excalibur.source.Source;

/**
 * Source visitor to obtain the first existing source.
 * 
 * @version $Id$
 */
public class ExistingSourceResolver implements SourceVisitor {
    
    private String uri;

    /**
     * Ctor.
     */
    public ExistingSourceResolver() {
        super();
    }
    
    /**
     * Returns the URI of the first existing source.
     * @return
     */
    public String getURI() {
        return uri;
    }

    /**
     * @see org.apache.lenya.cms.publication.templating.SourceVisitor#visit(org.apache.excalibur.source.Source)
     */
    public void visit(Source source) {
        if (this.uri == null && source.exists()) {
            this.uri = source.getURI();
        }
    }

}
