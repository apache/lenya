/*
 * $Id: DossierAction.java,v 1.13 2003/07/04 17:43:09 egli Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package ch.unizh.unipublic.lenya.cms;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;

import org.apache.log4j.Category;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.XPath;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.apache.lenya.cms.publishing.PublishingEnvironment;

import java.io.File;
import java.io.FileOutputStream;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 *
 * @author ah
 */
public class DossierAction extends AbstractComplementaryConfigurableAction {
    private static Category log = Category.getInstance(DossierAction.class);
    public static final String ARTICLE_FILE = "articlefile";
    public static final String DOSSIER_ID = "dossier-id";

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param sourceResolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param str DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public java.util.Map act(Redirector redirector, SourceResolver sourceResolver, Map objectModel,
        String str, Parameters parameters) throws Exception {
        log.debug("\n--------------------------" + "\n- DossierAction invoked" +
            "\n--------------------------");

        // Get Source
        Source inputSource = sourceResolver.resolveURI("");
        String publicationPath = inputSource.getURI();

        // Remove "file:" protocol
        publicationPath = publicationPath.substring(5);

        if (publicationPath.endsWith("/")) {
            publicationPath = publicationPath.substring(0, publicationPath.length() - 1);
        }

        int lastSlashIndex = publicationPath.lastIndexOf("/");
        String publicationId = publicationPath.substring(lastSlashIndex + 1);

        publicationPath = publicationPath.substring(0, lastSlashIndex + 1);

        String publicationPrefix = PublishingEnvironment.PUBLICATION_PREFIX;

        String contextPath = publicationPath.substring(0,
                publicationPath.length() - publicationPrefix.length());

        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        publicationPath = PublishingEnvironment.getPublicationPath(contextPath, publicationId);

        PublishingEnvironment environment = new PublishingEnvironment(publicationPath);

        // set parameters using the request parameters
        log.debug("\n<parameters>");

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            log.debug("\n  Parameter: " + name + " = " + request.getParameter(name));
        }

        log.debug("\n</parameters>");

        String articlePathParameter = request.getParameter(ARTICLE_FILE);
        String newDossierId = request.getParameter(DOSSIER_ID);

        String articlePath = publicationPath + environment.getAuthoringPath() + File.separator +
            articlePathParameter;

        String dossierBasePath = publicationPath + environment.getAuthoringPath() + File.separator +
            "dossiers" + File.separator;

        String newDossierPath = dossierBasePath + newDossierId + File.separator + "index.xml";

        File articleFile = new File(articlePath);
        log.debug("Article file: " + articleFile.getPath());

        SAXReader reader = new SAXReader();
        Document articleDocument = reader.read(articleFile);

        DocumentFactory factory = DocumentFactory.getInstance();

