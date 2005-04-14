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

import java.util.Map;

import org.apache.cocoon.servlet.multipart.Part;

/**
 * Manager for resources of a CMS document.
 */
public interface ResourcesManager {
    
    /**
     * The Avalon role.
     */
    String ROLE = ResourcesManager.class.getName();

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
     * @param document The document.
     * @param part the part that contains the uploaded file
     * @param metadata the metadata for the resource
     * @throws Exception if an error occurs
     */
    void addResource(Document document, Part part, Map metadata) throws Exception;

    /**
     * Delete a resource from the document.
     * @param document The document.
     * @param name The filename of the resource.
     * @throws Exception if an error occurs
     */
    void deleteResource(Document document, String name) throws Exception;

    /**
     * Get all resources for the associated document.
     * @param document The document.
     * @return all resources of the associated document
     */
    Resource[] getResources(Document document);

    /**
     * Get the image resources for the associated document.
     * @param document The document.
     * @return all meta data files for the resources for the associated
     *         document.
     */
    Resource[] getImageResources(Document document);

    /**
     * Deletes all resources.
     * @param document The document.
     */
    void deleteResources(Document document);

    /**
     * Copies the resources of a document to another document.
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws Exception when something went wrong.
     */
    void copyResources(Document sourceDocument, Document destinationDocument) throws Exception;

}
