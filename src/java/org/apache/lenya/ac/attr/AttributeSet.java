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

/**
 * Definition of user attribute names.
 */
public interface AttributeSet {

    /**
     * The service role.
     */
    String ROLE = AttributeSet.class.getName();

    /**
     * @return All available attribute names.
     */
    String[] getAttributeNames();
    
    /**
     * @param name An attribute name.
     * @return An attribute.
     */
    Attribute getAttribute(String name);
    
    /**
     * @return The name of the attribute set.
     */
    String getName();
    
}
