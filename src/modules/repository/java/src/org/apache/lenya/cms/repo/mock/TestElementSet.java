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
package org.apache.lenya.cms.repo.mock;

import org.apache.lenya.cms.repo.metadata.Element;
import org.apache.lenya.cms.repo.metadata.impl.ElementImpl;

public interface TestElementSet {

    public static final String NAME = "test";
    public static final String MULTIPLE_ELEMENT = "a";
    public static final String SINGLE_ELEMENT = "b";

    Element[] ELEMENTS = { new ElementImpl(MULTIPLE_ELEMENT, true),
            new ElementImpl(SINGLE_ELEMENT, false) };

}
