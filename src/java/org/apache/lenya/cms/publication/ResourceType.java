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
package org.apache.lenya.cms.publication;

import java.util.Date;
import org.apache.lenya.xml.Schema;

/**
 * @version $Id:$
 */
public interface ResourceType {
    
    /**
     * The Avalon service role.
     */
    String ROLE = ResourceType.class.getName();
    
    /**
     * Prefix for translating the resource type name, e.g.
     * &lt;i18n:text&gt;resourceType-&lt;jx:out value="${resourceType.getName()}"/&gt;&lt;/i18n:text&gt;
     */
    String I18N_PREFIX = "resourceType-";
    
    /**
     * Returns the date at which point the requested resource is considered expired
     * @return a string in RFC 1123 date format
     */
    Date getExpires();
    
    /**
     * Returns the name of this document type.
     * @return A string value.
     */
    String getName();

    /**
     * @return The source URI of the RelaxNG schema.
     */
    Schema getSchema();

    /**
     * Returns an array of XPaths representing attributes to be rewritten
     * when a document URL has changed.
     * @return An array of strings.
     */
    String[] getLinkAttributeXPaths();

    /**
     * Returns the a sample contents and their names for this type
     * @return A set of the sample names
     */
    String[] getSampleNames();
    
    /**
     * Returns the location of sample contents for this type
     * @param name The name of the sample.
     * @return A string value.
     * @see #getSampleNames()
     */
    String getSampleURI(String name);
    
    /**
     * @param name The name of the resource type.
     */
    void setName(String name);
    
    /**
     * @return All supported formats.
     */
    String[] getFormats();
    
    /**
     * @param format The format.
     * @return The URI to get the formatted content at.
     */
    String getFormatURI(String format);
    
}