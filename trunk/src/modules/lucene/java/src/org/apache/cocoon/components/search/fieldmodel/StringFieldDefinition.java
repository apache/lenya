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
package org.apache.cocoon.components.search.fieldmodel;

import org.apache.lucene.document.Field;

/**
 * String Field Definition (used for Text and Keyword type)
 * 
 * @author Nicolas Maisonneuve
 */
public final class StringFieldDefinition extends FieldDefinition {

    public StringFieldDefinition(String name, int type) {
        super(name, type);
    }

    /**
     * Create a Lucene Field
     * 
     * @param value
     *            value to index
     * @return
     * @see org.apache.lucene.document.Field
     */
    public final Field createLField(String value) {
        return new Field(name, value, store, true, index);
    }

}
