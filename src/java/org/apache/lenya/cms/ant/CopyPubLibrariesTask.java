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

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 *  
 */
public class CopyPubLibrariesTask extends Task {
    private Path pubsRootDirs;
    private String libDir;
    private String buildDir;
    private String excludes;

    /**
     * (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        /*
         * log("" + pubsRootDirs); log("" + libDir); log("" + buildDir); log("" + excludes);
         */

        int numberOfDirectoriesCreated = 0;
        int numberOfFilesCopied = 0;
        TwoTuple twoTuple = new TwoTuple(numberOfDirectoriesCreated, numberOfFilesCopied);

        StringTokenizer st = new StringTokenizer(pubsRootDirs.toString(), File.pathSeparator);

        FilenameFilter filter = new SCMFilenameFilter(excludes);

        while (st.hasMoreTokens()) {
            String pubsRootDir = st.nextToken();

            if (new File(pubsRootDir, "publication.xml").isFile()) {
                //log("" + pubsRootDir);
                CopyJavaSourcesTask.copyContentOfDir(
                        new File(pubsRootDir + File.separator + libDir), new File(buildDir),
                        twoTuple, filter, this);
            } else {
                log("TODO: Not implemented yet");
                //log("" + pubsRootDir);
                /*
                 * // FIXME: Look for publications defined by the file "publication.xml"
                 * CopyJavaSourcesTask.copyContentOfDir(new File(pubsRootDir), new
                 * File(toDir.toString()), twoTuple, null);
                 */
            }
        }

        numberOfDirectoriesCreated = twoTuple.x;
        numberOfFilesCopied = twoTuple.y;
        log("Copying " + numberOfDirectoriesCreated + " directories to " + buildDir);
        log("Copying " + numberOfFilesCopied + " files to " + buildDir);
    }

    /**
     * Where the publications are located
     * 
     * @param pubsRootDirs
     */
    public void setPubsRootDirs(Path pubsRootDirs) {
        this.pubsRootDirs = pubsRootDirs;
    }

    /**
     * @param libDir
     */
    public void setLibDir(String libDir) {
        this.libDir = libDir;
    }

    /**
     * Where the publications shall be copied to
     * 
     * @param excludes
     */
    public void setBuildDir(String buildDir) {
        this.buildDir = buildDir;
    }

    /**
     * Which filenames shall be excluded
     * 
     * @param excludes
     */
    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }
}