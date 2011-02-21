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
package org.apache.lenya.cms.site.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
//florent import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.utils.URLInformation;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Empty the trash.
 * 
 * @version $Id$
 */
public class EmptyTrash extends AbstractUsecase {

    protected static final String DOCUMENTS = "documents";

    private DocumentManager documentManager;

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        try {
            Document[] documents = getTrashDocuments();
            setParameter(DOCUMENTS, Arrays.asList(documents));
        } catch (SiteException e) {
            throw new RuntimeException(e);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lock the following objects:
     * <ul>
     * <li>all involved documents in the trash area</li>
     * <li>the document trash site structure</li>
     * </ul>
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        List nodes = new ArrayList();
        try {
            //florent String pubId = new URLInformation(getSourceURL()).getPublicationId();
        	String pubId = new URLInformation().getPublicationId();
            Publication publication = getSession().getPublication(pubId);
            Document[] docs = getTrashDocuments();
            for (int i = 0; i < docs.length; i++) {
                nodes.add(docs[i]);
            }
            SiteStructure structure = publication.getArea(Publication.TRASH_AREA).getSite();
            nodes.add(structure);
        } catch (Exception e) {
            throw new UsecaseException(e);
        }
        return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        Document[] documents = getTrashDocuments();
        DocumentSet set = new DocumentSet(documents);
        getDocumentManager().delete(set);
    }

    /**
     * @return The documents in the trash area.
     * @throws PublicationException if an error occurs.
     * @throws SiteException if an error occurs.
     */
    protected Document[] getTrashDocuments() throws PublicationException, SiteException {
        //florent String pubId = new URLInformation(getSourceURL()).getPublicationId();
    	String pubId = new URLInformation().getPublicationId();
        Publication pub = getSession().getPublication(pubId);
        return pub.getArea(Publication.TRASH_AREA).getDocuments();
    }

    protected DocumentManager getDocumentManager() {
        return documentManager;
    }

    /**
     * TODO: Bean wiring
     */
    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

}
