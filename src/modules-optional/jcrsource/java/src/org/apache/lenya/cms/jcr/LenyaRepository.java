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
package org.apache.lenya.cms.jcr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.PropDef;
import org.apache.jackrabbit.core.nodetype.PropDefImpl;
import org.apache.jackrabbit.name.QName;
import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreImpl;

/**
 * Lenya-specific repository implementation.
 */
public class LenyaRepository extends org.apache.cocoon.jcr.JackrabbitRepository {

    protected static final String CONTENT_NODE = "contentNode";

    protected static final String SESSION_ATTRIBUTE = javax.jcr.Session.class.getName();

    private Map namespaces = new HashMap();

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);

        Configuration[] namespaceConfigs = config.getChildren("namespace");
        for (int i = 0; i < namespaceConfigs.length; i++) {
            String prefix = namespaceConfigs[i].getAttribute("prefix");
            String uri = namespaceConfigs[i].getAttribute("uri");
            this.namespaces.put(prefix, uri);
        }
    }

    /**
     * @see javax.jcr.Repository#login()
     */
    public javax.jcr.Session login() throws LoginException, NoSuchWorkspaceException,
            RepositoryException {

        javax.jcr.Session jcrSession = null;

        Map objectModel = ContextHelper.getObjectModel(this.context);
        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession(false);
        if (session != null) {
            jcrSession = (javax.jcr.Session) session.getAttribute(SESSION_ATTRIBUTE);
            if (jcrSession == null) {
                jcrSession = super.login();
                session.setAttribute(SESSION_ATTRIBUTE, jcrSession);

                registerNamespaces(jcrSession);
                try {
                    registerNodeTypes(jcrSession);
                } catch (InvalidNodeTypeDefException e) {
                    throw new RepositoryException(e);
                }
            }
        }

        return jcrSession;
    }

    /**
     * Registers the Lenya-specific namespaces at the JCR workspace.
     * @param jcrSession The JCR session.
     * @throws RepositoryException if an error occurs.
     * @throws NamespaceException if an error occurs.
     * @throws UnsupportedRepositoryOperationException if an error occurs.
     * @throws AccessDeniedException if an error occurs.
     */
    protected void registerNamespaces(javax.jcr.Session jcrSession) throws RepositoryException,
            NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException {
        for (Iterator i = this.namespaces.keySet().iterator(); i.hasNext();) {
            String prefix = (String) i.next();
            String uri = (String) this.namespaces.get(prefix);
            NamespaceRegistry registry = jcrSession.getWorkspace().getNamespaceRegistry();
            if (!Arrays.asList(registry.getPrefixes()).contains(prefix)) {
                registry.registerNamespace(prefix, uri);
            }
        }
    }

    protected void registerNodeTypes(javax.jcr.Session jcrSession) throws RepositoryException,
            InvalidNodeTypeDefException {

        NodeTypeManagerImpl nodeTypeManager = (NodeTypeManagerImpl) jcrSession.getWorkspace()
                .getNodeTypeManager();
        NodeTypeRegistry registry = nodeTypeManager.getNodeTypeRegistry();
        if (!registry.isRegistered(new QName(LenyaMetaData.NAMESPACE, CONTENT_NODE))) {

            List propDefs = new ArrayList();
            Map key2namespace = new HashMap();
/*
            String[] lenyaKeys = LenyaMetaData.ELEMENTS;
            for (int i = 0; i < lenyaKeys.length; i++) {
                key2namespace.put(lenyaKeys[i], LenyaMetaData.NAMESPACE);
            }
*/
            List dcKeyList = DublinCoreImpl.getAttributeNames();
            String[] dcKeys = (String[]) dcKeyList.toArray(new String[dcKeyList.size()]);
            for (int i = 0; i < dcKeys.length; i++) {
                key2namespace.put(dcKeys[i], DublinCore.DC_NAMESPACE);
            }

            NodeTypeDef def = new NodeTypeDef();
            def.setMixin(true);
            def.setName(new QName(LenyaMetaData.NAMESPACE, CONTENT_NODE));

            for (Iterator i = key2namespace.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String namespace = (String) key2namespace.get(key);
                PropDefImpl propDef = new PropDefImpl();
                propDef.setDeclaringNodeType(def.getName());
                propDef.setName(new QName(namespace, key));
                propDef.setMandatory(false);
                propDef.setRequiredType(PropertyType.STRING);
                propDef.setMultiple(true);
                propDefs.add(propDef);
            }

            def.setPropertyDefs((PropDef[]) propDefs.toArray(new PropDef[propDefs.size()]));

            registry.registerNodeType(def);
        }
    }

}
