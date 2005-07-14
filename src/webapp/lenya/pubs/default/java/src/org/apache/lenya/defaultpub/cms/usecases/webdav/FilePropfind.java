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
package org.apache.lenya.defaultpub.cms.usecases.webdav;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.cms.rc.RCML;
import org.apache.lenya.cms.rc.RCMLEntry;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.usecases.SiteUsecase;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.DocumentBuilder;

/**
 * Usecase to display the overview tab in the site area for a document.
 * 
 * @version $Id: Overview.java 179790 2005-06-03 13:45:13Z andreas $
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

        Publication _publication = this.getPublication();

        ServiceSelector selector = null;
        SiteManager siteManager = null;
        DocumentBuilder docBuilder = null;
        Vector docs = new Vector();
        Vector checkedOut = new Vector();

        String request = getSourceURL();

        try {
            // get Parameters for RC
            String publicationPath = _publication.getDirectory().getCanonicalPath();
            RCEnvironment rcEnvironment = RCEnvironment.getInstance(_publication.getServletContext()
                    .getCanonicalPath());
            String rcmlDirectory = rcEnvironment.getRCMLDirectory();
            rcmlDirectory = publicationPath + File.separator + rcmlDirectory;
            String backupDirectory = rcEnvironment.getBackupDirectory();
            backupDirectory = publicationPath + File.separator + backupDirectory;

            // Initialize Revision Controller
            RevisionController rc = new RevisionController(rcmlDirectory,
                    backupDirectory,
                    publicationPath);

            Document doc = getTargetDocument(false);
            docs.add(doc);

            String filename = doc.getFile().getCanonicalPath();
            filename = filename.substring(publicationPath.length());
            RCMLEntry entry = rc.getRCML(filename).getLatestEntry();
            if ((entry != null) && (entry.getType() == RCML.co))
                checkedOut.add(entry);
            else
                checkedOut.add(null);

            setParameter(DOCUMENTS, docs);
            setParameter(RC, checkedOut);
            setParameter(SOURCEURL, request);
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz");
            setParameter(DATEFORMAT, format);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                    selector.release(docBuilder);
                }
                this.manager.release(selector);
            }
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
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            try {
                this.publication = factory.getPublication(this.manager, getSourceURL());
            } catch (PublicationException e) {
                throw new RuntimeException(e);
            }
        }
        return this.publication;
    }

}
