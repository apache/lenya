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

import java.net.MalformedURLException;
import java.util.StringTokenizer;

public class FallbackUri {
    
    private String uri;
    private String pubId;
    private String path;
    private String queryString;
    
    public FallbackUri(String uri) throws MalformedURLException {
        
        this.uri = uri;
        
        // Remove the protocol and the first '//'
        int pos = uri.indexOf("://");

        if (pos == -1) {
            throw new MalformedURLException("The URI [" + uri
                    + "] does not contain the string '://'");
        }

        String path = uri.substring(pos + 3);

        // extract publication ID
        String prefix = uri.substring(0, pos);
        StringTokenizer tokens = new StringTokenizer(prefix, ":");
        if (tokens.countTokens() > 1) {
            tokens.nextToken();
            this.pubId = tokens.nextToken();
        }

        // remove query string
        int questionMarkIndex = path.indexOf("?");
        if (questionMarkIndex > -1) {
            this.queryString = path.substring(questionMarkIndex);
            path = path.substring(0, questionMarkIndex);
        }

        if (path.length() == 0) {
            throw new MalformedURLException("The path after the protocol must not be empty!");
        }
        
        this.path = path;
    }
    
    public String getPubId() {
        return this.pubId;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getQeryString() {
        return this.queryString;
    }
    
    public String getUri() {
        return this.uri;
    }
}
