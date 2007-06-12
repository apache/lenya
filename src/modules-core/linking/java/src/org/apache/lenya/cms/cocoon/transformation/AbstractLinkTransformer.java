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
package org.apache.lenya.cms.cocoon.transformation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 * This transformer processes all links which are configured using
 * <code>&lt;transform/&gt;</code> elements:
 * </p>
 * <code><pre>
 *  &lt;map:transformer ... &gt;
 *    &lt;transform namespace=&quot;http://www.w3.org/1999/xhtml&quot; element=&quot;a&quot; attribute=&quot;href&quot;/&gt;
 *    &lt;transform namespace=&quot;...&quot; ... /&gt;
 *  &lt;/map:transformer&gt;
 * </pre></code>
 */
public abstract class AbstractLinkTransformer extends AbstractSAXTransformer {

    private List attrConfigs = new ArrayList();

    protected AttributeConfiguration[] getAttributeConfigurations() {
        return (AttributeConfiguration[]) this.attrConfigs
                .toArray(new AttributeConfiguration[this.attrConfigs.size()]);
    }

    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        Configuration[] transformConfigs = config.getChildren("transform");
        for (int i = 0; i < transformConfigs.length; i++) {
            String namespace = transformConfigs[i].getAttribute("namespace");
            String element = transformConfigs[i].getAttribute("element");
            String attribute = transformConfigs[i].getAttribute("attribute");
            AttributeConfiguration attrConfig = new AttributeConfiguration(namespace, element,
                    attribute);
            this.attrConfigs.add(attrConfig);
        }
    }

    /**
     * Declaration of an attribute which should be transformed.
     */
    public static class AttributeConfiguration {

        protected final String element;
        protected final String namespace;
        protected final String attribute;

        /**
         * @param namespace The namespace of the element.
         * @param element The local name of the element.
         * @param attribute The name of the attribute to transform.
         */
        public AttributeConfiguration(String namespace, String element, String attribute) {
            this.namespace = namespace;
            this.element = element;
            this.attribute = attribute;
        }

        /**
         * @param uri The namespace URI.
         * @param name The local name.
         * @param attrs The attributes.
         * @return If this configuration matches the parameters.
         */
        public boolean matches(String uri, String name, Attributes attrs) {
            return this.namespace.equals(uri) && this.element.equals(name)
                    && attrs.getValue(this.attribute) != null;
        }

    }

    protected AttributeConfiguration[] getMatchingConfigurations(String namespace,
            String localName, Attributes attrs) {
        List configs = new ArrayList();
        for (Iterator i = this.attrConfigs.iterator(); i.hasNext();) {
            AttributeConfiguration config = (AttributeConfiguration) i.next();
            if (config.matches(namespace, localName, attrs)) {
                configs.add(config);
            }
        }
        return (AttributeConfiguration[]) configs
                .toArray(new AttributeConfiguration[configs.size()]);
    }

    protected boolean existsMatchingConfiguration(String namespace, String localName) {
        boolean matches = false;
        AttributeConfiguration[] attrConfigs = getAttributeConfigurations();
        for (int i = 0; i < attrConfigs.length; i++) {
            if (attrConfigs[i].namespace.equals(namespace)
                    && attrConfigs[i].element.equals(localName)) {
                matches = true;
            }
        }
        return matches;
    }

    protected String indent = "";
    protected boolean ignoreLinkElement = false;

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String name, String qname, Attributes attrs)
            throws SAXException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    this.indent + "<" + qname + "> (ignoreAElement = " + this.ignoreLinkElement
                            + ")");
            this.indent += "  ";
        }

        AttributesImpl newAttrs = null;

        AttributeConfiguration[] configs = getMatchingConfigurations(uri, name, attrs);

        if (configs.length > 0) {

            this.ignoreLinkElement = false;

            for (int i = 0; i < configs.length; i++) {
                AttributeConfiguration config = configs[i];
                String linkUrl = attrs.getValue(config.attribute);
                try {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug(this.indent + "link URL: [" + linkUrl + "]");
                    }
                    newAttrs = new AttributesImpl(attrs);
                    handleLink(linkUrl, config, newAttrs);
                } catch (final Exception e) {
                    getLogger().error("startElement failed: ", e);
                    throw new SAXException(e);
                }
            }
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this.indent + "ignoreAElement: " + this.ignoreLinkElement);
        }

        if (!(configs.length > 0 && this.ignoreLinkElement)) {
            if (newAttrs != null) {
                attrs = newAttrs;
            }
            super.startElement(uri, name, qname, attrs);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this.indent + "<" + qname + "> sent");
            }
        }
    }

    /**
     * Handle a link in the source SAX stream.
     * @param linkUrl The link URL.
     * @param config The attribute configuration which matched the link.
     * @param newAttrs The new attributes which will be added to the result element.
     * @throws Exception if an error occurs.
     */
    protected abstract void handleLink(String linkUrl, AttributeConfiguration config,
            AttributesImpl newAttrs) throws Exception;
    
    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String name, String qname) throws SAXException {
        if (getLogger().isDebugEnabled()) {
            this.indent = this.indent.substring(2);
            getLogger().debug(this.indent + "</" + qname + ">");
        }
        if (existsMatchingConfiguration(uri, name) && this.ignoreLinkElement) {
            this.ignoreLinkElement = false;
        } else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this.indent + "</" + qname + "> sent");
            }
            super.endElement(uri, name, qname);
        }
    }

}
