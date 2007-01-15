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

package org.apache.lenya.cms.ac.usecase.impl;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.cache.BuildException;
import org.apache.lenya.ac.cache.InputStreamBuilder;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.util.Assert;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Builder for usecase roles.
 * 
 * @version $Id$
 */
public class UsecaseRolesBuilder implements InputStreamBuilder {

    protected static final String USECASES_ELEMENT = "usecases";
    protected static final String USECASE_ELEMENT = "usecase";
    protected static final String ROLE_ELEMENT = "role";
    protected static final String ID_ATTRIBUTE = "id";

    /**
     * @see org.apache.lenya.ac.cache.InputStreamBuilder#build(java.io.InputStream)
     */
    public Object build(InputStream stream) throws BuildException {

        UsecaseRoles usecaseRoles = new UsecaseRoles();

        Document document;
        try {
            document = DocumentHelper.readDocument(stream);
        } catch (Exception e) {
            throw new BuildException(e);
        }
        Assert.isTrue("Correct usecase policies XML", document.getDocumentElement().getLocalName()
                .equals(USECASES_ELEMENT));

        NamespaceHelper helper = new NamespaceHelper(AccessController.NAMESPACE,
                AccessController.DEFAULT_PREFIX, document);

        Element[] usecaseElements = helper.getChildren(document.getDocumentElement(),
                USECASE_ELEMENT);
        for (int i = 0; i < usecaseElements.length; i++) {
            String usecaseId = usecaseElements[i].getAttribute(ID_ATTRIBUTE);

            // add roles only if not overridden by child publication
            if (!usecaseRoles.hasRoles(usecaseId)) {
                Element[] roleElements = helper.getChildren(usecaseElements[i], ROLE_ELEMENT);
                Set roleIds = new HashSet();
                for (int j = 0; j < roleElements.length; j++) {
                    String roleId = roleElements[j].getAttribute(ID_ATTRIBUTE);
                    roleIds.add(roleId);
                }
                String[] roleIdArray = (String[]) roleIds.toArray(new String[roleIds.size()]);
                usecaseRoles.setRoles(usecaseId, roleIdArray);
            }
        }
        return usecaseRoles;
    }

    /**
     * Saves the usecase roles.
     * @param usecaseRoles The roles.
     * @param sourceUri The source to save to.
     * @param manager The service manager.
     * @throws BuildException if an error occurs.
     */
    public void save(UsecaseRoles usecaseRoles, String sourceUri, ServiceManager manager) throws BuildException {
        try {
            NamespaceHelper helper = new NamespaceHelper(AccessController.NAMESPACE,
                    AccessController.DEFAULT_PREFIX, USECASES_ELEMENT);
            String[] usecaseNames = usecaseRoles.getUsecaseNames();
            for (int u = 0; u < usecaseNames.length; u++) {
                Element usecaseElement = helper.createElement(USECASE_ELEMENT);
                helper.getDocument().getDocumentElement().appendChild(usecaseElement);
                usecaseElement.setAttribute(ID_ATTRIBUTE, usecaseNames[u]);
                String[] roles = usecaseRoles.getRoles(usecaseNames[u]);
                for (int r = 0; r < roles.length; r++) {
                    Element roleElement = helper.createElement(ROLE_ELEMENT);
                    usecaseElement.appendChild(roleElement);
                    roleElement.setAttribute(ID_ATTRIBUTE, roles[r]);
                }
            }
            SourceUtil.writeDOM(helper.getDocument(), sourceUri, manager);
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

}
