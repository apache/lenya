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

package org.apache.lenya.cms.cocoon.source;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.ModifiableTraversableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @version $Id$
 */
public final class SourceUtil {

    /**
     * <p>
     * Copies one Source to another using a source buffer i.e. the source Source
     * is buffered before it is copied to its final destination.
     * </p>
     * <p>
     * The optional buffering is sometimes useful, if the source Source somehow
     * depends on the destination Source. This situation may occur e.g. if
     * source Source is a Cocoon pipeline.
     * </p>
     * <p>
     * <em>NOTE:</em> o.a.e..s.SourceUtil.copy does not close streams on an
     * exception!!
     * </p>
     * @param source
     * @param destination
     * @param useBuffer If true, the source data will be read into a buffer
     *        before it is written to the final destination.
     * @throws IOException If an error occures.
     */
    public static void copy(Source source, ModifiableSource destination, boolean useBuffer)
            throws IOException {
        InputStream sourceInputStream = null;
        OutputStream destOutputStream = null;
        try {
            sourceInputStream = source.getInputStream();
            destOutputStream = destination.getOutputStream();

            if (useBuffer) {
                final ByteArrayOutputStream sourceBos = new ByteArrayOutputStream();
                IOUtils.copy(sourceInputStream, sourceBos);
                IOUtils.write(sourceBos.toByteArray(), destOutputStream);
            } else {
                IOUtils.copy(sourceInputStream, destOutputStream);
            }
        } finally {
            if (destOutputStream != null) {
                destOutputStream.flush();
                destOutputStream.close();
            }
            if (sourceInputStream != null) {
                sourceInputStream.close();
            }
        }
    }

    /**
     * Copies one Source to another. The source Source is optionally buffered.
     * @param resolver The SourceResolver to use for lookin up Sources.
     * @param sourceUri The source to be copied.
     * @param destUri The URI to copy to.
     * @param useBuffer If true, the source Source is buffered before copied to
     *        the final destination.
     * @throws IOException If an error occures.
     * @throws SourceException If the destination is not modifiable.
     * @see #copy(Source, ModifiableSource, boolean)
     */
    public static void copy(SourceResolver resolver, String sourceUri, String destUri,
            boolean useBuffer) throws IOException, SourceException {
        Source source = null;
        Source dest = null;
        try {
            source = resolver.resolveURI(sourceUri);
            dest = resolver.resolveURI(destUri);

            if (!(dest instanceof ModifiableSource))
                throw new SourceException("Destination '" + dest.getURI() + "' is not modifiable.");

            copy(source, (ModifiableSource) dest, useBuffer);
        } finally {
            if (source != null)
                resolver.release(source);
            if (dest != null)
                resolver.release(dest);
        }
    }

    /**
     * Copies a Source without buffering.
     * @param resolver A SourceResolver instance.
     * @param sourceUri The source URI to copy from.
     * @param destUri The destination URI to copy to.
     * @throws IOException If an error occures.
     * @see #copy(SourceResolver, String, String, boolean)
     */
    public static void copy(SourceResolver resolver, String sourceUri, String destUri)
            throws IOException {
        copy(resolver, sourceUri, destUri, false);
    }

