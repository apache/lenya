/*
 * $Id: RollbackAction.java,v 1.6 2003/02/20 13:40:40 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;


import java.io.FileNotFoundException;

import java.util.HashMap;
import java.util.Map;
import org.apache.cocoon.environment.ObjectModelHelper;


/**
 * DOCUMENT ME!
 *
 * @author Edith Chevrier
 * @version 2002.8.02
 */
public class RollbackAction extends RevisionControllerAction implements Configurable {
    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
    }

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

        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        // Get parameters                                                                                                                       
        String action = request.getParameter("action");
        String rollbackTime = request.getParameter("rollbackTime");

        if (action != null) {
            if (action.equals("rollback")) {
                // Do the rollback to an earlier version
                long newtime = 0;

                try {
                    newtime = rc.rollback(filename, username, true,
                            new Long(rollbackTime).longValue());
                } catch (FileNotFoundException e) {
                    getLogger().error("Unable to roll back!" + e);

                    return null;
                } catch (Exception e) {
                    getLogger().error("Unable to roll back!" + e);

                    return null;
                }

                getLogger().debug("rollback complete, old (and now current) time was " +
                    rollbackTime + " backup time is " + newtime);

                String location = request.getHeader("Referer");

                getLogger().debug("redirect to " + location);
                actionMap.put("location", location);

                return actionMap;

            } else {
                getLogger().error("The action is no defined");

                return null;
            }
        } else {
            getLogger().error("The action is null");

            return null;
        }
    }
}
