/*
$Id: UsecaseAuthorizer.java,v 1.4 2003/08/05 11:59:33 andreas Exp $
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

package org.apache.lenya.cms.ac2.usecase;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac2.AccessController;
import org.apache.lenya.cms.ac2.AccreditableManager;
import org.apache.lenya.cms.ac2.Authorizer;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.ac2.PolicyManager;
import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class UsecaseAuthorizer
    extends AbstractLogEnabled
    implements Authorizer, Configurable, Serviceable {

    public static final String USECASE_PARAMETER = "lenya.usecase";

    /**
     * @see org.apache.lenya.cms.ac2.Authorizer#authorize(org.apache.lenya.cms.ac2.AccreditableManager, org.apache.lenya.cms.ac2.PolicyManager, org.apache.lenya.cms.ac2.Identity, org.apache.cocoon.environment.Request)
     */
    public boolean authorize(
        AccreditableManager accreditableManager,
        PolicyManager policyManager,
        Identity identity,
        Request request)
        throws AccessControlException {

        String usecase = request.getParameter(USECASE_PARAMETER);
        boolean authorized = true;

        if (usecase != null) {

            getLogger().debug("Authorizing usecase [" + usecase + "]");

            if (usecaseToRoles.containsKey(usecase)) {

                getLogger().debug("Roles for usecase found.");

                Set usecaseRoles = getRoleIDs(usecase);

                String url = ServletHelper.getWebappURI(request);
                Role[] roles = policyManager.getPolicy(accreditableManager, url).getRoles(identity);

                int i = 0;
                authorized = false;
                while (!authorized && i < roles.length) {
                    authorized = usecaseRoles.contains(roles[i].getId());
                    getLogger().debug(
                        "Authorization for role ["
                            + roles[i].getId()
                            + "] is ["
                            + authorized
                            + "]");
                    i++;
                }
            } else {
                getLogger().debug("No roles for usecase found. Granting access.");
            }
        } else {
            getLogger().debug("No usecase to authorize. Granting access.");
        }

        return authorized;
    }

    private String configurationPath;

    protected static final String FILE_ELEMENT = "config-file";
    protected static final String SRC_ATTRIBUTE = "src";
    protected static final String USECASES_ELEMENT = "usecases";
    protected static final String USECASE_ELEMENT = "usecase";
    protected static final String ROLE_ELEMENT = "role";
    protected static final String ID_ATTRIBUTE = "id";

    // maps usecase IDs to Sets of role IDs
    private Map usecaseToRoles = new HashMap();

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {

        getLogger().debug("Configuring");
        Configuration fileConfig = config.getChild(FILE_ELEMENT, false);

        if (fileConfig == null) {
            getLogger().debug("No configuration file provided.");
        } else {
            getLogger().debug("Configuration file provided.");
            configurationPath = fileConfig.getAttribute(SRC_ATTRIBUTE);

            SourceResolver resolver = null;
            Source source = null;

            try {
                resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
                resolver.resolveURI(configurationPath);

                Document document = null;
                    document = DocumentHelper.readDocument(source.getInputStream());
                assert document.getDocumentElement().getLocalName().equals(USECASES_ELEMENT);
                
                NamespaceHelper helper =
                    new NamespaceHelper(
                        AccessController.NAMESPACE,
                        AccessController.DEFAULT_PREFIX,
                        document);

                Element[] usecaseElements =
                    helper.getChildren(document.getDocumentElement(), USECASE_ELEMENT);
                for (int i = 0; i < usecaseElements.length; i++) {
                    String usecaseId = usecaseElements[i].getAttribute(ID_ATTRIBUTE);
                    getLogger().debug("Found usecase [" + usecaseId + "]");
                    Element[] roleElements = helper.getChildren(usecaseElements[i], ROLE_ELEMENT);
                    Set roleIds = new HashSet();
                    for (int j = 0; j < roleElements.length; j++) {
                        String roleId = roleElements[i].getAttribute(ID_ATTRIBUTE);
                        roleIds.add(roleId);
                        getLogger().debug("Adding role [" + roleId + "]");
                    }
                    usecaseToRoles.put(usecaseId, roleIds);
                }

            } catch (Exception e) {
                throw new ConfigurationException("Building usecase role configuration failed: ", e);
            } finally {
                if (resolver != null) {
                    if (source != null) {
                        resolver.release(source);
                    }
                    manager.release(resolver);
                }
            }
        }
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * Returns the role names that are allowed to execute a certain usecase.
     * @param usecaseId The usecase ID.
     * @return A set.
     */
    protected Set getRoleIDs(String usecaseId) {
        Set usecaseRoles;
        if (usecaseToRoles.containsKey(usecaseId)) {
            usecaseRoles = (Set) usecaseToRoles.get(usecaseId);
        } else {
            usecaseRoles = Collections.EMPTY_SET;
        }
        return usecaseRoles;
    }

}
