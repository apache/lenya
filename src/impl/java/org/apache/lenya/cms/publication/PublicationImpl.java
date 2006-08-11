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

package org.apache.lenya.cms.publication;

import java.io.File;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * A publication.
 * @version $Id$
 */
public class PublicationImpl extends AbstractLogEnabled implements Publication {

    private PublicationConfiguration delegate;
    protected ServiceManager manager;
    private DocumentFactory factory;

    protected PublicationImpl(ServiceManager manager, DocumentFactory factory,
            PublicationConfiguration delegate) {
        this.delegate = delegate;
        this.manager = manager;
        this.factory = factory;
    }

    public boolean exists() {
        return delegate.exists();
    }

    public String getBreadcrumbPrefix() {
        return delegate.getBreadcrumbPrefix();
    }

    public String getContentDir() {
        return delegate.getContentDir();
    }

    public File getContentDirectory(String area) {
        return delegate.getContentDirectory(area);
    }

    public String getContentURI(String area) {
        return delegate.getContentURI(area);
    }

    public String getDefaultLanguage() {
        return delegate.getDefaultLanguage();
    }

    public File getDirectory() {
        return delegate.getDirectory();
    }

    public String getDocumentBuilderHint() {
        return delegate.getDocumentBuilderHint();
    }

    public String getId() {
        return delegate.getId();
    }

    public String getInstantiatorHint() {
        return delegate.getInstantiatorHint();
    }

    public String[] getLanguages() {
        return delegate.getLanguages();
    }

    public DocumentIdToPathMapper getPathMapper() {
        return delegate.getPathMapper();
    }

    public Proxy getProxy(Document document, boolean isSslProtected) {
        return delegate.getProxy(document, isSslProtected);
    }

    public Proxy getProxy(String area, boolean isSslProtected) {
        return delegate.getProxy(area, isSslProtected);
    }

    public String[] getResourceTypeNames() {
        return delegate.getResourceTypeNames();
    }

    public File getServletContext() {
        return delegate.getServletContext();
    }

    public String getSiteManagerHint() {
        return delegate.getSiteManagerHint();
    }

    public String getSourceURI() {
        return delegate.getSourceURI();
    }

    public String[] getTemplateIds() {
        return delegate.getTemplateIds();
    }

    public String getWorkflowSchema(ResourceType resourceType) {
        return delegate.getWorkflowSchema(resourceType);
    }

    public void setDefaultLanguage(String language) {
        delegate.setDefaultLanguage(language);
    }

    public void setPathMapper(DefaultDocumentIdToPathMapper mapper) {
        delegate.setPathMapper(mapper);
    }

    public Area getArea(String name) throws PublicationException {
        return new AreaImpl(this.manager, this.factory, this, name);
    }

    public String[] getAreaNames() {
        return delegate.getAreaNames();
    }

    public DocumentFactory getFactory() {
        return this.factory;
    }

    public boolean equals(Object obj) {
        if (!getClass().isInstance(obj)) {
            return false;
        }
        return ((Publication) obj).getId().equals(getId());
    }

    public int hashCode() {
        return getId().hashCode();
    }

}
