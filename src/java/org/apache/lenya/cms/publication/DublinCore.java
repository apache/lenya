/*
$Id: DublinCore.java,v 1.18 2003/08/28 09:51:53 egli Exp $
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

/**
 * 
 * @author egli
 * 
 */
public interface DublinCore {
    
    /**
     * Save the meta data.
     *
     * @throws DocumentException if the meta data could not be made persistent.
     */
    void save() throws DocumentException;

    /**
     * Get the creator
     * 
     * @return the creator
     * 
     * @throws DocumentException if an error occurs
     */
    String getCreator() throws DocumentException;

    /**
     * Set the DC creator
     * 
     * @param creator the Creator
     */
    void setCreator(String creator);

    /**
     * Get the title
     * 
     * @return the title
     * 
     * @throws DocumentException if an error occurs
     */
    String getTitle() throws DocumentException;

    /**
     * Set the DC title
     * 
     * @param title the title
     */
    void setTitle(String title);

    /**
     * Get the description
     * 
     * @return the description
     * 
     * @throws DocumentException if an error occurs
     */
    String getDescription() throws DocumentException;

    /**
     * Set the DC Description
     * 
     * @param description the description
     */
    void setDescription(String description);

    /**
     * Get the identifier
     * 
     * @return the identifier
     * 
     * @throws DocumentException if an error occurs
     */
    String getIdentifier() throws DocumentException;

    /**
     * Set the DC Identifier
     * 
     * @param identifier the identifier
     */
    void setIdentifier(String identifier);

    /**
     * Get the subject.
     * 
     * @return the subject
     * 
     * @throws DocumentException if an error occurs
     */
    String getSubject() throws DocumentException;

    /**
     * Set the DC Subject
     * 
     * @param subject the subject
     */
    void setSubject(String subject);

    /**
     * Get the publisher
     * 
     * @return the publisher
     * 
     * @throws DocumentException if an error occurs
     */
    String getPublisher() throws DocumentException;

    /**
     * Set the publisher
     * 
     * @param publisher the publisher
     */
    void setPublisher(String publisher);

    /**
     * Get the date of issue
     * 
     * @return the date of issue
     * 
     * @throws DocumentException if an error occurs
     */
    String getDateIssued() throws DocumentException;
    
    /**
     * Set the date of issue
     * 
     * @param dateIssued the date of issue
     */
    void setDateIssued(String dateIssued);
    
    /**
     * Get the date of creation
     * 
     * @return the date of creation
     * 
     * @throws DocumentException if an error occurs
     */
    String getDateCreated() throws DocumentException;
    
    /**
     * Set the date of creation
     * 
     * @param dateCreated the date of creation
     */
    void setDateCreated(String dateCreated);
    
    /**
     * Get the rights
     * 
     * @return the rights
     * 
     * @throws DocumentException if an error occurs
     */
    String getRights() throws DocumentException;
    
    /**
     * Set the DC Rights
     * 
     * @param rights the rights
     */
    void setRights(String rights);
}