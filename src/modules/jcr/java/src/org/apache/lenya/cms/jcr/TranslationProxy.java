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
package org.apache.lenya.cms.jcr;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.lenya.cms.jcr.mapping.AbstractNodeProxy;
import org.apache.lenya.cms.jcr.mapping.Path;
import org.apache.lenya.cms.jcr.mapping.PathElement;
import org.apache.lenya.cms.jcr.metadata.MetaDataProxy;
import org.apache.lenya.cms.repo.Asset;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Translation;
import org.apache.lenya.cms.repo.metadata.MetaData;

/**
 * Document proxy.
 */
public class TranslationProxy extends AbstractNodeProxy implements Translation {

    protected static final String NODE_NAME = "lenya:translation";
    protected static final String NODE_TYPE = "lnt:translation";
    protected static final String LANGUAGE_PROPERTY = "xml:lang";
    protected static final String LABEL_PROPERTY = "lenya:label";

    public void setLabel(String label) throws RepositoryException {
        setProperty(LABEL_PROPERTY, label);
    }

    public String getLanguage() throws RepositoryException {
        return getPropertyString(LANGUAGE_PROPERTY);
    }

    public String getLabel() throws RepositoryException {
        return getPropertyString(LABEL_PROPERTY);
    }

    protected Path getXPath() throws RepositoryException {
        return TranslationProxy.getPath((AssetProxy) getAsset(), getLanguage());
    }

    protected static Path getPath(AssetProxy assetProxy, String language)
            throws RepositoryException {
        return assetProxy.getAbsolutePath().append(getPathElement(NODE_NAME,
                LANGUAGE_PROPERTY,
                language));
    }

    protected ResourceProxy getResourceProxy() throws RepositoryException {
        Path path = getAbsolutePath().append(getPathElement(ResourceProxy.NODE_NAME));
        return (ResourceProxy) getRepository().getProxy(path);
    }

    public InputStream getInputStream() throws RepositoryException {
        return getResourceProxy().getInputStream();
    }

    public OutputStream getOutputStream() throws RepositoryException {
        return getResourceProxy().getOutputStream();
    }

    public long getContentLength() throws RepositoryException {
        return getResourceProxy().getContentLength();
    }

    public long getLastModified() throws RepositoryException {
        return getResourceProxy().getLastModified();
    }

    public Asset getAsset() throws RepositoryException {
        return (AssetProxy) getParentProxy();
    }

    public PathElement getPathElement() throws RepositoryException {
        return getPathElement(NODE_NAME, LANGUAGE_PROPERTY, getLanguage());
    }

    public MetaData getMetaData(String elementSet) throws RepositoryException {
        Path path = getAbsolutePath().append(getPathElement(MetaDataProxy.NODE_NAME,
                MetaDataProxy.ELEMENT_SET_PROPERTY,
                elementSet));
        if (getRepository().containsProxy(path)) {
            return (MetaData) getRepository().getProxy(path);
        } else {
            return (MetaData) getRepository().addByProperty(getAbsolutePath(),
                    MetaDataProxy.NODE_TYPE,
                    MetaDataProxy.class.getName(),
                    MetaDataProxy.NODE_NAME,
                    MetaDataProxy.ELEMENT_SET_PROPERTY,
                    elementSet);
        }
    }

    public String getMimeType() throws RepositoryException {
        return getResourceProxy().getMimeType();
    }

    public void checkin() throws RepositoryException {
        try {
            getNode().checkin();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void checkout() throws RepositoryException {
        try {
            getNode().checkout();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public boolean isCheckedOut() throws RepositoryException {
        try {
            return getNode().isCheckedOut();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void lock() throws RepositoryException {
        try {
            // deep, sessionScoped
            getNode().lock(true, true);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public void unlock() throws RepositoryException {
        try {
            getNode().unlock();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public boolean isLocked() throws RepositoryException {
        try {
            return getNode().isLocked();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

}
