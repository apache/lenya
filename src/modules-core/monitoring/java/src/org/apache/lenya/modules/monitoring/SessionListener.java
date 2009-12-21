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

/**
 * A Listener which store the session object in a HashMap on creation and 
 * remove it if the session is destroyed.
 * 
 * @version $Id: SessionListener.java 473861 2006-11-12 03:51:14Z gregor $
 */

import javax.servlet.http.*;
import java.util.WeakHashMap;

public class SessionListener implements HttpSessionListener {

	private static WeakHashMap allSessions = new WeakHashMap();

	public void sessionCreated(HttpSessionEvent se) {

		HttpSession session = se.getSession();
		String sessionID = session.getId();
		allSessions.put(sessionID,session);				
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		String sessionID = session.getId();
		if (allSessions.containsKey(sessionID)) {
			allSessions.remove(sessionID);
		}
	}

	public WeakHashMap getAllSessions() {
		return allSessions;
	}	
}