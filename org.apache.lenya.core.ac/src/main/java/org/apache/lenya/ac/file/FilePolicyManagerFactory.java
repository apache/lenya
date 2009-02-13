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
package org.apache.lenya.ac.file;

import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.cache.SourceCache;

public class FilePolicyManagerFactory {
    
    private SourceResolver sourceResolver;
    private SourceCache cache;
    
    public FilePolicyManager create() {
        FilePolicyManager manager = new FilePolicyManager();
        manager.setSourceResolver(this.sourceResolver);
        manager.setCache(cache);
        return manager;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    public void setCache(SourceCache cache) {
        this.cache = cache;
    }

}
