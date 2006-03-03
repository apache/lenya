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

import javax.swing.*;

/**
 * A GUI to configure Lenya build
 */
public class ConfigureGUI {

    /**
     * @param args Command line args
     */
    public static void main(String[] args) {
        System.out.println("\nWelcome to the GUI to configure the building process of Apache Lenya");

        if (args.length != 1) {
            System.err.println("No root dir specified (e.g. /home/USERNAME/src/lenya/trunk)!");
            return;
        }
        String rootDir = args[0];

        new ConfigureGUI(rootDir);
    }

    /**
     *
     */
    public ConfigureGUI(String rootDir) {
        System.out.println("Starting GUI ...");

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Apache Lenya Configuration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel("Hello Apache Lenya: " + rootDir);
        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);
    }
}