        try {
            //------------------------------------
            // article file
            //------------------------------------
            StringTokenizer tokenizer = new StringTokenizer(articlePathParameter, "/");
            String articleChannel = tokenizer.nextToken();
            String articleSection = tokenizer.nextToken();
            String articleYear = tokenizer.nextToken();
            String articleDirectory = tokenizer.nextToken();

            // create backup
            backupFile(articlePath);

            // on we go
            XPath articleXPath = factory.createXPath(
                    "/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/nitf/body/body.head");
            List parentNodes = articleXPath.selectNodes(articleDocument);
            Element parent = null;

            if (parentNodes.size() > 0) {
                parent = (Element) parentNodes.get(0);
                log.debug("\n--------------------------" + "\n- Parent element: " +
                    parent.getName() + "\n--------------------------");
            } else {
                log.error("\n--------------------------" + "\n- Parent node not found!" +
                    "\n--------------------------");
            }

            Element dossierElement = parent.element("dossier");

            String oldDossierId = "none";

            if (dossierElement != null) {
                oldDossierId = dossierElement.attribute("id").getStringValue();
            }

            boolean replace = !oldDossierId.equals(newDossierId);

            // ----------------------------------------------------
            // do we need to replace it at all?
            // ----------------------------------------------------
            if (replace) {
                // ----------------------------------------------------
                // does a previous dossier entry exist?
                // ----------------------------------------------------
                if (dossierElement != null) {
                    parent.remove(dossierElement);

                    // ----------------------------------------------------
                    // remove from old dossier file
                    // ----------------------------------------------------
                    String oldDossierPath = dossierBasePath + oldDossierId + File.separator +
                        "index.xml";
                    File oldDossierFile = new File(oldDossierPath);

                    Document oldDossierDocument = reader.read(oldDossierFile);

                    XPath dossierXPath = factory.createXPath("/dossier/articles");
                    List oldArticlesElements = dossierXPath.selectNodes(oldDossierDocument);
                    Element oldArticlesElement = null;

                    if (oldArticlesElements.size() > 0) {
                        oldArticlesElement = (Element) oldArticlesElements.get(0);
                        log.debug("\n--------------------------" +
                            "\n- Old Dossier Parent element: " + oldArticlesElement.getName() +
                            "\n--------------------------");
                    } else {
                        log.error("\n--------------------------" +
                            "\n- Old Dossier Parent node not found!" +
                            "\n--------------------------");
                    }

                    List articleElements = oldArticlesElement.elements("article");
                    Element elementToRemove = null;

                    for (Iterator i = articleElements.iterator(); i.hasNext();) {
                        Element articleElement = (Element) i.next();

                        if (articleElement.attribute("channel").getStringValue().equals(articleChannel) &&
                                articleElement.attribute("section").getStringValue().equals(articleSection) &&
                                articleElement.attribute("year").getStringValue().equals(articleYear) &&
                                articleElement.attribute("id").getStringValue().equals(articleDirectory)) {
                            elementToRemove = articleElement;
                        }
                    }

                    oldArticlesElement.remove(elementToRemove);
                    writeDocument(oldDossierPath, oldDossierDocument);

                    // ----------------------------------------------------
                    // removing done
                    // ----------------------------------------------------
                }

                if (!newDossierId.equals("none")) {
                    // ----------------------------------------------------
                    // insert new dossier element
                    // ----------------------------------------------------
                    dossierElement = factory.createElement("dossier");
                    dossierElement.add(factory.createAttribute(dossierElement, "id", newDossierId));
                    parent.add(dossierElement);

                    //------------------------------------
                    // new dossier file
                    //------------------------------------
                    // create backup
                    backupFile(newDossierPath);

                    File newDossierFile = new File(newDossierPath);
                    log.debug("New dossier file: " + newDossierFile.getPath());

                    Document newDossierDocument = reader.read(newDossierFile);

                    XPath dossierXPath = factory.createXPath("/dossier/articles");
                    List articlesElements = dossierXPath.selectNodes(newDossierDocument);
                    Element articlesElement = null;

                    if (articlesElements.size() > 0) {
                        articlesElement = (Element) articlesElements.get(0);
                        log.debug("\n--------------------------" + "\n- Parent element: " +
                            articlesElement.getName() + "\n--------------------------");
                    } else {
                        log.error("\n--------------------------" + "\n- Parent node not found!" +
                            "\n--------------------------");
                    }

                    Element articleElement = factory.createElement("article");
                    articleElement.add(factory.createAttribute(articleElement, "channel",
                            articleChannel));
                    articleElement.add(factory.createAttribute(articleElement, "section",
                            articleSection));
                    articleElement.add(factory.createAttribute(articleElement, "year", articleYear));
                    articleElement.add(factory.createAttribute(articleElement, "id",
                            articleDirectory));
                    articlesElement.add(articleElement);

                    writeDocument(newDossierPath, newDossierDocument);
                }

                // finally save article file
                writeDocument(articlePath, articleDocument);
            }
        } catch (Exception e) {
            log.error("Updating article failed: ", e);
        }

        //------------------------------------------------------------
        // get session
        //------------------------------------------------------------
        Session session = request.getSession(true);

        if (session == null) {
            getLogger().error("No session object");

            return null;
        }

        //------------------------------------------------------------
        // Return referer
        //------------------------------------------------------------
        String parent_uri = (String) session.getAttribute(
                "org.apache.lenya.cms.cocoon.acting.DossierAction.parent_uri");
        HashMap actionMap = new HashMap();
        actionMap.put("parent_uri", parent_uri);
        session.removeAttribute("org.apache.lenya.cms.cocoon.acting.DossierAction.parent_uri");

        return actionMap;
    }

    protected void backupFile(String path) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(path));

            String backupPath = path.substring(0, path.length() - 4);
            backupPath += "_backup.xml";
            writeDocument(backupPath, document);
        } catch (Exception e) {
            log.error("Backup failed: ", e);
        }
    }

    protected void writeDocument(String path, Document document) {
        try {
            File file = new File(path);
            file.createNewFile();

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");

            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            log.error("Writing document failed: ", e);
        }
    }
}
