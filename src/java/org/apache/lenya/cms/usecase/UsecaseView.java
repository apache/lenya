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
package org.apache.lenya.cms.usecase;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Information about a usecase view.
 * 
 * @version $Id:$
 */
public class UsecaseView implements Configurable {
    
    protected static final String ATTRIBUTE_TEMPLATE_URI = "template";

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {
        this.templateUri = config.getAttribute(ATTRIBUTE_TEMPLATE_URI);
    }

    private String templateUri;
    
    /**
     * @return The URI of the JX template;
     */
    public String getTemplateURI() {
        if (this.templateUri == null) {
            throw new RuntimeException("The template URI was not configured!");
        }
        return this.templateUri;
    }
    
}