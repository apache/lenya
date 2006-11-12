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
package org.apache.lenya.cms.repo.impl;

import org.apache.lenya.cms.repo.AssetType;
import org.apache.lenya.xml.Schema;

/**
 * Document type implementation.
 */
public class AssetTypeImpl implements AssetType {
    
    private String name;
    private Schema schema;
    private boolean isValidating;
    
    /**
     * Ctor.
     * @param name The name of the document type.
     * @param schema The schema to use. Can be <code>null</code>.
     * @param isValidating If the documents should be validated upon writing.
     */
    public AssetTypeImpl(String name, Schema schema, boolean isValidating) {
        this.name = name;
        this.schema = schema;
        this.isValidating = isValidating;
    }

    public String getName() {
        return this.name;
    }

    public Schema getSchema() {
        return this.schema;
    }

    public boolean isValidating() {
        return this.isValidating;
    }

}
