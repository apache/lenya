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
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
import java.text.SimpleDateFormat;

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
import org.apache.lenya.cms.publication.Resource;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.DocumentBuilder;

/**
 * Usecase to provide WebDAV propfind support for a document.
 * 
 * @version $Id: Overview.java 179790 2005-06-03 13:45:13Z andreas $
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

        ServiceSelector selector = null;
        SiteManager siteManager = null;
        ResourcesManager resourcesManager = null;
        DocumentBuilder docBuilder = null;
        Vector docs = new Vector();
        Vector checkedOut = new Vector();
         
        String request = getSourceURL();
        if(request.endsWith(".html"))
        	request = request.substring(0,request.indexOf(".html"));
        if(!request.endsWith("/"))
        	request = request+"/";
        try {
            //get Parameters for RC
            String publicationPath = _publication.getDirectory().getCanonicalPath();
            RCEnvironment rcEnvironment = RCEnvironment.getInstance(_publication.getServletContext()
                    .getCanonicalPath());
            String rcmlDirectory = rcEnvironment.getRCMLDirectory();
            rcmlDirectory = publicationPath + File.separator + rcmlDirectory;
            String backupDirectory = rcEnvironment.getBackupDirectory();
            backupDirectory = publicationPath + File.separator + backupDirectory;

            // Initialize Revision Controller
            RevisionController rc = new RevisionController(rcmlDirectory, backupDirectory, publicationPath);        	
        	
        	selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(_publication.getSiteManagerHint());
            Document[] documents = siteManager.getDocuments(getDocumentIdentityMap(),_publication,this.getArea());

            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            docBuilder = (DocumentBuilder) selector.select(_publication.getDocumentBuilderHint());
            
            for(int i=0; i< documents.length; i++) {
            	String test = documents[i].getCanonicalWebappURL().replaceFirst("/[^/]*.html","");
                if(!test.endsWith("/"))
                	test = test+"/";
            	if(test.equals(request)){
                    docs.add(documents[i]);

            		String filename = documents[i].getFile().getCanonicalPath();
            		filename = filename.substring(publicationPath.length());
                    RCMLEntry entry = rc.getRCML(filename).getLatestEntry();                    
                    if ((entry != null) && (entry.getType() == RCML.co) )
                    	checkedOut.add(entry);
                    else 
                    	checkedOut.add(null);
                    //get additional language documents
                    String[] langs = documents[i].getLanguages();
                    for(int j=0; j< langs.length; j++) {
                    	if(!(langs[j].equals(documents[i].getLanguage()))) {
                    		String url = documents[i].getCanonicalWebappURL().replaceFirst(".html$","_"+langs[j]+".html");
                    		Document langDoc = docBuilder.buildDocument(documents[i].getIdentityMap(),_publication,url);
                    		docs.add(langDoc);

                    		filename = langDoc.getFile().getCanonicalPath();
                    		filename = filename.substring(publicationPath.length());
                            entry = rc.getRCML(filename).getLatestEntry();                    
                            if ((entry != null) && (entry.getType() == RCML.co) )
                            	checkedOut.add(entry);
                            else 
                            	checkedOut.add(null);
                    	}
                    }
            	}
            }
             
            //get assets if we are currently looking at a document            		
            if (!request.endsWith("authoring/")) {
                String url = request.substring(0,request.length()-1)+".html";
    		    Document currentDoc = docBuilder.buildDocument(getDocumentIdentityMap(),_publication,url);
                if (currentDoc.exists()) {
    		        resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
                    Resource[] resources = resourcesManager.getResources(currentDoc);
                    setParameter("assets", Arrays.asList(resources)); 
                }
            }
              
            setParameter(DOCUMENTS, docs);
            setParameter(RC, checkedOut);
            setParameter(SOURCEURL,request);
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            setParameter(DATEFORMAT, format);
            Date rootModDate = new Date();
            setParameter("rootModDate",rootModDate);
            String defaultLang = _publication.getDefaultLanguage();
            setParameter("defaultLang",defaultLang);
            

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
