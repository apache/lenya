/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.publication;

import org.apache.lenya.cms.authoring.NodeCreatorInterface;

/**
 * @version $Id:$
 */
public interface ResourceType {
    
    /**
     * The Avalon service role.
     */
    String ROLE = ResourceType.class.getName();
    
    /**
     * Returns the name of this document type.
     * @return A string value.
     */
    String getName();

    /**
     * @return The source URI of the RelaxNG schema.
     */
    String getSchemaDefinitionSourceURI();

    /**
     * Returns an array of XPaths representing attributes to be rewritten
     * when a document URL has changed.
     * @return An array of strings.
     */
    String[] getLinkAttributeXPaths();

    /**
     * Returns the location of sample contents for this type
     * @return A string value.
     */
    String getSampleURI();
    
    /**
     * @return The creator.
     */
    NodeCreatorInterface getCreator();

    /**
     * @param name The name of the resource type.
     */
    void setName(String name);
}