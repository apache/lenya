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

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.GregorianCalendar;

import org.apache.lenya.cms.jcr.mapping.AbstractNodeProxy;
import org.apache.lenya.cms.jcr.mapping.PathElement;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.DocumentType;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.xml.Schema;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.auto.AutoSchemaReader;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

/**
 * Resource node proxy.
 */
public class ResourceProxy extends AbstractNodeProxy {

    protected static final String NODE_NAME = "lenya:resource";
    protected static final String NODE_TYPE = "lnt:resource";
    protected static final String DATA_PROPERTY = "jcr:data";
    protected static final String LAST_MODIFIED_PROPERTY = "jcr:lastModified";
    protected static final String MIME_TYPE_PROPERTY = "jcr:mimeType";

    protected DocumentProxy getDocumentProxy() throws RepositoryException {
        return (DocumentProxy) getParentProxy();
    }

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
            this.proxy = proxy;
        }

        public void close() throws IOException {
            if (!isClosed) {
                super.close();
                this.isClosed = true;
                try {
                    byte[] bytes = this.toByteArray();
                    InputStream stream = new ByteArrayInputStream(bytes);

                    boolean valid = validate(stream);
                    if (valid) {
                        stream = new ByteArrayInputStream(bytes);
                        this.proxy.setProperty(ResourceProxy.DATA_PROPERTY, stream);
                        this.proxy.setProperty(ResourceProxy.LAST_MODIFIED_PROPERTY,
                                new GregorianCalendar());
                        if (this.proxy.getContentLength() != bytes.length) {
                            throw new IOException("The content length is not correct: ["
                                    + this.proxy.getContentLength() + " != " + bytes.length + "]");
                        }
                    } else {
                        throw new IOException("The content to write is not valid!");
                    }
                } catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        protected boolean validate(InputStream stream) throws RepositoryException, IOException {
            Document doc = this.proxy.getDocumentProxy();
            DocumentType doctype = doc.getContentNode().getDocumentType();
            if (doctype.isValidating()) {
                Schema schema = doctype.getSchema();
                if (schema == null) {
                    throw new IOException("The doctype [" + doctype.getName()
                            + "] is validating but does not provide a schema!");
                }

                try {
                    InputSource in = ValidationDriver.uriOrFileInputSource(schema.getURI());
                    PropertyMapBuilder properties = new PropertyMapBuilder();
                    ByteArrayOutputStream error = new ByteArrayOutputStream();
                    ErrorHandlerImpl eh = new ErrorHandlerImpl(new BufferedWriter(new OutputStreamWriter(error)));
                    ValidateProperty.ERROR_HANDLER.put(properties, eh);
                    SchemaReader schemaReader = new AutoSchemaReader();
                    ValidationDriver driver = new ValidationDriver(properties.toPropertyMap(),
                            schemaReader);
                    if (driver.loadSchema(in)) {
                        if (driver.validate(new InputSource(stream))) {
                            System.out.println("" + error);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        throw new RepositoryException("Could not load schema!\n" + error);
                    }
                } catch (SAXException e) {
                    throw new RepositoryException(e);
                }
            }

            return true;
        }

    }

    public PathElement getPathElement() throws RepositoryException {
        return getPathElement(NODE_NAME);
    }
}
