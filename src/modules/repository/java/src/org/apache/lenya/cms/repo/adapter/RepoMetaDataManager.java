/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.repo.adapter;

import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataManager;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.Translation;
import org.apache.lenya.cms.repo.metadata.Element;
import org.apache.lenya.cms.repo.metadata.MetaDataOwner;
import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;

public class RepoMetaDataManager extends MetaDataManager {

    private Session session;
    private MetaDataOwner owner;

    public RepoMetaDataManager(Session session, MetaDataOwner owner, Logger _logger) {
        super(null, null, _logger);
        this.session = session;
        this.owner = owner;
    }

    protected void register(String name, Element[] elements) throws DocumentException {
        try {
            MetaDataRegistry registry = this.session.getRepository().getMetaDataRegistry();
            if (!registry.isRegistered(name)) {
                registry.register(name, elements);
            }
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    public MetaData getCustomMetaData() throws DocumentException {
        throw new RuntimeException("not implemented");
    }

    private RepoMetaData dublinCore = null;

    public MetaData getDublinCoreMetaData() throws DocumentException {
        try {
            register(DublinCoreElements.ELEMENT_SET, DublinCoreElements.getElements());
            if (this.dublinCore == null) {
                this.dublinCore = new RepoMetaData(this.owner.getMetaData(DublinCoreElements.ELEMENT_SET));
            }
            return this.dublinCore;
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    private RepoMetaData lenyaMetaData = null;

    public MetaData getLenyaMetaData() throws DocumentException {
        register(LenyaElements.ELEMENT_SET, LenyaElements.ELEMENTS);
        try {
            if (this.lenyaMetaData == null) {
                this.lenyaMetaData = new RepoLenyaMetaData(this.owner.getMetaData(LenyaElements.ELEMENT_SET),
                        (Translation) this.owner);
            }
            return this.lenyaMetaData;
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    protected String getSourceURI() {
        throw new RuntimeException("not implemented");
    }

    public void replaceMetaData(MetaDataManager sourceManager) throws DocumentException {
        throw new RuntimeException("not implemented");
    }

    public void setCustomMetaData(Map customMetaDataValues) throws DocumentException {
        throw new RuntimeException("not implemented");
    }

    public void setDublinCoreMetaData(Map dcMetaDataValues) throws DocumentException {
        throw new RuntimeException("not implemented");
    }

    public void setLenyaMetaData(Map lenyaMetaDataValues) throws DocumentException {
        throw new RuntimeException("not implemented");
    }

    public void setMetaData(Map dcMetaData, Map lenyaMetaData, Map customMetaData)
            throws DocumentException {
        throw new RuntimeException("not implemented");
    }

}
