/*
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
 * A proxy to the dublin core meta implementation so that meta data is 
 * only read from file when it is actually requested.
 *
 * @author <a href="mailto:egli@apache.org">Christian Egli</a>
 * @version $Id: DublinCoreProxy.java,v 1.8 2004/02/20 10:41:06 andreas Exp $
 */
public class DublinCoreProxy implements DublinCore {

    private DublinCoreImpl dcCore;
    private Document cmsDocument;


    /** 
     * Creates a new instance of Dublin Core
     * 
     * @param aDocument the document for which the Dublin Core instance is created.
     */
    public DublinCoreProxy(Document aDocument) {
        this.cmsDocument = aDocument;
    }

    /**
     * Instanciate a dublin core implementation object
     * 
     * @return a real dublin core object
     * @throws DocumentException when an error occurs.
     */
    protected DublinCoreImpl instance() throws DocumentException {
        if (dcCore == null) {
            dcCore = new DublinCoreImpl(this.cmsDocument);
        }
        return dcCore;
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getCreator()
     */
    public String getCreator() throws DocumentException {
        return instance().getFirstValue(DublinCore.ELEMENT_CREATOR);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getDateCreated()
     */
    public String getDateCreated() throws DocumentException {
        return instance().getFirstValue(DublinCore.TERM_CREATED);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getDateIssued()
     */
    public String getDateIssued() throws DocumentException {
        return instance().getFirstValue(DublinCore.TERM_ISSUED);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getDescription()
     */
    public String getDescription() throws DocumentException {
        return instance().getFirstValue(DublinCore.ELEMENT_DESCRIPTION);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getIdentifier()
     */
    public String getIdentifier() throws DocumentException {
        return instance().getFirstValue(DublinCore.ELEMENT_IDENTIFIER);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getPublisher()
     */
    public String getPublisher() throws DocumentException {
        return instance().getFirstValue(DublinCore.ELEMENT_PUBLISHER);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getRights()
     */
    public String getRights() throws DocumentException {
        return instance().getFirstValue(DublinCore.ELEMENT_RIGHTS);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getIsReferencedBy()
     */
    public String getIsReferencedBy() throws DocumentException {
        return instance().getFirstValue(DublinCore.TERM_ISREFERENCEDBY);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getSubject()
     */
    public String getSubject() throws DocumentException {
        return instance().getFirstValue(DublinCore.ELEMENT_SUBJECT);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getTitle()
     */
    public String getTitle() throws DocumentException {
        return instance().getFirstValue(DublinCore.ELEMENT_TITLE);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setCreator(java.lang.String)
     */
    public void setCreator(String creator) throws DocumentException {
        instance().setValue(DublinCore.ELEMENT_CREATOR, creator);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setDateCreated(java.lang.String)
     */
    public void setDateCreated(String dateCreated) throws DocumentException {
        instance().setValue(DublinCore.TERM_CREATED, dateCreated);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setDateIssued(java.lang.String)
     */
    public void setDateIssued(String dateIssued) throws DocumentException {
        instance().setValue(DublinCore.TERM_ISSUED, dateIssued);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setDescription(java.lang.String)
     */
    public void setDescription(String description) throws DocumentException {
        instance().setValue(DublinCore.ELEMENT_DESCRIPTION, description);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setIdentifier(java.lang.String)
     */
    public void setIdentifier(String identifier) throws DocumentException {
        instance().setValue(DublinCore.ELEMENT_IDENTIFIER, identifier);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setPublisher(java.lang.String)
     */
    public void setPublisher(String publisher) throws DocumentException {
        instance().setValue(DublinCore.ELEMENT_PUBLISHER, publisher);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setRights(java.lang.String)
     */
    public void setRights(String rights) throws DocumentException {
        instance().setValue(DublinCore.ELEMENT_RIGHTS, rights);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setIsReferencedBy(java.lang.String)
     */
    public void setIsReferencedBy(String isReferencedBy) throws DocumentException {
        instance().setValue(DublinCore.TERM_ISREFERENCEDBY, isReferencedBy);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setSubject(java.lang.String)
     */
    public void setSubject(String subject) throws DocumentException {
        instance().setValue(DublinCore.ELEMENT_SUBJECT, subject);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setTitle(java.lang.String)
     */
    public void setTitle(String title) throws DocumentException {
        instance().setValue(DublinCore.ELEMENT_TITLE, title);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#save()
     */
    public void save() throws DocumentException {
        instance().save();
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getValues(java.lang.String)
     */
    public String[] getValues(String key) throws DocumentException {
        return instance().getValues(key);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getFirstValue(java.lang.String)
     */
    public String getFirstValue(String key) throws DocumentException {
        return instance().getFirstValue(key);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#addValue(java.lang.String, java.lang.String)
     */
    public void addValue(String key, String value) throws DocumentException {
        instance().addValue(key, value);

    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#removeValue(java.lang.String, java.lang.String)
     */
    public void removeValue(String key, String value) throws DocumentException {
        instance().removeValue(key, value);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#removeAllValues(java.lang.String)
     */
    public void removeAllValues(String key) throws DocumentException {
        instance().removeAllValues(key);
    }
    
	/**
	 * @see org.apache.lenya.cms.publication.DublinCore#replaceBy(org.apache.lenya.cms.publication.DublinCore)
	 */
	public void replaceBy(DublinCore other) throws DocumentException {
		instance().replaceBy(other);

	}

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#addValues(java.lang.String, java.lang.String[])
     */
    public void addValues(String key, String[] values) throws DocumentException {
        instance().addValues(key, values);
        
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#setValue(java.lang.String, java.lang.String)
     */
    public void setValue(String key, String value) throws DocumentException {
        instance().setValue(key, value);
    }

}
