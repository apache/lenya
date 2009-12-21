/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.modules.lucene;

/**
 * Map meta data elements to Lucene index fields.
 */
public interface MetaDataFieldRegistry {
    
    /**
     * The Avalon service role.
     */
    String ROLE = MetaDataFieldRegistry.class.getName();

    /**
     * @param namespace The namespace URI of the meta data element set.
     * @param elementName The name of the meta data element.
     * @return The name of the corresponding Lucene index field.
     */
    String getFieldName(String namespace, String elementName);
    
    /**
     * @return All field names.
     */
    String[] getFieldNames();

}
