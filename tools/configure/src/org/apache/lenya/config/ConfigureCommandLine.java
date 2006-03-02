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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * A command line tool to configure Lenya build
 */
public class ConfigureCommandLine {

    /**
     * @param args Command line args
     */
    public static void main(String[] args) {
        System.out.println("Welcome to the command line interface to configure Apache Lenya for building");

        if (args.length != 1) {
            System.err.println("No root dir specified (e.g. /home/USERNAME/src/lenya/trunk)!");
            return;
        }
        String rootDir = args[0];

        // Define all configuration files
        FileConfiguration buildProperties = new BuildPropertiesConfiguration();
        buildProperties.setFilenameDefault(rootDir + "/build.properties");
        buildProperties.setFilenameLocal(rootDir + "/local.build.properties");

	Vector configs = new Vector();
        configs.addElement(buildProperties);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	for (int i = 0; i < configs.size(); i++) {
            Configuration config = (Configuration) configs.elementAt(i);
            config.read();
            Parameter[] params = config.getParameters();
	    for (int k = 0; k < params.length; k++) {
                System.out.println("\nParameter " + params[k].getName() + ":");
                System.out.println("  Default Value        : " + params[k].getDefaultValue());
                System.out.println("  Local Value          : " + params[k].getLocalValue());
                System.out.print  ("  New Local Value (D/L): ");
                try {
                    String newValue = br.readLine();
                    if (newValue.equals("D")) {
                        params[k].setLocalValue(params[k].getDefaultValue());
                    } else if (newValue.equals("L") || newValue.equals("")) {
                        params[k].setLocalValue(params[k].getLocalValue());
                    } else {
                        if (params[k].test(newValue)) {
                            params[k].setLocalValue(newValue);
                        } else {
                            // TODO: Implement this
                            System.err.println("Test failed! Re-enter value ...");
                        }
                    }
                System.out.println("  Value entered        : " + params[k].getLocalValue());
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                config.setParameter(params[k]);
            }
	    // TODO: Ask if existing local config should be overwritten
            config.writeLocal();
        }
	// Suggest to build now ./build.sh (depending on OS)
    }
}
