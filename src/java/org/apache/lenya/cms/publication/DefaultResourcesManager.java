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

/* $Id$  */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.avalon.excalibur.io.FileUtil;

import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreImpl;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;


/**
 * Manager for resources of a CMS document.
 */
public class DefaultResourcesManager extends AbstractLogEnabled implements ResourcesManager {

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

    private Document document = null;
    
    protected static final String NAMESPACE_META = "http://lenya.apache.org/meta/1.0";

    /**
     * Create a new instance of Resources.
     * @param _document the document for which the resources are managed
     */
    public DefaultResourcesManager(Document _document) {
        this.document = _document;
    }

    /**
     * Add the file in the Part either as a resource or content
     * @param part The Part
     * @param metadata Holds the metadata for the resource
     * @exception IOException if an error occurs
     */
    public void addResource(Part part, Map metadata) throws IOException {

        File resourceFile;

        try {
            String fileName = part.getFileName();
            if (!fileName.matches(FILE_NAME_REGEXP)) {
                // the file name contains characters which mean trouble
                // and are therefore not allowed.
                getLogger().warn("The filename [" + fileName + "]� is not valid for an asset.");
            }
            // convert spaces in the file name to underscores
            fileName = fileName.replace(' ', '_');
            String mimeType = part.getMimeType();
            int fileSize = part.getSize();

            metadata.put("format", mimeType);
            metadata.put("extent", Integer.toString(fileSize));

            /* if (type.equals("resource")) { */
            resourceFile = new File(this.getPath(), fileName);

            if (!this.getPath().exists()) {
                this.getPath().mkdirs();
            }

            // create an extra file containing the meta description for
            // the resource.
            File metaDataFile = new File(this.getPath(), fileName + RESOURCES_META_SUFFIX);
            createMetaData(metaDataFile, metadata);

            /*
             * } // must be a content upload then else { resourceFile = new
             * File(document.getFile().getParent(), fileName);
             * getLogger().debug("resourceFile: " + resourceFile); }
             */
            saveResource(resourceFile, part);
        } catch (final DocumentException e) {
            getLogger().error("Document exception " +e.toString());
            throw new RuntimeException(e);
        } catch (final IOException e) {
            getLogger().error("IO Error " +e.toString());
            throw e;
        }
    }

