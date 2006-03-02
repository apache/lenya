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

/* $Id: CreatorException.java 176415 2005-05-23 00:55:20Z gregor $  */

package org.apache.lenya.config;

/**
 * A command line tool to configure Lenya build
 */
public class ConfigureCommandLine {

    /**
     * @param args Command line args
     */
    public static void main(String[] args) {
        System.out.println("Hello Command Line");

        // Loop over config files
        //   - Read default values from config file
	//   - Ask for new values
	//   - Ask if existing config file should be overwritten
	// Suggest to build now ./build.sh (depending on OS)
    }
}
