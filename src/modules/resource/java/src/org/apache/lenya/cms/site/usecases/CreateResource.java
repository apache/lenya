/*
 * Copyright  1999-2006 The Apache Software Foundation
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
package org.apache.lenya.cms.site.usecases;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.RepositorySource;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.usecase.UsecaseException;

public class CreateResource extends CreateDocument {
    // A media type document is an aggregation of different files.

    /**
     * Validates the request parameters.
     * 
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {
        String title = getParameterAsString("title");

        if (title.length() == 0) {
            addErrorMessage("Please enter a title.");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        addResource();
    }

    /**
     * Adds the ressource. If asset upload is not enabled, an error message is added.
     * @throws IOException
     * @throws ServiceException
     * @throws DocumentException
     */
    protected void addResource() throws ServiceException, IOException, DocumentException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("Assets::addAsset() called");

        Part file = getPart("file");

        if (file.isRejected()) {
            String[] params = { Integer.toString(file.getSize()) };
            addErrorMessage("upload-size-exceeded", params);
        } else {
            addResource(file);
        }
    }

    /**
     * Adds the ressource.
     * 
     * @param file The part.
     * @throws IOException
     * @throws ServiceException
     * @throws DocumentException
     */
    protected void addResource(Part file) throws IOException, ServiceException, DocumentException {
        Document document = getNewDocument();
        MetaData mediaMeta = null;

        SourceResolver resolver = null;
        ModifiableSource source = null;
        OutputStream destOutputStream = null;
        InputStream inputStream = file.getInputStream();
        try {
            mediaMeta = document.getMetaData("http://apache.org/lenya/metadata/media/1.0");
            addResourceMeta(file, mediaMeta);
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            String destination = getNewDocument().getSourceURI();
            source = (ModifiableSource) resolver.resolveURI(destination);

            // now that the source is determined, lock involved nodes
            Node node = getRepositoryNode(destination);
            node.lock();

            destOutputStream = source.getOutputStream();
            final ByteArrayOutputStream sourceBos = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, sourceBos);
            IOUtils.write(sourceBos.toByteArray(), destOutputStream);
            
            document.setMimeType(file.getMimeType());
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (destOutputStream != null) {
                destOutputStream.flush();
                destOutputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug("Resource::addResource() done.");
    }

    protected String getSourceExtension() {
        String extension = "";

        Part file = getPart("file");
        String fileName = file.getFileName();
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > -1) {
            extension = fileName.substring(lastDotIndex + 1);
        } else {
            addErrorMessage("Please upload a file with an extension.");
        }
        return extension;
    }

    protected void addResourceMeta(Part part, MetaData customMeta) throws MetaDataException,
            IOException {
        String fileName = part.getFileName();
        String mimeType = part.getMimeType();
        int fileSize = part.getSize();
        if (customMeta != null) {
            customMeta.setValue("filename", fileName);
        }
        if (canReadMimeType(mimeType)) {
            BufferedImage input = ImageIO.read(part.getInputStream());
            String width = Integer.toString(input.getWidth());
            String height = Integer.toString(input.getHeight());
            if (customMeta != null) {
                customMeta.setValue("height", height);
                customMeta.setValue("width", width);
            }
        }
    }

    public static boolean canReadMimeType(String mimeType) {
        Iterator iter = ImageIO.getImageReadersByMIMEType(mimeType);
        return iter.hasNext();
    } 

    /**
     * @return The repository node that represents the document identified by the destination
     *         string.
     */
    public Node getRepositoryNode(String destination) {
        Node node = null;
        SourceResolver resolver = null;
        RepositorySource documentSource = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            documentSource = (RepositorySource) resolver.resolveURI(destination);
            node = documentSource.getNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (documentSource != null) {
                    resolver.release(documentSource);
                }
                this.manager.release(resolver);
            }
        }
        return node;
    }
}
