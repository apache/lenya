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

import java.util.HashMap;
import java.util.Map;

//florent : 
/*import org.apache.lenya.cms.repository.metadata.ElementSet;
import org.apache.lenya.cms.repository.metadata.MetaDataException;*/
import org.apache.lenya.cms.metadata.ElementSet;
import org.apache.lenya.cms.metadata.MetaDataException;

public class ElementSetWrapper implements org.apache.lenya.cms.metadata.ElementSet {

    //florent private org.apache.lenya.cms.repository.metadata.ElementSet delegate;
	private ElementSet delegate;
    private Map<String, ElementWrapper> elements = new HashMap<String, ElementWrapper>();

    public ElementSetWrapper(ElementSet delegate) {
        super();
        this.delegate = delegate;
    }

    public boolean containsElement(String name) {
        return this.delegate.containsElement(name);
    }

    public Element getElement(String name) throws org.apache.lenya.cms.metadata.MetaDataException {
        ElementWrapper wrapper = this.elements.get(name);
        if (wrapper == null) {
            try {
                wrapper = new ElementWrapper(this.delegate.getElement(name));
            } catch (MetaDataException e) {
                throw new org.apache.lenya.cms.metadata.MetaDataException(e);
            }
            this.elements.put(name, wrapper);
        }
        return wrapper;
    }

    public Element[] getElements() {
        //florent org.apache.lenya.cms.repository.metadata.Element[] elements = this.delegate.getElements();
    	Element[] elements = this.delegate.getElements();
        Element[] wrappers = new Element[elements.length];
        for (int i = 0; i < elements.length; i++) {
            try {
                wrappers[i] = getElement(elements[i].getName());
            } catch (org.apache.lenya.cms.metadata.MetaDataException e) {
                throw new RuntimeException(e);
            }
        }
        return wrappers;
    }

    public String getNamespaceUri() {
        // TODO Auto-generated method stub
        return null;
    }

}
