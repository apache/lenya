/*
 * $Id: ReservedCheckoutAction.java,v 1.8 2003/04/23 17:53:22 edith Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

import org.lenya.cms.rc.FileReservedCheckOutException;

import java.util.HashMap;
import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author Edth Chevrier
 * @version 2002.6.25
 */
public class ReservedCheckoutAction extends RevisionControllerAction {

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        super.act(redirector, resolver, objectModel, src, parameters);

        HashMap actionMap = new HashMap();

        //check out
        try {
            getLogger().debug(".act(): Filename: " + filename);
            getLogger().debug(".act(): Username: " + username);

            if (filename == null) {
                throw new Exception("Filename is null");
            }

            if (username == null) {
                throw new Exception("Username is null");
            }

            rc.reservedCheckOut(filename, username);
        } catch (FileReservedCheckOutException e) {
            actionMap.put("exception", "fileReservedCheckOutException");
            actionMap.put("filename", filename);
            actionMap.put("user", e.checkOutUsername);
            actionMap.put("date", e.checkOutDate);
            getLogger().warn("Document " + filename + " already checked-out by " +
                e.checkOutUsername + " since " + e.checkOutDate);

            return actionMap;
        } catch (Exception e) {
            actionMap.put("exception", "genericException");
            actionMap.put("filename", filename);
            getLogger().warn(".act(): The document " + filename + " couldn't be checked out: " + e);

            return actionMap;
        }

        return null;
    }
}
