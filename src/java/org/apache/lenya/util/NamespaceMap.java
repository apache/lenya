/*
 * $Id: NamespaceMap.java,v 1.2 2003/04/24 13:53:14 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * An object of this class provides an easy way to access
 * Strings in a Map that are prefixed like "prefix.foo".
 *
 * @author <a href="mailto:ah@wyona.org">Andreas Hartmann</a>
 */
public class NamespaceMap {
    public static final String SEPARATOR = ".";
    private Map map;
    private String prefix;

    /**
     * Creates a new JobDataMapWrapper object.
     *
     * @param prefix DOCUMENT ME!
     */
    public NamespaceMap(String prefix) {
        this(new HashMap(), prefix);
    }

    /**
     * Creates a new instance of JobDataMapWrapper
     *
     * @param map DOCUMENT ME!
     * @param prefix DOCUMENT ME!
     */
    public NamespaceMap(Map map, String prefix) {
        this.map = map;
        this.prefix = prefix;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the namespace prefix.
     *
     * @return The namespace prefix.
     */
    protected Map getMapObject() {
        return map;
    }

    /**
     * Returns a Map that contains only the prefixed objects.
     *
     * @return The map.
     */
    public Map getMap() {
        Map resultMap = new HashMap();
        
        Set keys = getMapObject().keySet();
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
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
     *
     * @param key The key without prefix.
     * @param value The value.
     */
    public void put(String key, String value) {
        getMapObject().put(getFullName(getPrefix(), key), value);
    }

    /**
     * Returns the value for a prefixed key.
     *
     * @param key The key without prefix.
     *
     * @return The value.
     */
    public Object get(String key) {
        return getMap().get(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getFullName(String prefix, String key) {
        return prefix + SEPARATOR + key;
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getShortName(String prefix, String key) {
        return key.substring(prefix.length() + SEPARATOR.length());
    }
}
