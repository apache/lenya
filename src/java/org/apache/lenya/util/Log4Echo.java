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

/* $Id: Log4Echo.java,v 1.1 2004/03/16 22:56:15 michi Exp $  */

package org.apache.lenya.util;

import org.apache.log4j.Category;


/**
 * Can be used within shell scripts resp. batch files
 */
public class Log4Echo {
    private static Category log = Category.getInstance(Log4Echo.class);
    
    /**
     * main
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java " + new Log4Echo().getClass().getName() + "log-level log-message");
            return;
        }

        String level = args[0].toLowerCase();
        String message = args[1];
        if (level.equals("debug")) {
            log.debug(message);
        } else if (level.equals("info")) {
            log.info(message);
        } else if (level.equals("warn")) {
            log.warn(message);
        } else if (level.equals("error")) {
            log.error(message);
        } else if (level.equals("fatal")) {
            log.fatal(message);
        } else {
            log.error("No such log level: " + level + " " + message);
        }
    }
}
