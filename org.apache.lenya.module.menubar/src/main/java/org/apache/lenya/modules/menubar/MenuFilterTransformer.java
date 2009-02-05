/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.modules.menubar;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.UsecaseResolver;
import org.apache.lenya.cms.usecase.gui.Tab;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Filter menu elements (blocks, items, ...) according to the attributes
 * <em>areas</em> and <em>resourceTypes</em>.
 */
public class MenuFilterTransformer extends AbstractSAXTransformer {

    protected static final String NAMESPACE = "http://apache.org/cocoon/lenya/menubar/1.0";
    private static final String ATTR_AREAS = "areas";
    private static final String ATTR_RESOURCE_TYPES = "resourceTypes";
    private Set attributeHandlers;

    public MenuFilterTransformer() {
        this.defaultNamespaceURI = NAMESPACE;
    }

    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters params)
            throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, params);

        this.attributeHandlers = new HashSet();

        Request request = ObjectModelHelper.getRequest(objectModel);
        String webappUri = ServletHelper.getWebappURI(request);

        String tabGroup = getTabGroup(request);
        URLInformation url = new URLInformation(webappUri);
        String area = tabGroup != null ? tabGroup : url.getArea();

        this.attributeHandlers.add(new AttributeHandler(ATTR_AREAS, area));
        
        try {
            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            String resourceType = factory.isDocument(webappUri) ?
                resourceType = factory.getFromURL(webappUri).getResourceType().getName()
                : null;
            this.attributeHandlers.add(new AttributeHandler(ATTR_RESOURCE_TYPES, resourceType));
        } catch (RepositoryException e) {
            throw new ProcessingException(e);
        }

    }

    protected String getTabGroup(Request request) throws ProcessingException {
        String webappUri = ServletHelper.getWebappURI(request);
        String tabGroup = null;
        String usecaseName = request.getParameter("lenya.usecase");
        if (usecaseName != null) {
            UsecaseResolver usecaseResolver = null;
            Usecase usecase = null;
            try {
                usecaseResolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
                usecase = usecaseResolver.resolve(webappUri, usecaseName);
                if (usecase.getView() != null) {
                    Tab tab = usecase.getView().getTab();
                    if (tab != null) {
                        tabGroup = tab.getGroup();
                    }
                }
            } catch (ServiceException e) {
                throw new ProcessingException(e);
            } finally {
                if (usecaseResolver != null) {
                    if (usecase != null) {
                        try {
                            usecaseResolver.release(usecase);
                        } catch (ServiceException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    this.manager.release(usecaseResolver);
                }
            }
        }
        return tabGroup;
    }

    public void startTransformingElement(String uri, String name, String raw, Attributes attr)
            throws ProcessingException, IOException, SAXException {

        if (this.ignoreEventsCount > 0) {
            this.ignoreEventsCount++;
        } else {
            boolean matches = true;
            for (Iterator i = this.attributeHandlers.iterator(); i.hasNext();) {
                AttributeHandler handler = (AttributeHandler) i.next();
                matches = matches && handler.matches(attr);
            }
            if (matches) {
                super.startTransformingElement(uri, name, raw, attr);
            } else {
                this.ignoreEventsCount++;
            }
        }
    }

    public void endTransformingElement(String uri, String name, String raw)
            throws ProcessingException, IOException, SAXException {
        if (this.ignoreEventsCount > 0) {
            this.ignoreEventsCount--;
        } else {
            super.endTransformingElement(uri, name, raw);
        }
    }

    /**
     * Searches an attribute value (space-separated list) for a certain value.
     */
    protected static class AttributeHandler {

        protected static final String DELIMITER = " ";
        private String attributeName;
        private String value;

        protected AttributeHandler(String attributeName, String value) {
            this.attributeName = attributeName;
            this.value = value;
        }

        protected boolean matches(Attributes attr) {
            String attrValue = attr.getValue(this.attributeName);
            if (attrValue == null) {
                return true;
            } else if (this.value != null) {
                StringTokenizer tokens = new StringTokenizer(attrValue, DELIMITER);
                while (tokens.hasMoreTokens()) {
                    if (tokens.nextToken().equals(this.value)) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

}
