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
 * A meta data element.
 */
public interface Element {

    /**
     * @return the name of the element.
     */
    String getName();

    /**
     * @return if the element can have multiple values.
     */
    boolean isMultiple();

    /**
     * @return the description of the element.
     */
    String getDescription();

    /**
     * @return if the element value can be edited.
     */
    boolean isEditable();

    /**
     * Copy all values if the meta data are copied.
     */
    int ONCOPY_COPY = 0;
    
    /**
     * Don't copy the values of this element if the meta data are copied.
     */
    int ONCOPY_IGNORE = 1;

    /**
     * Delete all values of this element if the meta data are copied.
     */
    int ONCOPY_DELETE = 2;

    /**
     * @return The action to be taken when meta data are copied from one owner to another.
     */
    int getActionOnCopy();

}