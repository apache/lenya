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

/* $Id: AbstractAuthorizerAction.java,v 1.17 2004/03/03 12:56:32 gregor Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.sitemap.PatternException;
import org.apache.lenya.util.Stack;
import org.apache.regexp.RE;
import org.apache.regexp.RECompiler;
import org.apache.regexp.REProgram;
import org.apache.regexp.RESyntaxException;

public abstract class AbstractAuthorizerAction extends AbstractComplementaryConfigurableAction
    implements Configurable {
    REProgram[] public_matchers;
    boolean logRequests = false;
    String authenticator_type = null;

    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);

        Configuration authenticatorConf = conf.getChild("authenticator");
        authenticator_type = authenticatorConf.getAttribute("type");

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(".configure(): authenticator type=" + authenticator_type);
        }

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
        Request req = ObjectModelHelper.getRequest(objectModel);

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
        Stack history = (Stack) session.getAttribute("org.apache.lenya.cms.cocoon.acting.History");

        if (history == null) {
            history = new Stack(10);
            session.setAttribute("org.apache.lenya.cms.cocoon.acting.History", history);
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

        // FIXME: Can't be here. Please see comment within PMLAuthorizerAction

        /*
                String authenticator_type = (String) session.getAttribute("org.apache.lenya.cms.cocoon.acting.Authenticator.id");
                if (!this.authenticator_type.equals(authenticator_type)) {
                    if (authenticator_type == null) {
                        getLogger().debug(".act(): No authenticator yet");
                    } else {
                        getLogger().warn(".act(): Authenticators do not match: " + authenticator_type + " (Authorizer's authenticator: " + this.authenticator_type + ")");
                    }

                    return null;
                }
        */
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
