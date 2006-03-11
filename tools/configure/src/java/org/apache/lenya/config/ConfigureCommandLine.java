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
        System.out.println("\nWelcome to the command line interface to configure the building process of Apache Lenya");

        if (args.length != 1) {
            System.err.println("No root dir specified (e.g. /home/USERNAME/src/lenya/trunk)!");
            return;
        }
        String rootDir = args[0];

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
        log4j.setFilenameDefault(rootDir + "src/webapp/WEB-INF/log4j.xconf");
        log4j.setFilenameLocal(rootDir + "src/webapp/WEB-INF/local.log4j.xconf");
        */

	Vector configs = new Vector();
        configs.addElement(buildProperties);
        //configs.addElement(defaultPub);


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	for (int i = 0; i < configs.size(); i++) {
            Configuration config = (Configuration) configs.elementAt(i);
            config.read();

            Parameter[] params = config.getConfigurableParameters();
            readParameters(params, config);

            if (config.localConfigExists()) {
                System.out.println("\nWARNING: Local configuration already exists!");
                System.out.print("Do you want to overwrite (y/N)? ");
                try {
                    String value = br.readLine();
                    if (value.equals("y")) {
                        config.writeLocal();
                    } else {
                        System.out.println("Local configuration has NOT been overwritten.");
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            } else {
                config.writeLocal();
            }
        }
	// TODO: Suggest to build now ./build.sh (depending on OS)
    }

    /**
     *
     */
    static public void readParameters(Parameter[] params, Configuration config) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    for (int k = 0; k < params.length; k++) {
                try {
                    boolean notOK = true;
                    while (notOK) {
                        System.out.println("\nParameter " + params[k].getName() + ":");
                        System.out.println("  Default Value        : " + params[k].getDefaultValue());
                        System.out.println("  Local Value          : " + params[k].getLocalValue());
                        System.out.print  ("  New Local Value (d/L): ");

                        // Read new value
                        String newValue = br.readLine();
                        if (newValue.equals("d")) {
                            newValue = params[k].getDefaultValue();
                        } else if (newValue.equals("L") || newValue.equals("")) {
                            newValue = params[k].getLocalValue();
                        }

                        // Test entered value
                        if (params[k].test(newValue)) {
                            params[k].setLocalValue(newValue);
                            notOK = false;
                        } else {
                            System.err.println("  WARNING: No such value available!");
                            System.err.println("           Available values: " + params[k].getAvailableValues());
                            System.err.println("           Re-enter value ...");
                        }
                    }

                    System.out.println("  Value entered        : " + params[k].getLocalValue());
                    Parameter[] subParams = params[k].getSubsequentParameters(params[k].getLocalValue(), config);
                    if (subParams != null) {
                        String sp = "";
	                for (int j = 0; j < subParams.length; j++) {
                            sp = sp + subParams[j].getName();
                            if (j != subParams.length -1) sp = sp + ", ";
                        }
                        System.out.println("  " + subParams.length + " Subsequent Params  : " + sp);
                        readParameters(subParams, config);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                config.setParameter(params[k]);
            }
    }
}
