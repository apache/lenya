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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

public class ResourceTypeFactory {
    
    private SourceResolver sourceResolver;
    
    public ResourceType createResourceType(String configUri) throws Exception {
        Source source = null;
        try {
            source = this.sourceResolver.resolveURI(configUri);
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration config = builder.build(source.getInputStream());
            ResourceTypeImpl type = new ResourceTypeImpl();
            type.configure(config);
            return type;
        }
        finally {
            if (source != null) {
                this.sourceResolver.release(source);
            }
        }
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

}
