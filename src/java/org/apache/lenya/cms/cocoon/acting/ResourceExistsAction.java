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

package org.apache.lenya.cms.cocoon.acting;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.TraversableSource;


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
 * <strong>Note:</strong> {@link org.apache.cocoon.selection.ResourceExistsSelector}
 * should be preferred to this component, as the semantics of a Selector better
 * match the supplied functionality.
 */
public class ResourceExistsAction extends AbstractAction implements ThreadSafe {
    /**
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
        Parameters parameters) throws Exception {
        String url = parameters.getParameter("url", source);
        String type = parameters.getParameter("type", "resource");
        Source src = null;

        try {
            src = resolver.resolveURI(url);
            
            if (src.exists()) {
                
                boolean isCollection = false;
                if (src instanceof TraversableSource) {
                    TraversableSource traversableSource = (TraversableSource) src;
                    isCollection = traversableSource.isCollection();
                }
                
                boolean exists = type.equals("resource")
                    || type.equals("file") && !isCollection
                    || type.equals("directory") && isCollection;
                
                if (exists) {
                    getLogger().debug(type + " exists: " + src.getURI());
                    return Collections.EMPTY_MAP;
                }
            }
            getLogger().debug(".act(): Resource " + source + " as type \"" + type +
                "\" does not exist");
        } finally {
            if (src != null) {
                resolver.release(src);
            }
        }

        return null;
    }
}
