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

/* $Id: DocumentIdToPath.java,v 1.10 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.DocumentIdToPathMapper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;

/**
 * Ant task to get the directory path of the xml files of a document with document id.
 * The path is given from the {area} directory.
 */
public class DocumentIdToPath extends PublicationTask {
    private String area;
    private String documentid;
    private String propertyname;

    /**
     * Creates a new instance of DocumentIdToPath
     */
    public DocumentIdToPath() {
        super();
    }

    /**
     * @return Sting The area.
     */
    public String getArea() {
        return area;
    }

    /**
     * @return string The document id 
     */
    protected String getDocumentid() {
        return documentid;
    }

    /**
     * @return propertyname. The name of the property for the directory path.
     */
    public String getPropertyname() {
        return propertyname;
    }

    /**
     * @param string The area.
     */
    public void setArea(String string) {
        area = string;
    }

    /**
     * @param string The name of the property.
     */
    public void setPropertyname(String string) {
        propertyname = string;
    }

    /**
     * Set the value of the document id.
     *   
     * @param string The document id. 
     */
    public void setDocumentid(String string) {
        documentid = string;
    }

    /**
     * Gets the directory path from the document id and sets this value in the 
     * property of the project with the name propertyname.   
    
     * @param area The area (ex authoring)
     * @param documentid  The document id.
     * @param propertyname The name of the property
     */
    public void compute(String area, String documentid, String propertyname) {

        Publication publication = getPublication();
        DocumentIdToPathMapper pathMapper = publication.getPathMapper();
        String path = pathMapper.getPath(documentid, "");
        log("path " + path);

        int index = path.lastIndexOf("/");
        String dir = path.substring(0, index);
        log("dir " + dir);

        Target target = getOwningTarget();
        Project project = target.getProject();
        project.setProperty(propertyname, dir);
    }

    /** 
     * @see org.apache.tools.ant.Task#execute()
     **/
    public void execute() throws BuildException {
        log("document-id " + getDocumentid());
        log("area " + getArea());
        log("property: " + getPropertyname());
        compute(getArea(), getDocumentid(), getPropertyname());
    }

}
