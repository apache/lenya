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
package org.apache.lenya.cms.cocoon.source;

import org.apache.excalibur.source.SourceValidity;

/**
 * Validity for repository sources.
 */
public class RepositorySourceValidity implements SourceValidity {

    private static final long serialVersionUID = 1L;

    private String sourceUri;
    private long lastModified;

    /**
     * @param source The source this validity is for.
     */
    public RepositorySourceValidity(RepositorySource source) {
        this.sourceUri = source.getSourceURI();
        this.lastModified = source.exists() ? source.getLastModified() : 0;
    }

    public int isValid() {
        return SourceValidity.UNKNOWN;
    }

    public int isValid(SourceValidity validity) {
        if (validity instanceof RepositorySourceValidity) {
            RepositorySourceValidity repoValidity = (RepositorySourceValidity) validity;
            String repoValidityUri = repoValidity.getSourceURI();

            if (!repoValidityUri.equals(this.sourceUri)) {
                throw new RuntimeException("Wrong source URI: [" + repoValidityUri
                        + "] instead of [" + this.sourceUri + "]!");
            }
            if (this.lastModified >= repoValidity.getLastModified()) {
                return SourceValidity.VALID;
            } else {
                return SourceValidity.INVALID;
            }
        } else {
            return SourceValidity.INVALID;
        }
    }

    protected long getLastModified() {
        return this.lastModified;
    }

    protected String getSourceURI() {
        return this.sourceUri;
    }

}
