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
package org.apache.lenya.cms.jcr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;

import org.apache.lenya.cms.jcr.mapping.AbstractNodeProxy;
import org.apache.lenya.cms.jcr.mapping.PathElement;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Resource node proxy.
 */
public class ResourceProxy extends AbstractNodeProxy {

    protected static final String NODE_NAME = "lenya:resource";
    protected static final String NODE_TYPE = "lnt:resource";
    protected static final String DATA_PROPERTY = "jcr:data";
    protected static final String LAST_MODIFIED_PROPERTY = "jcr:lastModified";
    protected static final String MIME_TYPE_PROPERTY = "jcr:mimeType";

    /**
     * @return The input stream.
     * @throws RepositoryException if an error occurs.
     */
    public InputStream getInputStream() throws RepositoryException {
        return getPropertyStream(DATA_PROPERTY);
    }

    /**
     * @return The output stream.
     * @throws RepositoryException if an error occurs.
     */
    public OutputStream getOutputStream() throws RepositoryException {
        return new JCROutputStream(this);
    }

    /**
     * @return The content length.
     * @throws RepositoryException if an error occurs.
     */
    public long getContentLength() throws RepositoryException {
        try {
            return getNode().getProperty(DATA_PROPERTY).getLength();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @return The last modification date.
     * @throws RepositoryException if an error occurs.
     */
    public long getLastModified() throws RepositoryException {
        return getPropertyLong(LAST_MODIFIED_PROPERTY);
    }
    
    /**
     * Initializes the node after it has been created.
     * @throws RepositoryException if an error occurs.
     */
    public void init() throws RepositoryException {
        DocumentProxy docProxy = (DocumentProxy) getParentProxy();
        String mimeType = docProxy.getContentNode().getDocumentType().getMimeType();
        setProperty(MIME_TYPE_PROPERTY, mimeType);
        setProperty(DATA_PROPERTY, "");
        setProperty(LAST_MODIFIED_PROPERTY, new GregorianCalendar());
    }

    /**
     * An outputStream that will save the session upon close, and discard it upon cancel.
     */
    private class JCROutputStream extends ByteArrayOutputStream {
        private boolean isClosed = false;

        private final ResourceProxy proxy;

        /**
         * Ctor.
         * @param proxy The resource proxy.
         */
        public JCROutputStream(ResourceProxy proxy) {
            System.out.println("create stream");
            this.proxy = proxy;
        }

        public void close() throws IOException {
            System.out.println("close stream?");
            if (!isClosed) {
                System.out.println("closing stream");
                super.close();
                this.isClosed = true;
                try {
                    byte[] bytes = this.toByteArray();
                    InputStream stream = new ByteArrayInputStream(bytes);
                    this.proxy.setProperty(ResourceProxy.DATA_PROPERTY, stream);
                    this.proxy.setProperty(ResourceProxy.LAST_MODIFIED_PROPERTY,
                            new GregorianCalendar());
                } catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public PathElement getPathElement() throws RepositoryException {
        return getPathElement(NODE_NAME);
    }
}
