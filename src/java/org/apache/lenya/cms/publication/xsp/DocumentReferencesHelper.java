/*
$Id: DocumentReferencesHelper.java,v 1.12 2003/11/03 17:49:39 egli Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/

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

/**
 * Helper class for finding references to the current document.
 * 
 * @author Christian Egli
 * @version $Revision: 1.12 $
 */
public class DocumentReferencesHelper {

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
     * e.g. /lenya/unicom/authoring/doctypes/2columns.html
     * 
     * @return the search string
     */
    protected String getReferencesSearchString() {
        return "href\\s*=\\s*\""
            + pageEnvelope.getContext()
            + "/"
            + pageEnvelope.getPublication().getId()
            + "/"
            + pageEnvelope.getDocument().getArea()
            + pageEnvelope.getDocument().getId();
    }

    /**
     * Construct a search string for the search of internal references, 
     * i.e from the current document to others. This is done using 
     * the assumption that internal links look as if they were copied 
     * directly from the browser, e.g. 
     * /lenya/unicom/authoring/doctypes/2columns.html
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
                + "/"
                + pageEnvelope.getPublication().getId()
                + "/"
                + pageEnvelope.getDocument().getArea()
                + "(/[-a-zA-Z0-9_/]+?)(_[a-z][a-z])?\\.html");
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
            sitetree = publication.getSiteTree(Publication.LIVE_AREA);
            String[] internalLinks =
                Grep.findPattern(
                    pageEnvelope.getDocument().getFile(),
                    internalLinkPattern,
                    1);
            String[] internalLinksLanguages =
                Grep.findPattern(
                    pageEnvelope.getDocument().getFile(),
                    internalLinkPattern,
                    2);

            for (int i = 0; i < internalLinks.length; i++) {
                String docId = internalLinks[i];
		String language = null;
                
		if (internalLinksLanguages[i] != null) {
		    // trim the leading '_'
		    language = internalLinksLanguages[i].substring(1);
		}
                SiteTreeNode documentNode = sitetree.getNode(docId);
                if (documentNode == null
                    || documentNode.getLabel(language) == null) {
                    // the docId has not been published for the given language
                    String url =
                        builder.buildCanonicalUrl(
                            publication,
                            Publication.AUTHORING_AREA,
                            docId,
                            language);
                    unpublishedReferences.add(
                        builder.buildDocument(publication, url));
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
