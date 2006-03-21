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

package org.apache.lenya.cms.publication.xsp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentDoesNotExistException;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.PathToDocumentIdMapper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.search.Grep;
import org.apache.log4j.Category;

/**
 * Helper class for finding references to the current document.
 */
public class DocumentReferencesHelper {

    private static final Category log = Category.getInstance(DocumentReferencesHelper.class);

    private PageEnvelope pageEnvelope = null;

    /**
     * Create a new DocumentReferencesHelper
     * 
     * @param objectModel the objectModel
     * 
     * @throws ProcessingException if the page envelope could not be created.
     */
    public DocumentReferencesHelper(Map objectModel)
        throws ProcessingException {
        try {
            this.pageEnvelope =
                PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (PageEnvelopeException e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * Construct a search string for the search of references, i.e.
     * links from other documents to the current document. This
     * is done using the assumption that internal links look as if
     * they were copied directly from the browser,
     * e.g. /lenya/default/authoring/doctypes/2columns.html
     * 
     * @return the search string
     */
    protected String getReferencesSearchString() {
        Publication publication = pageEnvelope.getPublication();
        Document document = pageEnvelope.getDocument();
        String langSuffix;
        if (document.getLanguage().equals(publication.getDefaultLanguage())) {
            langSuffix = "(_"+document.getLanguage()+")?";
        } else {
            langSuffix = "_"+document.getLanguage();
        }
        
        return "href\\s*=\\s*\""
            + pageEnvelope.getContext()
            + "/"
            + pageEnvelope.getPublication().getId()
            + "/"
            + pageEnvelope.getDocument().getArea()
            + pageEnvelope.getDocument().getId()
            + langSuffix + ".html";
    }

    /**
     * Construct a search string for the search of internal references, 
     * i.e from the current document to others. This is done using 
     * the assumption that internal links look as if they were copied 
     * directly from the browser, e.g. 
     * /lenya/default/authoring/doctypes/2columns.html
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

        return Pattern.compile(
            "href\\s*=\\s*\""
                + pageEnvelope.getContext()
                + "(/"
                + pageEnvelope.getPublication().getId()
                + "/"
                + pageEnvelope.getDocument().getArea()
                + "(/[-a-zA-Z0-9_/]+?)(_[a-z][a-z])?\\.html)");
    }

    /**
     * Find a list of document-ids which have references to the current
     * document.
     * 
     * @return an <code>array</code> of documents if there are references, 
     * an empty <code>array</code> otherwise 
     * 
     * @throws ProcessingException if the search for references failed.
     */
    public Document[] getReferences(String area) throws ProcessingException {

        ArrayList documents = new ArrayList();
        Publication publication = pageEnvelope.getPublication();
        DocumentIdToPathMapper mapper = publication.getPathMapper();
        if (mapper instanceof PathToDocumentIdMapper) {
            PathToDocumentIdMapper fileMapper = (PathToDocumentIdMapper)mapper;
            String documentId = null;
            String language = null;
            DocumentBuilder builder = publication.getDocumentBuilder();
            File[] inconsistentFiles;
            try {
                inconsistentFiles =
                    Grep.find(
                        publication.getContentDirectory(area),
                        getReferencesSearchString());
                for (int i = 0; i < inconsistentFiles.length; i++) {
                    documentId =
                        fileMapper.getDocumentId(
                            publication,
                            area,
                            inconsistentFiles[i]);
                    language = fileMapper.getLanguage(inconsistentFiles[i]);
                    if (log.isDebugEnabled()) {
                        log.debug("documentId: " + documentId);
                        log.debug("language: " + language);
                    }

                    String url = null;
                    if (language != null) {
                        url =
                            builder.buildCanonicalUrl(
                                publication,
                                area,
                                documentId,
                                language);
                    } else {
                        url =
                            builder.buildCanonicalUrl(
                                publication,
                                area,
                                documentId);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("url: " + url);
                    }
                    documents.add(builder.buildDocument(publication, url));
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } catch (DocumentDoesNotExistException e) {
                throw new ProcessingException(e);
            } catch (DocumentBuildException e) {
                throw new ProcessingException(e);
            }
        }
        return (Document[])documents.toArray(new Document[documents.size()]);
    }

    /**
     * Find all internal references in the current document to documents which have
     * not been published yet.
     * 
     * @return an <code>array</code> of <code>Document</code> of references 
     * from the current document to documents which have not been published yet.
     *
     * @throws ProcessingException if the current document cannot be opened.
     */
    public Document[] getInternalReferences() throws ProcessingException {
        ArrayList unpublishedReferences = new ArrayList();
        SiteTree sitetree;
        Pattern internalLinkPattern = getInternalLinkPattern();
        Publication publication = pageEnvelope.getPublication();
        DocumentBuilder builder = publication.getDocumentBuilder();
        try {
            sitetree = publication.getTree(Publication.LIVE_AREA);
            String[] internalLinks =
                Grep.findPattern(
                    pageEnvelope.getDocument().getFile(),
                    internalLinkPattern,
                    1);

            for (int i = 0; i < internalLinks.length; i++) {
                Document document = builder.buildDocument(publication, internalLinks[i]);
                
                String docId = document.getId();
                String language = document.getLanguage();

                if (log.isDebugEnabled()) {
                    log.debug("docId: " + docId);
                    log.debug("language: " + language);
                }

                SiteTreeNode documentNode = sitetree.getNode(docId);

                if (documentNode == null
                    || documentNode.getLabel(language) == null) {
                    // the docId has not been published for the given language
                    if (log.isDebugEnabled()) {
                        log.debug("url: " + internalLinks[i]);
                    }
                    unpublishedReferences.add(
                        builder.buildDocument(publication, internalLinks[i]));
                }
            }
        } catch (SiteTreeException e) {
            throw new ProcessingException(e);
        } catch (IOException e) {
            throw new ProcessingException(e);
        } catch (DocumentBuildException e) {
            throw new ProcessingException(e);
        }
        return (Document[])unpublishedReferences.toArray(
            new Document[unpublishedReferences.size()]);
    }
}
