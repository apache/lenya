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
import java.util.Arrays;
import java.util.StringTokenizer;

public class JavaFilenameFilter implements FilenameFilter {
    
    protected static final String[] SUFFIXES = { "java", "properties", "xsl" };

	/**
	 *  (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
    public boolean accept(File dir, String name) {
        boolean accept = true;
        if (new File(dir, name).isFile()) {
            String suffix = getExtension(name);
            if (!Arrays.asList(SUFFIXES).contains(suffix)) {
                accept = false;
            }
        }
        return accept;
    }

	/**
	 * Get the extension
	 * 
	 * @param filename the file name from which the extension is extracted
	 * @return the extension
	 */
    static public String getExtension(String filename) {
        StringTokenizer st = new StringTokenizer(filename, ".");
        st.nextToken();

        String extension = "";

        while (st.hasMoreTokens()) {
            extension = st.nextToken();
        }

        return extension;
    }
}
