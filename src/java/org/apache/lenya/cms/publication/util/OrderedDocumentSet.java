/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id$  */

package org.apache.lenya.cms.publication.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.site.SiteException;
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
        } catch (final SiteException e) {
            throw new RuntimeException(e);
        } catch (final PublicationException e) {
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