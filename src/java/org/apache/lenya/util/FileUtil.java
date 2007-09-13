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

package org.apache.lenya.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * @version $Id: FileUtil.java 473841 2006-11-12 00:46:38Z gregor $
 */
public final class FileUtil {
    private static Logger log = Logger.getLogger(FileUtil.class);

    /**
     * DOCUMENT ME!
     * 
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java " + FileUtil.class.getName());
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
            File file = org.apache.lenya.util.FileUtil.file(
                    "/root/temp/jpf-1.9/java/lenya/x/xps/samples/invoices/invoices",
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
            log.debug("Directory has been created: " + parent.getAbsolutePath());
        }

        OutputStream destination = new FileOutputStream(destination_name);
        byte[] bytes_buffer = new byte[1024];
        int bytes_read;

        while ((bytes_read = source.read(bytes_buffer)) >= 0) {
            destination.write(bytes_buffer, 0, bytes_read);
        }
    }

    /**
     * Copy a single File or a complete Directory including its Contents.
     * 
     * @param src the source File.
     * @param dest the destiantion File.
     * 
     * @throws FileNotFoundException if the source File does not exists.
     * @throws IOException if an error occures in the io system.
     */
    public static void copy(File src, File dest) throws FileNotFoundException, IOException {

        if (src.isFile()) {
            copySingleFile(src, dest);
        } else {
            File[] contents = src.listFiles();

            if (contents == null)
                return;

            dest.mkdirs();

            for (int i = 0; i < contents.length; i++) {
                String destPath = dest.getAbsolutePath() + File.separator + contents[i].getName();
                copy(contents[i], new File(destPath));
            }
        }
    }

    /**
     * Copy a single File.
     * 
     * @param src the source File.
     * @param dest the destiantion File.
     * 
     * @throws FileNotFoundException if the source File does not exists.
     * @throws IOException if an error occures in the io system.
     */
    protected static void copySingleFile(File src, File dest) throws FileNotFoundException,
            IOException {

        dest.getParentFile().mkdirs();
        dest.createNewFile();
        org.apache.commons.io.FileUtils.copyFile(src, dest);
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
        StringBuffer newAbsoluteDir = new StringBuffer(absoluteDir);

        if (absoluteDir.charAt(absoluteDir.length() - 1) != '/') {
            newAbsoluteDir.append("/");
        }

        if (relativeFile.indexOf("../") == 0) {
            StringTokenizer token = new StringTokenizer(newAbsoluteDir.toString(), "/");
            newAbsoluteDir = new StringBuffer("/");

            int numberOfTokens = token.countTokens();

            for (int i = 0; i < (numberOfTokens - 1); i++) {
                newAbsoluteDir.append(token.nextToken()).append("/");
            }

            String newRelativeFile = relativeFile.substring(3, relativeFile.length());
            fileName = fileName(newAbsoluteDir.toString(), newRelativeFile);
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

    /**
     * Deletes all dirs up to stop dir or if dirs in hirachy are not empty.
     * 
     * @param start File to delete the parents of. The File itself is not deleted.
     * @param stop Stop deleting at this dir. This dir is not deleted.
     * @throws IllegalArgumentException If stop is not a dir or start is not a descending sibling of
     *                   stop dir.
     */
    public static void deleteParentDirs(File start, File stop) throws IllegalArgumentException {
        if (!stop.isDirectory())
            throw new IllegalArgumentException("Stop dir '" + stop.getAbsolutePath()
                    + "' is not a directory");
        if (!start.getAbsolutePath().startsWith(stop.getAbsolutePath()))
            throw new IllegalArgumentException("Start dir '" + start.getAbsolutePath()
                    + "' is not a descending sibling of stop directory '" + stop.getAbsolutePath()
                    + "'.");

        File parent = start.getParentFile();

        while (!parent.equals(stop) && parent.delete())
            parent = parent.getParentFile();
    }
}