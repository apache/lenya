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

/* $Id: UsecaseMenuTransformer.java 407248 2006-05-17 13:20:01Z andreas $  */

package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Role;
import org.apache.lenya.cms.ac.PolicyUtil;
import org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.UsecaseMessage;
import org.apache.lenya.cms.usecase.UsecaseResolver;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This transformer disables menu items (by removing the href attribute) which are not allowed with
 * respect to the usecase policies.
 */
public class UsecaseMenuTransformer extends AbstractSAXTransformer implements Disposable {

    /**
     * <code>MENU_ELEMENT</code> The menu element
     */
    public static final String MENU_ELEMENT = "menu";
    /**
     * The menu namespace.
     */
    public static final String MENU_NAMESPACE = "http://apache.org/cocoon/lenya/menubar/1.0";
    /**
     * <code>ITEM_ELEMENT</code> The item element
     */
    public static final String ITEM_ELEMENT = "item";
    /**
     * <code>USECASE_ATTRIBUTE</code> The usecase attribute
     */
    public static final String USECASE_ATTRIBUTE = "usecase";
    
    /**
     * The value of the <em>checked</em> attribute can either be <em>true</em> or <em>false</em>.
     */
    public static final String CHECKED_ATTRIBUTE = "checked";
    
    /**
     * Comment for <code>HREF_ATTRIBUTE</code> The href attribute
     */
    public static final String HREF_ATTRIBUTE = "href";
    /**
     * <code>NAMESPACE</code> The usecase namespace
     */
    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/usecase/1.0";

    /**
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String,
     *      java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String raw, Attributes attr)
            throws SAXException {

        AttributesImpl attributes = new AttributesImpl(attr);
        List messages = null;

        UsecaseResolver usecaseResolver = null;
        try {
            usecaseResolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);

            if (this.authorizer != null && localName.equals(ITEM_ELEMENT)) {
                String usecaseName = attr.getValue(NAMESPACE, USECASE_ATTRIBUTE);

                // filter item if usecase not allowed
                if (usecaseName != null) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Found usecase [" + usecaseName + "]");
                    }
                    if (!this.authorizer.authorizeUsecase(usecaseName,
                            this.roles,
                            this.publication)) {
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("Usecase not authorized");
                        }
                        removeHrefAttribute(attributes);
                        UsecaseMessage message = new UsecaseMessage("Access denied");
                        messages = Collections.singletonList(message);
                    }
                }

                if (usecaseResolver.isRegistered(this.sourceUrl, usecaseName)) {
                    Usecase usecase = null;
                    try {
                        usecase = usecaseResolver.resolve(this.sourceUrl, usecaseName);
                        usecase.setSourceURL(this.sourceUrl);
                        usecase.setName(usecaseName);
                        String hrefAttribute = attr.getValue(HREF_ATTRIBUTE);
                        if (hrefAttribute != null) {
                            passRequestParameters(usecase, hrefAttribute);
                        }
                        usecase.checkPreconditions();
                        if (usecase.hasErrors()) {
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("Usecase preconditions not complied");
                            }
                            removeHrefAttribute(attributes);
                            messages = usecase.getErrorMessages();
                        }
                        else if (hrefAttribute == null) {
                            attributes.addAttribute("", HREF_ATTRIBUTE, HREF_ATTRIBUTE, "CDATA", this.sourceUrl);
                        }
                        setItemState(attributes, usecase);
                    } finally {
                        if (usecase != null) {
                            usecaseResolver.release(usecase);
                        }
                    }
                }
            }
        } catch (final Exception e) {
            throw new SAXException(e);
        } finally {
            if (usecaseResolver != null) {
                this.manager.release(usecaseResolver);
            }
        }

        super.startElement(uri, localName, raw, attributes);

        if (messages != null) {
            addMessages(messages);
        }

    }

    protected void setItemState(AttributesImpl attributes, Usecase usecase) {
        Object itemState = usecase.getParameter(Usecase.PARAMETER_ITEM_STATE);
        if (itemState != null && itemState instanceof Boolean) {
            Boolean state = (Boolean) itemState;
            attributes.addAttribute("", CHECKED_ATTRIBUTE, CHECKED_ATTRIBUTE, "CDATA", state.toString());
        }
    }

    /**
     * Removes the <code>href</code> attribute.
     * 
     * @param attributes The attributes.
     */
    protected void removeHrefAttribute(AttributesImpl attributes) {
        int hrefIndex = attributes.getIndex(HREF_ATTRIBUTE);
        if (hrefIndex > -1) {
            attributes.removeAttribute(hrefIndex);
        }
    }

