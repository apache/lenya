/*
$Id: DocumentReferencesHelper.java,v 1.1 2003/09/30 14:20:27 egli Exp $
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

import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.publication.DefaultDocumentBuilder;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentDoesNotExistException;
import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.PathToDocumentIdMapper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.search.Grep;

/**
 * Helper class for the policy GUI.
 * 
 * @author andreas
 */
public class DocumentReferencesHelper {

    private PageEnvelope pageEnvelope = null;

    /**
     * Create a new DocumentlanguageHelper.
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
     * Construct a search string for the search of references. 
     * This is done using the document-id, stripping the first '/'
     * and adding 'href=' to the front of it.
     * 
     * @param documentId the document-id for which we're searching
     * documents that reference to it.
     * 
     * @return the search string
     */
    protected String getSearchString(String documentId) {
        return "href\\s*=\\s*\"" + documentId.substring(1);
    }

    /**
     * Find a list of document-id which have references to the current
     * document.
     * 
     * @return an <code>array</code> of document-ids if there are references, 
     * an empty <code>array</code> otherwise 
     * 
     * @throws ProcessingException if the search for references failed.
     */
    public String[] getReferences(String area) throws ProcessingException {

        ArrayList documentIds = new ArrayList();
        Document document = pageEnvelope.getDocument();
        Publication publication = pageEnvelope.getPublication();
        DocumentIdToPathMapper mapper = publication.getPathMapper();
        if (mapper instanceof PathToDocumentIdMapper) {
            PathToDocumentIdMapper fileMapper = (PathToDocumentIdMapper)mapper;
            String documentId = null;
            File[] inconsistentFiles;
            try {
                inconsistentFiles =
                    Grep.find(
                        publication.getContentDirectory(area),
                        getSearchString(document.getId()));
                for (int i = 0; i < inconsistentFiles.length; i++) {

                    documentId =
                        fileMapper.getDocumentId(
                            publication,
                            area,
                            inconsistentFiles[i]);
                    documentIds.add(documentId);
                }
            } catch (IOException e) {
                throw new ProcessingException(e);
            } catch (DocumentDoesNotExistException e) {
                throw new ProcessingException(e);
            }
        }
        return (String[])documentIds.toArray(new String[documentIds.size()]);
    }

    /**
     * 
     * @param area
     * @param documentId
     * @return
     */
    public String getURL(String area, String documentId) {
        DocumentBuilder builder = DefaultDocumentBuilder.getInstance();
        Publication pub = pageEnvelope.getPublication();
        String prefix = pageEnvelope.getContext();

        return prefix + builder.buildCanonicalUrl(pub, area, documentId);
    }
}
