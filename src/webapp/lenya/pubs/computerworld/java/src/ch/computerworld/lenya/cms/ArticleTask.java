/*
 * $Id: ArticleTask.java,v 1.8 2003/03/04 17:46:49 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package ch.computerworld.wyona.cms;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.log4j.Category;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.lenya.cms.publishing.PublishingEnvironment;
import org.lenya.cms.task.AbstractTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 *
 * @author gjr
 */
public class ArticleTask extends AbstractTask {
    static Category log = Category.getInstance(ArticleTask.class);

    /**
     * DOCUMENT ME!
     *
     * @param publicationPath DOCUMENT ME!
     * @param authoringPath DOCUMENT ME!
     * @param sources DOCUMENT ME!
     */
    public void prepareArticle(String publicationPath, String authoringPath, String[] sources) {
        String absoluteAuthoringPath = publicationPath + authoringPath + "/";

        for (int i = 0; i < sources.length; i++) {
            File sourceFile = new File(absoluteAuthoringPath + sources[i]);

            try {
                setRevisionDateAndId(absoluteAuthoringPath + sources[i]);

                addToHeadlines(sources[i], absoluteAuthoringPath);

                log.info("Article prepared: " + sourceFile);
            }
             catch (Exception e) {
                log.error("EXCEPTION: Article not prepared (" + sourceFile + "): ", e);
            }
        }
    }

    /**
     * insert the article in the frontpage
     *
     * @param docId id to determine the article
     * @param domainPath path for the different domain (authoring or live)
     *
     * @throws Exception DOCUMENT ME!
     */
    private void addToHeadlines(String docId, String domainPath)
        throws Exception {
        File articleFile = new File(domainPath + docId);

        log.debug("\nArticle file: " + articleFile.getPath());

        Document articleDocument = new SAXReader().read(articleFile);

        String headlinePath = "/article" + "/head" + "/title";

        DocumentFactory factory = DocumentFactory.getInstance();

        XPath headlineXPath = factory.createXPath(headlinePath);

        List nodes = headlineXPath.selectNodes(articleDocument, headlineXPath);

        Element headlineElement = (Element) nodes.get(0);

        String title = headlineElement.getText();

        // article's id format, e.g. /news/article.xml 
        // FIXME: should be readen from the article, but now:
        StringTokenizer st = new StringTokenizer(docId, "/");

        st.nextToken();

        String id = st.nextToken();

        id = id.substring(0, id.length() - 4); // Remove ".xml" extension

        String headlines_filename = domainPath + ComputerworldEnvironment.headlinesFile;

        log.debug(".addToHeadline(): " + headlines_filename);

        Document headlinesDocument = getDocument(domainPath +
                ComputerworldEnvironment.headlinesFile);

        insertElement(headlinesDocument, "/articles", "article", id, title);

        writeDocument(domainPath + ComputerworldEnvironment.headlinesFile, headlinesDocument);
    }

    void writeDocument(String fileName, Document document) {
        // write the headlines
        File parent = new File(new File(fileName).getParent());

        if (!parent.exists()) {
            parent.mkdirs();
        }

        OutputFormat format = OutputFormat.createPrettyPrint();

        try {
            XMLWriter writer = new XMLWriter(new BufferedOutputStream(
                        new FileOutputStream(fileName)), format);

            writer.write(document);

            writer.close();
        } catch (Exception e) {
            log.debug(e);
        }
    }

    protected Document getDocument(String filePath) {
        //headlines in domain 
        // FIXME    headlinesPath=domainPath+conf.getChild("headlines").getAttribute("href");
        String filename = filePath;

        log.debug("filename: " + filename);

        try {
            Document document = new SAXReader().read("file:" + filename);

            return document;
        }
         catch (Exception e) {
            log.error("Can't get document: ", e);

            return null;
        }
    }

    protected void insertElement(Document document, String parentXPath, String elementName,
        String id, String title) {

        Element newArticleElement
             = (Element) document.selectSingleNode(parentXPath + "/" + elementName + "[@id='" + id +
                "']");

        if (newArticleElement != null) {
            log.info("the article  " + id + " is already on the frontpage");

            newArticleElement.setText(title);
        } else {
            newArticleElement = DocumentHelper.createElement(elementName);

            newArticleElement.addAttribute("id", id);

            newArticleElement.setText(title);

            Element articlesElement = (Element) document.selectSingleNode(parentXPath);

            List children = articlesElement.elements();

            if (children.size() > 0) {
                children.add(0, newArticleElement);
            }
            else {
                children.add(newArticleElement);
            }
        }
    }


    /**
     * set the revision date and the compute the revision id
     *
     * @param filename Filename of the article
     *
     * @throws Exception DOCUMENT ME!
     */
    private void setRevisionDateAndId(String filename)
        throws Exception {

        //read the article
        Document doc = new SAXReader().read("file:" + filename);

        //write the article
        OutputFormat format = OutputFormat.createPrettyPrint();

        try {
            XMLWriter writer = new XMLWriter(new BufferedOutputStream(
                        new FileOutputStream(filename)), format);

            writer.write(doc);

            writer.close();
        } catch (Exception e) {
            log.debug(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public synchronized String getMillis() {
        String millis = Long.toString(new Date().getTime());

        // wait to be sure the milliseconds are not used more than once
        try {
            this.wait(1);
        } catch (InterruptedException e) {
        }

        return millis;
    }

    /**
     * Execute the task.
     *
     * @param contextPath DOCUMENT ME!
     */
    public void execute(String contextPath) {
        try {
            String publicationId = getParameters().getParameter("publication-id");

            String publicationPath = PublishingEnvironment.getPublicationPath(contextPath,
                    publicationId);

            Parameters taskParameters = new Parameters();

            PublishingEnvironment environment = new PublishingEnvironment(contextPath, publicationId);

            // read default parameters from PublishingEnvironment
            taskParameters.setParameter("authoring-path", environment.getAuthoringPath());

            taskParameters.setParameter("tree-authoring-path", environment.getTreeAuthoringPath());

            taskParameters.setParameter("live-path", environment.getLivePath());

            taskParameters.setParameter("tree-live-path", environment.getTreeLivePath());

            taskParameters.merge(getParameters());

            parameterize(taskParameters);

            String sourcesString = getParameters().getParameter("sources");

            StringTokenizer st = new StringTokenizer(sourcesString, ",");

            String[] sources = new String[st.countTokens()];

            int i = 0;

            while (st.hasMoreTokens()) {
                sources[i++] = st.nextToken();
            }

            prepareArticle(publicationPath, getParameters().getParameter("authoring-path"), sources);
        } catch (Exception e) {
            log.error("Preparing article failed: ", e);
        }
    }
}
