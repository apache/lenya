/*
 * $Id: StaticHTMLExporter.java,v 1.5 2003/02/07 12:14:11 ah Exp $
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
package org.wyona.cms.publishing;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.log4j.Category;

import org.wyona.cms.task.Task;

import java.io.File;

import java.net.URL;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;


/**
 * This Exporter uses WGet to download HTML files from URIs and saves them. The Task parameters
 * are: <code><strong>server-uri</strong></code>: the server uri<br/>
 * <code><strong>server-port</strong></code>: the server port<br/>
 * <code><strong>publication-id</strong></code>: the publication id<br/>
 * <code><strong>export-path-prefix</strong></code>: the path to save the files to<br/>
 * <code><strong>uris</strong></code>: a comma-separated list of uris to download (without server
 * + port)<br/>
 * <code><strong>substitute-regexp</strong></code>: a regular expression to substitute a part of
 * the path<br/>
 *
 * @author <a href="mailto:andreas.hartmann@wyona.com">Andreas Hartmann</a>
 */
public class StaticHTMLExporter extends AbstractExporter {
    static Category log = Category.getInstance(StaticHTMLExporter.class);
    public static final String PARAMETER_URIS = "uris";

    /**
     * DOCUMENT ME!
     *
     * @param serverURI DOCUMENT ME!
     * @param serverPort DOCUMENT ME!
     * @param publicationPath DOCUMENT ME!
     * @param exportPath DOCUMENT ME!
     * @param uris DOCUMENT ME!
     * @param substituteExpression DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void export(URL serverURI, int serverPort, String publicationPath, String exportPath,
        String[] uris, String substituteExpression) throws Exception {
        try {
            String exportDirectory = publicationPath + exportPath;

            org.wyona.net.WGet wget = new org.wyona.net.WGet();
            log.info(".export(): Export directory: " + exportDirectory);
            wget.setDirectoryPrefix(exportDirectory);

            String fullServerURI = serverURI + ":" + serverPort;

            for (int i = 0; i < uris.length; i++) {
                URL uri = new URL(fullServerURI + uris[i]);
                log.info(".export(): Export static HTML: " + uri);

                byte[] response = wget.download(uri, substituteExpression);

                //wget.saveToFile(url.getFile(),response);
            }
        } catch (Exception e) {
            log.error("Export failed: ", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param contextPath DOCUMENT ME!
     */
    public void execute(String contextPath) {
        try {
            String publicationId = getParameters().getParameter(PARAMETER_PUBLICATION_ID);

            Parameters taskParameters = new Parameters();

            PublishingEnvironment environment = new PublishingEnvironment(contextPath, publicationId);

            // read default parameters from PublishingEnvironment
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_EXPORT_PATH,
                environment.getExportDirectory());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_SUBSTITUTE_REGEXP,
                environment.getSubstituteExpression());

            taskParameters.merge(getParameters());
            parameterize(taskParameters);

            String publicationPath = PublishingEnvironment.getPublicationPath(contextPath,
                    publicationId);

            int serverPort = getParameters().getParameterAsInteger(PARAMETER_SERVER_PORT);
            log.debug(".execute(): Server Port: " + serverPort);

            String serverURI = getParameters().getParameter(PARAMETER_SERVER_URI);

            String urisString = getParameters().getParameter(PARAMETER_URIS);
            StringTokenizer st = new StringTokenizer(urisString, ",");
            String[] uris = new String[st.countTokens()];
            int i = 0;

            while (st.hasMoreTokens()) {
                uris[i++] = st.nextToken();
            }

            export(new URL(serverURI), serverPort, publicationPath,
                getParameters().getParameter(PublishingEnvironment.PARAMETER_EXPORT_PATH), uris,
                getParameters().getParameter(PublishingEnvironment.PARAMETER_SUBSTITUTE_REGEXP));
        } catch (Exception e) {
            log.error("Export failed: ", e);
        }
    }
}
