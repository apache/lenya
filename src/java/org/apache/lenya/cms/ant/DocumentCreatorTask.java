/*
$Id
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
package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.authoring.CreatorException;
import org.apache.lenya.cms.authoring.DocumentCreator;

import org.apache.tools.ant.BuildException;

import java.io.File;


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
            creator.create(getPublication(),
                new File(getPublication().getDirectory(), getAuthoringPath()), getTreeFile(),
                getParentId(), getChildId(), getChildName(), getChildType(), documentType);
        } catch (CreatorException e) {
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
