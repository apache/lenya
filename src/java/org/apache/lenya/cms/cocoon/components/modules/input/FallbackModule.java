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

/* $Id: FallbackModule.java,v 1.2 2004/03/01 16:18:24 gregor Exp $  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;

/**
 * <p>
 * This module checks if a file exists in a publiation, and if not,
 * it chooses the core file. The attribute name must a path relatively
 * to the <code>webapps/lenya/lenya</code> directory.
 * </p>
 * <p>Example:
 * <code>{fallback:xslt/style.xsl}</code> looks if
 * <code>lenya/pubs/(publication-id)/lenya/xslt/style.xsl</code> exists,
 * and if not, it uses <code>lenya/xslt/style.xsl</code>.
 */
public class FallbackModule extends AbstractPageEnvelopeModule implements Serviceable {

    private ServiceManager manager;
    public static final String PATH_PREFIX = "lenya/";

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        String resolvedPath;

        PageEnvelope envelope = getEnvelope(objectModel);
        String publicationId = envelope.getPublication().getId();

        String corePath = PATH_PREFIX + name;
        String publicationPath =
            Publication.PUBLICATION_PREFIX_URI + "/" + publicationId + "/" + corePath;

        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context://" + publicationPath);
            if (source.exists()) {
                resolvedPath = publicationPath;
            } else {
                resolvedPath = corePath;
            }
        } catch (Exception e) {
            throw new ConfigurationException("Resolving attribute [" + name + "] failed: ", e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
        resolvedPath = resolvedPath.substring("lenya/".length());
        return resolvedPath;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        return Collections.EMPTY_SET.iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel)};

        return objects;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
