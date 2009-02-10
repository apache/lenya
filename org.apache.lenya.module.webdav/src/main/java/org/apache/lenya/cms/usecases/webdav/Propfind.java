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

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
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

        Publication pub = this.getPublication();

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

            Document[] documents = pub.getArea(Publication.AUTHORING_AREA).getDocuments();

            for (int i = 0; i < documents.length; i++) {
                String test = documents[i].getCanonicalWebappURL().replaceFirst("/[^/]*.html", "");
                if (!test.endsWith("/"))
                    test = test + "/";
                if (test.equals(request)) {
                    docs.add(documents[i]);
                    if (documents[i].isCheckedOut()) {
                        checkedOut.add(documents[i].getCheckoutUserId());
                    }
                    else {
                        checkedOut.add(null);
                    }
                }
            }

            setParameter(DOCUMENTS, docs);
            setParameter(RC, checkedOut);
            setParameter(SOURCEURL, request);
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz");
            setParameter(DATEFORMAT, format);
            Date rootModDate = new Date();
            setParameter("rootModDate", rootModDate);
            String defaultLang = pub.getDefaultLanguage();
            setParameter("defaultLang", defaultLang);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The area without the "info-" prefix.
     */
    public String getArea() {
        return Publication.AUTHORING_AREA;
    }

}
