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

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import org.apache.cocoon.servlet.multipart.Part;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.repository.Node;

/**
 * Manager for resources of a CMS document.
 * @version $Id$
 */
public class DefaultResourcesManager extends AbstractLogEnabled implements ResourcesManager,
        Serviceable {

    private static final class MetaSuffixFileFilter implements FileFilter {
        /**
         * @see java.io.FileFilter#accept(java.io.File)
         */
        public boolean accept(File file) {
            return file.isFile() && file.getName().endsWith(RESOURCES_META_SUFFIX);
        }
    }

    private static final class NotMetaSuffixFileFilter implements FileFilter {
        /**
         * @see java.io.FileFilter#accept(java.io.File)
         */
        public boolean accept(File file) {
            return file.isFile() && !file.getName().endsWith(RESOURCES_META_SUFFIX);
        }
    }

    private static final class ImageExtensionsFileFilter implements FileFilter {
        /**
         * @see java.io.FileFilter#accept(java.io.File)
         */
        public boolean accept(File file) {
            for (int i = 0; i < IMAGE_FILE_EXTENSIONS.length; i++)
                if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(IMAGE_FILE_EXTENSIONS[i]))
                    return true;
            return false;
        }
    }

    private static final class ResourceMetaFileFilter implements FileFilter {
        private final File resource;

        private ResourceMetaFileFilter(File _resource) {
            super();
            this.resource = _resource;
        }

        /**
         * @see java.io.FileFilter#accept(java.io.File)
         */
        public boolean accept(File file) {
            return file.isFile()
                    && file.getName().equals(this.resource.getName().concat(RESOURCES_META_SUFFIX));
        }
    }

    /**
     * Constructor
     */
    public DefaultResourcesManager() {
    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#addResource(org.apache.lenya.cms.publication.Document,
     *      org.apache.cocoon.servlet.multipart.Part, java.util.Map)
     */
    public void addResource(Document document, Part part, Map metadata) throws Exception {

        if (getLogger().isDebugEnabled())
            getLogger().debug("DefaultResourcesManager::addResource() called");

        try {
            String fileName = part.getFileName();
            if (!fileName.matches(FILE_NAME_REGEXP)) {
                // the file name contains characters which mean trouble
                // and are therefore not allowed.
                getLogger().warn("The filename [" + fileName + "]� is not valid for an asset.");
            }
            // convert spaces in the file name to underscores
            fileName = fileName.replace(' ', '_');

            Resource resource = new Resource(document, fileName, this.manager, getLogger());
            Node[] nodes = resource.getRepositoryNodes();
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].lock();
            }

            String mimeType = part.getMimeType();
            int fileSize = part.getSize();

            /*
             * complement and create the meta description for the resource.
             */
            metadata.put("format", mimeType);
            metadata.put("extent", Integer.toString(fileSize));
            Map lenyaMetaData = new HashMap(1);
            lenyaMetaData.put(LenyaMetaData.ELEMENT_CONTENT_TYPE, "asset");
            resource.getMetaDataManager().setMetaData(metadata, lenyaMetaData, null);

            saveResource(resource, part);
        } catch (final DocumentException e) {
            getLogger().error("Document exception " + e.toString());
            throw new RuntimeException(e);
        } catch (final IOException e) {
            getLogger().error("IO Error " + e.toString());
            throw e;
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug("DefaultResourcesManager::addResource() done.");
    }

    /**
     * Saves the resource to a file.
     * @param resource The resource.
     * @param part The part of the multipart request.
     * @throws IOException if an error occurs.
     */
    protected void saveResource(Resource resource, Part part) throws IOException {
        OutputStream out = null;
        InputStream in = null;

        SourceResolver resolver = null;
        ModifiableSource source = null;

        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (ModifiableSource) resolver.resolveURI(resource.getSourceURI());

            byte[] buf = new byte[4096];
            out = source.getOutputStream();
            in = part.getInputStream();
            int read = in.read(buf);

            while (read > 0) {
                out.write(buf, 0, read);
                read = in.read(buf);
            }
        } catch (final FileNotFoundException e) {
            getLogger().error("file not found" + e.toString());
            throw new IOException(e.toString());
        } catch (IOException e) {
            getLogger().error("IO error " + e.toString());
            throw new IOException(e.toString());
        } catch (Exception e) {
            getLogger().error("Exception" + e.toString());
            throw new IOException(e.toString());
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.flush();
                out.close();
            }

            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#getResources(org.apache.lenya.cms.publication.Document)
     */
    public Resource[] getResources(Document document) {

        // filter the meta files out. We only want to see the "real" resources.
        FileFilter filter = new NotMetaSuffixFileFilter();

        return getResources(document, filter);
    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#getImageResources(org.apache.lenya.cms.publication.Document)
     */
    public Resource[] getImageResources(Document document) {
        return getResources(document, new ImageExtensionsFileFilter());
    }

    /**
     * Returns the resources that are matched by a certain file filter.
     * @param document The document.
     * @param filter A file filter.
     * @return A resource array.
     */
    protected Resource[] getResources(Document document, FileFilter filter) {
        File[] files = new File[0];
        Resource tempResource = new Resource(document, "temp", this.manager, getLogger());

        SourceResolver resolver = null;
        Source source = null;
        try {
            Resource[] resources;

            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(tempResource.getBaseURI());
            if (source.exists()) {
                File directory = org.apache.excalibur.source.SourceUtil.getFile(source);
                if (directory.isDirectory()) {
                    files = directory.listFiles(filter);
                }
                resources = new Resource[files.length];
                for (int i = 0; i < files.length; i++) {
                    resources[i] = new Resource(document, files[i].getName(), this.manager,
                            getLogger());
                }
            } else {
                resources = new Resource[0];
            }
            return resources;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
     * @see org.apache.lenya.cms.publication.ResourcesManager#deleteResources(org.apache.lenya.cms.publication.Document)
     */
    public void deleteResources(Document document) {

        try {
            Resource[] resources = getResources(document);
            for (int i = 0; i < resources.length; i++) {
                SourceUtil.delete(resources[i].getSourceURI(), this.manager);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#copyResources(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copyResources(Document sourceDocument, Document destinationDocument)
            throws Exception {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    "Copying resources from [" + sourceDocument + "] to [" + destinationDocument
                            + "]");
        }

        SourceResolver resolver = null;
        try {
            Resource[] resources = getResources(sourceDocument);
            for (int i = 0; i < resources.length; i++) {
                Resource sourceResource = resources[i];
                Resource destinationResource = new Resource(destinationDocument, sourceResource
                        .getName(), this.manager, getLogger());

                Source sourceSource = null;
                ModifiableSource destSource = null;
                ModifiableSource destMetaSource = null;

                try {
                    resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                    sourceSource = resolver.resolveURI(sourceResource.getSourceURI());
                    destSource = (ModifiableSource) resolver.resolveURI(destinationResource
                            .getSourceURI());

                    SourceUtil.copy(sourceSource, destSource, true);
                } finally {
                    if (sourceSource != null) {
                        resolver.release(sourceSource);
                    }
                    if (destSource != null) {
                        resolver.release(destSource);
                    }
                    if (destMetaSource != null) {
                        resolver.release(destMetaSource);
                    }
                }
            }
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#getResource(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public Resource getResource(Document document, String resourceName) {
        Resource theResource = null;
        Resource[] resources = getResources(document);
        for (int i = 0; i < resources.length; i++) {
            if (resources[i].getName().equals(resourceName)) {
                theResource = resources[i];
                break;
            }
        }
        return theResource;
    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#deleteResource(org.apache.lenya.cms.publication.Resource)
     */
    public void deleteResource(Resource theResource) throws Exception {

        SourceUtil.delete(theResource.getSourceURI(), this.manager);
    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#deleteResource(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public void deleteResource(Document document, String name) throws Exception {
        Resource theResource = getResource(document, name);
        if (theResource == null)
            throw new Exception("no such resource [" + name + "] exists for document [ "
                    + document.getId() + "]");

        deleteResource(theResource);
    }

}
