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
import java.util.Map;

import org.apache.cocoon.servlet.multipart.Part;

/**
 * Manager for resources of a CMS document.
 */
public interface ResourcesManager {

    /**
     * <code>CONTENT_PREFIX</code> The content prefix
     */
    final String CONTENT_PREFIX = "content";
    /**
     * <code>RESOURCES_PREFIX</code> The resources prefix
     */
    final String RESOURCES_PREFIX = "resources";
    /**
     * <code>RESOURCES_META_SUFFIX</code> The suffix for resource meta files
     */
    final String RESOURCES_META_SUFFIX = ".meta";
    /**
     * <code>UPLOADASSET_PARAM_NAME</code> The parameter name for the uploaded asset
     */
    final String UPLOADASSET_PARAM_NAME = "properties.asset.data";
    /**
     * <code>UPLOADASSET_PARAM_PREFIX</code> The prefix for all asset upload parameters
     */
    final String UPLOADASSET_PARAM_PREFIX = "properties.asset.";
    /**
     * <code>UPLOADASSET_RETURN_FILESIZE</code> The file size
     */
    final String UPLOADASSET_RETURN_FILESIZE = "file-size";
    /**
     * <code>UPLOADASSET_RETURN_MIMETYPE</code> The mime type
     */
    final String UPLOADASSET_RETURN_MIMETYPE = "mime-type";
    /**
     * <code>FILE_NAME_REGEXP</code> The regular expression for valid asset names
     */
    final String FILE_NAME_REGEXP = "[-a-zA-Z0-9_.]+";
    /**
     * <code>IMAGE_FILE_EXTENSIONS</code> The valid extensions for images
     */
    final String[] IMAGE_FILE_EXTENSIONS = { ".jpg", ".png", ".bmp", ".gif", ".svg" };
    /**
     * <code>DUBLIN_CORE_PARAMETERS</code> The known Dublin Core values
     */
    // optional parameters for meta data according to dublin core
    final String[] DUBLIN_CORE_PARAMETERS = { "title", "creator", "subject", "description",
            "publisher", "contributor", "date", "type", "format", "identifier", "source",
            "language", "relation", "coverage", "rights" };

    /**
     * Add a resource to the document.
     * @param part the part that contains the uploaded file
     * @param metadata the metadata for the resource
     * @throws Exception if an error occurs
     */
    void addResource(Part part, Map metadata) throws Exception;

    /**
     * Delete a resource from the document.
     * @param name The filename of the resource.
     * @throws Exception if an error occurs
     */
    void deleteResource(String name) throws Exception;

    /**
     * Get the path to the resources.
     * @return the path to the resources
     * @deprecated the resource manager should not expose storage details.
     */
    File getPath();

    /**
     * Returns the path of a resource relative to the context prefix.
     * @param resource The resource
     * @return The path of a resource relative to the context prefix.
     */
    String getResourceUrl(File resource);

    /**
     * Get all resources for the associated document.
     * @return all resources of the associated document
     */
    File[] getResources();

    /**
     * Return all resources which are images.
     * @return All image resources.
     */
    File[] getImageResources();

    /**
     * Get the meta data for all resources for the associated document.
     * @return all meta data files for the resources for the associated
     *         document.
     */
    File[] getMetaFiles();

    /**
     * Returns a meta file for a given resource.
     * @param resource A resource the meta file should be returned for.
     * @return A file containing meta information about a resource. Returns null
     *         if no meta file was found.
     * @throws IllegalArgumentException If resource is a meta file itself.
     */
    File getMetaFile(final File resource) throws IllegalArgumentException;

    /**
     * Deletes all resources.
     */
    void deleteResources();

    /**
     * Gets the document this resource manager belongs to.
     * @return The document
     */
    Document getDocument();

    /**
     * Copies the resources of a document to another document.
     * @param destinationDocument The destination document.
     * @throws Exception when something went wrong.
     */
    void copyResourcesTo(Document destinationDocument) throws Exception;

}