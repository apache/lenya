/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
 * Element implementation.
 */
public class ElementImpl implements Element {
    
    private String name;
    private boolean multiple;
    private String description = "";
    private boolean editable;
    
    /**
     * Ctor.
     * @param name The name.
     * @param isMultiple if the element can have multiple values.
     * @param isEditable if the element can be edited.
     */
    public ElementImpl(String name, boolean isMultiple, boolean isEditable) {
        this.name = name;
        this.multiple = isMultiple;
        this.editable = isEditable;
    }

    /**
     * Ctor.
     * @param name The name.
     * @param isMultiple if the element can have multiple values.
     * @param description The description of the element.
     */
    public ElementImpl(String name, boolean isMultiple, String description) {
        this.name = name;
        this.multiple = isMultiple;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public boolean isMultiple() {
        return this.multiple;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isEditable() {
        return this.editable;
    }

}
