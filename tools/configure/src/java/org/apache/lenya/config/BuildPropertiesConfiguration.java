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

package org.apache.lenya.config;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 * Build Properties Configuration
 */
public class BuildPropertiesConfiguration extends PropertiesConfiguration {

    /**
     *
     */
    public String getVersionDefault() {
        return getParameter("build.properties.version").getDefaultValue();
    }

    /**
     *
     */
    public String getVersionLocal() {
        return getParameter("build.properties.version").getLocalValue();
    }

    /**
     *
     */
    public Parameter[] getConfigurableParameters() {
        Parameter[] p = new Parameter[6];

        p[0] = getParameter("cocoon.src.dir");
        p[1] = getParameter("pubs.root.dirs");
        p[2] = getParameter("modules.root.dirs");

        p[3] = new ServerParameter();
	p[3].setName(getParameter("web.app.server").getName());
	p[3].setDefaultValue(getParameter("web.app.server").getDefaultValue());
	p[3].setLocalValue(getParameter("web.app.server").getLocalValue());

        p[4] = getParameter("enable.uploads");
        p[5] = getParameter("lenya.revision");
        return p;
    }
}
