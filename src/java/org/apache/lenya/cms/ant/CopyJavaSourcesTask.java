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

/* $Id: CopyJavaSourcesTask.java,v 1.20 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

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

        File absoluteBuildDir = new File(getProject().getBaseDir(), Project.translatePath(buildDir));

        StringTokenizer st = new StringTokenizer(pubsRootDirs.toString(), File.pathSeparator);

        while (st.hasMoreTokens()) {
            String pubsRootDir = st.nextToken();

            File path = new File(pubsRootDir);

            if (path.isDirectory()) {
                if (new File(path, "publication.xml").isFile()) {
                    copyContentOfDir(new File(path, javaDir), absoluteBuildDir, twoTuple, new JavaFilenameFilter());
                } else {
                    // FIXME: Look for publications defined by the file "publication.xml"
                    String[] pubs = path.list();

                    for (int i = 0; i < pubs.length; i++) {
                        File pubJavaDir = new File(path, new File(pubs[i], javaDir).toString());

                        copyContentOfDir(pubJavaDir, absoluteBuildDir, twoTuple, new JavaFilenameFilter());
                    }
                }
            } else {
                throw new BuildException("No such directory: " + path);
            }
        }

        numberOfDirectoriesCreated = twoTuple.x;
        numberOfFilesCopied = twoTuple.y;
        System.out.println("Copying " + numberOfDirectoriesCreated + " directories to " + absoluteBuildDir);
        System.out.println("Copying " + numberOfFilesCopied + " files to " + absoluteBuildDir);
    }

    /**
     * Copies the directory "source" into the directory "destination"
     */
    static public void copyDir(File source, File destination, TwoTuple twoTuple, FilenameFilter filenameFilter) {
        File actualDestination = new File(destination, source.getName());
        actualDestination.mkdirs();
        copyContentOfDir(source, actualDestination, twoTuple, filenameFilter);
    }

    /**
     * Copies the content of a directory into another directory
     */
    static public void copyContentOfDir(File source, File destination, TwoTuple twoTuple, FilenameFilter filenameFilter) {
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
                    copyContentOfDir(file, new File(destination, files[i]), twoTuple, filenameFilter);
                } else {
                    System.err.println("CopyJavaSourcesTask.copyDir(): Neither file nor directory: " + file);
                }
            }
        } else {
        }
    }

    /**
     * Copies the content of a file into another file
     * @param destination File (not a directory!)
     */
    static public void copyFile(File source, File destination, TwoTuple twoTuple) {
        if (source.isFile()) {
            File parentDest = new File(destination.getParent());

            if (!parentDest.exists()) {
                parentDest.mkdirs();

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

            } catch (Exception e) {
                System.err.println("CopyJavaSourcesTask.copyFile(): " + e);
            }
        } else {
            System.err.println("CopyJavaSourcesTask.copyFile(): No such file: " + source);
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
