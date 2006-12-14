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

package org.apache.lenya.cms.cocoon.acting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.util.ServletHelper;

/**
 * Action that checks the sitetree if there is a node with the current
 * document-id and the current language, i.e. if the current document has a
 * version in the current language.
 */
public class LanguageExistsAction extends ServiceableAction {

    /**
     * Check if the current document-id has a document for the currently
     * requested language.
     * 
     * If yes return an empty map, if not return null.
     * 
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     * 
     * @return an empty <code>Map</code> if there is a version of this
     *         document for the current language, null otherwiese
     * @throws Exception if an error occurs
     * 
     * @exception Exception if the PageEnvelope could not be created or if the
     *            language information could not be fetched from the document.
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
            Parameters parameters) throws Exception {

        Request request = ObjectModelHelper.getRequest(objectModel);
        DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);

        String url = ServletHelper.getWebappURI(request);

        Publication pub = PublicationUtil.getPublication(this.manager, objectModel);
        if (!pub.exists()) {
            return null;
        }

        DocumentBuilder builder = pub.getDocumentBuilder();
        DocumentLocator locator = builder.getLocator(factory, url);

        Area area = pub.getArea(locator.getArea());
        SiteStructure site = area.getSite();

        List availableLanguages = new ArrayList();
        if (site.contains(locator.getPath())) {
            SiteNode node = site.getNode(locator.getPath());

            String[] languages = pub.getLanguages();
            for (int i = 0; i < languages.length; i++) {
                if (node.hasLink(languages[i])) {
                    availableLanguages.add(languages[i]);
                }
            }
        }
        if (availableLanguages.contains(locator.getLanguage())) {
            return Collections.unmodifiableMap(Collections.EMPTY_MAP);
        }

        return null;
    }
}