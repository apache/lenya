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

/* $Id: ResourceExistsAction.java,v 1.8 2004/03/01 16:18:21 gregor Exp $  */

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
