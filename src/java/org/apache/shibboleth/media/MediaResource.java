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
package org.apache.shibboleth.media;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * TODO:fj:b intro. method isIndependent -> no need to lock while serving this
 * resource (e.g. user can continue to click while downloading a large file)
 */
public interface MediaResource {

    /**
     * @return
     */
    public String getContentType();

    /**
     * @return
     */
    public Long getSize();

    /**
     * @return
     */
    public InputStream getInputStream();

    /**
     * @return
     */
    public Long getLastModified();

    /**
     * @param hres
     */
    public void prepare(HttpServletResponse hres);

    /**
     * 
     */
    public void release();

}