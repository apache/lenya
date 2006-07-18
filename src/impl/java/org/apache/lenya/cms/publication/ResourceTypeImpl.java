/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.publication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.xml.Schema;

/**
 * Resource type.
 * 
 * @version $Id:$
 */
public class ResourceTypeImpl extends AbstractLogEnabled implements Configurable, ThreadSafe,
        ResourceType {

    protected static final String SCHEMA_ELEMENT = "schema";
    protected static final String SRC_ATTRIBUTE = "src";
    protected static final String ELEMENT_REWRITE_ATTRIBUTE = "link-attribute";
    protected static final String ATTRIBUTE_XPATH = "xpath";
    protected static final String SAMPLE_NAME = "sample-name";
    protected static final String SAMPLE_NAME_ATTRIBUTE = "name";
    protected static final String ELEMENT_FORMAT = "format";
    protected static final String ATTRIBUTE_URI = "uri";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_LANGUAGE = "language";
    protected static final String EXPIRES_ELEMENT = "expires";
    protected static final String SECONDS_ATTRIBUTE = "seconds";

    private Schema schema = null;
    private String sampleUri = null;
    private String defaultSampleUri = null;
    private Map sampleUris = new HashMap();
    private String[] linkAttributeXPaths;
    private long expires = 0;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {

        try {

            Configuration schemaConf = config.getChild(SCHEMA_ELEMENT, false);

            if (schemaConf != null) {
                String uri = schemaConf.getAttribute(SRC_ATTRIBUTE);
                String language = schemaConf.getAttribute(ATTRIBUTE_LANGUAGE);
                this.schema = new Schema(language, uri);
            }

            // determine the sample content locations.
            Configuration[] samplesConf = config.getChildren(SAMPLE_NAME);
            for (int i = 0; i < samplesConf.length; i++) {
                if (samplesConf[i].getAttributeNames().length > 0)
                    this.sampleUris.put(samplesConf[i].getAttribute(SAMPLE_NAME_ATTRIBUTE),
                            samplesConf[i].getValue());
                else { // default sample doesn't have name attribute
                    this.sampleUri = samplesConf[i].getValue();
                    this.defaultSampleUri = samplesConf[i].getValue();
                }
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
            
            Configuration expiresConf = config.getChild(EXPIRES_ELEMENT, false);
            if (expiresConf != null) {
                this.expires = expiresConf.getAttributeAsLong(SECONDS_ATTRIBUTE);
            }

        } catch (Exception e) {
            throw new ConfigurationException("Configuring resource type failed: ", e);
        }

    }
    
    public String getExpires() {
        Date date = new Date();
        date.setTime(date.getTime() + this.expires * 1000l);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
        return sdf.format(date);
    }

    public Schema getSchema() {
        return this.schema;
    }

    public String[] getLinkAttributeXPaths() {
        return this.linkAttributeXPaths;
    }

    public String[] getSampleNames() {
        String[] names = new String[this.sampleUris.size()];
        Iterator it = this.sampleUris.keySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            names[count++] = (String) it.next();
        }
        return names;
    }

    public void setSampleURI(String name) {
        if (name.length() > 0 && this.sampleUris.containsKey(name))
            this.sampleUri = (String) this.sampleUris.get(name);
        else
            this.sampleUri = new String(this.defaultSampleUri);
    }

    public String getSampleURI() {
        return this.sampleUri;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return this.name;
    }

    private Map formats = new HashMap();

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

    /**
     * A format.
     */
    public static class Format {

        private String uri;

        /**
         * Ctor.
         * @param uri The uri.
         */
        public Format(String uri) {
            this.uri = uri;
        }

        /**
         * @return The uri.
         */
        public String getURI() {
            return this.uri;
        }

    }

}
