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
package org.apache.lenya.cms.site.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.site.AbstractSiteNode;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteStructure;

public class SimpleSiteNode extends AbstractSiteNode {
    
    public SimpleSiteNode(DocumentStore store, String path, String uuid) {
        super(store.getPublication(), store, path, uuid);
    }

    public Link getLink(String language) throws SiteException {
        DocumentStore store = (DocumentStore) getStructure();
        return new Label(store.getDelegate().getFactory(), this, "", language);
    }

    public String getName() {
        String[] steps = getPath().split("/");
        return steps[steps.length - 1];
    }

    public String[] getLanguages() {
        DocumentStore store = (DocumentStore) getStructure();
        List languages = new ArrayList();
        Document[] docs;
        try {
            docs = store.getDocuments();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < docs.length; i++) {
            if (docs[i].getUUID().equals(getUuid())) {
                if (languages.contains(docs[i].getLanguage())) {
                    throw new RuntimeException("Document [" + docs[i] + "] is contained twice!");
                }
                languages.add(docs[i].getLanguage());
            }
        }
        return (String[]) languages.toArray(new String[languages.size()]);
    }

}
