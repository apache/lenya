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

/* $Id$  */

package org.apache.lenya.cms.publishing;

import java.net.URL;

/**
 * An Exporter is used to copy files from the pending to the live server.
 */
public interface Exporter {
    /**
     * Export the specified publication as static HTML
     *
     * @param serverURI Domain name of the server
     * @param serverPort Server port
     * @param publicationPath Path to the publication
     * @param exportPathPrefix prefix to use for the export
     * @param uris starting URLs for the export
     * @param substituteExpression expression to be substituted
     * @param substituteReplacement replacement expression
     * @throws ExportException
     *
     */
    void export(URL serverURI, int serverPort, String publicationPath, String exportPathPrefix,
        String[] uris, String substituteExpression, String substituteReplacement)
        throws ExportException;
}
