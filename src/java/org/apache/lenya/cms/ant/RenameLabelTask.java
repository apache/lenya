/*
$Id: RenameLabelTask.java,v 1.2 2003/09/12 18:45:08 egli Exp $
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

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to rename a label in an existing node in a tree.
 * 
 * @author egli
 */
public class RenameLabelTask extends PublicationTask {
    private String documentid;
    private String labelName;
    private String area;
    private String language;

    /**
     * Creates a new instance of InsertLabelTask
     */
    public RenameLabelTask() {
        super();
    }

    /**
     * Get the area of the site tree.
     * 
     * @return  the area of the tree.
     */
    protected String getArea() {
        return area;
    }

    /**
     * Set the area.
     * 
     * @param area the area.
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * Set the value of the area of the tree.
     * 
     * @param area the area of the tree.
     */
    public void setAbsolutetreepath(String area) {
        this.area = area;
    }

    /**
     * Return the document-id corresponding to the node to delete.
     * 
     * @return string The document-id.
     */
    protected String getDocumentid() {
        return documentid;
    }

    /**
     * Set the value of the document-id corresponding to the node to delete.
     * 
     * @param string The document-id.
     */
    public void setDocumentid(String string) {
        documentid = string;
    }

    /**
     * Get the name of the label.
     * 
     * @return the labelName
     */
    public String getLabelName() {
        return labelName;
    }

    /**
     * Set the labelName.
     * 
     * @param labelName the name of the label
     */
    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    /**
     * Get the language.
     * 
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the language.
     * 
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Rename a label in an existing node in the tree.
     * 
     * @param documentid the document-id of the document.
     * @param labelName the new name of the label.
     * @param language the language of the label that is to be renamed.
     * @param area determines in which sitetree the label is to be renamed
     * 
     * @throws SiteTreeException if an error occurs.
     */
    public void renameLabel(
        String documentid,
        String labelName,
        String language,
        String area)
        throws SiteTreeException, DocumentException {

        DefaultSiteTree tree = null;
        tree = getPublication().getSiteTree(area);
        SiteTreeNode node = tree.getNode(documentid);
        if (node == null) {
            throw new DocumentException(
                "Document-id " + documentid + " not found.");
        }
        Label label = node.getLabel(language);
        if (label == null) {
            throw new DocumentException(
                "Label for language " + language + " not found.");
        }
	// FIXME: This is somewhat of a hack. The change of the label
	// name should not be done by removing the label and readding
	// it. Instead the node should probably have a setLabel method
	// which could be invoked by the Label.setLabel() method.
        tree.removeLabel(documentid, label);
        label.setLabel(labelName);
        tree.addLabel(documentid, label);
        tree.save();
    }

    /** (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("document-id corresponding to the node: " + getDocumentid());
            log("label name: " + getLabelName());
            log("language: " + getLanguage());
            log("area: " + getArea());
            renameLabel(
                getDocumentid(),
                getLabelName(),
                getLanguage(),
                getArea());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
