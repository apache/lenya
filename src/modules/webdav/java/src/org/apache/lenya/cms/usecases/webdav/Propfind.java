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
import java.util.Date;
import java.util.Vector;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.usecases.SiteUsecase;

/**
 * Usecase to provide WebDAV propfind support for a document.
 * 
 */
public class Propfind extends SiteUsecase {

    protected static final String DOCUMENT = "document";
    protected static final String DOCUMENTS = "documents";
    protected static final String SOURCEURL = "sourceURL";
    protected static final String DATEFORMAT = "dateFormat";
    protected static final String RC = "rc";

    /**
     * Ctor.
     */
    public Propfind() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Publication _publication = this.getPublication();

        ServiceSelector siteManagerSelector = null;
        SiteManager siteManager = null;
        Vector docs = new Vector();
        Vector checkedOut = new Vector();

        String request = getSourceURL();
        if (request.endsWith(".html"))
            request = request.substring(0, request.indexOf(".html"));
        if (!request.endsWith("/"))
            request = request + "/";
        if (request.indexOf("webdav") > -1) {
            request = request.replaceFirst("webdav", "authoring");
        }
        try {

            siteManagerSelector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE
                    + "Selector");
            siteManager = (SiteManager) siteManagerSelector.select(_publication.getSiteManagerHint());
            Document[] documents = siteManager.getDocuments(getDocumentFactory(),
                    _publication,
                    Publication.AUTHORING_AREA);

            for (int i = 0; i < documents.length; i++) {
                String test = documents[i].getCanonicalWebappURL().replaceFirst("/[^/]*.html", "");
                if (!test.endsWith("/"))
                    test = test + "/";
                if (test.equals(request)) {
                    docs.add(documents[i]);

                    Node node = documents[i].getRepositoryNode();
                    if (node.isCheckedOut()) {
                        checkedOut.add(node.getCheckoutUserId());
                    }
                    else {
                        checkedOut.add(null);
                    }
                }
            }

	    //FIXME: This is a workaround for the error
	    //  "This usecase can only be invoked on documents!"
	    // triggered in (superclass) DocumentUsecase.doCheckPreconditions()
	    // when the usecase parameter "document" is not set properly.
	    if (docs.size() > 0) {
		setParameter(DOCUMENT, docs.get(0));
	    }
            setParameter(DOCUMENTS, docs);
            setParameter(RC, checkedOut);
            setParameter(SOURCEURL, request);
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz");
            setParameter(DATEFORMAT, format);
            Date rootModDate = new Date();
            setParameter("rootModDate", rootModDate);
            String defaultLang = _publication.getDefaultLanguage();
            setParameter("defaultLang", defaultLang);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (siteManagerSelector != null) {
                if (siteManager != null) {
                    siteManagerSelector.release(siteManager);
                }
                this.manager.release(siteManagerSelector);
            }
        }
    }

    /**
     * @return The area without the "info-" prefix.
     */
    public String getArea() {
        return Publication.AUTHORING_AREA;
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
