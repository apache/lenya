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
package org.apache.lenya.cms.repo.adapter;

import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Translation;
import org.apache.lenya.cms.repo.metadata.MetaData;

public class RepoLenyaMetaData extends RepoMetaData {

    private Translation translation;

    public RepoLenyaMetaData(MetaData metaData, Translation translation) {
        super(metaData);
        this.translation = translation;
    }

    public String getFirstValue(String key) throws DocumentException {
        if (key.equals("resourceType")) {
            try {
                return translation.getAsset().getAssetType().getName();
            } catch (RepositoryException e) {
                throw new DocumentException(e);
            }
        }
        return super.getFirstValue(key);
    }

    public String[] getValues(String key) throws DocumentException {
        if (key.equals("resourceType")) {
            try {
                return new String[] { translation.getAsset().getAssetType().getName() };
            } catch (RepositoryException e) {
                throw new DocumentException(e);
            }
        }
        return super.getValues(key);
    }

}
