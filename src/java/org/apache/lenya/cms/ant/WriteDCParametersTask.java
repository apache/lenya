/*
$Id: WriteDCParametersTask.java,v 1.1 2003/08/14 10:46:23 egli Exp $
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

import org.apache.lenya.cms.publication.DefaultDocumentBuilder;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DublinCore;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to insert a label into an existing node in a tree.
 * 
 * @author egli
 */
public class WriteDCParametersTask extends PublicationTask {
    private String creator = null;
    private String title = null;
    private String description = null;
    private String subject = null;
    private String language = null;
    private String publisher = null;
    private String dateIssued = null;
    private String datePublished = null;    
    private String rights = null;
 
    /**
     * Creates a new instance of InsertLabelTask
     */
    public WriteDCParametersTask() {
        super();
    }

    /**
     * Get the creator
     * 
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Get the description
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the publisher
     * 
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Get the rights
     * 
     * @return the rights
     */
    public String getRights() {
        return rights;
    }

    /**
     * Get the subject
     * 
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Get the title
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the creator.
     * 
     * @param string the creator
     */
    public void setCreator(String string) {
        creator = string;
    }

    /**
     * Set the description
     * 
     * @param string the description
     */
    public void setDescription(String string) {
        description = string;
    }

    /**
     * Set the publisher.
     * 
     * @param string the publisher
     */
    public void setPublisher(String string) {
        publisher = string;
    }

    /**
     * Set the rights
     * 
     * @param string the rights
     */
    public void setRights(String string) {
        rights = string;
    }

    /**
     * Set the subject
     * 
     * @param string the subject
     */
    public void setSubject(String string) {
        subject = string;
    }

    /**
     * Set the title
     * 
     * @param string the title
     */
    public void setTitle(String string) {
        title = string;
    }

    /**
     * Get the date of issue
     * 
     * @return the date of issue
     */
    public String getDateIssued() {
        return dateIssued;
    }

    /**
     * Get the date of publication.
     *  
     * @return the date of publication
     */
    public String getDatePublished() {
        return datePublished;
    }

    /**
     * Set the date of first issue
     * 
     * @param string the date of issue
     */
    public void setDateIssued(String string) {
        dateIssued = string;
    }

    /**
     * Set the publication date
     * 
     * @param string the date of publication
     */
    public void setDatePublished(String string) {
        datePublished = string;
    }

    /**
     * Write the dublin core params.
     * 
     * @param creator the creator.
     * @param title the title
     * @param description the description
     * @param subject the subject
     * @param publisher the publisher
     * @param dateIssued the date of issue
     * @param datePublished the date of publication
     * @param rights the rights
     * 
     * @throws BuildException if an error occurs
     * @throws DocumentBuildException if an error occurs
     * @throws DocumentException if an error occurs
     */
    public void writeDublinCoreParameters(
        String creator,
        String title,
        String description,
        String subject,
        String publisher,
        String dateIssued,
        String datePublished,
        String rights)
        throws BuildException, DocumentBuildException, DocumentException {

        String url = null;

        DefaultDocumentBuilder builder = DefaultDocumentBuilder.getInstance();
        Document doc = builder.buildDocument(getPublication(), url);
        DublinCore dc = doc.getDublinCore();
        dc.setCreator(creator);
        dc.setTitle(title);
        dc.setDescription(description);
        dc.setSubject(subject);
        dc.setPublisher(publisher);
        dc.setDateIssued(dateIssued);
        dc.setDatePublished(datePublished);
        dc.setRights(rights);
        dc.save();
    }

    /** (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            writeDublinCoreParameters(
                getCreator(),
                getTitle(),
                getDescription(),
                getSubject(),
                getPublisher(),
                getDateIssued(),
                getDatePublished(),
                getRights());
            } catch (
                Exception e) {
            throw new BuildException(e);
        }
    }
}
