/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

package org.apache.lenya.cms.site;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.publication.DocumentFactory;

/**
 * Abstract base class for site managers.
 * 
 * @version $Id$
 */
public abstract class AbstractSiteManager extends AbstractLogEnabled implements SiteManager, Serviceable {

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
    
    /**
     * Ctor.
     * @param manager The service manager.
     */
    public AbstractSiteManager() {
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#sortAscending(org.apache.lenya.cms.publication.util.DocumentSet)
     */
    public SiteNode[] sortAscending(SiteNode[] nodes) throws SiteException {
        if (nodes.length > 0) {

            DocumentFactory map = nodes[0].getStructure().getPublication().getFactory();
            if (!check(map, new NodeSet(this.manager, nodes))) {
                throw new SiteException("The dependence relation is not a strict partial order!");
            }

            SiteNode[] sortedNodes = (SiteNode[]) Arrays.asList(nodes).toArray(new SiteNode[nodes.length]);
            Arrays.sort(sortedNodes, new NodeComparator(map));
            return sortedNodes;
        }
        else {
            return  nodes;
        }
    }

    /**
     * Checks if the dependence relation is a strict partial order.
     * 
     * @param map The identity map to operate on.
     * @param set The document set to check.
     * @return A boolean value.
     * @throws SiteException when something went wrong.
     */
    protected boolean check(DocumentFactory map, NodeSet set) throws SiteException {
        boolean isStrictPartialOrder = isIrreflexive(map, set) && isAntisymmetric(map, set)
                && isTransitive(map, set);
        return isStrictPartialOrder;
    }

    /**
     * Checks if the dependence relation is antisymmetric.
     * 
     * @param map The identity map to operate on.
     * @param set The document set to check.
     * @return A boolean value.
     * @throws SiteException when something went wrong.
     */
    protected boolean isAntisymmetric(DocumentFactory map, NodeSet set) throws SiteException {
        SiteNode[] resources = set.getNodes();
        boolean isAntisymmetric = true;
        for (int i = 0; i < resources.length; i++) {
            for (int j = i + 1; j < resources.length; j++) {
                if (requires(map, resources[i], resources[j])
                        && requires(map, resources[j], resources[i])
                        && !(resources[i] == resources[j])) {
                    isAntisymmetric = false;
                }
            }
        }
        return isAntisymmetric;
    }

    /**
     * Checks if the dependence relation is transitive.
     * 
     * @param map The identity map to operate on.
     * @param set The document set to check.
     * @return A boolean value.
     * @throws SiteException when something went wrong.
     */
    protected boolean isTransitive(DocumentFactory map, NodeSet set) throws SiteException {
        SiteNode[] resources = set.getNodes();
        boolean isTransitive = true;
        for (int i = 0; i < resources.length; i++) {
            for (int j = i + 1; j < resources.length; j++) {
                for (int k = j + 1; k < resources.length; k++) {
                    if (requires(map, resources[i], resources[j])
                            && requires(map, resources[j], resources[k])
                            && !requires(map, resources[i], resources[k])) {
                        isTransitive = false;
                    }
                }
            }
        }
        return isTransitive;
    }

    /**
     * Checks if the dependence relation is irreflexive.
     * 
     * @param map The identity map to operate on.
     * @param set The document set.
     * @return A boolean value
     * @throws SiteException
     */
    protected boolean isIrreflexive(DocumentFactory map, NodeSet set) throws SiteException {
        SiteNode[] resources = set.getNodes();
        boolean isIrreflexive = true;
        for (int i = 0; i < resources.length; i++) {
            if (requires(map, resources[i], resources[i])) {
                isIrreflexive = false;
            }
        }
        return isIrreflexive;
    }

    /**
     * Compares nodes according to the dependence relation.
     */
    public class NodeComparator implements Comparator {

        /**
         * Ctor.
         * 
         * @param map The identity map to operate on.
         */
        public NodeComparator(DocumentFactory map) {
            this.map = map;
        }

        private DocumentFactory map;

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object arg0, Object arg1) {
            int result = 0;
            if (arg0 instanceof SiteNode && arg1 instanceof SiteNode) {
                SiteNode doc1 = (SiteNode) arg0;
                SiteNode doc2 = (SiteNode) arg1;

                try {
                    if (AbstractSiteManager.this.requires(map, doc1, doc2)) {
                        result = 1;
                    }
                    else if (AbstractSiteManager.this.requires(map, doc2, doc1)) {
                        result = -1;
                    }
                } catch (SiteException e) {
                    throw new RuntimeException(e);
                }
            }
            return result;
        }
    }

}
