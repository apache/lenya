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

/* $Id: SCMFilenameFilter.java,v 1.1 2004/06/01 15:28:52 michi Exp $  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

/**
 *
 */
public class SCMFilenameFilter implements FilenameFilter {
    String[] excludes;

    /**
     *
     */
    public SCMFilenameFilter(String excludes) {
        //System.out.println("SCMFilenameFilter: " + excludes);
        this.excludes = excludes.split(",");
    }

    /**
     *  (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File dir, String name) {
        for (int i = 0; i < excludes.length; i++) {
            if (name.equals(excludes[i])) {
                return false;
            }
        }
        return true;
    }
}
