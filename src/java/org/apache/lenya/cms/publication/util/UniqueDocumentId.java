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

package org.apache.lenya.cms.publication.util;

import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.cms.site.tree.TreeSiteManager;

/**
 * class to compute an unique document id for a document, if there is already a node in the sitetree
 * for a document with this id. It will documentid_"number of version"
 */
public class UniqueDocumentId {

    /**
     * compute an unique document id
     * @param publication The publication the document belongs to.
     * @param area The area the document belongs to.
     * @param documentid The documentid .
     * @return the unique documentid
     */
    public String computeUniqueDocumentId(Publication publication, String area, String documentid) {
        try {
            
            DocumentIdentityMap map = new DocumentIdentityMap(publication);
            SiteManager manager = publication.getSiteManager(map);
            
            if (!(manager instanceof TreeSiteManager)) {
                throw new RuntimeException("Only supported for site trees!");
            }
            
            SiteTree tree = ((TreeSiteManager) manager).getTree(area);
            SiteTreeNode node = tree.getNode(documentid);
            String suffix = null;
            int version = 0;
            String idwithoutsuffix = null;

            if (node != null) {
                int n = documentid.lastIndexOf("/");
                String lastToken = "";
                String substring = documentid;
                if ((n < documentid.length()) && (n > 0)) {
                    lastToken = documentid.substring(n);
                    substring = documentid.substring(0, n);
                }

                int l = lastToken.length();
                int index = lastToken.lastIndexOf("-");
                if (0 < index && index < l) {
                    suffix = lastToken.substring(index + 1);
                    idwithoutsuffix = substring + lastToken.substring(0, index);
                    version = Integer.parseInt(suffix);
                } else {
                    idwithoutsuffix = substring + lastToken;
                }

                while (node != null) {
                    version = version + 1;
                    documentid = idwithoutsuffix + "-" + version;
                    node = tree.getNode(documentid);
                }
            }
        } catch (SiteException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return documentid;
    }
}