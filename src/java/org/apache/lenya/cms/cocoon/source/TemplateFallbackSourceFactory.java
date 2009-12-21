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
package org.apache.lenya.cms.cocoon.source;

import org.apache.lenya.cms.publication.templating.ExistingAncestorSourceResolver;
import org.apache.lenya.cms.publication.templating.VisitingSourceResolver;

/**
 * Source factory following the fallback principle, resolving the existing ancestor of the existing resource.
 * For more information about the URL syntax, see {@link FallbackSourceFactory}.
 * 
 * @version $Id: FallbackSourceFactory.java 264153 2005-08-29 15:11:14Z andreas $
 */
public class TemplateFallbackSourceFactory extends FallbackSourceFactory {

    protected VisitingSourceResolver getSourceVisitor() {
        return new ExistingAncestorSourceResolver();
    }

}