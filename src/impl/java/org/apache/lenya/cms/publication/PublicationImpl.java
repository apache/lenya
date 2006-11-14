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

package org.apache.lenya.cms.publication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private List allResourceTypes;

    protected String getFirstTemplateId() {
        String[] templateIds = getTemplateIds();
        if (templateIds.length > 0) {
            return templateIds[0];
        } else {
            return null;
        }
    }

    public String[] getResourceTypeNames() {
        if (this.allResourceTypes == null) {
            this.allResourceTypes = new ArrayList();
            this.allResourceTypes.addAll(Arrays.asList(this.delegate.getResourceTypeNames()));
            String templateId = getFirstTemplateId();
            if (templateId != null) {
                try {
                    Publication template = getFactory().getPublication(templateId);
                    String[] templateTypes = template.getResourceTypeNames();
                    this.allResourceTypes.addAll(Arrays.asList(templateTypes));
                } catch (PublicationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return (String[]) this.allResourceTypes.toArray(new String[this.allResourceTypes.size()]);
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
        String schema = this.delegate.getWorkflowSchema(resourceType);
        if (schema == null) {
            String templateId = getFirstTemplateId();
            try {
                Publication template = getFactory().getPublication(templateId);
                schema = template.getWorkflowSchema(resourceType);
            } catch (PublicationException e) {
                throw new RuntimeException(e);
            }
        }
        return schema;
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

    public String toString() {
        return getId();
    }

}
