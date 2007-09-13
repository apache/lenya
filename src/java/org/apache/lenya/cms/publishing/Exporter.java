/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

/* $Id: Exporter.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.cms.publishing;

import java.net.URL;

/**
 * An Exporter is used to copy files from the pending to the live server.
 */
public interface Exporter {
    /**
     * DOCUMENT ME!
     *
     * @param serverURI DOCUMENT ME!
     * @param serverPort DOCUMENT ME!
     * @param publicationPath DOCUMENT ME!
     * @param exportPathPrefix DOCUMENT ME!
     * @param uris DOCUMENT ME!
     * @param substituteExpression DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    void export(URL serverURI, int serverPort, String publicationPath, String exportPathPrefix,
        String[] uris, String substituteExpression, String substituteReplacement)
        throws ExportException;
}
