/*
$Id: RequestParamAction.java,v 1.11 2004/02/20 08:44:48 andreas Exp $
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
package org.apache.lenya.cms.cocoon.acting;


/*
 * Standard imports
 */
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;


/**
 * This action makes some request details available to the sitemap via parameter substitution.
 * {context}      - is the context path of the servlet (usually "/cocoon") {requestURI}   - is the
 * requested URI without parameters {requestQuery} - is the query string like "?param1=test" if
 * there is one Additionlly all request parameters can be made available for use in the sitemap.
 * if the parameter "parameters" is set to true. (A variable is created for each request parameter
 * (only if it doesn't exist) with the same name as the parameter itself) Default values can be
 * set for request parameters, by including sitemap parameters named
 * "default.&lt;parameter-name&gt;". Sitemap definition:
 * <pre>
 * &lt;map:action name="request" src="org.apache.cocoon.acting.RequestParamAction"/&gt;
 * </pre>
 *
 * <p>
 * Example use:
 * <pre>
 * &lt;map:match pattern="some-resource"&gt;
 *  &lt;map:act type="request"&gt;
 *     &lt;map:parameter name="parameters" value="true"/&gt;
 *     &lt;map:parameter name="default.dest" value="invalid-destination.html"/&gt;
 *     &lt;map:redirect-to uri="{context}/somewhereelse/{dest}"/&gt;
 *  &lt;/map:act&gt;
 * &lt;/map:match&gt;
 * </pre>
 * Redirection is only one example, another use:
 * <pre>
 * &lt;map:match pattern="some-resource"&gt;
 *  &lt;map:act type="request"&gt;
 *     &lt;map:parameter name="parameters" value="true"/&gt;
 *     &lt;map:generate src="users/menu-{id}.xml"/&gt;
 *  &lt;/map:act&gt;
 *  &lt;map:transform src="menus/personalisation.xsl"/&gt;
 *  &lt;map:serialize/&gt;
 * &lt;/map:match&gt;
 * </pre>
 * etc, etc.
 * </p>
 *
 * @author <a href="mailto:Marcus.Crafter@osa.de">Marcus Crafter</a>
 * @author <a href="mailto:tcurdt@dff.st">Torsten Curdt</a>
 * @version CVS $Id: RequestParamAction.java,v 1.11 2004/02/20 08:44:48 andreas Exp $
 */
public class RequestParamAction extends ServiceableAction
    implements ThreadSafe {
    public final static String MAP_URI = "requestURI";
    public final static String MAP_QUERY = "requestQuery";
    public final static String MAP_CONTEXTPATH = "context";
    public final static String PARAM_PARAMETERS = "parameters";
    public final static String PARAM_DEFAULT_PREFIX = "default.";

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param source DOCUMENT ME!
     * @param param DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
        Parameters param) throws Exception {
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("RequestInfoAction: no request object!");

            return (null);
        }

        Map map = new HashMap();

        map.put(MAP_URI, request.getRequestURI());

        String query = request.getQueryString();

        if ((query != null) && (query.length() > 0)) {
            map.put(MAP_QUERY, "?" + query);
        } else {
            map.put(MAP_QUERY, "");
        }

        map.put(MAP_CONTEXTPATH, request.getContextPath());
        map.put("serverPort", new String("" + request.getServerPort()));
        map.put("serverName", request.getServerName());

        if ("true".equalsIgnoreCase(param.getParameter(PARAM_PARAMETERS, null))) {
            Enumeration e = request.getParameterNames();

            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                String value = request.getParameter(name);

                if ((value != null) && !map.containsKey(name)) {
                    map.put(name, value);
                }
            }

            String[] paramNames = param.getNames();

            for (int i = 0; i < paramNames.length; i++) {
                if (paramNames[i].startsWith(PARAM_DEFAULT_PREFIX) &&
                        (request.getParameter(paramNames[i].substring(PARAM_DEFAULT_PREFIX.length())) == null)) {
                    map.put(paramNames[i].substring(PARAM_DEFAULT_PREFIX.length()),
                        param.getParameter(paramNames[i]));
                }
            }
        }

        return (map);
    }
}
