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
package org.apache.lenya.cms.jcr.mock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.cms.repo.metadata.Element;
import org.apache.lenya.cms.repo.metadata.ElementSet;
import org.apache.lenya.cms.repo.metadata.impl.ElementImpl;

public class TestElementSet implements ElementSet {

    private Map elements = new HashMap();

    public static final String NAME = "test";
    public static final String MULTIPLE_ELEMENT = "a";
    public static final String SINGLE_ELEMENT = "b";

    public TestElementSet() {
        this.elements.put(MULTIPLE_ELEMENT, new ElementImpl(MULTIPLE_ELEMENT, true));
        this.elements.put(SINGLE_ELEMENT, new ElementImpl(SINGLE_ELEMENT, false));
    }

    public Element[] getElements() {
        Collection values = this.elements.values();
        return (Element[]) values.toArray(new Element[values.size()]);
    }

    public Element getElement(String name) {
        return (Element) this.elements.get(name);
    }

}
