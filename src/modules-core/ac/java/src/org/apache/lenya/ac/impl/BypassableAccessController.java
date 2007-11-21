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

/* $Id$  */

package org.apache.lenya.ac.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.sitemap.PatternException;
import org.apache.lenya.ac.AccessControlException;
import org.apache.regexp.RE;
import org.apache.regexp.RECompiler;
import org.apache.regexp.REProgram;
import org.apache.regexp.RESyntaxException;

/**
 * AccessController that can be bypassed for certain URL patterns.
 */
public class BypassableAccessController extends DefaultAccessController {

    /**
     * Ctor.
     */
    public BypassableAccessController() {
	    // do nothing
    }

    private List publicMatchers = new ArrayList();
    private List publicExtensions = new ArrayList();

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
        
        getLogger().debug("Configuring bypass patterns");
        
        Configuration[] publics = conf.getChildren("public");

        for (int i = 0; i < publics.length; i++) {
            String publicHref = publics[i].getValue(null);

            try {
                this.publicMatchers.add(preparePattern(publicHref));
            } catch (PatternException pe) {
                throw new ConfigurationException("invalid pattern for public hrefs", pe);
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("CONFIGURATION: public: " + publicHref);
            }
        }
        
        Configuration[] extensionConfigs = conf.getChildren("public-extensions");
        for (int i = 0; i < extensionConfigs.length; i++) {
            String extensionString = extensionConfigs[i].getValue();
            String[] extensions = extensionString.split(",");
            for (int e = 0; e < extensions.length; e++) {
                String ext = extensions[e].trim();
                if (!ext.startsWith(".")) {
                    ext = "." + ext;
                }
                this.publicExtensions.add(ext);
            }
        }

    }

    /**
     * Compile the pattern in a <code>org.apache.regexp.REProgram</code>.
     * @param pattern The pattern to compile.
     * @return A RE program representing the pattern.
     * @throws PatternException when something went wrong.
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

    /**
     * Matches a string using a prepared pattern program.
     * @param preparedPattern The pattern program.
     * @param match The string to match.
     * @return <code>true</code> if the string matched the pattern, <code>false</code> otherwise.
     */
    protected boolean preparedMatch(REProgram preparedPattern, String match) {
        boolean result = false;
        
        if (match != null) {
            RE re = new RE(preparedPattern);
            result = re.match(match);
        }
        return result;
    }

    /**
     * @see org.apache.lenya.ac.AccessController#authorize(org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request)
        throws AccessControlException {
        
        assert request != null;
        
        resolveRoles(request);
        
        boolean authorized = false;
        
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        if (context == null) {
            context = "";
        }
        uri = uri.substring(context.length());
        
        // Check public uris from configuration above. Should only be used during development before the implementation of a concrete authorizer.
        int i = 0;
        while (!authorized && i < this.publicMatchers.size()) {
            getLogger().debug("Trying pattern: [" + this.publicMatchers.get(i) + "] with URL [" + uri + "]");
            if (preparedMatch((REProgram) this.publicMatchers.get(i), uri)) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Permission granted for free: [" + uri + "]");
                }
                authorized = true;
            }
            i++;
        }
        
        i = 0;
        while (!authorized && i < this.publicExtensions.size()) {
            String ext = (String) this.publicExtensions.get(i);
            authorized = uri.endsWith(ext);
            i++;
        }
        
        if (!authorized) {
            authorized = super.authorize(request);
        }
        
        return authorized;
    }

}
