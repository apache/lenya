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

/* $Id$  */

package org.apache.lenya.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.cocoon.environment.Request;

/**
 * Servlet utility class.
 */
public final class ServletHelper {
	
    /**
     * Ctor.
     */
    private ServletHelper() {
        // do nothing
    }

    /**
     * Returns the URL inside the web application (without the context prefix). 
     * @param request The request.
     * @return A string.
     */
    public static String getWebappURI(Request request) {
        String context = request.getContextPath();
        String requestUri = request.getRequestURI();
        return getWebappURI(context, requestUri);
    }

    /**
     * Returns the URL inside the web application (without the context prefix). 
     * @param context The context prefix.
     * @param requestUri The complete request URI.
     * @return A string.
     */
    public static String getWebappURI(String context, String requestUri) {
        if (context == null) {
            context = "";
        }
        String url = requestUri.substring(context.length());
		if (url.length() > 0 && !url.startsWith("/")) {
			url = "/" + url;
		}
		
        return url;
    }

    /**
     * Converts the request parameters to a map.
     * If a key is mapped to multiple parameters, a string array is used as the value.
     * @param request The request.
     * @return A map.
     */
    public static Map getParameterMap(Request request) {
        Map requestParameters = new HashMap();
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String[] values = request.getParameterValues(key);
            Object value;
            if (values.length == 1) {
                value = values[0];
            }
            else {
                value = values;
            }
            requestParameters.put(key, value);
        }
        return requestParameters;
    }

}
