/*
$Id: BypassableAccessController.java,v 1.2 2003/07/15 13:50:15 andreas Exp $
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
package org.apache.lenya.cms.ac2;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.sitemap.PatternException;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.regexp.RE;
import org.apache.regexp.RECompiler;
import org.apache.regexp.REProgram;
import org.apache.regexp.RESyntaxException;

/**
 * AccessController that can be bypassed for certain URL patterns.
 *
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class BypassableAccessController extends DefaultAccessController {

    /**
     * Ctor.
     */
    public BypassableAccessController() {
    }

    private REProgram[] publicMatchers;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
        
        Configuration[] publics = conf.getChildren("public");
        publicMatchers = new REProgram[publics.length];

        for (int i = 0; i < publics.length; i++) {
            String public_href = publics[i].getValue(null);

            try {
                publicMatchers[i] = preparePattern(public_href);
            } catch (PatternException pe) {
                throw new ConfigurationException("invalid pattern for public hrefs", pe);
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("CONFIGURATION: public: " + public_href);
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
     * @see org.apache.lenya.cms.ac2.AccessController#authorize(org.apache.lenya.cms.publication.Publication, org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request)
        throws AccessControlException {
        
        assert request != null;
        
        boolean authorized = false;
        
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        if (context == null) {
            context = "";
        }
        uri = uri.substring(context.length());
        
        // Check public uris from configuration above. Should only be used during development before the implementation of a concrete authorizer.
        int i = 0;
        while (!authorized && i < publicMatchers.length) {
            if (preparedMatch(publicMatchers[i], uri)) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Permission granted for free: [" + uri + "]");
                }
                authorized = true;
            }
            i++;
        }
        
        return super.authorize(request);
    }

}
