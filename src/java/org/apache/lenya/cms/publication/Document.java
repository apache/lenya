/*
$Id: Document.java,v 1.11 2003/07/25 15:22:10 gregor Exp $
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
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public interface Document {
    /**
     * Returns the document ID of this document.
     * @return A URI object.
     */
    String getId();

    /**
     * Returns the publication this document belongs to.
     * @return A publication object.
     */
    Publication getPublication();

    /**
     * Returns the complete URL of this document:<br/>
     * /{publication-id}/{area}{document-id}{language-suffix}.{extension}
     * @return A string.
     */
    String getCompleteUrl();

    /**
     * Returns the URL of this document:
     * {document-id}{language-suffix}.{extension}
     * The URL always starts with a slash (/).
     * @return A string.
     */
    String getDocumentUrl();

	/**
	 * Returns the dublin core class for this document.
	 * @return A DublinCore object.
	 */
	DublinCore getDublinCore();

    /**
     * Returns the language of this document.
     * @return A string denoting the language.
     */
    String getLanguage();

	/**
	 * Returns the abstract of this document.
	 * @return A string denoting the abstract.
	 */
	String getAbstract();

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
    
}
