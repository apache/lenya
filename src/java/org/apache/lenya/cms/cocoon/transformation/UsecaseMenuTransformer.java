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

/* $Id: UsecaseMenuTransformer.java,v 1.13 2004/03/01 16:18:20 gregor Exp $  */

package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This transformer disables menu items (by removing the href attribute)
 * which are not allowed with respect to the usecase policies.
 */
public class UsecaseMenuTransformer extends AbstractSAXTransformer implements Disposable {

    public static final String MENU_ELEMENT = "menu";
    public static final String ITEM_ELEMENT = "item";
    public static final String USECASE_ATTRIBUTE = "usecase";
    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/usecase/1.0";

    /** (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String raw, Attributes attr)
        throws SAXException {

        Attributes attributes = attr;

        if (authorizer != null && localName.equals(ITEM_ELEMENT)) {
            String usecase = attr.getValue(NAMESPACE, USECASE_ATTRIBUTE);

            // filter item if usecase not allowed 
            if (usecase != null) {
                getLogger().debug("Found usecase [" + usecase + "]");

                try {
                    if (!authorizer.authorizeUsecase(usecase, roles, publication)) {
                        getLogger().debug("Usecase not authorized");
                        int hrefIndex = attributes.getIndex("href");
                        if (hrefIndex > -1) {
                            attributes = new AttributesImpl(attr);
                            ((AttributesImpl) attributes).removeAttribute(hrefIndex);
                        }
                    }
                } catch (AccessControlException e) {
                    throw new SAXException(e);
                }
            }
        }

        super.startElement(uri, localName, raw, attributes);

    }

    private UsecaseAuthorizer authorizer;
    private ServiceSelector serviceSelector = null;
    private Role[] roles;
    private Publication publication;
    private AccessControllerResolver acResolver;

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters)
        throws ProcessingException, SAXException, IOException {

        getLogger().debug("Setting up transformer");

        serviceSelector = null;
        acResolver = null;
        authorizer = null;

        Request request = ObjectModelHelper.getRequest(objectModel);

        try {
            roles = PolicyAuthorizer.getRoles(request);

            publication = PublicationFactory.getPublication(objectModel);

            serviceSelector =
                (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            acResolver =
                (AccessControllerResolver) serviceSelector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);
            getLogger().debug("Resolved AC resolver [" + acResolver + "]");

            String webappUrl = ServletHelper.getWebappURI(request);
            AccessController accessController = acResolver.resolveAccessController(webappUrl);

            if (accessController instanceof DefaultAccessController) {
                DefaultAccessController defaultAccessController =
                    (DefaultAccessController) accessController;
                Authorizer[] authorizers = defaultAccessController.getAuthorizers();
                for (int i = 0; i < authorizers.length; i++) {
                    if (authorizers[i] instanceof UsecaseAuthorizer) {
                        authorizer = (UsecaseAuthorizer) authorizers[i];
                    }
                }
            }

            getLogger().debug("Using authorizer [" + authorizer + "]");
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        getLogger().debug("Disposing transformer");
        if (serviceSelector != null) {
            if (acResolver != null) {
                serviceSelector.release(acResolver);
            }
            manager.release(serviceSelector);
        }
    }

}
