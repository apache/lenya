/*
$Id: OrderedDocumentSet.java,v 1.2 2004/02/18 18:45:19 andreas Exp $
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * A document set which is ordered by dependence, starting with the document
 * which does not require any other documents.
 * </p>
 * 
 * <p>Dependence on a set of documents must be a strict partial order <strong>&lt;</strong>:</p>
 * <ul>
 * <li>irreflexive: d<strong>&lt;</strong>d does not hold for any document d</li>
 * <li>antisymmetric: d<strong>&lt;</strong>e and e<strong>&lt;</strong>d implies d=e</li>
 * <li>transitive: d<strong>&lt;</strong>e and e<strong>&lt;</strong>f implies d<strong>&lt;</strong>f</li>
 * </ul>
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class OrderedDocumentSet extends DocumentSet {

    private List documents = new ArrayList();

    /**
     * This method throws an exception when a loop in the
     * dependency graph occurs.
     * 
     * @see org.apache.lenya.cms.publication.DocumentSet#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(Document document) {

        Publication publication = document.getPublication();
        int i = 0;

        try {
            while (i < documents.size()
                && publication.dependsOn(document, (Document) documents.get(i))) {
                i++;
            }

            documents.add(i, document);
        
            if (!check()) {
                documents.remove(i);
                throw new PublicationException("The dependence relation is not a strict partial order!");
            }
        } catch (PublicationException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            throw new IllegalStateException(writer.toString());
        }
        
    }

    /**
     * Checks if the dependence relation is a strict partial order.
     * @return A boolean value.
     * @throws PublicationException when something went wrong.
     */
    protected boolean check() throws PublicationException {
        boolean isStrictPartialOrder = isIrreflexive() && isAntisymmetric() && isTransitive();
        return isStrictPartialOrder;
    }

    /**
     * Checks if the dependence relation is antisymmetric.
     * @return A boolean value.
     * @throws PublicationException when something went wrong.
     */
    protected boolean isAntisymmetric() throws PublicationException {
        Document[] documents = getDocuments();
        boolean isAntisymmetric = true;
        for (int i = 0; i < documents.length; i++) {
            Publication publication = documents[i].getPublication();
            for (int j = i + 1; j < documents.length; j++) {
                if (publication.dependsOn(documents[i], documents[j])
                    && publication.dependsOn(documents[j], documents[i])
                    && !(documents[i].equals(documents[j]))) {
                    isAntisymmetric = false;
                }
            }
        }
        return isAntisymmetric;
    }

    /**
     * Checks if the dependence relation is transitive.
     * @return A boolean value.
     * @throws PublicationException when something went wrong.
     */
    protected boolean isTransitive() throws PublicationException {
        Document[] documents = getDocuments();
        boolean isTransitive = true;
        for (int i = 0; i < documents.length; i++) {
            Publication publication = documents[i].getPublication();
            for (int j = i + 1; j < documents.length; j++) {
                for (int k = j + 1; k < documents.length; k++) {
                    if (publication.dependsOn(documents[i], documents[j])
                        && publication.dependsOn(documents[j], documents[k])
                        && !publication.dependsOn(documents[i], documents[k])) {
                        isTransitive = false;
                    }
                }
            }
        }
        return isTransitive;
    }

    /**
     * Checks if the dependence relation is irreflexive.
     * @return
     * @throws PublicationException
     */
    protected boolean isIrreflexive() throws PublicationException {
        Document[] documents = getDocuments();
        boolean isIrreflexive = true;
        for (int i = 0; i < documents.length; i++) {
            Publication publication = documents[i].getPublication();
            if (publication.dependsOn(documents[i], documents[i])) {
                isIrreflexive = false;
            }
        }
        return isIrreflexive;
    }

    /**
     * Checks if this set is empty.
     * 
     * @return A boolean value.
     */
    public boolean isEmpty() {
        return documents.isEmpty();
    }

    /**
     * Returns the documents contained in this set in ascending order.
     * 
     * @return An array of documents.
     */
    public Document[] getDocuments() {
        return (Document[]) documents.toArray(new Document[documents.size()]);
    }

    /**
     * Visits the document set in ascending order (required document before
     * requiring document).
     * @param visitor The visitor.
     * @throws DocumentException when a loop occurs or an error occurs during visiting.
     */
    public void visitAscending(DocumentSetVisitor visitor) throws DocumentException {
        visit(visitor);
    }

    /**
     * Visits the document set in descending order (requiring document before
     * required document).
     * @param visitor The visitor.
     * @throws DocumentException when a loop occurs or an error occurs during visiting.
     */
    public void visitDescending(DocumentSetVisitor visitor) throws DocumentException {
        Document[] documents = getDocuments();
        List list = Arrays.asList(documents);
        Collections.reverse(list);
        documents = (Document[]) list.toArray(new Document[list.size()]);
        for (int i = 0; i < documents.length; i++) {
            documents[i].accept(visitor);
        }
    }

}
