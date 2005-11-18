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

import javax.jcr.Node;

import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR-based document implementation.
 */
public class JCRDocument extends NodeWrapper implements Document {

    /**
     * Ctor.
     * @param node The JCR node.
     */
    public JCRDocument(Node node) {
        super(node);
    }

    public String getLanguage() throws RepositoryException {
        try {
            return getNode().getProperty(JCRDocumentBuilder.LANGUAGE_ATTRIBUTE).getString();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public void setLanguage(String language) throws RepositoryException {
        try {
            getNode().getProperty(JCRDocumentBuilder.LANGUAGE_ATTRIBUTE).setValue(language);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    protected Node getResourceNode() throws RepositoryException {
        try {
            Node resourceNode = getNode().getNode("lenya:resource");
            if (resourceNode == null) {
                throw new RepositoryException("Node [" + getNode().getPath()
                        + "] doesn't have a resource child node!");
            }
            return resourceNode;
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public InputStream getInputStream() throws RepositoryException {
        try {
            return getResourceNode().getProperty("jcr:data").getStream();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public OutputStream getOutputStream() throws RepositoryException {
        return new JCROutputStream(getResourceNode());
    }

    public long getContentLength() throws RepositoryException {
        try {
            return getResourceNode().getProperty("jcr:data").getLength();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected String getMimeType() throws RepositoryException {
        try {
            return getResourceNode().getProperty("jcr:mimeType").getString();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public long getLastModified() throws RepositoryException {
        try {
            return getResourceNode().getProperty("jcr:lastModified").getLong();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * An outputStream that will save the session upon close, and discard it upon cancel.
     */
    private class JCROutputStream extends ByteArrayOutputStream {
        private boolean isClosed = false;

        private final Node contentNode;

        /**
         * Ctor.
         * @param contentNode The JCR node.
         */
        public JCROutputStream(Node contentNode) {
            this.contentNode = contentNode;
        }

        public void close() throws IOException {
            if (!isClosed) {
                super.close();
                this.isClosed = true;
                try {
                    contentNode.setProperty("jcr:data",
                            new ByteArrayInputStream(this.toByteArray()));
                } catch (javax.jcr.RepositoryException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
