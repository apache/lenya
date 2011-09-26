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
 * Owner of meta-data.
 *
 */
public interface MetaDataOwner {

    /**
     * Returns a meta data object.
     * @param namespaceUri The namespace URI.
     * @return A meta data object.
     * @throws MetaDataException if an error occurs.
     */
    MetaData getMetaData(String namespaceUri) throws MetaDataException;
    
    /**
     * Returns the URIs of the meta data currently supported by the owner.
     * @return An array of strings.
     * @throws MetaDataException if an error occurs.
     */
    String[] getMetaDataNamespaceUris() throws MetaDataException;
    
}
