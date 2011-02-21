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

//florent import org.apache.lenya.cms.repository.metadata.Element;
import org.apache.lenya.cms.metadata.Element;

public class ElementWrapper implements org.apache.lenya.cms.metadata.Element {
    
    //private org.apache.lenya.cms.repository.metadata.Element delegate;
	private org.apache.lenya.cms.metadata.Element delegate;

    public ElementWrapper(Element delegate) {
        this.delegate = delegate;
    }

    public int getActionOnCopy() {
        return this.delegate.getActionOnCopy();
    }

    public String getDescription() {
        return this.delegate.getDescription();
    }

    public String getName() {
        return this.delegate.getName();
    }

    public boolean isEditable() {
        return this.delegate.isEditable();
    }

    public boolean isMultiple() {
        return this.delegate.isMultiple();
    }

    public boolean isSearchable() {
        return this.delegate.isSearchable();
    }

}