    protected void addMessages(List messages) throws SAXException {

        for (Iterator i = messages.iterator(); i.hasNext();) {
            UsecaseMessage message = (UsecaseMessage) i.next();
            super.startElement(MENU_NAMESPACE, "message", "message", new AttributesImpl());
            String messageString = message.getMessage();
            super.characters(messageString.toCharArray(), 0, messageString.length());
            if (message.hasParameters()) {
                String[] parameters = message.getParameters();
                for (int p = 0; p < parameters.length; p++) {
                    super.startElement(MENU_NAMESPACE,
                            "parameter",
                            "parameter",
                            new AttributesImpl());
                    super.characters(parameters[p].toCharArray(), 0, parameters[p].length());
                    super.endElement(MENU_NAMESPACE, "parameter", "parameter");
                }
            }
            super.endElement(MENU_NAMESPACE, "message", "message");
        }

    }

    /**
     * Pass the request parameters from the <code>href</code> attribute to the usecase handler.
     * 
     * @param usecase The usecase handler.
     * @param href The value of the <code>href</code> attribute.
     */
    void passRequestParameters(Usecase usecase, String href) {
        int questionMarkIndex = href.indexOf("?");
        if (questionMarkIndex > -1) {
            String queryString = href.substring(questionMarkIndex + 1);
            String[] nameValuePairs = queryString.split("&");
            for (int i = 0; i < nameValuePairs.length; i++) {
                String[] pair = nameValuePairs[i].split("=");
                if (pair.length == 2) {
                    String name = pair[0];
                    String value = pair[1];
                    usecase.setParameter(name, value);
                }
            }
        }
    }

    private UsecaseAuthorizer authorizer;
    private ServiceSelector serviceSelector = null;
    private Role[] roles;
    private Publication publication;
    private AccessControllerResolver acResolver;
    private String sourceUrl;

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver,
     *      java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver _resolver, Map _objectModel, String src, Parameters _parameters)
            throws ProcessingException, SAXException, IOException {

        super.setup(_resolver, _objectModel, src, _parameters);

        getLogger().debug("Setting up transformer");

        this.sourceUrl = ServletHelper.getWebappURI(this.request);

        try {
            this.roles = PolicyUtil.getRoles(this.request);
            this.publication = PublicationUtil.getPublication(this.manager, _objectModel);

            this.serviceSelector = (ServiceSelector) this.manager.lookup(AccessControllerResolver.ROLE
                    + "Selector");
            this.acResolver = (AccessControllerResolver) this.serviceSelector.select(AccessControllerResolver.DEFAULT_RESOLVER);
            getLogger().debug("Resolved AC resolver [" + this.acResolver + "]");

            String webappUrl = ServletHelper.getWebappURI(this.request);
            AccessController accessController = this.acResolver.resolveAccessController(webappUrl);

            Authorizer[] authorizers = accessController.getAuthorizers();
            for (int i = 0; i < authorizers.length; i++) {
                if (authorizers[i] instanceof UsecaseAuthorizer) {
                    this.authorizer = (UsecaseAuthorizer) authorizers[i];
                }
            }

            getLogger().debug("Using authorizer [" + this.authorizer + "]");
        } catch (final Exception e) {
            throw new ProcessingException(e);
        }

    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        getLogger().debug("Disposing transformer");
        if (this.serviceSelector != null) {
            if (this.acResolver != null) {
                this.serviceSelector.release(this.acResolver);
            }
            this.manager.release(this.serviceSelector);
        }
    }

    public void recycle() {
        super.recycle();
        this.publication = null;
        this.roles = null;
        this.serviceSelector = null;
        this.acResolver = null;
        this.authorizer = null;
        this.sourceUrl = null;
    }

}
