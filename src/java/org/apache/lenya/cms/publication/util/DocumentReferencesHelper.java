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

package org.apache.lenya.cms.publication.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentDoesNotExistException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.PathToDocumentIdMapper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.search.Grep;
import org.apache.log4j.Logger;

/**
 * Helper class for finding references to the current document.
 */
public class DocumentReferencesHelper {

    private static final Logger log = Logger.getLogger(DocumentReferencesHelper.class);

    private PageEnvelope pageEnvelope = null;
    private DocumentFactory identityMap;

    /**
     * Create a new DocumentReferencesHelper
     * @param map The identity map.
     * @param objectModel the objectModel
     * @param manager The service manager.
     * 
     * @throws ProcessingException if the page envelope could not be created.
     */
    public DocumentReferencesHelper(DocumentFactory map, Map objectModel, ServiceManager manager)
            throws ProcessingException {
        this.identityMap = map;
        try {
            Publication pub = PublicationUtil.getPublication(manager, objectModel);
            this.pageEnvelope = PageEnvelopeFactory.getInstance().getPageEnvelope(manager,
                    map,
                    objectModel,
                    pub);
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * Construct a search string for the search of references, i.e. links from other documents to
     * the current document. This is done using the assumption that internal links look as if they
     * were copied directly from the browser, e.g. /lenya/default/authoring/doctypes/2columns.html
     * 
     * @return the search string
     */
    protected String getReferencesSearchString() {
        return "href\\s*=\\s*\"" + this.pageEnvelope.getContext() + "/"
                + this.pageEnvelope.getPublication().getId() + "/"
                + this.pageEnvelope.getDocument().getArea()
                + this.pageEnvelope.getDocument().getId();
    }

    /**
     * Construct a search string for the search of internal references, i.e from the current
     * document to others. This is done using the assumption that internal links look as if they
     * were copied directly from the browser, e.g. /lenya/default/authoring/doctypes/2columns.html
     * 
     * @return the search string
     */
    protected Pattern getInternalLinkPattern() {
        // FIXME: The following method is not very robust and certainly
        // will fail if the mapping between URL and document-id changes

        // Link Management now assumes that internal links are of the
        // form
        // href="$CONTEXT_PREFIX/$PUBLICATION_ID/$AREA$DOCUMENT_ID(_[a-z][a-z])?.html
        // If there is a match in a document file it is assumed that
        // this is an internal link and is treated as such (warning if
        // publish with unpublished internal links and warning if
        // deactivate with internal references).

        // However this is not coordinated with the
        // DocumentToPathMapper and will probably fail if the URL
        // looks different.

        return Pattern.compile("href\\s*=\\s*\"" + this.pageEnvelope.getContext() + "/"
                + this.pageEnvelope.getPublication().getId() + "/"
                + this.pageEnvelope.getDocument().getArea()
                + "(/[-a-zA-Z0-9_/]+?)(_[a-z][a-z])?\\.html");
    }

    /**
     * Find a list of document-ids which have references to the current document.
     * @param area The area.
     * 
     * @return an <code>array</code> of documents if there are references, an empty
     *         <code>array</code> otherwise
     * 
     * @throws ProcessingException if the search for references failed.
     */
    public Document[] getReferences(String area) throws ProcessingException {

        ArrayList documents = new ArrayList();
        Publication publication = this.pageEnvelope.getPublication();
        DocumentIdToPathMapper mapper = publication.getPathMapper();
        if (mapper instanceof PathToDocumentIdMapper) {
            PathToDocumentIdMapper fileMapper = (PathToDocumentIdMapper) mapper;
            String documentId = null;
            String language = null;
            File[] inconsistentFiles;
            try {
                inconsistentFiles = Grep.find(publication.getContentDirectory(area),
                        getReferencesSearchString());
                for (int i = 0; i < inconsistentFiles.length; i++) {
                    // for performance reasons the getReferencesSearchString()
                    // is
                    // constructed in a way such that it will catch all files
                    // which
                    // have a link to any language version of the current
                    // document.
                    // That's why we need to do some additional tests for each
                    // hit.
                    String languageOfCurrentDocument = this.pageEnvelope.getDocument()
                            .getLanguage();
                    String defaultLanguage = this.pageEnvelope.getPublication()
                            .getDefaultLanguage();
                    Pattern referencesSearchStringWithLanguage = Pattern.compile(getReferencesSearchString()
                            + "_" + languageOfCurrentDocument);
                    Pattern referencesSearchStringWithOutLanguage = Pattern.compile(getReferencesSearchString()
                            + "\\.html");
                    log.debug("languageOfCurrentDocument: " + languageOfCurrentDocument);
                    log.debug("defaultLanguage: " + defaultLanguage);
                    log.debug("referencesSearchStringWithOutLanguage: "
                            + referencesSearchStringWithOutLanguage.pattern());
                    log.debug("referencesSearchStringWithLanguage: "
                            + referencesSearchStringWithLanguage.pattern());
                    // a link is indeed to the current document if the following
                    // conditions
                    // are met:
                    // 1. the link is to foo_xx and the language of the current
                    // document is xx.
                    // 2. or the link is to foo.html and the language of the
                    // current
                    // document is the default language.
                    // Now negate the expression because we continue if above
                    // (1) and (2) are
                    // false, and you'll get the following if statement
                    if (!Grep.containsPattern(inconsistentFiles[i],
                            referencesSearchStringWithLanguage)
                            && !(Grep.containsPattern(inconsistentFiles[i],
                                    referencesSearchStringWithOutLanguage) && languageOfCurrentDocument.equals(defaultLanguage))) {
                        // the reference foo_xx is neither to the language of
                        // the current
                        // document.
                        // nor is the reference foo.html and the current
                        // document is in the
                        // default language.
                        // So the reference is of no importance to us, skip
                        continue;
                    }

                    documentId = fileMapper.getDocumentId(publication, area, inconsistentFiles[i]);
                    log.debug("documentId: " + documentId);

                    language = fileMapper.getLanguage(inconsistentFiles[i]);
                    if (language == null) {
                        language = publication.getDefaultLanguage();
                    }
                    log.debug("language: " + language);

                    documents.add(this.identityMap.get(publication, area, documentId, language));
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } catch (DocumentDoesNotExistException e) {
                throw new ProcessingException(e);
            } catch (DocumentBuildException e) {
                throw new ProcessingException(e);
            }
        }
        return (Document[]) documents.toArray(new Document[documents.size()]);
    }

    /**
     * Find all internal references in the current document to documents which have not been
     * published yet.
     * 
     * @return an <code>array</code> of <code>Document</code> of references from the current
     *         document to documents which have not been published yet.
     * 
     * @throws ProcessingException if the current document cannot be opened.
     */
    public Document[] getInternalReferences() throws ProcessingException {
        ArrayList unpublishedReferences = new ArrayList();
        Pattern internalLinkPattern = getInternalLinkPattern();
        Publication publication = this.pageEnvelope.getPublication();
        try {
            String[] internalLinks = Grep.findPattern(this.pageEnvelope.getDocument().getFile(),
                    internalLinkPattern,
                    1);
            String[] internalLinksLanguages = Grep.findPattern(this.pageEnvelope.getDocument()
                    .getFile(), internalLinkPattern, 2);

            for (int i = 0; i < internalLinks.length; i++) {
                String docId = internalLinks[i];
                String language = null;

                log.debug("docId: " + docId);
                if (internalLinksLanguages[i] != null) {
                    // trim the leading '_'
                    language = internalLinksLanguages[i].substring(1);
                }

                if (language == null) {
                    language = publication.getDefaultLanguage();
                }
                log.debug("language: " + language);

                Document liveDocument = this.identityMap.get(publication,
                        Publication.LIVE_AREA,
                        docId,
                        language);
                if (!liveDocument.exists()) {
                    // the docId has not been published for the given language
                    String liveLanguage = language;
                    if (liveLanguage == null) {
                        liveLanguage = publication.getDefaultLanguage();
                    }
                    unpublishedReferences.add(liveDocument.getTranslation(liveLanguage));
                }
            }
        } catch (final DocumentBuildException e) {
            throw new ProcessingException(e);
        } catch (final DocumentException e) {
            throw new ProcessingException(e);
        } catch (final IOException e) {
            throw new ProcessingException(e);
        }

        return (Document[]) unpublishedReferences.toArray(new Document[unpublishedReferences.size()]);
    }
}