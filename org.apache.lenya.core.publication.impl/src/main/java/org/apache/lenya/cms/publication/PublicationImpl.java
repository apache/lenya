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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.RepositoryItem;

/**
 * A publication.
 */
public class PublicationImpl extends AbstractLogEnabled implements Publication, RepositoryItem {

    private PublicationConfiguration delegate;
    private NodeFactory nodeFactory;
    //private Session session;
    //florent : TODO : bean wiring for pubmanager
    private PublicationManager pubmanager;

    protected PublicationImpl(Session session, NodeFactory nodeFactory,
            PublicationConfiguration delegate) {
        this.delegate = delegate;
        this.session = session;
        this.nodeFactory = nodeFactory;
    }

    public boolean exists() {
        return delegate.exists();
    }

    public String getBreadcrumbPrefix() {
        return delegate.getBreadcrumbPrefix();
    }

    public String getContentUri() {
        return delegate.getContentUri();
    }

    public String getContentUri(String area) {
        return delegate.getContentUri(area);
    }

    public String getDefaultLanguage() {
        return delegate.getDefaultLanguage();
    }

    private DocumentBuilder documentBuilder;

    public DocumentBuilder getDocumentBuilder() {
        if (this.documentBuilder == null) {
            try {
                this.documentBuilder = (DocumentBuilder) WebAppContextUtils
                        .getCurrentWebApplicationContext().getBean(
                                DocumentBuilder.class.getName() + "/"
                                        + delegate.getDocumentBuilderHint());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.documentBuilder;
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
        return getProxy(document.getArea(), isSslProtected);
    }

    public Proxy getProxy(String area, boolean isSslProtected) {
        return delegate.getProxy(area, isSslProtected);
    }

    private String contextPath;

    protected String getContextPath() {
        if (this.contextPath == null) {
            ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                    .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
            this.contextPath = process.getRequest().getContextPath();
        }
        return this.contextPath;
    }

    private List allResourceTypes;

    public String[] getResourceTypeNames() {
        if (this.allResourceTypes == null) {
            this.allResourceTypes = new ArrayList();
            this.allResourceTypes.addAll(Arrays.asList(this.delegate.getResourceTypeNames()));
            String templateId = getTemplateId();
            if (templateId != null) {
                Publication template = getSession().getPublication(templateId);
                String[] templateTypes = template.getResourceTypeNames();
                this.allResourceTypes.addAll(Arrays.asList(templateTypes));
            }
        }
        return (String[]) this.allResourceTypes.toArray(new String[this.allResourceTypes.size()]);
    }

    public String getPubBaseUri() {
        return delegate.getPubBaseUri();
    }

    public String getSiteManagerHint() {
        return delegate.getSiteManagerHint();
    }

    public String getSourceUri() {
        return delegate.getSourceUri();
    }

    public String getTemplateId() {
        return delegate.getTemplateId();
    }

    public String getWorkflowSchema(ResourceType resourceType) {
        String schema = this.delegate.getWorkflowSchema(resourceType);
        if (schema == null && getTemplateId() != null) {
            String templateId = getTemplateId();
            //Publication template = getSession().getPublication(templateId);
            Publication template = pubmanager.getPublication(templateId);
            schema = template.getWorkflowSchema(resourceType);
        }
        return schema;
    }

    public void setDefaultLanguage(String language) {
        delegate.setDefaultLanguage(language);
    }

    //florent : to remove dependencie with document-impl 
    //public void setPathMapper(DefaultDocumentIdToPathMapper mapper) {
    public void setPathMapper(DocumentIdToPathMapper mapper) {
        delegate.setPathMapper(mapper);
    }

    private Map areas = new HashMap();

    public Area getArea(String name) {
        if (!this.areas.containsKey(name)) {
            Area area = new AreaImpl(this.session, this.nodeFactory, this, name);
            this.areas.put(name, area);
        }
        return (Area) this.areas.get(name);
    }

    public String[] getAreaNames() {
        return delegate.getAreaNames();
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

    public String getName() {
        return delegate.getName();
    }

    public org.apache.lenya.cms.repository.Session getRepositorySession() {
        return (org.apache.lenya.cms.repository.Session) getSession();
    }

    public void addLanguage(String language) {
        this.delegate.addLanguage(language);
    }

    public String[] getModuleNames() {
        return this.delegate.getModuleNames();
    }

    public void removeLanguage(String language) {
        this.delegate.removeLanguage(language);
    }

    public void saveConfiguration() {
        this.delegate.saveConfiguration();
    }

    public void setName(String name) {
        this.delegate.setName(name);
    }

    public org.apache.lenya.cms.publication.Session getSession() {
        return this.session;
    }

}
