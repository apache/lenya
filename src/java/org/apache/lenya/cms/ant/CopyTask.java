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

/* $Id: CopyTask.java,v 1.8 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;


public class CopyTask extends Task {
    private Path pubsRootDirs;
    private Path toDir;

	/** (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
    public void execute() throws BuildException {
        int numberOfDirectoriesCreated = 0;
        int numberOfFilesCopied = 0;
        TwoTuple twoTuple = new TwoTuple(numberOfDirectoriesCreated, numberOfFilesCopied);

        StringTokenizer st = new StringTokenizer(pubsRootDirs.toString(), File.pathSeparator);

        while (st.hasMoreTokens()) {
            String pubsRootDir = st.nextToken();

            if (new File(pubsRootDir, "publication.xml").isFile()) {
                CopyJavaSourcesTask.copyDir(new File(pubsRootDir), new File(toDir.toString()),
                    twoTuple, null);
            } else {
                // FIXME: Look for publications defined by the file "publication.xml"
                CopyJavaSourcesTask.copyContentOfDir(new File(pubsRootDir),
                    new File(toDir.toString()), twoTuple, null);
            }
        }

        numberOfDirectoriesCreated = twoTuple.x;
        numberOfFilesCopied = twoTuple.y;
        System.out.println("Copying " + numberOfDirectoriesCreated + " directories to " + toDir);
        System.out.println("Copying " + numberOfFilesCopied + " files to " + toDir);
    }

	/**
	 * 
	 * 
	 * @param pubsRootDirs
	 */
    public void setPubsRootDirs(Path pubsRootDirs) {
        this.pubsRootDirs = pubsRootDirs;
    }

	/**
	 * 
	 * 
	 * @param toDir
	 */
    public void setToDir(Path toDir) {
        this.toDir = toDir;
    }
}
