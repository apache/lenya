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

/* $Id: URLUtil.java,v 1.10 2004/03/01 16:18:14 gregor Exp $  */

package org.apache.lenya.util;


/**
 * DOCUMENT ME!
 */
public class URLUtil {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        System.out.println(URLUtil.complete("http://www.apache.org/download/index.html",
                "../images/lenya.jpeg"));
        System.out.println(URLUtil.complete("http://www.apache.org/download/index.html",
                "/images/lenya.jpeg"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String complete(String parent, String child) {
        String url = child;
        String urlLowCase = child.toLowerCase();
        String currentURLPath = parent.substring(0, parent.lastIndexOf("/"));
        String rootURL = parent.substring(0, parent.indexOf("/", 8));

        if (urlLowCase.startsWith("/")) {
            url = rootURL + url;
        } else if (urlLowCase.startsWith("./")) {
            url = currentURLPath + url.substring(1, url.length());
        } else if (urlLowCase.startsWith("../")) {
            int back = 1;

            while (urlLowCase.indexOf("../", back * 3) != -1)
                back++;

            int pos = currentURLPath.length();
            int count = back;

            while (count-- > 0) {
                pos = currentURLPath.lastIndexOf("/", pos) - 1;
            }

            url = currentURLPath.substring(0, pos + 2) + url.substring(3 * back, url.length());
        } else if (urlLowCase.startsWith("javascript:")) {
            // handle javascript:...
            System.err.println(".parseHREF(): parseJavaScript is not implemented yet");
        } else if (urlLowCase.startsWith("#")) {
            // internal anchor... ignore.
            url = null;
        } else if (urlLowCase.startsWith("mailto:")) {
            // handle mailto:...
            url = null;
        } else {
            url = currentURLPath + "/" + url;
        }

        // strip anchor if exists otherwise crawler may index content multiple times
        // links to the same url but with unique anchors would be considered unique
        // by the crawler when they should not be
        if (url != null) {
            int i;

            if ((i = url.indexOf("#")) != -1) {
                url = url.substring(0, i);
            }
        }

        return url;
    }
}
