/*
 * $Id: CopyJavaSourcesTask.java,v 1.7 2003/04/24 13:52:37 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
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
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 * </License>
 */
package org.apache.lenya.cms.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:michael.wechner@lenya.org">Michael Wechner</a>
 */
public class CopyJavaSourcesTask extends Task {
    private Path pubsRootDirs;
    private String javaDir;
    private String buildDir;

    /**
     *
     */
    public void execute() throws BuildException {
        int numberOfDirectoriesCreated = 0;
        int numberOfFilesCopied = 0;
        TwoTuple twoTuple = new TwoTuple(numberOfDirectoriesCreated, numberOfFilesCopied);

        File absoluteBuildDir = new File(project.getBaseDir(), project.translatePath(buildDir));
        //System.out.println("CopyJavaSourcesTask.execute(): " + absoluteBuildDir);

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
                    copyDir(pubJavaDir, absoluteBuildDir, twoTuple, new JavaFilenameFilter());
                }
            } else {
                throw new BuildException("No such directory: " + path);
            }
        }
        numberOfDirectoriesCreated = twoTuple.x;
        numberOfFilesCopied = twoTuple.y;
        System.out.println("Copying " + numberOfDirectoriesCreated + " directories to " + absoluteBuildDir);
        System.out.println("Copying " + numberOfFilesCopied + " files to "+absoluteBuildDir);
    }


    /**
     *
     */
    static public void copyDir(File source, File destination, TwoTuple twoTuple, FilenameFilter filenameFilter) {
        if (source.isDirectory()) {
            String[] files;
            if (filenameFilter != null) {
                files = source.list(filenameFilter);
            } else {
                files = source.list();
            }
            for (int i = 0; i < files.length; i++) {
                File file = new File(source, files[i]);
                if (file.isFile()) {
                    copyFile(file, new File(destination, files[i]), twoTuple);
                } else if (file.isDirectory()) {
                    //System.out.println("CopyJavaSourcesTask.copyDir(): " + source + " " + destination);
                    copyDir(file, new File(destination, files[i]), twoTuple, filenameFilter);
                } else {
                    System.err.println("CopyJavaSourcesTask.copyDir(): Neither file nor directory: " + file);
                }
            }
        }
    }


    /**
     *
     */
    static public void copyFile(File source, File destination, TwoTuple twoTuple) {
        if (source.isFile()) {
            File parentDest = new File(destination.getParent());
            if (!parentDest.exists()) {
                parentDest.mkdirs();

                //System.out.println("CopyJavaSourcesTask.copyFile(): Directory created: " + parentDest);
                int numberOfDirectoriesCreated = twoTuple.x;
                numberOfDirectoriesCreated++;
                twoTuple.x = numberOfDirectoriesCreated;
            }

            if (destination.isFile()) {
                if (destination.lastModified() > source.lastModified()) {
                    return;
                }
            }

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

                int numberOfFilesCopied = twoTuple.y;
                numberOfFilesCopied++;
                twoTuple.y = numberOfFilesCopied;
                //System.out.println("CopyJavaSourcesTask.copyFile(): File copied (" + numberOfFilesCopied  + "): " + source + " " + destination);
            } catch (Exception e) {
                System.err.println("CopyJavaSourcesTask.copyFile(): " + e);
            }
        }
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