    /**
     * Saves the resource to a file.
     * @param resourceFile The resource file.
     * @param part The part of the multipart request.
     * @throws IOException if an error occurs.
     */
    protected void saveResource(File resourceFile, Part part) throws IOException {
        FileOutputStream out = null;
        InputStream in = null;
        
        if (!resourceFile.exists()) {
            boolean created = resourceFile.createNewFile();
            if (!created) {
                throw new IOException("The file [" + resourceFile + "]�could not be created.");
            }
        }

	    try {
            byte[] buf = new byte[4096];
            out = new FileOutputStream(resourceFile);
            in = part.getInputStream();
            int read = in.read(buf);

            while (read > 0) {
                out.write(buf, 0, read);
                read = in.read(buf);
            }
        } catch (final FileNotFoundException e) {
            getLogger().error("file not found" +e.toString());
            throw new IOException(e.toString());
        } catch (IOException e) {
            getLogger().error("IO error " +e.toString());
            throw new IOException(e.toString());
        } catch (Exception e) {
            getLogger().error("Exception" +e.toString());
            throw new IOException(e.toString());
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }

    /**
     * Create the meta data file given the dublin core parameters.
     * @param metaDataFile the file where the meta data file is to be created
     * @param metadata a <code>Map</code> containing the dublin core values
     * @throws DocumentException if an error occurs
     */
    protected void createMetaData(File metaDataFile, Map metadata)
            throws DocumentException {

        assert (metaDataFile.getParentFile().exists());
		try {
            
            if (!metaDataFile.exists()) {
                metaDataFile.createNewFile();
                NamespaceHelper helper = new NamespaceHelper(NAMESPACE_META, "", "meta");
                DocumentHelper.writeDocument(helper.getDocument(), metaDataFile);
            }
            String		key;
            String		value;
            Map.Entry	entry;
            DublinCoreImpl dc = new DublinCoreImpl(metaDataFile);
            Iterator iter = metadata.entrySet().iterator();

            while (iter.hasNext()) {
            	entry 	= (Map.Entry)iter.next();
            	key 	= (String)entry.getKey();
            	value 	= (String)entry.getValue();
                dc.setValue(key, value);
            }
            dc.save();
        } catch (final Exception e) {
            getLogger().error("Saving of [" + metaDataFile + "] �failed.");
            throw new DocumentException(e);
        }
    }

    /**
     * Get the path to the resources.
     * @return the path to the resources
     */
    private String getPathFromPublication() {
        return RESOURCES_PREFIX + "/" + getDocument().getArea() + getDocument().getId();
    }

    /**
     * Get the path to the resources.
     * @return the path to the resources
     */
    public File getPath() {
        File publicationPath = getDocument().getPublication().getDirectory();
        File resourcesPath = new File(publicationPath, getPathFromPublication().replace('/',
                File.separatorChar));
        return resourcesPath;
    }

    /**
     * Returns the path of a resource relative to the context prefix.
     * @param resource The resource
     * @return The path of a resource relative to the context prefix.
     */
    public String getResourceUrl(File resource) {
        return getDocument().getPublication().getId() + "/" + getDocument().getArea()
                + getDocument().getId() + "/" + resource.getName();
    }

    /**
     * Get all resources for the associated document.
     * @return all resources of the associated document
     */
    public File[] getResources() {

        // filter the meta files out. We only want to see the "real" resources.
        FileFilter filter = new NotMetaSuffixFileFilter();

        return getFiles(filter);
    }

    /**
     * Return all resources which are images.
     * @return All image resources.
     */
    public File[] getImageResources() {
        return getFiles(new ImageExtensionsFileFilter());
    }

    /**
     * Returns the resources that are matched by a certain file filter.
     * @param filter A file filter.
     * @return A file array.
     */
    protected File[] getFiles(FileFilter filter) {
        File[] files = new File[0];
        if (getPath().isDirectory()) {
            files = getPath().listFiles(filter);
        }

        return files;
    }

    /**
     * Get the meta data for all resources for the associated document.
     * @return all meta data files for the resources for the associated
     *         document.
     */
    public File[] getMetaFiles() {
        FileFilter filter = new MetaSuffixFileFilter();
        return getFiles(filter);
    }

    /**
     * Returns a meta file for a given resource.
     * @param resource A resource the meta file should be returned for.
     * @return A file containing meta information about a resource. Returns null
     *         if no meta file was found.
     * @throws IllegalArgumentException If resource is a meta file itself.
     */
    public File getMetaFile(final File resource) throws IllegalArgumentException {
        if (resource.getName().endsWith(RESOURCES_META_SUFFIX))
            throw new IllegalArgumentException("File is itself a meta file.");

        final FileFilter filter = new ResourceMetaFileFilter(resource);

        final File[] metaFiles = getFiles(filter);
        assert (metaFiles.length == 0);
        return metaFiles[0];
    }

    /**
     * Deletes all resources.
     */
    public void deleteResources() {

        File[] resources = getResources();
        for (int i = 0; i < resources.length; i++) {
            resources[i].delete();
        }

        File[] metas = getMetaFiles();
        for (int i = 0; i < metas.length; i++) {
            metas[i].delete();
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#getDocument()
     */
    public Document getDocument() {
        return this.document;
    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#copyResourcesTo(org.apache.lenya.cms.publication.Document)
     */
    public void copyResourcesTo(Document destinationDocument) throws Exception {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Copying resources of document [" + getDocument() + "]");
        }

        ResourcesManager _destinationManager = destinationDocument.getResourcesManager();

        List resourcesList = new ArrayList(Arrays.asList(getResources()));
        resourcesList.addAll(Arrays.asList(getMetaFiles()));
        File[] resources = (File[]) resourcesList.toArray(new File[resourcesList.size()]);
        File destinationDirectory = _destinationManager.getPath();

        for (int i = 0; i < resources.length; i++) {
            File destinationResource = new File(destinationDirectory, resources[i].getName());

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Copy file [" + resources[i].getAbsolutePath() + "] to ["
                        + destinationResource.getAbsolutePath() + "]");
            }
            FileUtil.copyFile(resources[i], destinationResource);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#deleteResource(java.lang.String)
     */
    public void deleteResource(String name) throws Exception {
        File[] resources = getResources();
        for (int i = 0; i < resources.length; i++) {
            if (resources[i].getName().equals(name)) {
                File metaFile = getMetaFile(resources[i]);
                metaFile.delete();
                resources[i].delete();
            }
        }
    }

}