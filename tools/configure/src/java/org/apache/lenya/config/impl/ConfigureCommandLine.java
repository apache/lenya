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

package org.apache.lenya.config.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.lenya.config.core.Configuration;
import org.apache.lenya.config.core.FileConfiguration;
import org.apache.lenya.config.core.Parameter;

/**
 * A command line tool to configure Lenya 1.4 build
 */
public class ConfigureCommandLine extends org.apache.lenya.config.core.ConfigureCommandLine {

    /**
     * @param args Command line args
     */
    public static void main(String[] args) {
        System.out.println("\nWelcome to the command line interface to configure the building process of Apache Lenya 1.4-dev");

        if (args.length != 1) {
            System.err.println("No root dir specified (e.g. /home/USERNAME/src/lenya/trunk)!");
            return;
        }
        String rootDir = args[0];

        ConfigureCommandLine ccl = new ConfigureCommandLine();
        Vector configs = ccl.setConfigurations(rootDir);
        ccl.changeConfigurations(configs);
    }

    /**
     *
     */
    public Vector setConfigurations(String rootDir) {
        // Define all configuration files
        FileConfiguration buildProperties = new BuildPropertiesConfiguration();
        buildProperties.setFilenameDefault(rootDir + "/build.properties");
        buildProperties.setFilenameLocal(rootDir + "/local.build.properties");

        /*
        FileConfiguration defaultPub = new PublicationConfiguration();
        defaultPub.setFilenameDefault(rootDir + "src/pubs/default/config/publication.xconf");
        defaultPub.setFilenameLocal(rootDir + "src/pubs/default/config/local.publication.xconf");
        */

        /*
        FileConfiguration log4j = new Log4jConfiguration();
        // src/confpatch/log4j-*
        log4j.setFilenameDefault(rootDir + "src/webapp/WEB-INF/log4j.xconf");
        log4j.setFilenameLocal(rootDir + "src/webapp/WEB-INF/local.log4j.xconf");
        */

	Vector configs = new Vector();
        configs.addElement(buildProperties);
        //configs.addElement(defaultPub);

        return configs;
    }
}
