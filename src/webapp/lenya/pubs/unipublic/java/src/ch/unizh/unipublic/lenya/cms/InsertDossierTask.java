/*
 * $Id: InsertDossierTask.java,v 1.9 2003/04/24 13:54:03 gregor Exp $
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

import org.apache.log4j.Category;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.apache.lenya.cms.publishing.PublishingEnvironment;
import org.apache.lenya.cms.task.AbstractTask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.util.List;
import java.util.StringTokenizer;


/**
 * DOCUMENT ME!
 *
 * @author ah
 */
public class InsertDossierTask extends AbstractTask {
    static Category log = Category.getInstance(InsertDossierTask.class);

    /**
     * DOCUMENT ME!
     *
     * @param publicationPath DOCUMENT ME!
     * @param authoringPath DOCUMENT ME!
     * @param source DOCUMENT ME!
     */
    public void insertDossier(String publicationPath, String authoringPath, String source) {
        try {
            // replace leading slash
            String dossierId = source.substring(1);

            StringTokenizer tokenizer = new StringTokenizer(dossierId, "/");
            tokenizer.nextToken();
            dossierId = tokenizer.nextToken() + "/" + tokenizer.nextToken();

            File dossierFile = new File(publicationPath + authoringPath + "/" + source);
            log.debug("\nDossier file: " + dossierFile.getPath());

            Document dossierDocument = new SAXReader().read(dossierFile);

            String headlinePath = "/dossier" + "/head" + "/title";

            DocumentFactory factory = DocumentFactory.getInstance();
            XPath headlineXPath = factory.createXPath(headlinePath);
            List nodes = headlineXPath.selectNodes(dossierDocument, headlineXPath);
            Element headlineElement = (Element) nodes.get(0);
            String title = headlineElement.getText();

            String dossiersFilename = publicationPath + authoringPath + "/frontpage/dossier.xml";

            log.debug("Dossiers file: " + dossiersFilename);

            Document dossiersDocument = new SAXReader().read("file:" + dossiersFilename);

            //insert the dossier at the top
            DocumentHelper documentHelper = new DocumentHelper();

            Element newDossierElement = (Element) dossiersDocument.selectSingleNode(
                    "/dossiers/dossier[@id='" + dossierId + "']");

            if (newDossierElement != null) {
                log.info("the dossier " + dossierId + " is already on the frontpage");
                newDossierElement.setText(title);
            } else {
                newDossierElement = DocumentHelper.createElement("dossier");
                newDossierElement.setText(title);
                newDossierElement.setAttributeValue("id", dossierId);

                Element dossiersElement = (Element) dossiersDocument.selectSingleNode("/dossiers");
                List children = dossiersElement.elements();

                if (children.size() > 0) {
                    children.add(0, newDossierElement);
                } else {
                    children.add(newDossierElement);
                }
            }

            // write the headlines
            File parent = new File(new File(dossiersFilename).getParent());

            if (!parent.exists()) {
                parent.mkdirs();
            }

            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(new BufferedOutputStream(
                        new FileOutputStream(dossiersFilename)), format);
            writer.write(dossiersDocument);
            writer.close();
        } catch (Exception e) {
            log.error("Could not insert dossier: ", e);
        }
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

            taskParameters.merge(getParameters());
            parameterize(taskParameters);

            String sourcesString = getParameters().getParameter("sources");
            StringTokenizer st = new StringTokenizer(sourcesString, ",");
            String[] sources = new String[st.countTokens()];
            int i = 0;

            while (st.hasMoreTokens()) {
                sources[i++] = st.nextToken();
            }

            insertDossier(publicationPath, getParameters().getParameter("authoring-path"),
                sources[0]);
        } catch (Exception e) {
            log.error("Inserting dossier failed: ", e);
        }
    }
}
