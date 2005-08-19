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
/* $Id:$ */
package org.apache.lenya.cms.jcr.usecases;

import java.io.IOException;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Item;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Import JCR content.
 */
public class JCRImport extends AbstractUsecase {
    private static final Logger log = Logger.getLogger(JCRImport.class);
    
    private static final String IMPORT_TARGET_PARAM = "lenya.usecase.importExport.import";
    private static final String IMPORT_TARGET_PUBLICATION = "publication";
    private static final String IMPORT_TARGET_REPOSITORY = "repository";
    
    private static final String JCR_LENYA_ROOT = "/";
    private static final String JCR_LENYA_BASE_NAME = "lenya";
    private static final String JCR_LENYA_PUBLICATON_ROOT = "/lenya/pubs";
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        Request request = ContextHelper.getRequest(this.context);
        
        Part jcrImport = (Part)request.get("jcrcontent");
        
        // Get name of first JCR node ('lenya' or publication name).
        String firstNodeName;
        try {
            firstNodeName = getFirstNodeName(new InputSource(jcrImport.getInputStream()));
        } catch (Exception e) {
            throw new JCRImportException("Error getting first node name of import data");
        }
        if (firstNodeName == null) {
            throw new JCRImportException("Reading repository import data failed");
        }

        Repository repo = null;
        try {
            repo = (Repository)manager.lookup(Repository.class.getName());
        } catch (Exception e) {
            throw new CascadingRuntimeException("Cannot lookup repository", e);
        }
        
        try {
            Session session;
            try {
                session = repo.login();
            } catch (LoginException e1) {
                throw new JCRImportException("Login to repository failed", e1);
            } catch (RepositoryException e1) {
                throw new JCRImportException("Cannot access repository", e1);
            }
            
            Workspace ws = session.getWorkspace();
    
            String importTarget = request.getParameter(IMPORT_TARGET_PARAM);
            if (IMPORT_TARGET_REPOSITORY.equals(importTarget)) {
                // Import Lenya repository
                log.debug("Importing Lenya repository into JCR");
                if (!JCR_LENYA_BASE_NAME.equals(firstNodeName)) {
                    throw new JCRImportException("Corrupt Lenya repository data file");
                }
                if (!session.itemExists(JCR_LENYA_ROOT)) {
                    throw new JCRImportException("Lenya JCR root not found [" + JCR_LENYA_ROOT + "]");
                }
                // Remove existing Lenya repository.
                String lenyaBasePath = JCR_LENYA_ROOT +
                    (JCR_LENYA_ROOT.endsWith("/") ? JCR_LENYA_BASE_NAME : "/" + JCR_LENYA_BASE_NAME);
                if (session.itemExists(lenyaBasePath)) {
                    Item jcrLenyaBase = session.getItem(lenyaBasePath);
                    jcrLenyaBase.remove();
                    session.save();
                }
    
                // Import Lenya repository. Use workspace instead of session because of performance.
                try {
                    ws.importXML(JCR_LENYA_ROOT, jcrImport.getInputStream(),
                            ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
                } catch (Exception e) {
                    throw new JCRImportException("Error importing data into workspace");
                }
            } else if (IMPORT_TARGET_PUBLICATION.equals(importTarget)) {
                // Import Lenya publication 
                log.debug("Importing Lenya publication into JCR");
                if (!session.itemExists(JCR_LENYA_PUBLICATON_ROOT)) {
                    throw new JCRImportException("Lenya JCR root not found [" + JCR_LENYA_ROOT + "]");
                    // TODO: Create JCR_LENYA_PUBLICATON_ROOT
                }
                
                // Remove existing Lenya repository.
                String lenyaPublicationPath = JCR_LENYA_PUBLICATON_ROOT +
                    (JCR_LENYA_PUBLICATON_ROOT.endsWith("/") ? firstNodeName : "/" + firstNodeName);
                if (session.itemExists(lenyaPublicationPath)) {
                    Item jcrPublicationBase = session.getItem(lenyaPublicationPath);
                    jcrPublicationBase.remove();
                    session.save();
                }
    
                // Import Lenya publication. Use workspace instead of session because of performance.
                try {
                    ws.importXML(JCR_LENYA_PUBLICATON_ROOT, jcrImport.getInputStream(),
                            ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
                } catch (Exception e) {
                    throw new JCRImportException("Error importing data into workspace");
                }
            }
        } catch (RepositoryException e) {
            throw new JCRImportException("Error accessing JCR repository while importing data", e);
        }

        super.doExecute();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
    }
    
    private String getFirstNodeName(InputSource xmlInput)
        throws SAXException, IOException
    {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        
        FirstNodeNameHandler contentHandler = new FirstNodeNameHandler(); 
        xmlReader.setContentHandler(contentHandler);
        xmlReader.parse(xmlInput);
        return contentHandler.getFirstNodeName();
    }
    
    class FirstNodeNameHandler extends DefaultHandler
    {
        private static final String NODE_NAME_Q_ATTR = "sv:name";
        
        private boolean isFirstElement = true;
        private String firstNodeName = null;
        
        /**
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException
        {
            if (isFirstElement) {
                firstNodeName = attributes.getValue(NODE_NAME_Q_ATTR);
                isFirstElement = false;
            } else {
                super.startElement(uri, localName, qName, attributes);
            }
        }
        
        protected String getFirstNodeName() {
            return firstNodeName;
        }
    }
}
