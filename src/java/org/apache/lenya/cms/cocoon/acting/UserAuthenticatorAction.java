/*
 * $Id: UserAuthenticatorAction.java,v 1.1 2003/06/19 14:47:33 egli Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2003 Wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment: "This product
 *    includes software developed by Wyona (http://www.wyona.com)"
 *
 * 4. The name "Lenya" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact contact@wyona.com
 *
 * 5. Products derived from this software may not be called "Lenya" nor
 *    may "Lenya" appear in their names without prior written permission
 *    of Wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment: "This product includes software developed by Wyona
 *    (http://www.wyona.com)"
 *
 * THIS SOFTWARE IS PROVIDED BY Wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS
 * OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * Wyona WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS A RESULT
 * OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF Wyona HAS BEEN
 * ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. Wyona WILL NOT BE LIABLE
 * FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */

package org.apache.lenya.cms.cocoon.acting;

import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.ac.Identity;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac.UserManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.w3c.dom.Document;

/**
 * @author egli
 * 
 * 
 */
public class UserAuthenticatorAction extends IMLAuthenticatorAction {

    private Publication publication;

    /* 
     * This is an implementation of an authenticator which uses 
     * the User classes and delegates the authentication to them.
     * An LDAPUser authenticates itself differently than a FileUser
     * 
     * @param username
     * @param password
     * @param request
     * @param map is ignored
     * 
     * @return true if authentication succeded
     */
    public boolean authenticate(
        String username,
        String password,
        Request request,
        Map map)
        throws Exception {

        UserManager manager = UserManager.instance(publication);
        User user = manager.getUser(username);

        if (user.authenticate(password)) {
            String context = request.getContextPath();
            int port = request.getServerPort();
            Document idoc = getIdentityDoc(username, port, context);

            Session session = request.getSession(true);

            if (session == null) {
                return false;
            }

            Identity identity = new Identity(idoc);

            session.setAttribute("org.apache.lenya.cms.ac.Identity", identity);

            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String src,
        Parameters parameters)
        throws Exception {

        publication = PublicationFactory.getPublication(objectModel);
        return super.act(redirector, resolver, objectModel, src, parameters);
    }

}
