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
package org.apache.lenya.cms.site.usecases;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Helper class for clipboard handling.
 * 
 * @version $Id$
 */
public class ClipboardHelper {

    /**
     * Returns the clipboard attachted to the session.
     * @param context The context containing the session.
     * @return A clipboard or <code>null</code> if no clipboard is attached to
     *         the session.
     */
    public Clipboard getClipboard(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Clipboard) session.getAttribute(getSessionAttributeName());
    }
    
    /**
     * @return The name of the session attribute to hold the clipboard.
     */
    protected String getSessionAttributeName() {
        return Clipboard.class.getName();
    }

    /**
     * Saves the clipboard to the session.
     * @param context The context.
     * @param clipboard The clipboard.
     */
    public void saveClipboard(HttpServletRequest request, Clipboard clipboard) {
        HttpSession session = request.getSession();
        session.setAttribute(getSessionAttributeName(), clipboard);
    }
    
    /**
     * Removes the clipboard from the session.
     * @param context The context.
     */
    public void removeClipboard(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute(getSessionAttributeName());
    }
    
}