/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ant;

import java.io.File;

import org.apache.lenya.cms.authoring.CreatorException;
import org.apache.lenya.cms.authoring.DocumentCreator;
import org.apache.tools.ant.BuildException;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DocumentCreatorTask extends PublicationTask {

    private String parentId;
    private String childId;
    private String childName;
    private String childType;
    private String documentType;
    private String treeFile;
    private String authoringPath;

    /* (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {

        DocumentCreator creator = new DocumentCreator();
//        File contentDirectory = new  File(getPublication().getDirectory(), get
        
        try {
            creator.create(
                getPublication(),
                new File(getPublication().getDirectory(), getAuthoringPath()),
                getTreeFile(),
                getParentId(),
                getChildId(),
                getChildName(),
                getChildType(),
                documentType);
        }  catch (CreatorException e) {
            throw new BuildException(e);
        }
    }

    /**
     * @return
     */
    public String getChildType() {
        assertString(childType); 
        return childType;
    }

    /**
     * @return
     */
    public String getDocumentType() {
        assertString(documentType); 
        return documentType;
    }

    /**
     * @return
     */
    public String getParentId() {
        assertString(parentId); 
        return parentId;
    }

    /**
     * @param string
     */
    public void setChildType(String string) {
        assertString(string); 
        childType = string;
    }

    /**
     * @param string
     */
    public void setDocumentType(String string) {
        assertString(string); 
        documentType = string;
    }

    /**
     * @param string
     */
    public void setParentId(String string) {
        assertString(string); 
        parentId = string;
    }

    /**
     * @return
     */
    public String getChildId() {
        assertString(childId); 
        return childId;
    }

    /**
     * @return
     */
    public String getChildName() {
        assertString(childName); 
        return childName;
    }

    /**
     * @param string
     */
    public void setChildId(String string) {
        assertString(string); 
        childId = string;
    }

    /**
     * @param string
     */
    public void setChildName(String string) {
        assertString(string); 
        childName = string;
    }

    /**
     * @return
     */
    public String getAuthoringPath() {
        assertString(authoringPath); 
        return authoringPath;
    }

    /**
     * @return
     */
    public String getTreeFile() {
        assertString(treeFile); 
        return treeFile;
    }

    /**
     * @param string
     */
    public void setAuthoringPath(String string) {
        assertString(string); 
        authoringPath = string;
    }

    /**
     * @param string
     */
    public void setTreeFile(String string) {
        assertString(string); 
        treeFile = string;
    }

}
