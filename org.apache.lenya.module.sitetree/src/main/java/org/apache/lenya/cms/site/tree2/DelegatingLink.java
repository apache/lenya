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
package org.apache.lenya.cms.site.tree2;

import org.apache.lenya.cms.site.AbstractLink;

/**
 * Site tree link which delegates all operations to a shared link.
 */
public class DelegatingLink extends AbstractLink {
    
    /**
     * @param node The node which this link belongs to.
     * @param label The label.
     * @param language The language.
     */
    public DelegatingLink(DelegatingNode node, String label, String language) {
        super(node, label, language);
    }

    public void delete() {
        throw new UnsupportedOperationException();
    }

    public void setLabel(String label) {
        throw new UnsupportedOperationException();
    }

}
