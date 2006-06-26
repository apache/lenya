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

/* $Id: BulkCopyTask.java 416058 2006-06-21 18:24:05Z andreas $  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

/**
 * Copies all files matching filename or a all specififed filesets from each source directory.
 * <br/><br/>
 * Usage:
 * &lt;bulkCopy 
 *     sourcedirs="dirOne:dirTwo" 
 *     todir="WEB-INF/" 
 *     flatten="true">
 *     &lt;fileset includes="java/lib/*"/&gt;
 * &lt;/bulkCopy&gt;
 * <br/><br/>
 * The above sample copies <em>dirOne/java/lib/*</em> and <em>dirTwo/java/lib/*</em>
 * to <em>WEB-INF/lib</em>.  
 */
public class BulkCopyTask extends Copy {
    
    private Path sourceDirs;
    
    /** 
     * @see org.apache.tools.ant.taskdefs.Copy#execute()
     */
    public void execute() throws BuildException {
                
        final StringTokenizer sourceDirTokens = new StringTokenizer(this.sourceDirs.toString(), File.pathSeparator);
            
        while (sourceDirTokens.hasMoreTokens()) {
            final String sourceDir = sourceDirTokens.nextToken();
            
            for(int i=0; i<getFileSets().size(); i++)
                ((FileSet) getFileSets().get(i)).setDir(new File(sourceDir));
            
            super.execute();
        }
    }

    /**
     * @param _sourceDirs Colon separated list of source directories
     */
    public void setSourceDirs(Path _sourceDirs) {
        this.sourceDirs = _sourceDirs;
    }
    
    /**
     * @return Returns the fileSet.
     */
    private List getFileSets() {
        return super.filesets;
    }    
}
