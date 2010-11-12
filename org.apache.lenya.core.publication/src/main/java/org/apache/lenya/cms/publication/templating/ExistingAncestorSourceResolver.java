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
import org.apache.excalibur.source.SourceResolver;

/**
 * Source visitor to obtain the first existing source.
 * 
 * @version $Id: ExistingSourceResolver.java 179568 2005-06-02 09:27:26Z jwkaltz $
 */
public class ExistingAncestorSourceResolver implements VisitingSourceResolver {

    private Source source;

    /**
     * Ctor.
     */
    public ExistingAncestorSourceResolver() {
        super();
    }

    /**
     * @return the ancestor of the first existing source.
     */
    public Source getSource() {
        return this.source;
    }

    private int matches = 0;

    public void visit(SourceResolver resolver, String sourceUri) {
        Source source = null;
        try {
            source = resolver.resolveURI(sourceUri);
            if (source.exists()) {
                matches++;
                if (matches == 2) {
                    this.source = source;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (source != null) {
                resolver.release(source);
            }
        }
    }

}
