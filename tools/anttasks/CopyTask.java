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
 * Task to copy files
 */
public class CopyTask extends Task {
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

            if (new File(pubsRootDir, "publication.xml").isFile()) {
                CopyJavaSourcesTask.copyDir(new File(pubsRootDir), new File(this.toDir.toString()),
                    twoTuple, filter, this);
            } else {
                // FIXME: Look for publications defined by the file "publication.xml"
                CopyJavaSourcesTask.copyContentOfDir(new File(pubsRootDir),
                    new File(this.toDir.toString()), twoTuple, filter, this);
            }
        }

        numberOfDirectoriesCreated = twoTuple.x;
        numberOfFilesCopied = twoTuple.y;
        log("Copying " + numberOfDirectoriesCreated + " directories to " + this.toDir);
        log("Copying " + numberOfFilesCopied + " files to " + this.toDir);
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
