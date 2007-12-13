/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lenya.modules.svg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.ParsedURLData;
import org.apache.batik.util.ParsedURLProtocolHandler;
import org.apache.cocoon.CascadingIOException;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.util.Assert;

/**
 * Batik URL protocol handler for protocols which are handled by the SourceResolver.
 */
public class ProtocolHandler extends AbstractLogEnabled implements ParsedURLProtocolHandler,
        ThreadSafe, Startable, Component, Serviceable, Parameterizable {

    protected static final String PARAM_PROTOCOL = "protocol";

    public static final String ROLE = ProtocolHandler.class.getName();
    
    private SourceResolver resolver;
    private ServiceManager manager;

    private String protocol;

    public ParsedURLData parseURL(String urlStr) {
        if (this.resolver == null) {
            throw new IllegalStateException("Please call setResolver() first!");
        }
        return new ParsedUrlData(getProtocolHandled(), this.resolver, urlStr);
    }

    public ParsedURLData parseURL(ParsedURL basepurl, String urlStr) {
        return parseURL(urlStr);
    }

    static class ParsedUrlData extends ParsedURLData {

        private Source source;
        private SourceResolver resolver;
        private String url;
        private long lastModified;

        public ParsedUrlData(String protocol, SourceResolver resolver, String url) {
            this.url = url;
            String prefix = protocol + ":";
            Assert.isTrue("valid URL", url.length() > prefix.length());
            this.path = url.substring(prefix.length());
            this.protocol = protocol;
            this.resolver = resolver;
            try {
                this.source = resolver.resolveURI(url);
                this.lastModified = this.source.getLastModified();
                this.contentType = this.source.getMimeType();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Open a stream for the data.
         */
        protected InputStream openStreamInternal(String userAgent, Iterator mimeTypes,
                Iterator encodingTypes) throws IOException {
            try {
                return this.source.getInputStream();
            } catch (Exception e) {
                throw new CascadingIOException("Cannot open URL " + this.url, e);
            } finally {
                this.resolver.release(this.source);
            }
        }

        // Batik uses toString for caching.
        // Use URL and last modified time for proper caching of source images.
        public String toString() {
            return url + source.getLastModified();
        }

        /**
         * Object.equals 
         */
        public boolean equals(Object obj) {
            if(obj == null) {
                return false;
            }
            if( ! (obj instanceof ParsedUrlData)) {
                return false;
            }
            // super.equals() doesn't take lastModified into account
            ParsedUrlData pud = (ParsedUrlData)obj;
            if(pud.url.equals(this.url) && pud.lastModified == this.lastModified ) {
                return true;
            }
            return false;
        }
 
        /**
         * Returns hashCode for object
         */
        public int hashCode() {
            return (url + lastModified).hashCode();
        }
    }

    public void start() throws Exception {
        this.resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
        ParsedURL.registerHandler(this);
    }

    public void stop() throws Exception {
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public String getProtocolHandled() {
        return this.protocol;
    }

    public void parameterize(Parameters params) throws ParameterException {
        this.protocol = params.getParameter(PARAM_PROTOCOL);
    }

}
