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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.Validate;

public class RelativeToAbsoluteLinkRewriter implements LinkRewriter {

    private String sourceUri;

    protected static final String[] PROTOCOLS = { "/", "http:", "https:", "ftp:", "ftps:",
            "mailto:", "file:" };

    public RelativeToAbsoluteLinkRewriter(String sourceUri) {
        Validate.notNull(sourceUri);
        this.sourceUri = sourceUri;
    }

    public boolean matches(String url) {
        Validate.notNull(url);
        for (int i = 0; i < PROTOCOLS.length; i++) {
            if (url.startsWith(PROTOCOLS[i])) {
                return false;
            }
        }
        return true;
    }

    public String rewrite(final String url) {
        try {
            final int lastSlashIndex = this.sourceUri.lastIndexOf('/');
            final String prefix = lastSlashIndex < 0 ? "" : this.sourceUri.substring(0,
                    lastSlashIndex + 1);
            final String newUrl = prefix + url;
            final String normalizedUrl = new URI(newUrl).normalize().toString();
            return normalizedUrl;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
