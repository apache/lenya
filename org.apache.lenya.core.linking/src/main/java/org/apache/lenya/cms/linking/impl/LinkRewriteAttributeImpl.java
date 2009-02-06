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
package org.apache.lenya.cms.linking.impl;

import org.apache.lenya.cms.linking.LinkRewriteAttribute;
import org.xml.sax.Attributes;

public class LinkRewriteAttributeImpl implements LinkRewriteAttribute {
    
    public String namespace;
    public String element;
    public String attribute;
    
    public String getNamespace() {
        return namespace;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public String getElement() {
        return element;
    }
    
    public void setElement(String element) {
        this.element = element;
    }
    
    public String getAttribute() {
        return attribute;
    }
    
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    /**
     * @param uri The namespace URI.
     * @param name The local name.
     * @param attrs The attributes.
     * @return If this configuration matches the parameters.
     */
    public boolean matches(String uri, String name, Attributes attrs) {
        return this.namespace.equals(uri) && this.element.equals(name)
                && attrs.getValue(this.attribute) != null;
    }

}
