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

/* $Id: Main.java,v 1.11 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.lucene;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * DOCUMENT ME!
 */
public class Main {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: " + new Main().getClass().getName() + " uri");

            return;
        }

        try {
            new Main().crawl(new URL(args[0]));
        } catch (MalformedURLException e) {
            System.err.println(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param start_url DOCUMENT ME!
     */
    public void crawl(URL start_url) {
        System.out.println(".crawl(): INFO: " + start_url);
    }
}
