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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;

import org.w3c.tidy.Tidy;


/**
 * A commend line interface to Tidy
 */
public class TidyCommandLine {
    /**
     * Command line interface
     * @param args command line args
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java TidyCommandLine http://www.lenya.org index.xhtml error.log");
            return;
        }

        try {
        	new TidyCommandLine().tidy(new URL(args[0]), new File(args[1]), new File(args[2]), true);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Tidy the given URL and store it into a file
     * @param url The URL
     * @param file The file
     * @param err The file to hold error messages
     * @param xhtml Whether to produce XHTML
     * @throws Exception if an error occurs
     */
    public void tidy(URL url, File file, File err, boolean xhtml)
        throws Exception {
        Tidy tidy = new Tidy();
        tidy.setXmlOut(xhtml);
        tidy.setErrout(new PrintWriter(new FileWriter(err.getAbsolutePath()), true));

        BufferedInputStream in = new BufferedInputStream(url.openStream());
        FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
        tidy.parse(in, out);
    }
}
