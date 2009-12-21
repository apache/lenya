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
package org.apache.lenya.util;

/**
 * String utility.
 */
public final class StringUtil {
    
    /**
     * Joins an array of objects into a string.
     * @param objects The objects.
     * @param delimiter The delimiter to use.
     * @return A string.
     */
    public static final String join(Object[] objects, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < objects.length; i++) {
            if (i > 0) {
                buffer.append(delimiter);
            }
            buffer.append(objects[i].toString());
        }
        return buffer.toString();
    }

}
