/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.modules.monitoring;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 * Logs session information.
 */
public class SessionCountLogger implements HttpSessionListener {

    protected Logger log = Logger.getLogger(SessionCountLogger.class);
    private static Object LOCK = SessionCountLogger.class;
    private static int sessionCount = 0;

    public SessionCountLogger() {
    }

    public void sessionCreated(HttpSessionEvent event) {
        synchronized (LOCK) {
            sessionCount++;
        }
        log.info("Sessions: " + this.sessionCount);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        synchronized (LOCK) {
            --sessionCount;
        }
        log.info("Sessions: " + this.sessionCount);
    }
    
    public static int getSessionCount() {
        return sessionCount;
    }

}
