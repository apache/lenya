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
package org.apache.lenya.cms.usecases.webdav;

import java.text.SimpleDateFormat;
import java.util.Vector;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.usecases.SiteUsecase;

/**
 * Usecase to display the overview tab in the site area for a document.
 * 
 */
public class FilePropfind extends SiteUsecase {

    protected static final String DOCUMENT = "document";
    protected static final String DOCUMENTS = "documents";
    protected static final String SOURCEURL = "sourceURL";
    protected static final String DATEFORMAT = "dateFormat";
    protected static final String RC = "rc";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Vector docs = new Vector();
        Vector checkedOut = new Vector();

        String request = getSourceURL();

        try {

            Document doc = getTargetDocument(false);
            docs.add(doc);
            
            Node node = doc.getRepositoryNode();
            if (node.isCheckedOut()) {
                checkedOut.add(node.getCheckoutUserId());
            }
            else {
                checkedOut.add(null);
            }

            setParameter(DOCUMENTS, docs);
            setParameter(RC, checkedOut);
            setParameter(SOURCEURL, request);
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz");
            setParameter(DATEFORMAT, format);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The area without the "info-" prefix.
     */
    public String getArea() {
        URLInformation info = new URLInformation(getSourceURL());
        return info.getArea();
    }

    /**
     * Returns the document to be redirected to after the usecase has been completed. If the
     * parameter <code>success</code> is false, the source document is returned (override this
     * method to change this behaviour).
     * @param success If the usecase was successfully completed.
     * @return A document.
     */
    protected Document getTargetDocument(boolean success) {
        Document document = (Document) getParameter(TARGET_DOCUMENT);
        if (document == null) {
            document = getSourceDocument();
        }
        return document;
    }

    private Publication publication;

    /**
     * Access to the current publication. Use this when the publication is not yet known in the
     * usecase: e.g. when creating a global asset. When adding a resource or a child to a document,
     * access the publication via that document's interface instead.
     * 
     * @return the publication in which the use-case is being executed
     */
    protected Publication getPublication() {
        if (this.publication == null) {
            try {
                this.publication = PublicationUtil.getPublicationFromUrl(this.manager,
                        getDocumentFactory(),
                        getSourceURL());
            } catch (PublicationException e) {
                throw new RuntimeException(e);
            }
        }
        return this.publication;
    }

}
