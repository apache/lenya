/*
 * $Id: AbstractAuthorizerAction.java,v 1.10 2003/03/06 20:45:41 gregor Exp $
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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.sitemap.PatternException;

import org.apache.regexp.RE;
import org.apache.regexp.RECompiler;
import org.apache.regexp.REProgram;
import org.apache.regexp.RESyntaxException;

import org.lenya.util.Stack;

import java.util.HashMap;
import java.util.Map;
import org.apache.cocoon.environment.ObjectModelHelper;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version $Id: AbstractAuthorizerAction.java,v 1.10 2003/03/06 20:45:41 gregor Exp $
 */
public abstract class AbstractAuthorizerAction extends AbstractComplementaryConfigurableAction
    implements Configurable {
    REProgram[] public_matchers;
    boolean logRequests = false;

    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);

        Configuration[] publics = conf.getChildren("public");
        public_matchers = new REProgram[publics.length];

        for (int i = 0; i < publics.length; i++) {
            String public_href = publics[i].getValue(null);

            try {
                public_matchers[i] = preparePattern(public_href);
            } catch (PatternException pe) {
                throw new ConfigurationException("invalid pattern for public hrefs", pe);
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("CONFIGURATION: public: " + public_href);
            }
        }

        Configuration log = conf.getChild("log");

        if (log.getValue("off").equals("on")) {
            logRequests = true;
        }

        if (logRequests) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("CONFIGURATION: log requests: on");
            }
        } else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("CONFIGURATION: log requests: off");
            }
        }
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
        // Get request object
        Request req = (Request) ObjectModelHelper.getRequest(objectModel);

        if (req == null) {
            getLogger().error("No request object");

            return null;
        }

        Session session = req.getSession(true);

        if (session == null) {
            getLogger().error("No session object");

            return null;
        }

        // Get uri
        String request_uri = req.getRequestURI();
        String sitemap_uri = req.getSitemapURI();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("request-uri=" + request_uri);
            getLogger().debug("sitemap-uri=" + sitemap_uri);
        }

        // Set history
        Stack history = (Stack) session.getAttribute("org.lenya.cms.cocoon.acting.History");

        if (history == null) {
            history = new Stack(10);
            session.setAttribute("org.lenya.cms.cocoon.acting.History", history);
        }

        history.push(sitemap_uri);

        // Check public uris from configuration above. Should only be used during development before the implementation of a concrete authorizer.
        for (int i = 0; i < public_matchers.length; i++) {
            if (preparedMatch(public_matchers[i], sitemap_uri)) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Permission granted for free: " + request_uri);
                }

                HashMap actionMap = new HashMap();

                return actionMap;
            }
        }

        String query_string = req.getQueryString();

        if (query_string != null) {
            session.setAttribute("protected_destination", request_uri + "?" + req.getQueryString());
        } else {
            session.setAttribute("protected_destination", request_uri);
        }

        HashMap actionMap = new HashMap();

        if (authorize(req, actionMap)) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Permission granted dues to authorisation: " + request_uri);
            }

            return actionMap;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Permission denied: " + request_uri);
        }

        return null;
    }

    /**
     * Compile the pattern in a <code>org.apache.regexp.REProgram</code>.
     *
     * @param pattern DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PatternException DOCUMENT ME!
     */
    protected REProgram preparePattern(String pattern)
        throws PatternException {
        if (pattern == null) {
            throw new PatternException("null passed as a pattern", null);
        }

        if (pattern.length() == 0) {
            pattern = "^$";

            if (getLogger().isWarnEnabled()) {
                getLogger().warn("The empty pattern string was rewritten to '^$'" +
                    " to match for empty strings.  If you intended" +
                    " to match all strings, please change your" + " pattern to '.*'");
            }
        }

        try {
            RECompiler compiler = new RECompiler();
            REProgram program = compiler.compile(pattern);

            return program;
        } catch (RESyntaxException rse) {
            getLogger().debug("Failed to compile the pattern '" + pattern + "'", rse);
            throw new PatternException(rse.getMessage(), rse);
        }
    }

    protected boolean preparedMatch(REProgram preparedPattern, String match) {
        RE re = new RE(preparedPattern);

        if (match == null) {
            return false;
        }

        return re.match(match);
    }

    /**
     * Should be implemented by a concrete authorizer
     *
     * @return DOCUMENT ME!
     */
    public abstract boolean authorize(Request request, Map map)
        throws Exception;
}
