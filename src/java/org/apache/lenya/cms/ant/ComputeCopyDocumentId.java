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

import org.apache.lenya.cms.publication.UniqueDocumentId;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import java.util.StringTokenizer;


/**
 * Compute the unique document id for the copy from the document id
 * of the copied file and this of the parent document of the copy.
 * @author edith
 */
public class ComputeCopyDocumentId extends Task {
    private String absolutetreepath;
    private String firstdocumentid;
    private String secdocumentid;

    /**
     * Creates a new instance of ComputeCopyDocumentId
     */
    public ComputeCopyDocumentId() {
        super();
    }

    /**
     * @return absolutetreepath The absolute path of the tree
     */
    protected String getAbsolutetreepath() {
        return absolutetreepath;
    }

    /**
     * set the value of the absolute path of the tree
     * @param string The absolute path of the tree
     */
    public void setAbsolutetreepath(String string) {
        absolutetreepath = string;
    }

    /**
     * @return string The document id of the file to copy
     */
    protected String getFirstdocumentid() {
        return firstdocumentid;
    }

    /**
     * set the value of the document id of the file to copy
     * @param string The document id of the file to copy
     */
    public void setFirstdocumentid(String string) {
        firstdocumentid = string;
    }

    /**
     * @return documentId The document id of the parent of the copy file.
     */
    protected String getSecdocumentid() {
        return secdocumentid;
    }

    /**
     * set the value of the document id of the parent of the copy file.
     * @param documentId The document id of the parent of the copy file.
     */
    public void setSecdocumentid(String documentId) {
        secdocumentid = documentId;
    }

    /**
    * Compute the unique document id for the copy file
    * from the document id of the copied file and this of
    * the parent document of the copy file.
      * @param firstdocumentid  The document id of the copied document
     * @param secdocumentid  The document id of the parent document of the copy file
     * @param absolutetreepath The absolute path of the tree
     */
    public void compute(String firstdocumentid, String secdocumentid, String absolutetreepath) {
        StringTokenizer st = new StringTokenizer(firstdocumentid, "/", true);
        int l = st.countTokens();

        for (int i = 1; i < l; i++) {
            st.nextToken();
        }

        secdocumentid = secdocumentid + "/" + st.nextToken();

        UniqueDocumentId uniqueDocumentId = new UniqueDocumentId();
        secdocumentid = uniqueDocumentId.computeUniqueDocumentId(absolutetreepath, secdocumentid);

        Target target = getOwningTarget();
        Project project = target.getProject();
        project.setProperty("node.newdocumentid", secdocumentid);
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     **/
    public void execute() throws BuildException {
        try {
            log("first-document-id " + getFirstdocumentid());
            log("sec-document-id " + getSecdocumentid());
            log("Absolute Tree Path: " + getAbsolutetreepath());
            compute(getFirstdocumentid(), getSecdocumentid(), getAbsolutetreepath());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
