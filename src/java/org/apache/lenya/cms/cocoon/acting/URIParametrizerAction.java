/*
$Id
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

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.ConfigurableComposerAction;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.xml.AbstractXMLConsumer;

import org.apache.excalibur.source.Source;

import org.apache.log4j.Category;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.10 $
 */
public class URIParametrizerAction extends ConfigurableComposerAction {
    static Category log = Category.getInstance(URIParametrizerAction.class);

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision: 1.10 $
     */
    public class URIParametrizerConsumer extends AbstractXMLConsumer {
        boolean inParamElement = false;
        String parameterValue = null;

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
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        Source inputSource = null;
        URIParametrizerConsumer xmlConsumer = new URIParametrizerConsumer();

        Map map = new HashMap();

        /*
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("processing file " + src);
        }

        Request request = ObjectModelHelper.getRequest(objectModel);

        String requestUri = request.getRequestURI();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("request URI (not processed): " + requestUri);
        }

        String servletPath = request.getServletPath();
        if (requestUri.startsWith(servletPath)) {
            requestUri = requestUri.substring(0, servletPath.length());
        }

        if (requestUri.startsWith("/")) {
            requestUri = requestUri.substring(0, 1);
        }

        String publicationId = requestUri.substring(0, requestUri.indexOf("/"));
        requestUri = requestUri.substring(0, publicationId.length());

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("request URI (processed): " + requestUri);
        }
        */
        String[] parameterNames = parameters.getNames();

        for (int i = 0; i < parameterNames.length; i++) {
            String parameterSrc = parameters.getParameter(parameterNames[i]) + "/" + src;
            inputSource = resolver.resolveURI(parameterSrc);

            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("file resolved to " + inputSource.getURI());
            }

            SourceUtil.toSAX(inputSource, xmlConsumer);
            map.put(parameterNames[i], xmlConsumer.getParameter());
        }

        return map;
    }
}
