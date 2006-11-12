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

/* $Id: LanguageExistsAction.java 43248 2004-08-17 21:58:49Z michi $  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentDoesNotExistException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.NoChildDocumentExistException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;

/**
 * Action that checks the sitetree if there is a child document with the 
 * current language.
 */
public class FirstChildExistsAction extends AbstractAction {
    

    /**
     * Check if the current document-id has a child document for the 
     * currently requested language.
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
     * document for the current language, null otherwiese
     *
     * @exception DocumentDoesNotExistException if there is no document with the specified document-id.
     * @exception PageEnvelopeException if the PageEnvelope could not be created.
     * @exception DocumentException if the language information could not be fetched from the document.
     * @throws SiteTreeException if the sitetree couldn't be created
     * @throws DocumentBuildException if the DocumentBuilder couldn't be created
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws PageEnvelopeException, DocumentDoesNotExistException, DocumentException, SiteTreeException, DocumentBuildException {

        PageEnvelope pageEnvelope =
            PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        Publication publication = pageEnvelope.getPublication();
        String area = pageEnvelope.getDocument().getArea();
        String language = pageEnvelope.getDocument().getLanguage();
        String id = pageEnvelope.getDocument().getId();

        SiteTree siteTree = publication.getTree(area);
        SiteTreeNode node = siteTree.getNode(id);

        if (node == null) {
            throw new DocumentDoesNotExistException("The document " + pageEnvelope.getDocument().getId() + " does not exist. Check sitetree, it might need to be reloaded.");
        }
        
        SiteTreeNode[] children = node.getChildren(language);
        if (children.length > 0) {

            String childNodeId = children[0].getId();
            String childId = id + "/" + childNodeId;
            DocumentBuilder builder = publication.getDocumentBuilder();
            String url = builder.buildCanonicalUrl(publication, area, childId, language);
            Document childDocument = builder.buildDocument(publication, url);

            if (!childDocument.existsInAnyLanguage()) {
                throw new NoChildDocumentExistException("The document " + childId + " does not exist. Check sitetree, it might need to be reloaded.");
            }
            List availableLanguages = Arrays.asList(childDocument.getLanguages());

            if (availableLanguages.contains(language)) {
                return Collections.unmodifiableMap(Collections.EMPTY_MAP);
            }
        } else {
            throw new NoChildDocumentExistException("This document has no children. Check sitetree, it might need to be reloaded.");
        }
        return null;
    }
}
