/*
$Id: NamespaceMap.java,v 1.8 2004/02/17 09:39:00 andreas Exp $
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
package org.apache.lenya.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * An object of this class provides an easy way to access
 * Strings in a Map that are prefixed like "prefix.foo".
 * The actual map wrapped by this object can contain more
 * key-value-pairs, but you can access only the prefixed keys
 * through the mapper.
 *
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class NamespaceMap {
    public static final String SEPARATOR = ".";
    private Map map;
    private String prefix;

    /**
     * Creates a new NamespaceMap object.
     * @param prefix The prefix.
     */
    public NamespaceMap(String prefix) {
        this(new HashMap(), prefix);
    }

    /**
     * Creates a new NamespaceMap.
     * @param map A map containing the prefixed key-value-pairs.
     * @param prefix The prefix.
     */
    public NamespaceMap(Map map, String prefix) {
        this.map = map;
        this.prefix = prefix;
    }

    /**
     * Returns the prefix.
     * @return A string.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the namespace prefix.
     * @return The namespace prefix.
     */
    protected Map getMapObject() {
        return map;
    }

    /**
     * Returns a map that contains only the un-prefixed key-value-pairs.
     * @return The map.
     */
    public Map getMap() {
        Map resultMap = new HashMap();

        Set keys = getMapObject().keySet();

        for (Iterator i = keys.iterator(); i.hasNext();) {
            Object key = i.next();

            if (key instanceof String) {
                String keyString = (String) key;

                if (keyString.startsWith(getPrefix() + SEPARATOR)) {
                    resultMap.put(getShortName(getPrefix(), keyString), getMapObject().get(key));
                }
            }
        }

        return resultMap;
    }

    /**
     * Puts a value for prefixed key into the map.
     * @param key The key without prefix.
     * @param value The value.
     */
    public void put(String key, Object value) {
        getMapObject().put(getFullName(getPrefix(), key), value);
    }

    /**
     * Returns the value for a prefixed key.
     * @param key The key without prefix.
     * @return The value.
     */
    public Object get(String key) {
        return getMap().get(key);
    }

    /**
     * Returns the full (prefixed) key for a short (un-prefixed) key.
     * @param prefix The prefix.
     * @param key The un-prefixed key.
     * @return A string (prefix + {@link #SEPARATOR} + key).
     */
    public static String getFullName(String prefix, String key) {
        return prefix + SEPARATOR + key;
    }

    /**
     * Returns the short (un-prefixed) key for a full (prefixed) key.
     * @param prefix The prefix.
     * @param key The full (prefixed) key.
     * @return A string.
     */
    public static String getShortName(String prefix, String key) {
        return key.substring(prefix.length() + SEPARATOR.length());
    }
    
    /**
     * Puts all prefixed key-value-pairs of map into this map.
     * @param map A map.
     */
    public void putAll(Map map) {
        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            put(key, map.get(key));
        }
    }
    
    /**
     * Returns a map with prefixed keys.
     * @return A map.
     */
    public Map getPrefixedMap() {
        return new HashMap(getMapObject());
    }
    
}
