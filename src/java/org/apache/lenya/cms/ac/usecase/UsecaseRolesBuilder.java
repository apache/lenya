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

/* $Id: UsecaseRolesBuilder.java,v 1.2 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.cms.ac.usecase;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.cache.BuildException;
import org.apache.lenya.ac.cache.InputStreamBuilder;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UsecaseRolesBuilder implements InputStreamBuilder {

    protected static final String USECASES_ELEMENT = "usecases";
    protected static final String USECASE_ELEMENT = "usecase";
    protected static final String ROLE_ELEMENT = "role";
    protected static final String ID_ATTRIBUTE = "id";

    /**
     * @see org.apache.lenya.cms.ac2.cache.InputStreamBuilder#build(java.io.InputStream)
     */
    public Object build(InputStream stream) throws BuildException {

        UsecaseRoles usecaseRoles = new UsecaseRoles();

        Document document;
        try {
            document = DocumentHelper.readDocument(stream);
        } catch (Exception e) {
            throw new BuildException(e);
        }
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
            Element[] roleElements = helper.getChildren(usecaseElements[i], ROLE_ELEMENT);
            Set roleIds = new HashSet();
            for (int j = 0; j < roleElements.length; j++) {
                String roleId = roleElements[j].getAttribute(ID_ATTRIBUTE);
                roleIds.add(roleId);
            }
            String[] roleIdArray = (String[]) roleIds.toArray(new String[roleIds.size()]);
            usecaseRoles.setRoles(usecaseId, roleIdArray);
        }
        return usecaseRoles;
    }

}
