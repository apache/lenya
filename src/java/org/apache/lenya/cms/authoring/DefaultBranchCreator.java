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

/* $Id: DefaultBranchCreator.java,v 1.15 2004/03/01 16:18:27 gregor Exp $  */

package org.apache.lenya.cms.authoring;

import java.io.File;

public class DefaultBranchCreator extends DefaultCreator {
    /**
     * Return the child type.
     *
     * @param childType a <code>short</code> value
     *
     * @return a <code>short</code> value
     *
     * @exception Exception if an error occurs
     */
    public short getChildType(short childType) throws Exception {
        return BRANCH_NODE;
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.authoring.DefaultCreator#getChildFileName(java.io.File, java.lang.String)
     */
    protected String getChildFileName(
        File parentDir,
        String childId,
        String language) {
        return parentDir
            + File.separator
            + childId
            + File.separator
            + "index"
            + getLanguageSuffix(language)
            + ".xml";
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.authoring.DefaultCreator#getChildMetaFileName(java.io.File, java.lang.String)
     */
    protected String getChildMetaFileName(
        File parentDir,
        String childId,
        String language) {
        return parentDir
            + File.separator
            + childId
            + File.separator
            + "indexmeta"
            + getLanguageSuffix(language)
            + ".xml";
    }
}
