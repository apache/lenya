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
package org.apache.lenya.cms.publication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * Wrapper class for the ODT resource type.
 */
public class OpenDocumentWrapper extends AbstractLogEnabled {

    /**
     * The extension for ODT documents.
     */
    public static final String ODT_EXTENSION = "odt";

    /**
     * The mime-type for ODT documents.
     */
    public static final String ODT_MIME_TYPE = "application/vnd.oasis.opendocument.text";

    private Document delegate;

    /**
     * @param doc The document to wrap.
     * @param logger The logger.
     */
    public OpenDocumentWrapper(Document doc, Logger logger) {
        enableLogging(logger);
        this.delegate = doc;
    }

    /**
     * @param file The part to write.
     */
    public void write(Part file) {
        if (!file.getMimeType().equals(ODT_MIME_TYPE)) {
            throw new IllegalArgumentException("Invalid mime type: [" + file.getMimeType() + "]");
        }
        try {
            write(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes the content of the resource.
     * @param inputStream The input stream providing the content.
     */
    public void write(InputStream inputStream) {
        final ByteArrayOutputStream sourceBos = new ByteArrayOutputStream();

        OutputStream destOutputStream = null;
        try {

            IOUtils.copy(inputStream, sourceBos);

            destOutputStream = delegate.getOutputStream();
            IOUtils.write(sourceBos.toByteArray(), destOutputStream);

            delegate.setMimeType(ODT_MIME_TYPE);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (destOutputStream != null) {
                try {
                    destOutputStream.flush();
                    destOutputStream.close();
                } catch (Exception ignore) {
                }
            }
        }

    }

}
