/*
$Id: UsecaseRolesBuilder.java,v 1.1 2003/08/13 13:13:08 andreas Exp $
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

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.cms.ac2.AccessController;
import org.apache.lenya.cms.ac2.cache.BuildException;
import org.apache.lenya.cms.ac2.cache.InputStreamBuilder;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
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
