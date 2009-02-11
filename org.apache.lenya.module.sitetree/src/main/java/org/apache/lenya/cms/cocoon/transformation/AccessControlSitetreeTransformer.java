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

/* $Id: AccessControlSitetreeTransformer.java 153561 2005-02-12 21:49:18Z gregor $  */

package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.Role;
import org.apache.lenya.cms.cocoon.generation.SitetreeFragmentGenerator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This transformer is applied to the sitetree. It marks the site element and all node elements the
 * current identity is not allowed to access with a <code>protected="true"</code> attribute.
 */
public class AccessControlSitetreeTransformer extends AbstractSAXTransformer {

    /**
     * <code>ATTRIBUTE_PROTECTED</code> The attribute for protected
     */
    public static final String ATTRIBUTE_PROTECTED = "protected";

    private PolicyManager policyManager;
    private AccessControllerResolver acResolver;
    private AccreditableManager accreditableManager;
    private Identity identity;

    private String pubId;
    private String area;
    private String basePath;
    private Stack pathElements = new Stack();

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver,
     *      java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver _resolver, Map _objectModel, String src, Parameters par)
            throws ProcessingException, SAXException, IOException {
        super.setup(_resolver, _objectModel, src, par);

        this.acResolver = null;
        this.policyManager = null;

        this.identity = Identity.getIdentity(this.request.getSession(false));

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Setting up transformer");
            getLogger().debug("    Identity:       [" + this.identity + "]");
        }

        this.acResolver = (AccessControllerResolver) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(AccessControllerResolver.ROLE);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("    Resolved AC resolver [" + this.acResolver + "]");
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    /**
     * (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String,
     *      java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String raw, Attributes attr)
            throws SAXException {

        Attributes attributes = attr;

        if (isFragmentElement(uri, localName)) {
            this.pubId = attr.getValue(SitetreeFragmentGenerator.ATTR_PUBLICATION);
            assert this.pubId != null;

            String area = attr.getValue("area");
            if (area != null) {
                this.area = area;
            }

            String basePath = attr.getValue(SitetreeFragmentGenerator.ATTR_BASE);
            this.basePath = basePath == null ? "" : basePath;

            try {
                AccessController accessController = this.acResolver.resolveAccessController("/"
                        + this.pubId + "/");
                this.accreditableManager = accessController.getAccreditableManager();
                this.policyManager = accessController.getPolicyManager();
            } catch (AccessControlException e) {
                throw new SAXException(e);
            }

        } else if (isSiteElement(uri, localName)) {
            this.area = attr.getValue("area");
            assert this.area != null;
        } else if (isNodeElement(uri, localName)) {
            String id = attr.getValue(SitetreeFragmentGenerator.ATTR_ID);
            assert id != null;
            this.pathElements.push(id);

            try {
                Role[] roles = this.policyManager.getGrantedRoles(this.accreditableManager,
                        this.identity, getUrl());
                if (roles.length == 0 || roles.length == 1 && roles[0].getId().equals("session")) {
                    AttributesImpl attributesImpl = new AttributesImpl(attributes);
                    attributesImpl.addAttribute("", ATTRIBUTE_PROTECTED, ATTRIBUTE_PROTECTED, "",
                            Boolean.toString(true));
                    attributes = attributesImpl;
                }
            } catch (AccessControlException e) {
                throw new SAXException(e);
            }
        }

        super.startElement(uri, localName, raw, attributes);
    }

    protected String getUrl() {
        assert this.pubId != null;
        assert this.area != null;
        assert this.basePath != null;
        StringBuffer path = new StringBuffer();
        for (Iterator i = this.pathElements.iterator(); i.hasNext();) {
            path.append("/").append(i.next());
        }
        return "/" + this.pubId + "/" + this.area + this.basePath + path.toString();
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public void endElement(String uri, String localName, String raw) throws SAXException {
        if (isNodeElement(uri, localName)) {
            this.pathElements.pop();
        }
        super.endElement(uri, localName, raw);
    }

    /**
     * Returns if an element represents a sitetree node.
     * @param uri The namespace URI.
     * @param localName The local name.
     * @return A boolean value.
     */
    protected boolean isNodeElement(String uri, String localName) {
        return uri.equals(SitetreeFragmentGenerator.URI)
                && localName.equals(SitetreeFragmentGenerator.NODE_NODE);
    }

    /**
     * Returns if an element represents a fragment node.
     * @param uri The namespace URI.
     * @param localName The local name.
     * @return A boolean value.
     */
    protected boolean isFragmentElement(String uri, String localName) {
        return uri.equals(SitetreeFragmentGenerator.URI)
                && localName.equals(SitetreeFragmentGenerator.NODE_FRAGMENT);
    }

    /**
     * Returns if an element represents a site node.
     * @param uri The namespace URI.
     * @param localName The local name.
     * @return A boolean value.
     */
    protected boolean isSiteElement(String uri, String localName) {
        return uri.equals(SitetreeFragmentGenerator.URI)
                && localName.equals(SitetreeFragmentGenerator.NODE_SITE);
    }

    public void recycle() {
        super.recycle();
        this.pubId = null;
        this.area = null;
        this.pathElements.clear();
    }

}
