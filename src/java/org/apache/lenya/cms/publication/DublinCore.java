/*
$Id: DublinCore.java,v 1.8 2003/07/30 15:29:33 gregor Exp $
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

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PublicationException;

import org.apache.lenya.xml.DocumentHelper;

import java.io.File;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * A publication.
 *
 * @author <a href="mailto:gregor@apache.org">Gregor J. Rothfuss</a>
 */
public class DublinCore {
	private Document cmsdocument;
	private File infofile;
    private NodeList nodelist;
    private String string;	

	private static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";

    /** 
     * Creates a new instance of Dublin Core
     * 
     */
    protected DublinCore(Document mydocument) {
    	this.cmsdocument = mydocument;
    	this.infofile = cmsdocument.getPublication().getPathMapper().getFile(cmsdocument.getPublication(), cmsdocument.getPublication().AUTHORING_AREA, cmsdocument.getId(), cmsdocument.getLanguage());
    }

	/**
	 * @see org.apache.lenya.cms.publication.Document#getDCTitle()
	 */
	public String getTitle() throws PublicationException {
		return getDCNode("title");
	}

	/**
	 * Set the DC title
	 * 
	 * @param title the title
	 */
	public void setTitle(String title) {
	}

	/**
	 * @see org.apache.lenya.cms.publication.Document#getDCTitle()
	 */
	public String getCreator() throws PublicationException{ 
		return getDCNode("creator");
		}

	/**
	 * Set the DC creator
	 * 
	 * @param creator the Creator
	 */
	public void setCreator(String creator) {
	}

	/**
	 * @see org.apache.lenya.cms.publication.Document#getDCTitle()
	 */
	public String getSubject() throws PublicationException { 
		return getDCNode("subject");
	}

	private String getDCNode(String node) {
			try {
				nodelist = DocumentHelper.readDocument(infofile).getElementsByTagNameNS(DC_NAMESPACE, node);
				try {
				string = nodelist.item(0).getFirstChild().getNodeValue();
			} catch (Exception e) {
								string = "";
				}
			} catch (Exception e) {
				string = e.toString();
			}
			
			return string;
		}

	private void setDCNode(String node, String text) {
		Node oldnode;
		Node newnode;
			try {
				nodelist = DocumentHelper.readDocument(infofile).getElementsByTagNameNS(DC_NAMESPACE, node);
				oldnode = nodelist.item(0).getFirstChild();
			} catch (Exception e) {
				string = e.toString();
			}
		}

	/**
	 * Set the DC Subject
	 * 
	 * @param subject the subject
	 */
	public void setSubject(String subject) {
	}

	/**
	 * @see org.apache.lenya.cms.publication.Document#getDCTitle()
	 */
	public String getDescription() throws PublicationException { 
		return getDCNode("description");
		}
	
	/**
	 * Set the DC Description
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
	}

	/**
	 * @see org.apache.lenya.cms.publication.Document#getDCTitle()
	 */
	public String getRights() throws PublicationException { 
		return getDCNode("rights");
		}

	/**
	 * Set the DC Rights
	 * 
	 * @param rights the rights
	 */
	public void setRights(String rights) {
	}


}
