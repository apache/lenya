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

/* $Id: StaticHTMLExporter.java,v 1.20 2004/03/01 16:18:18 gregor Exp $  */

package org.apache.lenya.cms.publishing;

import java.io.File;
import java.net.URL;
import java.util.StringTokenizer;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.log4j.Category;


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
 */
public class StaticHTMLExporter extends AbstractExporter {
    private static Category log = Category.getInstance(StaticHTMLExporter.class);
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
     * @throws ExportException DOCUMENT ME!
     */
    public void export(URL serverURI, int serverPort, String publicationPath, String exportPath,
        String[] uris, String substituteExpression, String substituteReplacement)
        throws ExportException {
        try {
            String exportDirectory = publicationPath + exportPath;

            if (new File(exportPath).isAbsolute()) {
                exportDirectory = exportPath;
            }

            log.info(".export(): Export directory: " + exportDirectory + " (" + publicationPath +
                " , " + exportPath + ")");

            org.apache.lenya.net.WGet wget = new org.apache.lenya.net.WGet();
            wget.setDirectoryPrefix(exportDirectory);

            String fullServerURI = serverURI + ":" + serverPort;

            for (int i = 0; i < uris.length; i++) {
                URL uri = new URL(fullServerURI + uris[i]);
                log.info(".export(): Export static HTML: " + uri);

                wget.download(uri, substituteExpression, substituteReplacement);
            }
        } catch (Exception e) {
            throw new ExportException(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param contextPath DOCUMENT ME!
     */
    public void execute(String contextPath) throws ExecutionException {
        try {
            String publicationId = getParameters().getParameter(PARAMETER_PUBLICATION_ID);

            Parameters taskParameters = new Parameters();

            PublishingEnvironment environment = new PublishingEnvironment(contextPath, publicationId);

            // read default parameters from PublishingEnvironment
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_EXPORT_PATH,
                environment.getExportDirectory());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_SUBSTITUTE_REGEXP,
                environment.getSubstituteExpression());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_SUBSTITUTE_REPLACEMENT,
                environment.getSubstituteReplacement());

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
                getParameters().getParameter(PublishingEnvironment.PARAMETER_SUBSTITUTE_REGEXP),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_SUBSTITUTE_REPLACEMENT));
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
}
