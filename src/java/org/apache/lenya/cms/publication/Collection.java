/*
$Id: Collection.java,v 1.4 2004/01/09 11:14:39 andreas Exp $
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
 * A document representing a collection of documents.
 * This class is in prototyping stage.
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public interface Collection extends Document {

    String NAMESPACE = "http://apache.org/cocoon/lenya/collection/1.0";
    String DEFAULT_PREFIX = "col";
    
    String ELEMENT_COLLECTION = "collection";
    String ELEMENT_DOCUMENT = "document";
    String ATTRIBUTE_ID = "id";

    /**
     * Returns the documents in this collection.
     * @return An array of documents.
     * @throws DocumentException when something went wrong.
     */
    Document[] getDocuments() throws DocumentException;

    /**
     * Adds a document to the collection.
     * @param document A document.
     * @throws DocumentException when an error occurs.
     */
    void add(Document document) throws DocumentException;

    /**
     * Inserts a document into the collection at a specific position.
     * @param document A document.
     * @param position The position of the document after insertion,
     * starting with 0.
     * @throws DocumentException when something went wrong.
     */
    void add(int position, Document document) throws DocumentException;

    /**
     * Removes a document from the collection.
     * @param document A document.
     * @throws DocumentException when the document is not contained
     * or another error occurs.
     */
    void remove(Document document) throws DocumentException;
    
    /**
     * Removes all documents from this collection.
     * @throws DocumentException when something went wrong.
     */
    void clear() throws DocumentException;
    
    /**
     * Checks if this collection contains a specific document.
     * @param document The document to check.
     * @return A boolean value.
     * @throws DocumentException when something went wrong.
     */
    boolean contains(Document document) throws DocumentException;
    
    /**
     * Returns the first position of this document in the collection.
     * @param document The document.
     * @return An integer.
     * @throws DocumentException when the document is not contained.
     */
    int getFirstPosition(Document document) throws DocumentException;
    
    /**
     * Returns the number of documents in this collection.
     * @return An integer value.
     * @throws DocumentException when something went wrong.
     */
    int size() throws DocumentException;
    
}
