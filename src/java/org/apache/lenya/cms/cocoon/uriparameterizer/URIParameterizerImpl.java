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

/* $Id$  */

package org.apache.lenya.cms.cocoon.uriparameterizer;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.xml.AbstractXMLConsumer;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.util.CacheMap;
import org.xml.sax.Attributes;

/**
 * Default mplementation of the {@link URIParameterizer}.
 */
public class URIParameterizerImpl extends AbstractLogEnabled implements URIParameterizer,
        Serviceable {

    /**
     * Consumer to handle URIParameterizer events.
     */
    public class URIParametrizerConsumer extends AbstractXMLConsumer {
        private boolean inParamElement = false;
        private String parameterValue = null;

        private Logger logger;

        /**
         * Ctor.
         * @param _logger The logger to use.
         */
        public URIParametrizerConsumer(Logger _logger) {
            this.logger = _logger;
        }

        /**
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
         *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String uri, String loc, String raw, Attributes a) {
            if (loc.equals("parameter")) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("start Element " + uri + ":" + loc + ":" + raw);
                }
                this.inParamElement = true;
            }
        }

        /**
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
         *      java.lang.String, java.lang.String)
         */
        public void endElement(String uri, String loc, String raw) {
            if (loc.equals("parameter")) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("stop Element " + uri + ":" + loc + ":" + raw);
                }
                this.inParamElement = false;
            }

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("processing Element " + uri + ":" + loc + ":" + raw);
            }
        }

        /**
         * @see org.xml.sax.ContentHandler#characters(char[], int, int)
         */
        public void characters(char[] ch, int start, int len) {
            if (this.inParamElement) {
                this.parameterValue = new String(ch, start, len);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("grab Element " + this.parameterValue);
                }
            }
        }

        /**
         * Returns the parameter value that was obtained.
         * @return A string.
         */
        public String getParameter() {
            return this.parameterValue;
        }

    }

    /**
     * Ctor.
     */
    public URIParameterizerImpl() {
	    // do nothing
    }

    /**
     * Receives the URI parameters for a source.
     * @param uri The URI.
     * @param src The source.
     * @param parameters The parameters.
     * @return The URI parameters.
     * @throws URIParameterizerException when something went wrong.
     */
    public Map parameterize(String uri, String src, Parameters parameters)
            throws URIParameterizerException {

        Map uriParameters = new HashMap();
        SourceResolver resolver = null;
        Source inputSource = null;

        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            URIParametrizerConsumer xmlConsumer = new URIParametrizerConsumer(getLogger());

            String[] parameterNames = parameters.getNames();

            for (int i = 0; i < parameterNames.length; i++) {

                String key = uri + "_" + parameterNames[i];
                String value = (String) cache.get(key);

                if (value == null) {
                    String parameterSrc = parameters.getParameter(parameterNames[i]) + "/" + src;

                    inputSource = resolver.resolveURI(parameterSrc);

                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("File resolved to " + inputSource.getURI());
                    }

                    SourceUtil.toSAX(inputSource, xmlConsumer);
                    value = xmlConsumer.getParameter();
                    cache.put(key, value);
                }

                uriParameters.put(parameterNames[i], value);
            }
        } catch (Exception e) {
            getLogger().error("URI parameterizing failed: ", e);
            throw new URIParameterizerException(e);
        } finally {
            if (resolver != null) {
                if (inputSource != null) {
                    resolver.release(inputSource);
                }
                this.manager.release(resolver);
            }
        }
        return uriParameters;
    }

    protected static final int CACHE_CAPACITY = 1000;

    private static Map cache = new CacheMap(CACHE_CAPACITY);

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

}