/*
$Id: URIParameterizerImpl.java,v 1.3 2004/02/20 08:51:17 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
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
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class URIParameterizerImpl
    extends AbstractLogEnabled
    implements URIParameterizer, Serviceable {

    /**
     * Consumer to handle URIParameterizer events.
     *
     * @author $Author: andreas $
     * @version $Revision: 1.3 $
     */
    public class URIParametrizerConsumer extends AbstractXMLConsumer {
        private boolean inParamElement = false;
        private String parameterValue = null;

        private Logger logger;

        /**
         * Ctor.
         * @param logger The logger to use.
         */
        public URIParametrizerConsumer(Logger logger) {
            this.logger = logger;
        }

        /**
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String uri, String loc, String raw, Attributes a) {
            if (loc.equals("parameter")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("start Element " + uri + ":" + loc + ":" + raw);
                }
                inParamElement = true;
            }
        }

        /**
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String uri, String loc, String raw, Attributes a) {
            if (loc.equals("parameter")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("stop Element " + uri + ":" + loc + ":" + raw);
                }
                inParamElement = false;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("processing Element " + uri + ":" + loc + ":" + raw);
            }
        }

        /**
         * @see org.xml.sax.ContentHandler#characters(char[], int, int)
         */
        public void characters(char[] ch, int start, int len) {
            if (inParamElement) {
                parameterValue = new String(ch, start, len);
                if (logger.isDebugEnabled()) {
                    logger.debug("grab Element " + parameterValue);
                }
            }
        }

        /**
         * Returns the parameter value that was obtained.
         * @return A string.
         */
        public String getParameter() {
            return parameterValue;
        }

    }

    /**
     * Ctor.
     */
    public URIParameterizerImpl() {
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

        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            URIParametrizerConsumer xmlConsumer = new URIParametrizerConsumer(getLogger());

            String[] parameterNames = parameters.getNames();

            for (int i = 0; i < parameterNames.length; i++) {

                String key = uri + "_" + parameterNames[i];

                String value = (String) cache.get(key);

                if (value == null) {
                    String parameterSrc = parameters.getParameter(parameterNames[i]) + "/" + src;

                    Source inputSource = null;
                    try {
                        inputSource = resolver.resolveURI(parameterSrc);

                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("File resolved to " + inputSource.getURI());
                        }

                        SourceUtil.toSAX(inputSource, xmlConsumer);
                    } finally {
                        if (inputSource != null) {
                            resolver.release(inputSource);
                        }
                    }
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
                manager.release(resolver);
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
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
