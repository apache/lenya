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

package org.apache.lenya.config.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 * Properties Configuration
 */
abstract public class PropertiesConfiguration extends FileConfiguration {

    private Properties defaultProps;
    private Properties localProps;

    /**
     *
     */
    public void readDefault() {
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
    public void readLocal() {
        try {
        localProps = new Properties();
        localProps.load(new FileInputStream(getFilenameLocal()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     *
     */
    public void writeLocal() {
        String header = "Created by org.apache.lenya.config.PropertiesConfiguration";

        try {
        PrintWriter out = new PrintWriter(new FileOutputStream(getFilenameLocal()));
        out.println("#" + header);
        for (int i = 0; i < params.length; i++) {
            out.println("\n#");
            out.println(params[i].getName() + "=" + params[i].getLocalValue());
        }
        out.close();
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }

/*
        Properties newLocalProperties = new Properties();
        for (int i = 0; i < params.length; i++) {
            newLocalProperties.setProperty(params[i].getName(), params[i].getLocalValue());
        }

        try {
            newLocalProperties.store(new FileOutputStream(getFilenameLocal()), header);
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
*/
    }

    /**
     *
     */
    public void read() {
        readDefault();
        readLocal();

        Vector p = new Vector();
        Enumeration names = defaultProps.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            Parameter param = new Parameter();
            param.setName(name);
            param.setDefaultValue(defaultProps.getProperty(name));
            String localValue = localProps.getProperty(name);
            if (localValue != null) {
                param.setLocalValue(localProps.getProperty(name));
            } else {
                param.setLocalValue(defaultProps.getProperty(name));
            }
            p.addElement(param);
        }
        params = new Parameter[p.size()];
        for (int i = 0; i < params.length; i++) {
            params[i] = (Parameter) p.elementAt(i);
        }
    }

    /**
     *
     */
    public Parameter[] getParameters() {
        return params;
    }
}
