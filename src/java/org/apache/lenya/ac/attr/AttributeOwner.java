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
package org.apache.lenya.ac.attr;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;

/**
 * An attribute owner provides a map of key-value pairs which are used for authorization decisions.
 * The values are string arrays, i.e. the AttributeOwner can provide multiple values for each key.
 * 
 * @see Group#matches(AttributeOwner)
 * @see AttributeRuleEvaluator
 */
public interface AttributeOwner {

    /**
     * @return The names of all possible attributes.
     */
    String[] getAttributeNames();

    /**
     * @param name The attribute name.
     * @return The attribute values or <code>null</code> if no value is available for the
     *         attribute.
     * @throws AccessControlException if the attribute is not supported.
     */
    String[] getAttributeValues(String name) throws AccessControlException;

}
