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
 * Properties Configuration
 */
public class PropertiesConfiguration extends FileConfiguration {

    private Properties defaultProps;

    /**
     *
     */
    public void read() {
        try {
        defaultProps = new Properties();
        defaultProps.load(new FileInputStream(getFilenameDefault()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     *
     */
    public void write() {
        System.out.println(getFilenameLocal());
    }

    /**
     *
     */
    public Parameter[] getParameters() {
        Vector params = new Vector();
        Enumeration names = defaultProps.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            Parameter param = new Parameter();
            param.setName(name);
            param.setDefaultValue(defaultProps.getProperty(name));
            params.addElement(param);
        }
        Parameter[] p = new Parameter[params.size()];
        for (int i = 0; i < p.length; i++) {
            p[i] = (Parameter) params.elementAt(i);
        }
        return p;
    }
}
