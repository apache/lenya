/*
$Id: UsecaseMenuTransformer.java,v 1.1 2003/08/05 16:27:41 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac2.Authorizer;
import org.apache.lenya.cms.ac2.usecase.UsecaseAuthorizer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This transformer disables menu items (by removing the href attribute)
 * which are not allowed with respect to the usecase policies.
 * 
 * @author andreas
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

        if (localName.equals(ITEM_ELEMENT)) {
            String usecase = attr.getValue(NAMESPACE, USECASE_ATTRIBUTE);

            // filter item if usecase not allowed 
            if (usecase != null) {
                getLogger().debug("Found usecase [" + usecase + "]");

                try {
                    if (!authorizer.authorizeUsecase(webappUrl, usecase)) {
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
    private ComponentSelector selector = null;
    private String webappUrl;

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters)
        throws ProcessingException, SAXException, IOException {

        getLogger().debug("Setting up transformer");

        ComponentSelector selector = null;
        authorizer = null;

        Request request = ObjectModelHelper.getRequest(objectModel);

        try {
            selector = (ComponentSelector) manager.lookup(Authorizer.ROLE + "Selector");
            authorizer = (UsecaseAuthorizer) selector.select(UsecaseAuthorizer.TYPE);
            getLogger().debug("Using authorizer [" + authorizer + "]");
            authorizer.setup(request);
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        getLogger().debug("Disposing transformer");
        if (selector != null) {
            if (authorizer != null) {
                selector.release(authorizer);
            }
            manager.release(selector);
        }
    }

}
