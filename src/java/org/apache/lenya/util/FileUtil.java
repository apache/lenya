/*
$Id: FileUtil.java,v 1.12 2004/02/02 02:50:37 stefano Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.apache.log4j.Category;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 1.11.14
 */
public class FileUtil {
    private static Category log = Category.getInstance(FileUtil.class);
    
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
            File file = org.apache.lenya.util.FileUtil.file("/root/temp/jpf-1.9/java/lenya/x/xps/samples/invoices/invoices",
                    "../addresses/lenya.xml");
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
