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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataManager;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.repo.Translation;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.Session;

/**
 * Repository node.
 */
public class RepoNode extends AbstractLogEnabled implements Node {

    private Translation translation;

    /**
     * @param url
     */
    public RepoNode(Translation trans, Logger logger) {
        this.translation = trans;
        ContainerUtil.enableLogging(this, logger);
    }

    public Translation getTranslation() {
        return this.translation;
    }

    public Session getSession() {
        throw new RuntimeException("not implemented");
    }

    public boolean exists() throws RepositoryException {
        return this.translation != null;
    }

    public boolean isCollection() throws RepositoryException {
        throw new RuntimeException("not implemented");
    }

    public Collection getChildren() throws RepositoryException {
        throw new RuntimeException("not implemented");
    }

    public InputStream getInputStream() throws RepositoryException {
        try {
            return getTranslation().getInputStream();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public OutputStream getOutputStream() throws RepositoryException {
        try {
            return getTranslation().getOutputStream();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public long getLastModified() throws RepositoryException {
        try {
            return getTranslation().getLastModified();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public long getContentLength() throws RepositoryException {
        try {
            return getTranslation().getContentLength();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public String getMimeType() throws RepositoryException {
        try {
            return getTranslation().getMimeType();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public String getSourceURI() {
        throw new RuntimeException("not implemented");
    }

    public void lock() throws RepositoryException {
        try {
            getTranslation().lock();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void unlock() throws RepositoryException {
        try {
            getTranslation().unlock();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void checkout() throws RepositoryException {
        try {
            getTranslation().checkout();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void checkin() throws RepositoryException {
        try {
            getTranslation().checkin();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void registerDirty() throws RepositoryException {
        throw new RuntimeException("not implemented");
    }

    public boolean isCheckedOut() throws RepositoryException {
        try {
            return getTranslation().isCheckedOut();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public boolean isCheckedOutByUser() throws RepositoryException {
        return isCheckedOut();
    }

    public boolean isLocked() throws RepositoryException {
        try {
            return getTranslation().isLocked();
        } catch (org.apache.lenya.cms.repo.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void registerRemoved() throws RepositoryException {
        throw new RuntimeException("not implemented");
    }

    private MetaDataManager metaDataManager;

    public MetaDataManager getMetaDataManager() throws DocumentException {
        if (this.metaDataManager == null) {
            org.apache.lenya.cms.repo.Session session;
            try {
                session = getTranslation().getAsset()
                        .getContent()
                        .getArea()
                        .getPublication()
                        .getSession();
            } catch (org.apache.lenya.cms.repo.RepositoryException e) {
                throw new DocumentException(e);
            }
            this.metaDataManager = new RepoMetaDataManager(session, getTranslation(), getLogger());
        }
        return this.metaDataManager;
    }

    public String getIdentifiableType() {
        return Node.IDENTIFIABLE_TYPE;
    }

    public MetaData getMetaData(String namespaceUri) throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getMetaDataNamespaceUris() throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

}
