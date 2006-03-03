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

/**
 * Server Parameter web.app.server
 */
public class ServerParameter extends Parameter {

    /**
     *
     */
    public boolean test(String value) {
        if (value.equals("Jetty") || value.equals("Tomcat") || value.equals("WLS")) {
            return true;
        }
        return false;
    }

    /**
     *
     */
    public String getAvailableValues() {
        return "Jetty, Tomcat, WLS";
    }

    /**
     *
     */
    public Parameter[] getSubsequentParameters(String value, Configuration config) {
        if (value.equals("Jetty")) {
            Parameter[] p = new Parameter[2];
            p[0] = config.getParameter("web.app.server.jetty.port");
            p[1] = config.getParameter("web.app.server.jetty.admin.port");
            return p;
        }
        return null;
    }
}
