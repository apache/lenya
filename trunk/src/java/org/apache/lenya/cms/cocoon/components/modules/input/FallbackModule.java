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

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;

/**
 * <p>
 * This module checks if a file exists in a publiation, and if not, it chooses the core file. The
 * attribute name must a path relatively to the <code>webapps/lenya/lenya</code> directory.
 * </p>
 * <p>
 * Example: <code>{fallback:xslt/style.xsl}</code> looks if
 * <code>lenya/pubs/(publication-id)/lenya/xslt/style.xsl</code> exists, and if not, it uses
 * <code>lenya/xslt/style.xsl</code>.
 * 
 * @version $Id$
 */
public class FallbackModule extends AbstractPageEnvelopeModule {

    private String[] baseUris;

    /**
     * <code>PATH_PREFIX</code> The path prefix from the webapp
     */
    public static final String PATH_PREFIX = "lenya/";

    protected static final String ELEMENT_PATH = "directory";

    protected static final String ATTRIBUTE_SRC = "src";

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);

        Configuration[] pathConfigs = conf.getChildren(ELEMENT_PATH);
        List baseUriList = new ArrayList();

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            Source source = null;
            for (int i = 0; i < pathConfigs.length; i++) {
                String uri = pathConfigs[i].getAttribute(ATTRIBUTE_SRC);
                try {
                    source = resolver.resolveURI(uri);
                    if (source.exists()) {
                        File file = SourceUtil.getFile(source);
                        if (file.isDirectory()) {
                            baseUriList.add(uri);
                        } else {
                            getLogger().warn("Omitting path [" + uri + "] (not a directory).");
                        }
                    } else {
                        getLogger().warn("Omitting path [" + uri + "] (does not exist).");
                    }
                } catch (Exception e) {
                    getLogger().error("Could not resolve path [" + uri + "]: ", e);
                    throw e;
                } finally {
                    if (source != null) {
                        resolver.release(source);
                    }
                }
            }
        } catch (Exception e) {
            throw new ConfigurationException("Configuring failed: ", e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }

        this.baseUris = (String[]) baseUriList.toArray(new String[baseUriList.size()]);
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving file for path [" + name + "]");
        }

        String resolvedUri = resolveURI(name, objectModel);
        return resolvedUri;
    }

    /**
     * Resolves the URI for a certain path.
     * @param path The path.
     * @param objectModel The object model.
     * @return A string.
     * @throws ConfigurationException if an error occurs.
     */
    protected String resolveURI(final String path, Map objectModel) throws ConfigurationException {
        String resolvedUri = null;
        String checkedUris = "\n";

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            String[] _baseUris = getBaseURIs(objectModel, path);
            Source source = null;
            int i = 0;
            while (resolvedUri == null && i < _baseUris.length) {
                String uri = _baseUris[i] + "/" + path;

                checkedUris += uri + "\n";

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Trying to resolve URI [" + uri + "]");
                }

                try {
                    source = resolver.resolveURI(uri);
                    if (source.exists()) {
                        resolvedUri = uri;
                    } else {
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("Skipping URI [" + uri + "] (does not exist).");
                        }
                    }
                } catch (Exception e) {
                    getLogger().error("Could not resolve URI [" + uri + "]: ", e);
                    throw e;
                } finally {
                    if (source != null) {
                        resolver.release(source);
                    }
                }
                i++;
            }

        } catch (Exception e) {
            throw new ConfigurationException("Resolving attribute [" + path + "] failed: ", e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }

        if (resolvedUri == null) {
            /*
            throw new ConfigurationException("Could not resolve file for path [" + path + "]."
                    + "\nChecked URIs:" + checkedUris);
            */
            resolvedUri = this.baseUris[this.baseUris.length - 1] + "/" + path;
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("No URI resolved, choosing last defined URI: [" + resolvedUri + "]");
            }
        }
        else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Resolved URI: [" + resolvedUri + "]");
            }
        }
        return resolvedUri;
    }

    /**
     * Returns the base directory URIs in the order they should be traversed.
     * @param objectModel The object model.
     * @param attributeName The name of the module attribute.
     * @return An array of strings.
     * @throws ConfigurationException if an error occurs.
     */
    protected String[] getBaseURIs(Map objectModel, String attributeName) throws ConfigurationException {
        return this.baseUris;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        return Collections.EMPTY_SET.iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };

        return objects;
    }

}