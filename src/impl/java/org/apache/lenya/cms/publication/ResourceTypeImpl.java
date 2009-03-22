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
package org.apache.lenya.cms.publication;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.util.Assert;
import org.apache.lenya.xml.Schema;

/**
 * Resource type.
 * 
 * @version $Id:$
 */
public class ResourceTypeImpl extends AbstractLogEnabled implements Configurable, ThreadSafe,
        ResourceType, Serviceable {

    /**
     * The default sample name.
     */
    public static final String DEFAULT_SAMPLE_NAME = "Default Sample";

    protected static final String ATTRIBUTE_URI = "uri";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ELEMENT_SCHEMA = "schema";
    protected static final String ATTRIBUTE_NAMESPACE = "namespace";
    protected static final String ELEMENT_REWRITE_ATTRIBUTE = "link-attribute";
    protected static final String ATTRIBUTE_XPATH = "xpath";
    protected static final String ELEMENT_SAMPLES = "samples";
    protected static final String ELEMENT_SAMPLE = "sample";
    protected static final String ATTRIBUTE_MIME_TYPE = "mime-type";
    protected static final String ELEMENT_FORMAT = "format";
    protected static final String ELEMENT_EXPIRES = "expires";
    protected static final String ATTRIBUTE_SECONDS = "seconds";
    protected static final String ELEMENT_SUPPORTS = "supports";
    protected static final String ELEMENT_MIME_TYPE = "mime-type";

    private Schema schema = null;
    private String[] linkAttributeXPaths;
    private Map samples;
    private String samplesUri = null;
    private Map formats = new HashMap();
    private long expires = 0;
    private String name;
    private ServiceManager manager;
    private String[] mimeTypes;

    /**
     * A format.
     */
    public static class Format {
        private String uri;
        /**
         * @param uri The uri.
         */
        public Format(String uri) {
            Assert.notNull("uri", uri);
            this.uri = uri;
        }
        /**
         * @return The uri.
         */
        public String getURI() {
            return this.uri;
        }
    }


    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {

        try {

            Configuration schemaConf = config.getChild(ELEMENT_SCHEMA, false);

            if (schemaConf != null) {
                String uri = schemaConf.getAttribute(ATTRIBUTE_URI);
                String language = schemaConf.getAttribute(ATTRIBUTE_NAMESPACE);
                this.schema = new Schema(language, uri);
            }

            // determine the sample content locations.
            Configuration samplesFileConf = config.getChild(ELEMENT_SAMPLES, false);
            if (samplesFileConf != null) {
                this.samplesUri = samplesFileConf.getAttribute(ATTRIBUTE_URI);
            }
            else {
                this.samples = loadSamples(config);
            }
            

            Configuration[] rewriteAttributeConfigs = config.getChildren(ELEMENT_REWRITE_ATTRIBUTE);
            List xPaths = new ArrayList();
            for (int i = 0; i < rewriteAttributeConfigs.length; i++) {
                String xPath = rewriteAttributeConfigs[i].getAttribute(ATTRIBUTE_XPATH);
                xPaths.add(xPath);
            }
            this.linkAttributeXPaths = (String[]) xPaths.toArray(new String[xPaths.size()]);

            Configuration[] formatConfigs = config.getChildren(ELEMENT_FORMAT);
            for (int i = 0; i < formatConfigs.length; i++) {
                String name = formatConfigs[i].getAttribute(ATTRIBUTE_NAME);
                String uri = formatConfigs[i].getAttribute(ATTRIBUTE_URI);
                this.formats.put(name, new Format(uri));
            }

            Configuration expiresConf = config.getChild(ELEMENT_EXPIRES, false);
            if (expiresConf != null) {
                this.expires = expiresConf.getAttributeAsLong(ATTRIBUTE_SECONDS);
            }
            
            Configuration supportsConf = config.getChild(ELEMENT_SUPPORTS, false);
            if (supportsConf != null) {
                Configuration[] mimeTypeConfigs = supportsConf.getChildren(ELEMENT_MIME_TYPE);
                this.mimeTypes = new String[mimeTypeConfigs.length];
                List types = new ArrayList();
                for (int i = 0; i < mimeTypeConfigs.length; i++) {
                    this.mimeTypes[i] = mimeTypeConfigs[i].getValue();
                }
            }
            
        } catch (Exception e) {
            throw new ConfigurationException("Configuring resource type failed: ", e);
        }

    }

    protected Map loadSamples(Configuration samplesParentConfig) throws ConfigurationException {
        Configuration[] samplesConf = samplesParentConfig.getChildren(ELEMENT_SAMPLE);
        Map samples = new LinkedHashMap();
        for (int i = 0; i < samplesConf.length; i++) {
            String name = samplesConf[i].getAttribute(ATTRIBUTE_NAME, DEFAULT_SAMPLE_NAME);
            String mimeType = samplesConf[i].getAttribute(ATTRIBUTE_MIME_TYPE);
            String uri = samplesConf[i].getAttribute(ATTRIBUTE_URI);
            samples.put(name, new Sample(name, mimeType, uri));
        }
        return samples;
    }

    protected Configuration readConfiguration(String uri) throws ConfigurationException {
        Configuration config;
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(uri);
            config = builder.build(source.getInputStream());
        }
        catch (Exception e) {
            throw new ConfigurationException("Loading samples from URI [" + uri + "] failed: ", e);
        }
        finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
        return config;
    }

    public Date getExpires() {
        Date date = new Date();
        date.setTime(date.getTime() + this.expires * 1000l);
        return date;
    }

    public Schema getSchema() {
        return this.schema;
    }

    public String[] getLinkAttributeXPaths() {
        return this.linkAttributeXPaths;
    }
    
    protected Map getSamples() {
        if (this.samplesUri == null) {
            return this.samples;
        }
        else {
            try {
                Configuration samplesConfig = readConfiguration(this.samplesUri);
                return loadSamples(samplesConfig);
            } catch (ConfigurationException e) {
                throw new RuntimeException(e);
            }
            
        }
    }

    public String[] getSampleNames() {
        Set names = getSamples().keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }
    
    public Sample getSample(String name) {
        Map samples = getSamples();
        if (!samples.containsKey(name)) {
            throw new IllegalArgumentException("No sample with name [" + name + "] found.");
        }
        return (Sample) samples.get(name);
    }

    public void setName(String name) {
        Assert.notNull("name", name);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String[] getFormats() {
        Set names = this.formats.keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }

    public String getFormatURI(String format) {

        if (!this.formats.containsKey(format)) {
            throw new RuntimeException("The resource type [" + getName()
                    + "] does not support the format [" + format + "].");
        }

        return ((Format) this.formats.get(format)).getURI();
    }
    
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
    
    public String toString() {
        return getName();
    }

    public String[] getMimeTypes() {
        return this.mimeTypes;
    }
    
}
