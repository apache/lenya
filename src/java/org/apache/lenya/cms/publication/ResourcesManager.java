/*
$Id: ResourcesManager.java,v 1.9 2004/01/21 16:12:39 edith Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.FileFilter;

/**
 * Manager for resources of a CMS document.
 *
 * @author <a href="mailto:egli@apache.org">Christian Egli</a>
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
