/*
 * $Id: CopyJavaSourcesTask.java,v 1.1 2003/03/01 08:49:07 michi Exp $
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
 * </License>
 */
package org.wyona.cms.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:michael.wechner@wyona.org">Michael Wechner</a>
 */
public class CopyJavaSourcesTask extends Task {
    private Path pubsRootDirs;
    private String javaDir;
    private String buildDir;

    private int numberOfEmptyDirectoriesCreated;
    private int numberOfFilesCopied;

    /**
     *
     */
    public void execute() throws BuildException {
        numberOfEmptyDirectoriesCreated = 0;
        numberOfFilesCopied = 0;

        File absoluteBuildDir = new File(project.getBaseDir(), buildDir);

        //System.out.println("CopyJavaSourcesTask.execute(): " + pubsRootDirs);

        StringTokenizer st = new StringTokenizer(pubsRootDirs.toString(),File.pathSeparator);
        while (st.hasMoreTokens()) {
            String pubsRootDir = st.nextToken();
            //System.out.println("CopyJavaSourcesTask.execute(): " + pubsRootDir);
            File path = new File(pubsRootDir);
            if (path.isDirectory()) {
                String[] pubs = path.list();
                for (int i = 0; i < pubs.length; i++) {
                    //System.out.println("CopyJavaSourcesTask.execute(): " + pubs[i]);
                    File pubJavaDir = new File(path, new File(pubs[i], javaDir).toString());
                    //System.out.println("CopyJavaSourcesTask.execute(): " + pubJavaDir);

                    //System.out.println("CopyJavaSourcesTask.execute(): " + absoluteBuildDir);
                    copyDir(pubJavaDir, absoluteBuildDir);
                }
            } else {
                throw new BuildException("No such directory: " + path);
            }
        }
        System.out.println("Copying " + numberOfEmptyDirectoriesCreated + " directories to " + absoluteBuildDir);
        System.out.println("Copying " + numberOfFilesCopied + " files to "+absoluteBuildDir);
    }


    /**
     *
     */
    public void copyDir(File source, File destination) {
        if (source.isDirectory()) {
            String[] files = source.list();
            for (int i = 0; i < files.length; i++) {
                File file = new File(source, files[i]);
                if (file.isFile()) {
                    if (getExtension(file).equals("java")) {
                        copyFile(file, new File(destination, files[i]));
                    }
                } else if (file.isDirectory()) {
                    //System.out.println("CopyJavaSourcesTask.copyDir(): " + source + " " + destination);
                    copyDir(file, new File(destination, files[i]));
                } else {
                    System.err.println("CopyJavaSourcesTask.copyDir(): Neither file nor directory: " + file);
                }
            }
        }
    }


    /**
     *
     */
    public void copyFile(File source, File destination) {
        if (source.isFile()) {
            File parentDest = new File(destination.getParent());
            if (!parentDest.exists()) {
                parentDest.mkdirs();
                //System.out.println("CopyJavaSourcesTask.copyFile(): Directory created: " + parentDest);
                numberOfEmptyDirectoriesCreated++;
            }

            if (destination.isFile()) {
                if (destination.lastModified() > source.lastModified()) {
                    return;
                }
            }

            //System.out.println("CopyJavaSourcesTask.copyFile(): " + source + " " + destination);
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = -1;
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(destination);
                while ((bytesRead = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                in.close();
                numberOfFilesCopied++;
            } catch (Exception e) {
                System.err.println("CopyJavaSourcesTask.copyFile(): " + e);
            }
        }
    }


    /**
     *
     */
    public String getExtension(File file) {
        StringTokenizer st = new StringTokenizer(file.getName(),".");
        st.nextToken();
        String extension="";
        while (st.hasMoreTokens()) {
            extension = st.nextToken();
        }
        return extension;
    }


    /**
     *
     */
    public void setPubsRootDirs(Path pubsRootDirs) {
        this.pubsRootDirs = pubsRootDirs;
    }


    /**
     *
     */
    public void setJavaDir(String javaDir) {
        this.javaDir = javaDir;
    }


    /**
     *
     */
    public void setBuildDir(String buildDir) {
        this.buildDir = buildDir;
    }
}
