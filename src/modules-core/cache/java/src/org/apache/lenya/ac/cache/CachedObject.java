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

package org.apache.lenya.ac.cache;

import org.apache.excalibur.source.SourceValidity;

/**
 * Cached object.
 * @version $Id$
 */
public class CachedObject {

    private SourceValidity validityObject;
    private Object value;
    
    /**
     * Returns the value.
     * @return An object.
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Returns the source validity.
     * @return A source validity.
     */
    public SourceValidity getValidityObject() {
        return this.validityObject;
    }

    /**
     * Ctor.
     * @param validity The source validity.
     * @param _value The value.
     */
    public CachedObject(SourceValidity validity, Object _value) {
        this.validityObject = validity;
        this.value = _value;
    }

}
