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

/* $Id: SitemapPolicyManager.java,v 1.3 2004/03/08 16:48:20 gregor Exp $  */

package org.apache.lenya.cms.ac;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.impl.PolicyBuilder;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;

public class SitemapPolicyManager
    extends AbstractLogEnabled
    implements PolicyManager, Serviceable {

    /**
     * @see org.apache.lenya.ac.PolicyManager#getPolicy(org.apache.lenya.ac.AccreditableManager, java.lang.String)
     */
    public Policy getPolicy(AccreditableManager accreditableManager, String url)
        throws AccessControlException {

        url = url.substring(1);

        int slashIndex = url.indexOf("/");
        if (slashIndex == -1) {
            slashIndex = url.length();
        }

        String publicationId = url.substring(0, slashIndex);
        url = url.substring(publicationId.length());

        SourceResolver resolver = null;
        Policy policy = null;
        Source source = null;
        try {
            resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);

            String policyUrl = publicationId + "/policies" + url + ".acml";
            getLogger().debug("Policy URL: " + policyUrl);
            source = resolver.resolveURI("cocoon://" + policyUrl);
            Document document = DocumentHelper.readDocument(source.getInputStream());
            policy = new PolicyBuilder(accreditableManager).buildPolicy(document);

        } catch (Exception e) {
            throw new AccessControlException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                getManager().release(resolver);
            }
        }
        return policy;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * Returns the service manager.
     * @return A service manager.
     */
    public ServiceManager getManager() {
        return manager;
    }

    /**
     * @see org.apache.lenya.ac.PolicyManager#accreditableRemoved(org.apache.lenya.ac.AccreditableManager, org.apache.lenya.ac.Accreditable)
     */
    public void accreditableRemoved(AccreditableManager manager, Accreditable accreditable) throws AccessControlException {
        // TODO Auto-generated method stub
        
    }

}
