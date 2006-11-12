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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.TraversableSource;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.repository.RepositoryException;

/**
 * Wrapper to handle resource documents.
 */
public class ResourceWrapper extends AbstractLogEnabled {

    private ServiceManager manager;
    private Document document;

    /**
     * @param document The document to wrap.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public ResourceWrapper(Document document, ServiceManager manager, Logger logger) {
        this.document = document;
        this.manager = manager;
        enableLogging(logger);
    }

    protected Document getDocument() {
        return this.document;
    }

    /**
     * @param file The part to write.
     * @throws IOException
     * @throws MetaDataException
     * @throws ServiceException
     * @throws RepositoryException
     * @throws DocumentException
     */
    public void write(Part file) throws IOException, MetaDataException, ServiceException,
            RepositoryException, DocumentException {

        String mimeType = file.getMimeType();
        String fileName = file.getFileName();
        int fileSize = file.getSize();
        InputStream inputStream = file.getInputStream();

        write(inputStream, mimeType, fileName, fileSize);
    }

    /**
     * Writes the content of the resource from a source.
     * @param sourceUri The source URI.
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws IOException
     * @throws RepositoryException
     * @throws DocumentException
     * @throws MetaDataException
     */
    public void write(String sourceUri) throws ServiceException, MalformedURLException,
            IOException, RepositoryException, DocumentException, MetaDataException {
        SourceResolver resolver = null;
        TraversableSource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (TraversableSource) resolver.resolveURI(sourceUri);
            write(source.getInputStream(),
                    source.getMimeType(),
                    source.getName(),
                    source.getContentLength());
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /**
     * Writes the content of the resource.
     * @param inputStream The input stream providing the content.
     * @param mimeType The mime type.
     * @param fileName The file name.
     * @param fileSize The file size.
     * @throws IOException
     * @throws MetaDataException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws RepositoryException
     * @throws DocumentException
     */
    public void write(InputStream inputStream, String mimeType, String fileName, long fileSize)
            throws IOException, MetaDataException, ServiceException, MalformedURLException,
            RepositoryException, DocumentException {
        final ByteArrayOutputStream sourceBos = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, sourceBos);

        InputStream input = new ByteArrayInputStream(sourceBos.toByteArray());

        MetaData mediaMeta = null;

        SourceResolver resolver = null;
        ModifiableSource source = null;
        OutputStream destOutputStream = null;
        try {
            mediaMeta = document.getMetaData("http://apache.org/lenya/metadata/media/1.0");
            addResourceMeta(fileName, mimeType, input, mediaMeta);
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (ModifiableSource) resolver.resolveURI(document.getSourceURI());

            destOutputStream = source.getOutputStream();
            IOUtils.write(sourceBos.toByteArray(), destOutputStream);

            document.setMimeType(mimeType);

        } finally {
            if (destOutputStream != null) {
                destOutputStream.flush();
                destOutputStream.close();
            }
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug("Resource::addResource() done.");
    }

    protected void addResourceMeta(String fileName, String mimeType, InputStream stream,
            MetaData customMeta) throws MetaDataException, IOException {
        if (customMeta != null) {
            customMeta.setValue("filename", fileName);
        }
        if (canReadMimeType(mimeType)) {
            BufferedImage input = ImageIO.read(stream);
            String width = Integer.toString(input.getWidth());
            String height = Integer.toString(input.getHeight());
            customMeta.setValue("height", height);
            customMeta.setValue("width", width);
        }
    }

    /**
     * Checks if a mime type denotes an image.
     * @param mimeType The mime type.
     * @return A boolean value.
     */
    public static boolean canReadMimeType(String mimeType) {
        Iterator iter = ImageIO.getImageReadersByMIMEType(mimeType);
        return iter.hasNext();
    }

}
