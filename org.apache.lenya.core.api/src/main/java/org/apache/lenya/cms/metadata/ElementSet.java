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
package org.apache.lenya.cms.metadata;

/**
 * Definition of a set of meta data elements.
 */
public interface ElementSet {
    
    /**
     * @return The supported elements.
     */
    Element[] getElements();
    
    /**
     * @param name The name.
     * @return The element.
     * @throws MetaDataException if the element with this name does not exist.
     */
    Element getElement(String name) throws MetaDataException;
    
    /**
     * @return The namespace URI of this element set.
     */
    String getNamespaceUri();
    
    /**
     * Checks if an element with a certain name is contained.
     * @param name The name.
     * @return A boolean value.
     */
    boolean containsElement(String name);
    
}
