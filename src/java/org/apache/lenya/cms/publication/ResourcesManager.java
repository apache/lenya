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

/* $Id: ResourcesManager.java,v 1.10 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.FileFilter;

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
        return RESOURCES_PREFIX + "/" + document.getArea() + document.getId();
    }

    /**
     * Get the path to the resources.
     * 
     * @return the path to the resources
     */
    public File getPath() {
        File publicationPath = document.getPublication().getDirectory();
        File resourcesPath =
            new File(publicationPath, getPathFromPublication().replace('/', File.separatorChar));
        return resourcesPath;
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
     * Deletes all resources.
     */
    public void deleteResources() {
        
        File[] resources = getResources();
        File[] metas = getMetaFiles();
        for (int i = 0; i < resources.length; i++) {
            resources[i].delete();
        }
        for (int i = 0; i < metas.length; i++) {
            metas[i].delete();
        }
        File directory = getPath();
        if (directory.isDirectory() && directory.listFiles().length == 0) {
            directory.delete();
        }
    }
    
}
