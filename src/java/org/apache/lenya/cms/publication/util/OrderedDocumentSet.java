/*
 $Id: OrderedResourceSet.java,v 1.1 2004/02/18 18:47:07 andreas Exp $
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
package org.apache.lenya.cms.publication.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.site.SiteManager;

/**
 * <p>
 * A resource set which is ordered by dependence, starting with the resource
 * which does not require any other resources.
 * </p>
 * 
 * <p>
 * Dependence on a set of resources must be a strict partial order <strong>&lt;
 * </strong>:
 * </p>
 * <ul>
 * <li>irreflexive: d <strong>&lt; </strong>d does not hold for any resource d
 * </li>
 * <li>antisymmetric: d <strong>&lt; </strong>e and e <strong>&lt; </strong>d
 * implies d=e</li>
 * <li>transitive: d <strong>&lt; </strong>e and e <strong>&lt; </strong>f
 * implies d <strong>&lt; </strong>f</li>
 * </ul>
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann </a>
 */
public class OrderedDocumentSet extends DocumentSet {

    /**
     * Ctor.
     */
    public OrderedDocumentSet() {
        super();
    }

    /**
     * Ctor.
     * @param resources The initial resources.
     * @throws PublicationException if something went wrong.
     */
    public OrderedDocumentSet(Document[] resources) throws PublicationException {
        super(resources);
    }

    /**
     * This method throws an exception when a loop in the dependency graph
     * occurs.
     * 
     * @see org.apache.lenya.cms.publication.util.DocumentSet#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(Document document) {

        Publication publication = document.getPublication();
        try {
            SiteManager manager = publication.getSiteManager(document.getIdentityMap());

            if (manager == null) {
                throw new RuntimeException("The site manager must not be null!");
            }

            int i = 0;

            while (i < getList().size() && manager.requires(document, (Document) getList().get(i))) {
                i++;
            }

            getList().add(i, document);

            if (!check()) {
                getList().remove(i);
                throw new PublicationException(
                        "The dependence relation is not a strict partial order!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        Document[] resources = getDocuments();
        boolean isAntisymmetric = true;
        for (int i = 0; i < resources.length; i++) {
            Publication publication = resources[i].getPublication();
            SiteManager manager = publication.getSiteManager(resources[i].getIdentityMap());
            for (int j = i + 1; j < resources.length; j++) {
                if (manager.requires(resources[i], resources[j])
                        && manager.requires(resources[j], resources[i])
                        && !(resources[i] == resources[j])) {
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
        Document[] resources = getDocuments();
        boolean isTransitive = true;
        for (int i = 0; i < resources.length; i++) {
            Publication publication = resources[i].getPublication();
            SiteManager manager = publication.getSiteManager(resources[i].getIdentityMap());
            for (int j = i + 1; j < resources.length; j++) {
                for (int k = j + 1; k < resources.length; k++) {
                    if (manager.requires(resources[i], resources[j])
                            && manager.requires(resources[j], resources[k])
                            && !manager.requires(resources[i], resources[k])) {
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
        Document[] resources = getDocuments();
        boolean isIrreflexive = true;
        for (int i = 0; i < resources.length; i++) {
            Publication publication = resources[i].getPublication();
            SiteManager manager = publication.getSiteManager(resources[i].getIdentityMap());
            if (manager.requires(resources[i], resources[i])) {
                isIrreflexive = false;
            }
        }
        return isIrreflexive;
    }

    /**
     * Visits the resource set in ascending order (required resource before
     * requiring resource).
     * @param visitor The visitor.
     * @throws PublicationException when an error occurs during visiting.
     */
    public void visitAscending(DocumentVisitor visitor) throws PublicationException {
        visit(visitor);
    }

    /**
     * Visits the resource set in descending order (requiring resource before
     * required resource).
     * @param visitor The visitor.
     * @throws PublicationException when an error occurs during visiting.
     */
    public void visitDescending(DocumentVisitor visitor) throws PublicationException {
        Document[] resources = getDocuments();
        List list = Arrays.asList(resources);
        Collections.reverse(list);
        resources = (Document[]) list.toArray(new Document[list.size()]);
        for (int i = 0; i < resources.length; i++) {
            resources[i].accept(visitor);
        }
    }

}