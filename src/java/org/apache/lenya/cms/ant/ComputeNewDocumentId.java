/*
$Id: ComputeNewDocumentId.java,v 1.3 2003/08/20 16:50:33 edith Exp $
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

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.UniqueDocumentId;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;


/**
 * Ant task to set the property "newdocumentid" in the project with the 
 * value of computed unique document id, needed for the destination file
 * Overriden for copy/move/rename.  
 * @param area The area in which is the sitetree.
 * @param firstdocumentid The document id of the source
 * @param secdocumentid The document id of the parent of the destination
 * or in case of rename the new name
 * @author edith
 */
public class ComputeNewDocumentId extends PublicationTask{
	private String area;
	private String firstdocumentid;
	private String secdocumentid;

    /**
     * Creates a new instance of ComputeNewDocumentId
     */
    public ComputeNewDocumentId() {
        super();
    }

	/**
	 * @return string The area in which is the sitetree.
	 */
	public String getArea() {
		return area;
	}

	/**
	 * set the value of the area in which is the sitetree
	 * @param string The area of the sitetree.
	 */
	public void setArea(String string) {
		area = string;
	}

    /**
     * @return string The document id of the source 
     */
    protected String getFirstdocumentid() {
        return firstdocumentid;
    }

    /**
     * set the value of the document id of the source
     * @param string The document id of the source
     */
    public void setFirstdocumentid(String string) {
        firstdocumentid = string;
    }

    /**
     * @return string The document id of the parent of the destination
     * or the new name in case of rename
     */
    protected String getSecdocumentid() {
        return secdocumentid;
    }

    /**
     * @param string The document id of the parent of the destination
     * or the new name in case of rename
     */
    public void setSecdocumentid(String string) {
        secdocumentid = string;
    }

	/**
	 * Method to be overriden to compute the document id of the destination.
	 * @param firstdocumentid The document id of the source.
	 * @param secdocumentid Some string to characterize the destination (ex 
	 * document id of parent, new name).
	 * @return string. The new document id
	 */
	protected String compute(String firstdocumentid, String secdocumentid) {
		return secdocumentid;
	}

    /**
     * Compute the unique document id: append a "_version number" to the id,
     * if there is already a node in the sitetree with this id.
     * @param documentid  The document id.
     * @param area The area in which is the sitetree.
     * @return newdocumentid The unique document id. 
     */
	protected String computeUniqueId(String documentid, String area) {

		Publication publication = getPublication();

		UniqueDocumentId uniqueDocumentId = new UniqueDocumentId();
        String newdocumentid = uniqueDocumentId.computeUniqueDocumentId(publication, area, documentid);
		return newdocumentid;

    }

	/**
	 * Set the property node.newdocumentid for the project
	 * @param documentid The new document id.
	 */
	protected void setNewProperty(String documentid) {
		Target target = getOwningTarget();
		Project project = target.getProject();
		project.setProperty("node.newdocumentid", documentid);
	}

    /**
     * @see org.apache.tools.ant.Task#execute()
     **/
    public void execute() throws BuildException {
        try {
            log("first-document-id " + getFirstdocumentid());
            log("sec-document-id " + getSecdocumentid());
            log("area: " + getArea());
            String documentId = compute(getFirstdocumentid(), getSecdocumentid());
			String uniqueId = computeUniqueId(documentId, getArea());
            setNewProperty(uniqueId);
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
