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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 */
public class RedirectMediaResource extends AbstractLogEnabled implements MediaResource {

    private String redirectUrl;

    /**
     * @param redirectURL The redirect URL.
     * @param logger The logger.
     */
    public RedirectMediaResource(String redirectURL, Logger logger) {
        ContainerUtil.enableLogging(this, logger);
        this.redirectUrl = redirectURL;
    }

    public String getContentType() {
        return null;
    }

    public Long getSize() {
        return null;
    }

    public InputStream getInputStream() {
        return null;
    }

    public Long getLastModified() {
        return null;
    }

    public void prepare(HttpServletResponse hres) {
        try {
            hres.sendRedirect(redirectUrl);
        } catch (IOException e) {
            // if redirect failed, we do nothing; the browser may have stopped
            // the
            // tcp/ip or whatever
            getLogger().error("redirect failed: url=" + redirectUrl, e);
        } catch (IllegalStateException ise) {
            // redirect failed, to find out more about the strange null null
            // exception
            // FIXME:pb:a decide if this catch has to be removed again, after
            // finding problem.
            getLogger().error("redirect failed: url=" + redirectUrl, ise);
            // introduced only more debug information but behavior is still the
            // same
            throw (ise);
        }
    }

    public void release() {
        // nothing to do
    }

}