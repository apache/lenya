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
package org.apache.lenya.cms.ac.usecases;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Role;
import org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.usecase.UsecaseResolver;

/**
 * Edit usecase policies.
 */
public class Usecases extends AccessControlUsecase {

    protected void initParameters() {
        super.initParameters();

        UsecaseResolver resolver = null;
        try {
            resolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
            String[] allUsecases = resolver.getUsecaseNames();
            SortedSet rootUsecases = new TreeSet();
            for (int i = 0; i < allUsecases.length; i++) {
                if (allUsecases[i].indexOf("/") == -1) {
                    rootUsecases.add(allUsecases[i]);
                }
            }
            
            String[] usecases = (String[]) rootUsecases.toArray(new String[rootUsecases.size()]);
            
            setParameter("usecases", usecases);

            Role[] roles = getAccessController().getAccreditableManager().getRoleManager()
                    .getRoles();
            String[] roleNames = new String[roles.length];
            for (int r = 0; r < roles.length; r++) {
                roleNames[r] = roles[r].getId();
            }
            Arrays.sort(roleNames);
            setParameter("roles", roleNames);

            Publication pub = getPublication();
            setParameter("publicationId", pub.getId());
            setParameter("template", pub.getTemplateId());

            for (int u = 0; u < usecases.length; u++) {
                for (int r = 0; r < roles.length; r++) {
                    boolean value = getUsecaseAuthorizer().isPermitted(usecases[u], pub, roles[r]);
                    setParameter(usecases[u] + ":" + roles[r], Boolean.valueOf(value));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }

    }

    protected Publication getPublication() throws PublicationException {
        String pubId = new URLInformation(getSourceURL()).getPublicationId();
        Publication pub = getDocumentFactory().getPublication(pubId);
        return pub;
    }

    protected void doExecute() throws Exception {
        super.doExecute();

        String[] usecases = (String[]) getParameter("usecases");
        String[] roleNames = (String[]) getParameter("roles");

        Publication pub = getPublication();
        for (int u = 0; u < usecases.length; u++) {
            for (int r = 0; r < roleNames.length; r++) {
                String key = usecases[u] + ":" + roleNames[r];
                String stringValue = getBooleanCheckboxParameter(key);
                boolean value = Boolean.valueOf(stringValue).booleanValue();
                Role role = getAccessController().getAccreditableManager().getRoleManager()
                        .getRole(roleNames[r]);
                getUsecaseAuthorizer().setPermission(usecases[u], pub, role, value);
            }
        }

    }

    private UsecaseAuthorizer authorizer;

    protected UsecaseAuthorizer getUsecaseAuthorizer() {
        if (this.authorizer == null) {
            Authorizer[] authorizers = getAccessController().getAuthorizers();
            for (int i = 0; i < authorizers.length; i++) {
                if (authorizers[i] instanceof UsecaseAuthorizer) {
                    this.authorizer = (UsecaseAuthorizer) authorizers[i];
                }
            }
        }
        return this.authorizer;
    }

}
