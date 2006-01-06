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

package org.apache.lenya.cms.cocoon.source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @version $Id$
 */
public final class SourceUtil {

    /**
     * <p>
     * Copies one Source to another using a source buffer i.e. the source Source is buffered before
     * it is copied to its final destination.
     * </p>
     * <p>
     * The optional buffering is sometimes useful, if the source Source somehow depends on the
     * destination Source. This situation may occur e.g. if source Source is a Cocoon pipeline.
     * </p>
     * <p>
     * <em>NOTE:</em> o.a.e..s.SourceUtil.copy does not close streams on an exception!!
     * </p>
     * @param source
     * @param destination
     * @param useBuffer If true, the source data will be read into a buffer before it is written to
     *            the final destination.
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
                CopyUtils.copy(sourceInputStream, sourceBos);
                CopyUtils.copy(sourceBos.toByteArray(), destOutputStream);
            } else {
                CopyUtils.copy(sourceInputStream, destOutputStream);
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
     * @param useBuffer If true, the source Source is buffered before copied to the final
     *            destination.
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
            Writer writer = new OutputStreamWriter(oStream);
            DocumentHelper.writeDocument(document, writer);
            if (oStream != null) {
                oStream.flush();
                try {
                    oStream.close();
                } catch (Throwable t) {
                    throw new RuntimeException("Could not write document: ", t);
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
     * Writes a DOM to an output stream.
     * @param document The document.
     * @param oStream The output stream.
     * @param manager The service manager.
     * @throws TransformerConfigurationException if an error occurs.
     * @throws TransformerException if an error occurs.
     * @throws ServiceException if the source resolver could not be obtained.
     * @throws MalformedURLException if the source URI is not valid.
     * @throws IOException if an error occurs.
     */
    public static void writeDOM(Document document, OutputStream oStream, ServiceManager manager)
            throws TransformerConfigurationException, TransformerException, ServiceException,
            MalformedURLException, IOException {
        Writer writer = new OutputStreamWriter(oStream);
        DocumentHelper.writeDocument(document, writer);
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
        ModifiableSource source = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = (ModifiableSource) resolver.resolveURI(sourceUri);
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
     * @param manager The service manager.
     * @param contextPath The relative path inside the context.
     * @return The file system path.
     * @throws IOException if an error occurs.
     * @throws MalformedURLException if an error occurs. 
     * @throws ServiceException  if an error occurs.
     */
    public static String getRealPath(ServiceManager manager, String contextPath)
            throws MalformedURLException, IOException, ServiceException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context://");
            File contextFile = org.apache.excalibur.source.SourceUtil.getFile(source);
            File file = new File(contextFile, contextPath.replace('/', File.separatorChar));
            return file.getAbsolutePath();
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }

    }
}