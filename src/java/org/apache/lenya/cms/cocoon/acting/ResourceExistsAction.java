/*
$Id: ResourceExistsAction.java,v 1.7 2004/02/20 08:44:48 andreas Exp $
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

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;


/**
 * This action simply checks to see if a given resource exists. It checks
 * whether the specified in the src attribute source exists or not.
 * The action returns empty <code>Map</code> if it exists, null otherwise.
 * <p>Instead of src attribute, source can be specified using
 * parameter named 'url' (this is old syntax).
 * <p>In order to differentiate between files and directories, the type can be specified
 * using the parameter 'type' (&lt;map:parameter name="type" value="file"/&gt; or
 * &lt;map:parameter name="type" value="directory"/&gt;). The parameter 'type' is optional.
 * <p>
 * <b>Note:</b> {@link org.apache.cocoon.selection.ResourceExistsSelector}
 * should be preferred to this component, as the semantics of a Selector better
 * match the supplied functionality.
 *
 * @author <a href="mailto:balld@apache.org">Donald Ball</a>
 * @author <a href="mailto:michi@apache.org">Michael Wechner</a>
 * @version CVS $Id: ResourceExistsAction.java,v 1.7 2004/02/20 08:44:48 andreas Exp $
 */
public class ResourceExistsAction extends ServiceableAction implements ThreadSafe {
    /**
     *
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
        Parameters parameters) throws Exception {
        String urlstring = parameters.getParameter("url", source);
        String typestring = parameters.getParameter("type", "resource");
        Source src = null;

        try {
            src = resolver.resolveURI(urlstring);

            File resource = new File(new URL(src.getURI()).getFile());

            if (typestring.equals("resource") && src.exists()) {
                getLogger().debug(".act(): Resource (file or directory) exists: " + src.getURI());

                return EMPTY_MAP;
            } else if (typestring.equals("file") && resource.isFile()) {
                getLogger().debug(".act(): File exists: " + resource);

                return EMPTY_MAP;
            } else if (typestring.equals("directory") && resource.isDirectory()) {
                getLogger().debug(".act(): Directory exists: " + resource);

                return EMPTY_MAP;
            } else {
                getLogger().debug(".act(): Resource " + resource + " as type \"" + typestring +
                    "\" does not exist");
            }
        } catch (Exception e) {
            getLogger().warn(".act(): Exception", e);
        } finally {
            resolver.release(src);
        }

        return null;
    }
}
