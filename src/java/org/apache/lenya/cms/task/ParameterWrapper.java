/*
$Id: ParameterWrapper.java,v 1.1 2003/08/25 15:40:55 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.util.NamespaceMap;
import org.apache.log4j.Category;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ParameterWrapper {
    
    private static Category log = Category.getInstance(ParameterWrapper.class);
    private NamespaceMap parameters;
    
    /**
     * Returns the un-prefixed parameters.
     * @return A map.
     */
    public Map getMap() {
        return parameters.getMap();
    }

    /**
     * Ctor.
     * @param prefixedParameters The prefixed parameters to wrap.
     */
    public ParameterWrapper(Map prefixedParameters) {
        parameters = new NamespaceMap(prefixedParameters, getPrefix());
    }
    
    /**
     * Returns the namespace prefix.
     * @return A string.
     */
    public abstract String getPrefix();
    
    /**
     * Adds a key-value pair. If the value is null, no pair is added.
     * @param key The key.
     * @param value The value.
     */
    public void put(String key, String value) {
        if (value != null) {
            log.debug("Setting parameter: [" + key + "] = [" + value + "]");
            parameters.put(key, value);
        }
        else {
            log.debug("Not setting parameter: [" + key + "] = [" + value + "]");
        }
    }
    
    /**
     * Returns the value for a key.
     * @param key The key.
     * @return The value.
     */
    public String get(String key) {
        return (String) parameters.get(key);
    }
    
    /**
     * Returns the required keys.
     * @return A string array.
     */
    protected abstract String[] getRequiredKeys();
    
    /**
     * Checks if this parameters object contains all necessary parameters.
     * @return A boolean value.
     */
    public boolean isComplete() {
        boolean complete = true;
        Map parameterMap = getMap();
        String[] requiredKeys = getRequiredKeys();
        int i = 0;
        while (complete && i < requiredKeys.length) {
            log.debug("Checking parameter: [" + requiredKeys[i] + "]");
            complete = complete && parameterMap.containsKey(requiredKeys[i]);
            log.debug("OK: [" + complete + "]");
            i++;
        }
        return complete;
    }

    /**
     * Returns the missing parameters parameters.
     * @return A string array.
     */
    public String[] getMissingKeys() {
        String[] requiredKeys = getRequiredKeys();
        Map parameterMap = getMap();
        List keyList = new ArrayList();
        for (int i = 0; i < requiredKeys.length; i++) {
            if (!parameterMap.containsKey(requiredKeys[i])) {
                keyList.add(requiredKeys[i]);
            }
        }
        return (String[]) keyList.toArray(new String[keyList.size()]);
    }
    
    /**
     * Parameterizes this wrapper with un-prefixed parameters.
     * @param parameters A parameters object.
     */
    public void parameterize(Parameters parameters) {
        String[] keys = parameters.getNames();
        for (int i = 0; i < keys.length; i++) {
            put(keys[i], parameters.getParameter(keys[i], null));
        }
    }
    
}
