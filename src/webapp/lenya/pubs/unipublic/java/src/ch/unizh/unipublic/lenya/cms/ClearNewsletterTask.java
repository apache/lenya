/*
 * $Id: ClearNewsletterTask.java,v 1.3 2003/03/04 17:47:47 gregor Exp $
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
/*
 * ClearNewsletterTask.java
 *
 * Created on November 20, 2002, 5:08 PM
 */
package ch.unizh.unipublic.wyona.cms;

import org.apache.log4j.Category;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.lenya.cms.publishing.PublishingEnvironment;
import org.lenya.cms.task.AbstractTask;

import org.lenya.xml.DOMWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * DOCUMENT ME!
 *
 * @author ah
 */
public class ClearNewsletterTask extends AbstractTask {
    static Category log = Category.getInstance(ClearNewsletterTask.class);

    /**
     * DOCUMENT ME!
     *
     * @param publicationPath DOCUMENT ME!
     * @param authoringPath DOCUMENT ME!
     */
    public void clearNewsletter(String publicationPath, String authoringPath) {
        String fileName = publicationPath + authoringPath + "/" +
            UnipublicEnvironment.newsletterFile;
        File file = new File(fileName);
        Document document = loadDocument(file);
        saveDocument(document, new File(fileName + ".backup"));

        Element newsletterElement = document.getDocumentElement();
        NodeList articlesElements = newsletterElement.getElementsByTagName("articles");
        Element articlesElement = (Element) articlesElements.item(0);

        NodeList childList = articlesElement.getElementsByTagName("article");
        Element[] children = new Element[childList.getLength()];

        for (int i = 0; i < children.length; i++)
            children[i] = (Element) childList.item(i);

        for (int i = 0; i < children.length; i++)
            articlesElement.removeChild(children[i]);

        saveDocument(document, file);
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document loadDocument(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            return document;
        } catch (Exception e) {
            log.error("Loading document failed: ", e);

            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param file DOCUMENT ME!
     */
    public void saveDocument(Document document, File file) {
        try {
            file.createNewFile();

            PrintWriter printWriter = new PrintWriter(new FileWriter(file));
            DOMWriter writer = new DOMWriter(printWriter);
            writer.print(document);
        } catch (Exception e) {
            log.error("Writing document failed: ", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param contextPath DOCUMENT ME!
     */
    public void execute(String contextPath) {
        try {
            String publicationId = getParameters().getParameter("publication-id");
            PublishingEnvironment environment = new PublishingEnvironment(contextPath, publicationId);
            String publicationPath = environment.getPublicationPath();
            String authoringPath = environment.getAuthoringPath();
            clearNewsletter(publicationPath, authoringPath);
        } catch (Exception e) {
            log.error("Clearing newsletter failed: ", e);
        }
    }
}
