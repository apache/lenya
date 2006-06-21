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

/* $Id: BulkCopyTask.java 383219 2006-03-04 23:04:55Z michi $  */
package org.apache.lenya.cms.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.xml.AntDocumentHelper;
import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Module descriptor.
 */
public class ModuleDescriptor {

    protected static final String NAMESPACE = "http://apache.org/lenya/module/1.0";

    private String id;
    private List dependencies = new ArrayList();
    private File sourceDir;

    /**
     * Ctor.
     * @param sourceDir The source dir.
     * @throws BuildException if an error occurs.
     */
    public ModuleDescriptor(File sourceDir) throws BuildException {
        this.sourceDir = sourceDir;
        File moduleFile = new File(sourceDir, "module.xml");
        try {
            Document doc = AntDocumentHelper.readDocument(moduleFile);
            Element idElement = AntDocumentHelper.getChildren(doc.getDocumentElement(),
                    NAMESPACE,
                    "id")[0];
            this.id = AntDocumentHelper.getSimpleElementText(idElement).trim();
            Element[] dependencyElements = AntDocumentHelper.getChildren(doc.getDocumentElement(),
                    NAMESPACE,
                    "depends");
            for (int i = 0; i < dependencyElements.length; i++) {
                String value = dependencyElements[i].getAttribute("module");
                if (value == null || value.equals("")) {
                    throw new BuildException("Invalid dependency for module [" + getId() + "]");
                }
                this.dependencies.add(value);
            }
        } catch (BuildException e) {
            throw e;
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    /**
     * @return A list of the module IDs this module depends on.
     */
    public String[] getDependencies() {
        return (String[]) dependencies.toArray(new String[dependencies.size()]);
    }

    /**
     * @return The module ID.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The source directory.
     */
    public File getSourceDir() {
        return sourceDir;
    }


}
