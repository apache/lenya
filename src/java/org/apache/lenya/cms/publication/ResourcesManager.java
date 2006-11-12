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

/* $Id$  */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.FileFilter;

import org.apache.lenya.util.FileUtil;

/**
 * Manager for resources of a CMS document.
 */
public class ResourcesManager {

    private Document document;

    public static final String RESOURCES_PREFIX = "resources";

    public static final String RESOURCES_META_SUFFIX = ".meta";

    /**
     * Create a new instance of Resources.
     * 
     * @param document the document for which the resources are managed
     */
    public ResourcesManager(Document document) {
        this.document = document;
    }

    /**
     * Get the path to the resources.
     * 
     * @return the path to the resources
     */
    public String getPathFromPublication() {
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
        return 
            getDocument().getPublication().getId() 
            + "/" 
            + getDocument().getArea()
            + getDocument().getId()
            + "/" 
            + resource.getName();   
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
        final String[] IMAGE_FILE_EXTENSIONS = {".jpg", ".png", ".bmp", ".gif", ".svg"};
        return getFiles( new FileFilter() {
                public boolean accept(File file) {
                    for(int i=0; i<IMAGE_FILE_EXTENSIONS.length; i++)
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
     * @return all meta data files for the resources for the associated document.
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
     * @return A file containing meta information about a resource. 
     * Returns null if no meta file was found.
     * @throws IllegalArgumentException If resource is a meta file itself.
     */
    public File getMetaFile(final File resource) throws IllegalArgumentException {
        if(resource.getName().endsWith(RESOURCES_META_SUFFIX))
            throw new IllegalArgumentException("File is itself a meta file.");
        
        final FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile() && 
                    file.getName().equals(resource.getName().concat(RESOURCES_META_SUFFIX));
            }
        };

        final File[] metaFiles = getFiles(filter);
        assert(metaFiles.length == 0);
        return metaFiles[0];
    }
    
    /**
     * Deletes all resources.
     */
    public void deleteResources() {

        File stopDirectory = new File(getDocument().getPublication().getDirectory(), RESOURCES_PREFIX);

        File[] resources = getResources();
        for (int i = 0; i < resources.length; i++) {
            resources[i].delete();
            FileUtil.deleteParentDirs(resources[i], stopDirectory);
        }

        File[] metas = getMetaFiles();
        for (int i = 0; i < metas.length; i++) {
            metas[i].delete();
            FileUtil.deleteParentDirs(metas[i], stopDirectory);
        }
    }

    public Document getDocument() {
        return document;
    }
}