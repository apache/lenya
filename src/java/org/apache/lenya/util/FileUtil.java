/*
 * $Id: FileUtil.java,v 1.5 2003/03/04 17:46:47 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.util;

import org.apache.log4j.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 1.11.14
 */
public class FileUtil {
    static Category log = Category.getInstance(FileUtil.class);

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java " + new FileUtil().getClass().getName());

            return;
        }

        if (args[0].equals("--copy")) {
            if (args.length != 3) {
                System.err.println("Usage: --copy source destination");

                return;
            }

            try {
                System.err.println("cp " + args[1] + " " + args[2]);
                copy(args[1], args[2]);
            } catch (FileNotFoundException e) {
                System.err.println(e);
            } catch (IOException e) {
                System.err.println(e);
            }

            return;
        }

        if (args[0].equals("--concatPath")) {
	    // FIXME:
            File file = org.lenya.util.FileUtil.file("/root/temp/jpf-1.9/java/wyona/x/xps/samples/invoices/invoices",
                    "../addresses/wyona.xml");
            System.out.println(file.getAbsolutePath());
        } else {
        }
    }

    /**
     * Copying a file
     *
     * @param source_name DOCUMENT ME!
     * @param destination_name DOCUMENT ME!
     *
     * @throws FileNotFoundException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public static void copy(String source_name, String destination_name)
        throws FileNotFoundException, IOException {
        InputStream source = new FileInputStream(source_name);
        File destination_file = new File(destination_name);
        File parent = new File(destination_file.getParent());

        if (!parent.exists()) {
            parent.mkdirs();
            log.warn("Directory has been created: " + parent.getAbsolutePath());
        }

        OutputStream destination = new FileOutputStream(destination_name);
        byte[] bytes_buffer = new byte[1024];
        int bytes_read;

        while ((bytes_read = source.read(bytes_buffer)) >= 0) {
            destination.write(bytes_buffer, 0, bytes_read);
        }
    }

    /**
     * Returns a file by specifying an absolute directory name and a relative file name
     *
     * @param absoluteDir DOCUMENT ME!
     * @param relativeFile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static File file(String absoluteDir, String relativeFile) {
        File file = new File(fileName(absoluteDir, relativeFile));

        return file;
    }

    /**
     * Returns an absolute file name by specifying an absolute directory name and a relative file
     * name
     *
     * @param absoluteDir DOCUMENT ME!
     * @param relativeFile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String fileName(String absoluteDir, String relativeFile) {
        String fileName = null;
        String newAbsoluteDir = null;

        if (!(absoluteDir.charAt(absoluteDir.length() - 1) == '/')) {
            newAbsoluteDir = absoluteDir + "/";
        } else {
            newAbsoluteDir = absoluteDir;
        }

        if (relativeFile.indexOf("../") == 0) {
            StringTokenizer token = new StringTokenizer(newAbsoluteDir, "/");
            newAbsoluteDir = "/";

            int numberOfTokens = token.countTokens();

            for (int i = 0; i < (numberOfTokens - 1); i++) {
                newAbsoluteDir = newAbsoluteDir + token.nextToken() + "/";
            }

            String newRelativeFile = relativeFile.substring(3, relativeFile.length());
            fileName = fileName(newAbsoluteDir, newRelativeFile);
        } else if (relativeFile.indexOf("./") == 0) {
            fileName = newAbsoluteDir + relativeFile.substring(2, relativeFile.length());
        } else {
            fileName = newAbsoluteDir + relativeFile;
        }

        return fileName;
    }

    /**
     * Returns an absolute file name by specifying an absolute directory name and a relative file
     * name
     *
     * @param absoluteFile DOCUMENT ME!
     * @param relativeFile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String concat(String absoluteFile, String relativeFile) {
        File file = new File(absoluteFile);

        if (file.isFile()) {
            return fileName(file.getParent(), relativeFile);
        }

        return fileName(absoluteFile, relativeFile);
    }
}
