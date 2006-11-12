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

/* $Id: CopyPublicationsTask.java 416058 2006-06-21 18:24:05Z andreas $  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;


/**
 * Task to copy files of publications and modules
 */
public class CopyPublicationsTask extends Task {
    private Path pubsRootDirs;
    private Path toDir;
    private String excludes;

    /** (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        int numberOfDirectoriesCreated = 0;
        int numberOfFilesCopied = 0;
        TwoTuple twoTuple = new TwoTuple(numberOfDirectoriesCreated, numberOfFilesCopied);

        StringTokenizer st = new StringTokenizer(this.pubsRootDirs.toString(), File.pathSeparator);

        log("Excludes " + this.excludes);
        FilenameFilter filter = new SCMFilenameFilter(this.excludes);

        while (st.hasMoreTokens()) {
            String pubsRootDir = st.nextToken();

            copy(pubsRootDir, filter, twoTuple);
        }

        numberOfDirectoriesCreated = twoTuple.x;
        numberOfFilesCopied = twoTuple.y;
        log("Copying " + numberOfDirectoriesCreated + " directories to " + this.toDir);
        log("Copying " + numberOfFilesCopied + " files to " + this.toDir);
    }

    /**
     *
     */
    public void copy(String pubsRootDir, FilenameFilter filter, TwoTuple twoTuple) {
            // In the case the pubsRootDir is publication dir
            if (new File(pubsRootDir, "publication.xml").isFile()) {
                File pubDir = new File(pubsRootDir);
                log("Copy publication: " + pubDir);
                CopyJavaSourcesTask.copyDir(pubDir, new File(this.toDir.toString()), twoTuple, filter, this);
                File localPublicationXConf = new File(pubDir, "/config/local.publication.xconf");
                if (localPublicationXConf.isFile()) {
                    File publicationXConf = new File(this.toDir.toString() + "/" + pubDir.getName() + "/config/publication.xconf");
                    log("Patch config file with local version: " + localPublicationXConf + " " + publicationXConf);
                    CopyJavaSourcesTask.copyFile(localPublicationXConf, publicationXConf, twoTuple, this, true);
                }

                File localAccessControlXConf = new File(pubDir, "/config/ac/local.ac.xconf");
                if (localAccessControlXConf.isFile()) {
                    File accessControlXConf = new File(this.toDir.toString() + "/" + pubDir.getName() + "/config/ac/ac.xconf");
                    log("Patch access control config file with local version: " + localAccessControlXConf + " " + accessControlXConf);
                    CopyJavaSourcesTask.copyFile(localAccessControlXConf, accessControlXConf, twoTuple, this, true);
                }

            // In the case the pubsRootDir is module dir
	    } else if (new File(pubsRootDir, "module.xml").isFile()) {
                log("Copy module: " + pubsRootDir);
                CopyJavaSourcesTask.copyDir(new File(pubsRootDir), new File(this.toDir.toString()), twoTuple, filter, this);
            } else {
                File[] files = new File(pubsRootDir).listFiles(filter);
                for (int i = 0; i < files.length; i++) {
                    copy(files[i].getAbsolutePath(), filter, twoTuple);
                }
                if (files.length < 1) log("ERROR: No children: " + pubsRootDir);
            }
    }

    /**
     * Where the publications are located
     * 
     * @param _pubsRootDirs
     */
    public void setPubsRootDirs(Path _pubsRootDirs) {
        this.pubsRootDirs = _pubsRootDirs;
    }

    /**
     * Where the publications shall be copied to
     * 
     * @param _toDir
     */
    public void setToDir(Path _toDir) {
        this.toDir = _toDir;
    }

    /**
     * Which filenames shall be excluded
     *
     * @param _excludes
     */
    public void setExcludes(String _excludes) {
        this.excludes = _excludes;
    }
}
