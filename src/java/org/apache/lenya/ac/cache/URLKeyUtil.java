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

/* $Id: URLKeyUtil.java,v 1.2 2004/03/03 12:56:32 gregor Exp $  */

package org.apache.lenya.ac.cache;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

public final class URLKeyUtil {

    /**
     * Ctor.
     */
    private URLKeyUtil() {
    }

    /**
     * Generates a cache key for a context and a webapp URL.
     * @param resolver The resolver of the context.
     * @param webappUrl The webapp Url.
     * @return A String.
     * @throws MalformedURLException when something went wrong.
     * @throws IOException when something went wrong.
     */
    public static String generateKey(SourceResolver resolver, String webappUrl)
        throws MalformedURLException, IOException {
        Source source = null;
        String key;
        try {
            source = resolver.resolveURI("context:///");
            key = source.getURI() + "_" + webappUrl;
        }
        finally {
            if (source != null) {
                resolver.release(source);
            }
        }
        return key;
    }

}
