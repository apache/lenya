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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.avalon.excalibur.io.FileUtil;

import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreImpl;


/**
 * Manager for resources of a CMS document.
 */
public class DefaultResourcesManager extends AbstractLogEnabled implements ResourcesManager {

    private Document document = null;
    private DublinCoreImpl dc = null;

    /**
     * Create a new instance of Resources.
     * 
     * @param document the document for which the resources are managed
     */
    public DefaultResourcesManager(Document document) {
        this.document = document;
    }

    /**
     * Add the file in the Part either as a resource or content
     * @param part The Part
     * @param metadata Holds the metadata for the resource
     * 
     * @exception Exception if an error occurs
     */
    public void addResource(Part part, Map metadata) throws Exception {

        File resourceFile;

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
    }

    /**
     * Saves the resource to a file.
     * 
     * @param resourceFile The resource file.
     * @param part The part of the multipart request.
     * @throws Exception if an error occurs.
     */
    protected void saveResource(File resourceFile, Part part) throws Exception {
        if (!resourceFile.exists()) {
            boolean created = resourceFile.createNewFile();
            if (!created) {
                throw new RuntimeException("The file [" + resourceFile + "]�could not be created.");
            }
        }

        byte[] buf = new byte[4096];
        FileOutputStream out = new FileOutputStream(resourceFile);
        try {
            InputStream in = part.getInputStream();
            int read = in.read(buf);

            while (read > 0) {
                out.write(buf, 0, read);
                read = in.read(buf);
            }
        } finally {
            out.close();
        }
    }

    /**
     * Create the meta data file given the dublin core parameters.
     * 
     * @param metaDataFile the file where the meta data file is to be created
     * @param metadata a <code>Map</code> containing the dublin core values
     * @throws TransformerConfigurationException if an error occurs.
     * @throws TransformerException if an error occurs.
     * @throws IOException if an error occurs
     * @throws ParserConfigurationException if an error occurs.
     */
    protected void createMetaData(File metaDataFile, Map metadata)
            throws TransformerConfigurationException, TransformerException, IOException,
            ParserConfigurationException {

        assert (metaDataFile.getParentFile().exists());
        try {
            dc = new DublinCoreImpl(metaDataFile);
            Iterator iter = metadata.keySet().iterator();

            while (iter.hasNext()) {
                String key = (String) iter.next();
                String value = (String) metadata.get(key);
                dc.setValue(key, value);
            }
            dc.save();
        } catch (Exception e) {
            getLogger().error("Saving of [" + metaDataFile + "]�failed.");
        }
    }

    /**
     * Get the path to the resources.
     * 
     * @return the path to the resources
     */
    private String getPathFromPublication() {
        return RESOURCES_PREFIX + "/" + getDocument().getArea() + getDocument().getId();
    }

    /**
     * Get the path to the resources.
     * 
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
     * @return The path of a resource relative to the context prefix.
     */
    public String getResourceUrl(File resource) {
        return getDocument().getPublication().getId() + "/" + getDocument().getArea()
                + getDocument().getId() + "/" + resource.getName();
    }

    /**
     * Get all resources for the associated document.
     * 
     * @return all resources of the associated document
     */
    public File[] getResources() {

        // filter the meta files out. We only want to see the "real" resources.
        FileFilter filter = new FileFilter() {

            public boolean accept(File file) {
                return file.isFile() && !file.getName().endsWith(RESOURCES_META_SUFFIX);
            }
        };

        return getFiles(filter);
    }

    /**
     * Return all resources which are images.
     * @return All image resources.
     */
    public File[] getImageResources() {
        return getFiles(new FileFilter() {
            public boolean accept(File file) {
                for (int i = 0; i < IMAGE_FILE_EXTENSIONS.length; i++)
                    if (file.getName().toLowerCase().endsWith(IMAGE_FILE_EXTENSIONS[i]))
                        return true;
                return false;
            }
        });
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
     * 
     * @return all meta data files for the resources for the associated
     *         document.
     */
    public File[] getMetaFiles() {
        FileFilter filter = new FileFilter() {

            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(RESOURCES_META_SUFFIX);
            }
        };
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

        final FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile()
                        && file.getName().equals(resource.getName().concat(RESOURCES_META_SUFFIX));
            }
        };

        final File[] metaFiles = getFiles(filter);
        assert (metaFiles.length == 0);
        return metaFiles[0];
    }

    /**
     * Deletes all resources.
     */
    public void deleteResources() {

        File stopDirectory = new File(getDocument().getPublication().getDirectory(),
                RESOURCES_PREFIX);

        File[] resources = getResources();
        for (int i = 0; i < resources.length; i++) {
            resources[i].delete();
            //TODO replace with excalibur
            // FileUtil.deleteParentDirs(resources[i], stopDirectory);
        }

        File[] metas = getMetaFiles();
        for (int i = 0; i < metas.length; i++) {
            metas[i].delete();
            //TODO replace with excalibur FileUtil.deleteParentDirs(metas[i],
            // stopDirectory);
        }
    }

    public Document getDocument() {
        return document;
    }

    /**
     * @see org.apache.lenya.cms.publication.ResourcesManager#copyResourcesTo(org.apache.lenya.cms.publication.Document)
     */
    public void copyResourcesTo(Document destinationDocument) throws Exception {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Copying resources of document [" + getDocument() + "]");
        }

        ResourcesManager destinationManager = destinationDocument.getResourcesManager();

        List resourcesList = new ArrayList(Arrays.asList(getResources()));
        resourcesList.addAll(Arrays.asList(getMetaFiles()));
        File[] resources = (File[]) resourcesList.toArray(new File[resourcesList.size()]);
        File destinationDirectory = destinationManager.getPath();

        for (int i = 0; i < resources.length; i++) {
            File destinationResource = new File(destinationDirectory, resources[i].getName());

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Copy file [" + resources[i].getAbsolutePath() + "] to ["
                        + destinationResource.getAbsolutePath() + "]");
            }
            FileUtil.copyFile(resources[i], destinationResource);
        }
    }

}