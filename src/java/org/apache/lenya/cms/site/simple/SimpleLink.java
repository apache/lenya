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

import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.site.Label;

/**
 * Link for SimpleSiteManager.
 */
public class SimpleLink extends Label {

    protected SimpleLink(DocumentFactory factory, SimpleSiteNode node, String _label, String language) {
        super(factory, node, _label, language);
    }

    public void delete() {
        SimpleSiteNode node = (SimpleSiteNode) getNode();
        DocumentStore store = (DocumentStore) node.getStructure();
        try {
            store.remove(getDocument());
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLabel(String label) {
    }
    
}
