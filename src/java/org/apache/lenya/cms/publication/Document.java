/*
$Id: Document.java,v 1.20 2003/09/12 10:01:15 egli Exp $
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

import java.io.File;
import java.util.Date;

/**
 * A CMS document.
 *
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public interface Document {
    
    String NAMESPACE = "http://apache.org/cocoon/lenya/document/1.0";
    String DEFAULT_PREFIX = "lenya";
    
    /**
     * Returns the document ID of this document.
     * @return the document-id of this document.
     */
    String getId();
    
    /**
     * Instead of returning the full document-id for this
     * document it just returns the id of the particular 
     * node, basically the basename of the document-id.
     * 
     * @return the node id, i.e. the basename of the document-id
     */
    String getNodeId();

    /**
     * Returns the publication this document belongs to.
     * @return A publication object.
     */
    Publication getPublication();

    /**
     * Returns the complete URL of this document in the info area:<br/>
     * /{publication-id}/info-{area}{document-id}{language-suffix}.{extension}
     * @return A string.
     */
    String getCompleteInfoURL();

    /**
     * Returns the complete URL of this document:<br/>
     * /{publication-id}/{area}{document-id}{language-suffix}.{extension}
     * @return A string.
     */
    String getCompleteURL();

    /**
     * Returns the complete URL of this document without 
     * the language-suffix: 
     * /{publication-id}/{area}{document-id}.{extension}
     * The URL always starts with a slash (/).
     * @return A string.
     */
    String getCompleteURLWithoutLanguage();

    /**
     * Returns the URL of this document:
     * {document-id}{language-suffix}.{extension}
     * The URL always starts with a slash (/).
     * @return A string.
     */
    String getDocumentURL();

	/**
	 * Returns the dublin core class for this document.
	 * @return A DublinCore object.
	 */
	DublinCore getDublinCore();

    /**
     * Returns the language of this document.
     * Each document has one language associated to it. 
     * @return A string denoting the language.
     */
    String getLanguage();

	/**
	 * Returns all the languages this document is available in.
     * A document has one associated language (@see Document#getLanguage)
     * but there are possibly a number of other languages for which a 
     * document with the same document-id is also available in. 
     * 
 	 * @return An array of strings denoting the languages.
     * 
     * @throws DocumentException if an error occurs
	 */
	String[] getLanguages() throws DocumentException;

	/**
	 * Get the navigation label associated with this document 
	 * for the language.
	 * 
	 * @return the label String
	 * 
	 * @throws DocumentException if an error occurs
	 */
	String getLabel() throws DocumentException;

	/**
	 * Returns the date of the last modification of this document.
	 * @return A date denoting the date of the last modification.
	 */
	Date getLastModified();

    /**
     * Returns the area this document belongs to.
     * @return The area.
     */
    String getArea();

    /**
     * Returns the file for this document.
     * @return A file object.
     */
    File getFile();

    /**
     * Returns the extension in the URL.
     * @return A string.
     */
    String getExtension();
    
    /**
     * Check if a document with the given document-id, language and in the given
     * area actually exists.
     * 
     * @return true if the document exists, false otherwise
     * 
     * @throws DocumentException if an error occurs
     */
    boolean exists() throws DocumentException;
    
    /**
     * Check if a document exists with the given document-id and the given area
     * independently of the given language.
     * 
     * @return true if a document with the given document-id and area exists,
     * null otherwise
     * 
     * @throws DocumentException if an error occurs
     */
    boolean existsInAnyLanguage() throws DocumentException;
}