    /**
     * Copies a Source without buffering.
     * @param manager A service manager.
     * @param sourceUri The source URI to copy from.
     * @param destUri The destination URI to copy to.
     * @throws IOException If an error occures.
     * @throws ServiceException
     * @see #copy(SourceResolver, String, String, boolean)
     */
    public static void copy(ServiceManager manager, String sourceUri, String destUri)
            throws IOException, ServiceException {
        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            copy(resolver, sourceUri, destUri, false);
        } finally {
            if (resolver != null) {
                manager.release(resolver);
            }
        }
    }

    /**
     * Reads a DOM from a source.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @return A document or <code>null</code> if the source does not exist.
     * @throws ServiceException if an error occurs.
     * @throws SourceNotFoundException if an error occurs.
     * @throws ParserConfigurationException if an error occurs.
     * @throws SAXException if an error occurs.
     * @throws IOException if an error occurs.
     */
    public static Document readDOM(String sourceUri, ServiceManager manager)
            throws ServiceException, SourceNotFoundException, ParserConfigurationException,
            SAXException, IOException {
        SourceResolver resolver = null;
        Source source = null;
        Document document = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(sourceUri);

            if (source.exists()) {
                document = DocumentHelper.readDocument(source.getInputStream());
            }
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
        return document;
    }

    /**
     * Writes a DOM to a source.
     * @param document The document.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @throws TransformerConfigurationException if an error occurs.
     * @throws TransformerException if an error occurs.
     * @throws ServiceException if the source resolver could not be obtained.
     * @throws MalformedURLException if the source URI is not valid.
     * @throws IOException if an error occurs.
     */
    public static void writeDOM(Document document, String sourceUri, ServiceManager manager)
            throws TransformerConfigurationException, TransformerException, ServiceException,
            MalformedURLException, IOException {
        SourceResolver resolver = null;
        ModifiableSource source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = (ModifiableSource) resolver.resolveURI(sourceUri);

            OutputStream oStream = source.getOutputStream();
            writeDOM(document, oStream);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * @param document The XML document.
     * @param oStream The output stream.
     * @throws TransformerConfigurationException if an error occurs.
     * @throws TransformerException if an error occurs.
     * @throws IOException if an error occurs.
     */
    public static void writeDOM(Document document, OutputStream oStream)
            throws TransformerConfigurationException, TransformerException, IOException {
        DocumentHelper.writeDocument(document, oStream);
        if (oStream != null) {
            oStream.flush();
            try {
                oStream.close();
            } catch (Throwable t) {
                throw new RuntimeException("Could not write document: ", t);
            }
        }
    }

    /**
     * Deletes a source if it exists.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @throws ServiceException if an error occurs.
     * @throws MalformedURLException if an error occurs.
     * @throws IOException if an error occurs.
     */
    public static void delete(String sourceUri, ServiceManager manager) throws ServiceException,
            MalformedURLException, IOException {
        SourceResolver resolver = null;
        ModifiableTraversableSource source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = (ModifiableTraversableSource) resolver.resolveURI(sourceUri);
            if (source.exists()) {
                source.delete();
            }

        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * Deletes all empty collections in a subtree.
     * @param sourceUri The root source URI.
     * @param manager The service manager.
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws IOException
     */
    public static void deleteEmptyCollections(String sourceUri, ServiceManager manager)
            throws ServiceException, MalformedURLException, IOException {
        SourceResolver resolver = null;
        ModifiableTraversableSource source = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = (ModifiableTraversableSource) resolver.resolveURI(sourceUri);
            if (source.isCollection()) {
                for (Iterator i = source.getChildren().iterator(); i.hasNext();) {
                    ModifiableTraversableSource child = (ModifiableTraversableSource) i.next();
                    deleteEmptyCollections(child.getURI(), manager);
                }
                if (source.getChildren().size() == 0) {
                    source.delete();
                }
            }
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * Checks if a source exists.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @return A boolean value.
     * @throws ServiceException if an error occurs.
     * @throws MalformedURLException if an error occurs.
     * @throws IOException if an error occurs.
     */
    public static boolean exists(String sourceUri, ServiceManager manager) throws ServiceException,
            MalformedURLException, IOException {
        SourceResolver resolver = null;
        Source source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(sourceUri);

            return source.exists();
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * Returns the last modification date of a source.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @return A long value.
     * @throws ServiceException if an error occurs.
     * @throws MalformedURLException if an error occurs.
     * @throws IOException if an error occurs.
     */
    public static long getLastModified(String sourceUri, ServiceManager manager)
            throws ServiceException, MalformedURLException, IOException {
        SourceResolver resolver = null;
        Source source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(sourceUri);

            return source.getLastModified();
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * Checks out a repository source.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @throws RepositoryException If an error occurs.
     * @throws ServiceException If an error occurs.
     * @throws MalformedURLException If an error occurs.
     * @throws IOException If an error occurs.
     */
    public static void checkout(String sourceUri, ServiceManager manager)
            throws RepositoryException, ServiceException, MalformedURLException, IOException {
        SourceResolver resolver = null;
        RepositorySource source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = (RepositorySource) resolver.resolveURI(sourceUri);

            source.getNode().checkout();

        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * Checks in a transactionable source.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @throws RepositoryException If an error occurs.
     * @throws ServiceException If an error occurs.
     * @throws MalformedURLException If an error occurs.
     * @throws IOException If an error occurs.
     */
    public static void checkin(String sourceUri, ServiceManager manager)
            throws RepositoryException, ServiceException, MalformedURLException, IOException {
        SourceResolver resolver = null;
        RepositorySource source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = (RepositorySource) resolver.resolveURI(sourceUri);

            source.getNode().checkin();

        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * Locks a transactionable source.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @throws RepositoryException If an error occurs.
     */
    public static void lock(String sourceUri, ServiceManager manager) throws RepositoryException {
        SourceResolver resolver = null;
        RepositorySource source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = (RepositorySource) resolver.resolveURI(sourceUri);

            source.getNode().lock();

        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * Unlocks a transactionable source.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @throws RepositoryException If an error occurs.
     */
    public static void unlock(String sourceUri, ServiceManager manager) throws RepositoryException {
        SourceResolver resolver = null;
        RepositorySource source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = (RepositorySource) resolver.resolveURI(sourceUri);

            source.getNode().unlock();

        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * Registers a source as dirty.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     */
    public static void registerDirty(String sourceUri, ServiceManager manager) {
        SourceResolver resolver = null;
        RepositorySource source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = (RepositorySource) resolver.resolveURI(sourceUri);
            source.getNode().registerDirty();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @return A content length.
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws IOException
     */
    public static long getContentLength(String sourceUri, ServiceManager manager)
            throws ServiceException, MalformedURLException, IOException {
        SourceResolver resolver = null;
        Source source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(sourceUri);

            return source.getContentLength();
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @return A mime type.
     * @throws ServiceException
     * @throws IOException
     * @throws MalformedURLException
     */
    public static String getMimeType(String sourceUri, ServiceManager manager)
            throws ServiceException, MalformedURLException, IOException {
        SourceResolver resolver = null;
        Source source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(sourceUri);

            return source.getMimeType();
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
    }

    /**
     * @param resolver
     * @param sourceUri
     * @param destOutputStream
     * @throws MalformedURLException
     * @throws IOException
     */
    public static void copy(SourceResolver resolver, String sourceUri, OutputStream destOutputStream) throws MalformedURLException, IOException {
        boolean useBuffer = true;
        InputStream sourceInputStream = null;
        Source source = null;
        try {
            source = resolver.resolveURI(sourceUri);
            sourceInputStream = source.getInputStream();

            if (useBuffer) {
                final ByteArrayOutputStream sourceBos = new ByteArrayOutputStream();
                IOUtils.copy(sourceInputStream, sourceBos);
                IOUtils.write(sourceBos.toByteArray(), destOutputStream);
            } else {
                IOUtils.copy(sourceInputStream, destOutputStream);
            }
        } finally {
            if (destOutputStream != null) {
                destOutputStream.flush();
                destOutputStream.close();
            }
            if (sourceInputStream != null) {
                sourceInputStream.close();
            }
            if (source != null) {
                resolver.release(source);
            }
        }
    }
}