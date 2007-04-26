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

import java.util.Map;

import org.apache.avalon.framework.context.Context;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;

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
    public Clipboard getClipboard(Context context) {
        Session session = getSession(context);
        Clipboard clipboard = (Clipboard) session.getAttribute(getSessionAttributeName());
        return clipboard;
    }
    
    /**
     * @return The name of the session attribute to hold the clipboard.
     */
    protected String getSessionAttributeName() {
        return Clipboard.class.getName();
    }

    /**
     * @param context The context.
     * @return The session of the context.
     */
    protected Session getSession(Context context) {
        Map objectModel = ContextHelper.getObjectModel(context);
        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession(true);
        return session;
    }

    /**
     * Saves the clipboard to the session.
     * @param context The context.
     * @param clipboard The clipboard.
     */
    public void saveClipboard(Context context, Clipboard clipboard) {
        Session session = getSession(context);
        session.setAttribute(getSessionAttributeName(), clipboard);
    }
    
    /**
     * Removes the clipboard from the session.
     * @param context The context.
     */
    public void removeClipboard(Context context) {
        Session session = getSession(context);
        session.removeAttribute(getSessionAttributeName());
    }
    
}