/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.metadata.dublincore;

import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataOwner;

/**
 * Helper class to access dublin core meta data.
 */
public class DublinCoreHelper {

    /**
     * @param owner The owner.
     * @return The dublin core title.
     * @throws MetaDataException if the owner has no dublin core meta data.
     */
    public static String getTitle(MetaDataOwner owner) throws MetaDataException {
        return getDublinCore(owner).getFirstValue(DublinCore.ELEMENT_TITLE);
    }
    
    protected static MetaData getDublinCore(MetaDataOwner owner) throws MetaDataException {
        return owner.getMetaData(DublinCore.DC_NAMESPACE);
    }
    
}
