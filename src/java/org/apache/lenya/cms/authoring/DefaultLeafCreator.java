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

/* $Id$  */

package org.apache.lenya.cms.authoring;

import java.io.File;

import org.apache.lenya.cms.publication.Publication;

public class DefaultLeafCreator extends DefaultCreator {
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
        return LEAF_NODE;
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.authoring.DefaultCreator#getChildFileName(java.io.File, java.lang.String)
     */
    protected String getChildFileName(
        Publication publication,
        String area,
        String parentId,
        String childId,
        String language) {
        return publication.getPathMapper().getFile(publication, area, parentId + "/" + childId, language).getAbsolutePath();
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
            + "-meta"
            + getLanguageSuffix(language)
            + ".xml";
    }
}
