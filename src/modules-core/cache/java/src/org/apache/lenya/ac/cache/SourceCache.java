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

package org.apache.lenya.ac.cache;

import org.apache.avalon.framework.component.Component;

/**
 * Source cache component.
 * @version $Id$
 */
public interface SourceCache extends Component {

    /**
     * Avalon role of the SourceCache interface.
     */
    String ROLE = SourceCache.class.getName();

    /**
     * Returns a cached object or builds a new object when the cached
     * object does not exist or is not up to date.
     * @param sourceUri The URI to build the source from.
     * @param builder The builder to create a new object if needed.
     * @return An object.
     * @throws CachingException when something went wrong.
     */
    Object get(String sourceUri, InputStreamBuilder builder) throws CachingException;
    
}
