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
package org.apache.lenya.cms.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.TraversableSource;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Publication;

/**
 * Provide access to a source.
 */
public class SourceWrapper extends AbstractLogEnabled {

    private SourceNode node;
    private String sourceUri;
    private SourceResolver sourceResolver;

    /**
     * Ctor.
     * @param node
     * @param sourceUri
     * @param resolver 
     * @param logger
     */
    public SourceWrapper(SourceNode node, String sourceUri, SourceResolver resolver, Log logger) {
        Validate.notNull(node);
        Validate.notNull(sourceUri);
        Validate.notNull(resolver);
        this.node = node;
        this.sourceUri = sourceUri;
        this.sourceResolver = resolver;
    }

    protected static final String FILE_PREFIX = "file:/";
    protected static final String CONTEXT_PREFIX = "context://";

    protected SourceNode getNode() {
        return this.node;
    }

    private String realSourceUri;

    /**
     * Returns the URI of the actual source which is used.
     * 
     * @return A string.
     */
    protected String getRealSourceUri() {
        if (this.realSourceUri == null) {
            this.realSourceUri = computeRealSourceUri(getSourceResolver(), getNode()
                    .getRepositorySession(), this.sourceUri, getLogger());
        }
        return this.realSourceUri;
    }

    protected static final String computeRealSourceUri(SourceResolver sourceResolver,
            Session session, String sourceUri, Log logger) {
        Validate.isTrue(sourceUri.startsWith(Node.LENYA_PROTOCOL));
        String pathInPubs = sourceUri.substring(Node.LENYA_PROTOCOL.length());
        StringTokenizer tokens = new StringTokenizer(pathInPubs, "/");
        String pubId = tokens.nextToken();
        org.apache.lenya.cms.publication.Session pubSession = (org.apache.lenya.cms.publication.Session) session
                .getHolder();
        Publication pub = pubSession.getPublication(pubId);
        String pubContentUri = pub.getContentUri();

        String absPathInPub = pathInPubs.substring((pubId + "/content").length());
        /*
         * if (new File(pubContentUri).isAbsolute()) { // Absolute contentBaseUri =
         * repo.getBaseUri() + pubContentUri; uriSuffix = File.separator + fileSuffix; } else { //
         * Relative contentBaseUri = CONTEXT_PREFIX + pubContentUri; uriSuffix = "/" + fileSuffix; }
         */

        String realSourceUri = pubContentUri + absPathInPub;

        if (logger.isDebugEnabled()) {
            try {
                if (!SourceUtil.exists(pubContentUri, sourceResolver)) {
                    logger.debug("The content directory [" + pubContentUri + "] does not exist. "
                            + "It will be created as soon as documents are added.");
                }
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            logger.debug("Real Source URI: " + realSourceUri);
        }

        return realSourceUri;
    }

    /**
     * @throws RepositoryException if an error occurs.
     * @see org.apache.lenya.transaction.Transactionable#deleteTransactionable()
     */
    public void deleteTransactionable() throws RepositoryException {
        try {
            if (!getNode().isCheckedOut()) {
                throw new RuntimeException("Cannot delete source [" + getSourceUri()
                        + "]: not checked out!");
            } else {
                this.data = null;
                SourceUtil.delete(getRealSourceUri(), getSourceResolver());
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    byte[] data = null;

    /**
     * @return An input stream.
     * @throws RepositoryException if an error occurs.
     * @see org.apache.lenya.cms.repository.Node#getInputStream()
     */
    public synchronized InputStream getInputStream() throws RepositoryException {
        loadData();
        if (this.data == null) {
            throw new RuntimeException(this + " does not exist!");
        }
        return new ByteArrayInputStream(this.data);
    }

    /**
     * @return A boolean value.
     * @throws RepositoryException if an error occurs.
     * @see org.apache.lenya.cms.repository.Node#exists()
     */
    public boolean exists() throws RepositoryException {
        if (this.deleted == true) {
            return false;
        } else if (this.data != null) {
            return true;
        } else {
            try {
                return SourceUtil.exists(getRealSourceUri(), getSourceResolver());
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
    }

    private boolean deleted;
    private int loadRevision = -1;

    protected void delete() {
        this.deleted = true;
    }

    /**
     * Loads the data from the real source.
     * 
     * @throws RepositoryException if an error occurs.
     */
    protected synchronized void loadData() throws RepositoryException {

        if (this.deleted || this.data != null) {
            return;
        }

        ByteArrayOutputStream out = null;
        InputStream in = null;
        Source source = null;
        try {
            source = getSourceResolver().resolveURI(getRealSourceUri());

            if (source.exists()
                    && !(source instanceof TraversableSource && ((TraversableSource) source)
                            .isCollection())) {
                byte[] buf = new byte[4096];
                out = new ByteArrayOutputStream();
                in = source.getInputStream();
                int read = in.read(buf);

                while (read > 0) {
                    out.write(buf, 0, read);
                    read = in.read(buf);
                }

                this.data = out.toByteArray();
                this.mimeType = source.getMimeType();
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
            if (source != null) {
                getSourceResolver().release(source);
            }
        }
        this.loadRevision = this.node.getCurrentRevisionNumber();
    }

    /**
     * Store the source URLs which are currently written.
     */
    private static Map<String, Object> lockedUris = new WeakHashMap<String, Object>();

    /**
     * @throws RepositoryException if an error occurs.
     * @see org.apache.lenya.transaction.Transactionable#saveTransactionable()
     */
    protected synchronized void saveTransactionable() throws RepositoryException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Saving [" + this + "] to source [" + getRealSourceUri() + "]");
        }

        if (this.data != null) {

            String realSourceUri = getRealSourceUri();
            Object lock = lockedUris.get(realSourceUri);
            if (lock == null) {
                lock = new Object();
                lockedUris.put(realSourceUri, lock);
            }

            synchronized (lock) {
                saveTransactionable(realSourceUri);
            }
        }
    }

    protected void saveTransactionable(String realSourceUri) throws RepositoryException {
        ModifiableSource source = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            source = (ModifiableSource) getSourceResolver().resolveURI(realSourceUri);

            out = source.getOutputStream();

            byte[] buf = new byte[4096];
            in = new ByteArrayInputStream(this.data);
            int read = in.read(buf);

            while (read > 0) {
                out.write(buf, 0, read);
                read = in.read(buf);
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {

            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Throwable t) {
                throw new RuntimeException("Could not close streams: ", t);
            }
            if (source != null) {
                getSourceResolver().release(source);
            }
        }
    }

    /**
     * Output stream.
     */
    private class NodeOutputStream extends ByteArrayOutputStream {
        /**
         * @see java.io.OutputStream#close()
         */
        public synchronized void close() throws IOException {
            SourceWrapper.this.data = super.toByteArray();
            try {
                SourceWrapper.this.getNode().registerDirty();
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
            super.close();
        }
    }

    /**
     * @return The content length.
     * @throws RepositoryException if an error occurs.
     * @see org.apache.lenya.cms.repository.Node#getContentLength()
     */
    public long getContentLength() throws RepositoryException {
        loadData();
        return this.data.length;
    }

    private String mimeType;

    /**
     * @return A string.
     * @throws RepositoryException if an error occurs.
     * @see org.apache.lenya.cms.repository.Node#getMimeType()
     */
    public String getMimeType() throws RepositoryException {
        loadData();
        return this.mimeType;
    }

    /**
     * @return The source URI.
     */
    public String getSourceUri() {
        return this.sourceUri;
    }

    /**
     * @return An output stream.
     * @throws RepositoryException if an error occurs.
     * @see org.apache.lenya.cms.repository.Node#getOutputStream()
     */
    public synchronized OutputStream getOutputStream() throws RepositoryException {
        if (getLogger().isDebugEnabled())
            getLogger().debug("Get OutputStream for " + getSourceUri());
        loadData();
        return new NodeOutputStream();
    }

    protected int getLoadRevision() {
        return this.loadRevision;
    }

    protected SourceResolver getSourceResolver() {
        return sourceResolver;
    }

}
