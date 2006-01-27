/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.cocoon.source;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.impl.AbstractSource;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * This source provides read-only access to a document's content.
 */
public class ContentSource extends AbstractSource implements LogEnabled {

    /**
     * @param document The document.
     * @param logger The logger.
     */
    public ContentSource(Document document, Logger logger) {
        ContainerUtil.enableLogging(this, logger);
        this.document = document;
    }

    private Document document;

    private Logger logger;

    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    /**
     * @see org.apache.excalibur.source.Source#exists()
     */
    public boolean exists() {
        return this.document != null;
    }

    /**
     * @see org.apache.excalibur.source.Source#getInputStream()
     */
    public InputStream getInputStream() throws IOException, SourceNotFoundException {
        if (getLogger().isDebugEnabled())
            getLogger().debug("Get InputStream for " + getURI());
        if (!exists()) {
            throw new SourceNotFoundException("The source [" + getURI() + "] does not exist!");
        }
        try {
            return this.document.getInputStream();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    protected InputStream convert(org.w3c.dom.Document edoc) throws IOException {

        final org.w3c.dom.Document doc = edoc;
        final PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream();
        pis.connect(pos);

        (new Thread(new Runnable() {

            public void run() {
                try {
                    transform(doc, pos);
                } catch (TransformerException e) {
                    throw new RuntimeException("Failed to tranform org.w3c.dom.Document to PipedOutputStream",
                            e);
                } finally {
                    try {
                        pos.close();
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    }
                }
            }
        },
                getClass().getName() + ".convert(org.w3c.dom.Document edoc)")).start();

        return pis;
    }

    void transform(org.w3c.dom.Document edoc, PipedOutputStream pos) throws TransformerException {

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        transformer.setOutputProperty("encoding", "UTF-8");
        transformer.setOutputProperty("indent", "yes");

        transformer.transform(new DOMSource(edoc), new StreamResult(pos));

    }

    /**
     * @see org.apache.excalibur.source.Source#getContentLength()
     */
    public long getContentLength() {
        try {
            return this.document.getContentLength();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.excalibur.source.Source#getLastModified()
     */
    public long getLastModified() {
        try {
            return this.document.getLastModified();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.excalibur.source.Source#getMimeType()
     */
    public String getMimeType() {
        try {
            return this.document.getMimeType();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The parent.
     */
    public Source getParent() {
        getLogger().warn("getParent() not implemented yet!");
        return null;
    }

    /**
     * 
     */
    public void makeCollection() {
        getLogger().warn("RepositorySource().makeCollection() not implemented yet!");
    }

    /**
     * @return The name of the source.
     */
    public String getName() {
        // Quick and dirty
        String name = new java.io.File(getURI()).getName();
        if (getLogger().isDebugEnabled())
            getLogger().debug("getName(): URI: " + name);
        return name;
    }

}
