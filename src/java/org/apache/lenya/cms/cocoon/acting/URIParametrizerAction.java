/*
$Id: URIParametrizerAction.java,v 1.15 2003/08/04 13:48:45 egli Exp $
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
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ConfigurableComposerAction;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.xml.AbstractXMLConsumer;

import org.apache.excalibur.source.Source;

import org.apache.lenya.util.CacheMap;
import org.apache.log4j.Category;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author $Author: egli $
 * @version $Revision: 1.15 $
 */
public class URIParametrizerAction extends ConfigurableComposerAction {
    private static Category log = Category.getInstance(URIParametrizerAction.class);

    /**
     * DOCUMENT ME!
     *
     * @author $Author: egli $
     * @version $Revision: 1.15 $
     */
    public class URIParametrizerConsumer extends AbstractXMLConsumer {
        private boolean inParamElement = false;
        private String parameterValue = null;

        /**
         * DOCUMENT ME!
         *
         * @param uri DOCUMENT ME!
         * @param loc DOCUMENT ME!
         * @param raw DOCUMENT ME!
         * @param a DOCUMENT ME!
         */
        public void startElement(String uri, String loc, String raw, Attributes a) {
            if (loc.equals("parameter")) {
                log.debug("start Element " + uri + ":" + loc + ":" + raw);
                inParamElement = true;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param uri DOCUMENT ME!
         * @param loc DOCUMENT ME!
         * @param raw DOCUMENT ME!
         * @param a DOCUMENT ME!
         */
        public void endElement(String uri, String loc, String raw, Attributes a) {
            if (loc.equals("parameter")) {
                log.debug("stop Element " + uri + ":" + loc + ":" + raw);
                inParamElement = false;
            }

            log.debug("processing Element " + uri + ":" + loc + ":" + raw);
        }

        /**
         * DOCUMENT ME!
         *
         * @param ch DOCUMENT ME!
         * @param start DOCUMENT ME!
         * @param len DOCUMENT ME!
         */
        public void characters(char[] ch, int start, int len) {
            if (inParamElement) {
                parameterValue = new String(ch, start, len);
                log.debug("grab Element " + parameterValue);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String getParameter() {
            return parameterValue;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String src,
        Parameters parameters)
        throws Exception {

        Request request = ObjectModelHelper.getRequest(objectModel);
        String uri = request.getRequestURI();

        Map map = parameterize(uri, src, parameters, resolver);
        return map;

    }
    
    protected static final int CACHE_CAPACITY = 1000;

    private static Map cache = new CacheMap(CACHE_CAPACITY);

    /**
     * Receives the URI parameters for a source.
     * @param uri The URI.
     * @param src The source.
     * @param parameters The Action parameters.
     * @param resolver The source resolver.
     * @return The URI parameters.
     * @throws ParameterException when something went wrong.
     * @throws ProcessingException when something went wrong.
     * @throws SAXException when something went wrong.
     * @throws IOException when something went wrong.
     */
    protected Map parameterize(String uri, String src, Parameters parameters, SourceResolver resolver)
        throws ParameterException, ProcessingException, SAXException, IOException {
        Source inputSource = null;
        URIParametrizerConsumer xmlConsumer = new URIParametrizerConsumer();
        Map uriParameters = new HashMap();

        String[] parameterNames = parameters.getNames();

        for (int i = 0; i < parameterNames.length; i++) {
            
            String key = uri + "_" + parameterNames[i];
            
            String value = (String) cache.get(key);
            
            if (value == null) {
                String parameterSrc = parameters.getParameter(parameterNames[i]) + "/" + src;
                inputSource = resolver.resolveURI(parameterSrc);

                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug("File resolved to " + inputSource.getURI());
                }

                SourceUtil.toSAX(inputSource, xmlConsumer);
                value = xmlConsumer.getParameter();
                cache.put(key, value);
            }
            
            uriParameters.put(parameterNames[i], value);
        }
        return uriParameters;
    }

}

