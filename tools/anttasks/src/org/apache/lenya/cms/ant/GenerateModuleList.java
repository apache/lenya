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
import java.util.StringTokenizer;

import org.apache.lenya.xml.AntDocumentHelper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generate an XML containing modules.
 */
public class GenerateModuleList extends Task {

    private Path moduleDirectories;

    private String moduleFile;

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {

        List descriptors = new ArrayList();

        StringTokenizer st = new StringTokenizer(this.moduleDirectories
                .toString(), File.pathSeparator);

        while (st.hasMoreTokens()) {
            String moduleDir = st.nextToken();
            File path = new File(moduleDir);

            if (path.isDirectory()) {
                if (isModuleDirectory(path)) {
                    descriptors.add(path);
                } else {
                    String[] dirs = path.list();
                    boolean matched = false;
                    for (int i = 0; i < dirs.length; i++) {
                        File moduleSubDir = new File(path, dirs[i]);
                        if (isModuleDirectory(moduleSubDir)) {
                            descriptors.add(moduleSubDir);
                            matched = true;
                        }
                    }
                    if (!matched) {
                        log("No module(s) found in directory [" + path + "]");
                    }
                }
            } else {
                throw new BuildException("No such directory: " + path);
            }
        }

        try {
            Document doc = AntDocumentHelper.createDocument(NAMESPACE,
                    "modules", null);
            File[] modules = (File[]) descriptors.toArray(new File[descriptors
                    .size()]);
            for (int i = 0; i < modules.length; i++) {
                Element element = doc.createElementNS(NAMESPACE, "module");
                element.setAttribute("src", modules[i].getAbsolutePath());
                doc.getDocumentElement().appendChild(element);
            }
            File file = new File(this.moduleFile.replace('/',
                    File.separatorChar));
            AntDocumentHelper.writeDocument(doc, file);
        } catch (Exception e) {
            throw new BuildException(e);
        }

    }

    protected static final String NAMESPACE = "http://apache.org/lenya/module-list/1.0";

    protected boolean isModuleDirectory(File path) {
        File moduleFile = new File(path, "module.xml");
        return moduleFile.isFile();
    }

    /**
     * Set the module source directories.
     * 
     * @param dirs
     *            The module directories.
     */
    public void setModuleDirs(Path dirs) {
        this.moduleDirectories = dirs;
    }

    /**
     * Set the file to generate.
     * 
     * @param file
     *            The file.
     */
    public void setModuleFile(String file) {
        this.moduleFile = file;
    }

}
