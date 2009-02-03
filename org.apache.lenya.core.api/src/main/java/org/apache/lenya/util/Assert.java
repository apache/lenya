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
 * Assertion utility.
 */
public final class Assert {

    /**
     * @param parameterName The parameter name to use in the exception.
     * @param object The object.
     * @throws RuntimeException if the object is null.
     */
    public static final void notNull(String parameterName, Object object) {
        if (object == null) {
            throw new RuntimeException("The parameter [" + parameterName + "] must not be null!");
        }
    }

    /**
     * @param parameterName The parameter name to use in the exception.
     * @param object The object.
     * @throws RuntimeException if the object is null.
     */
    public static final void notNull(Object object) {
        notNull("undefined", object);
    }

    /**
     * @param expression The expression to check.
     * @param value The value.
     * @throws RuntimeException if the value is not true.
     */
    public static final void isTrue(String expression, boolean value) {
        if (!value) {
            throw new RuntimeException("The expression [" + expression + "] is not true!");
        }
    }
}
