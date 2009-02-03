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
package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

/**
 * <p>The linking module is a utility to add parameters to link URLs,
 * depending on request parameters. Supported attributes:</p>
 * <ul>
 * <li><code>rev</code> - inserts the parameter <code>,rev=...</code>
 * if a request parameter <code>lenya.revision</code> is present.</li>
 * </ul>
 */
public class LinkingModule extends AbstractInputModule {
    
    protected static final String ATTRIBUTE_REVISION = "rev";
    protected static final String REQUEST_PARAM_REVISION = "lenya.revision";

    public Object getAttribute(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
        
        if (name.equals(ATTRIBUTE_REVISION)) {
            Request request = ObjectModelHelper.getRequest(objectModel);
            String revision = request.getParameter(REQUEST_PARAM_REVISION);
            if (revision != null) {
                return ",rev=" + revision;
            }
            else {
                return "";
            }
        }
        else {
            throw new ConfigurationException("The attribute [" + name + "] is not supported.");
        }
    }


}
