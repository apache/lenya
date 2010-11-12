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
package org.apache.lenya.ac;

import org.apache.commons.validator.EmailValidator;

/**
 * Utility class for access control items.
 */
public class ItemUtil {

    /**
     * Checks if a string is a valid ID.
     * @param id The string to test.
     * @return A boolean value.
     */
    public static boolean isValidId(String id) {
        return id != null && id.matches("\\w+");
    }
    
    /**
     * Checks if a string is a valid e-mail address.
     * @param email The string.
     * @return A boolean value.
     */
    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

}
