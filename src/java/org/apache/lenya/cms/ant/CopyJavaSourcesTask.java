/*
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


/**
 * @author Michael Wechner
 * @version $Id: CopyJavaSourcesTask.java,v 1.18 2003/11/16 18:46:16 michi Exp $
 */
public class CopyJavaSourcesTask extends Task {
    private Path pubsRootDirs;
    private String javaDir;
    private String buildDir;

    private static final String FILENAMEPATTERN = ".*\\.java";

    /**
     *
     */
    public void execute() throws BuildException {
        int numberOfDirectoriesCreated = 0;
        int numberOfFilesCopied = 0;
        TwoTuple twoTuple = new TwoTuple(numberOfDirectoriesCreated, numberOfFilesCopied);

        File absoluteBuildDir = new File(getProject().getBaseDir(), Project.translatePath(buildDir));

        //System.out.println("CopyJavaSourcesTask.execute(): " + absoluteBuildDir);
        //System.out.println("CopyJavaSourcesTask.execute(): " + pubsRootDirs);
        StringTokenizer st = new StringTokenizer(pubsRootDirs.toString(), File.pathSeparator);

        while (st.hasMoreTokens()) {
            String pubsRootDir = st.nextToken();

            //System.out.println("CopyJavaSourcesTask.execute(): " + pubsRootDir);
            File path = new File(pubsRootDir);

            if (path.isDirectory()) {
                if (new File(path, "publication.xml").isFile()) {
                    // FIXME: RegexFilter doesn't work properly
                    //copyContentOfDir(new File(path, javaDir), absoluteBuildDir, twoTuple, new RegexFilter(FILENAMEPATTERN));
                    copyContentOfDir(new File(path, javaDir), absoluteBuildDir, twoTuple, new JavaFilenameFilter());
                } else {
                    // FIXME: Look for publications defined by the file "publication.xml"
                    String[] pubs = path.list();

                    for (int i = 0; i < pubs.length; i++) {
                        //System.out.println("CopyJavaSourcesTask.execute(): " + pubs[i]);
                        File pubJavaDir = new File(path, new File(pubs[i], javaDir).toString());

                        //System.out.println("CopyJavaSourcesTask.execute(): " + pubJavaDir);
                        //System.out.println("CopyJavaSourcesTask.execute(): " + absoluteBuildDir);

                        // FIXME: RegexFilter doesn't work properly
                        //copyContentOfDir(pubJavaDir, absoluteBuildDir, twoTuple, new RegexFilter(FILENAMEPATTERN));
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
            //System.out.println("CopyJavaSourcesTask.copyContentOfDir(): Number of files found: " + files.length);

            for (int i = 0; i < files.length; i++) {
                File file = new File(source, files[i]);

                if (file.isFile()) {
                    copyFile(file, new File(destination, files[i]), twoTuple);
                } else if (file.isDirectory()) {
                    //System.out.println("CopyJavaSourcesTask.copyContentOfDir(): " + source + " " + destination);
                    copyContentOfDir(file, new File(destination, files[i]), twoTuple, filenameFilter);
                } else {
                    System.err.println("CopyJavaSourcesTask.copyDir(): Neither file nor directory: " + file);
                }
            }
        } else {
            //System.err.println("CopyJavaSourcesTask.copyContentOfDir(): No such directory: " + source);
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
