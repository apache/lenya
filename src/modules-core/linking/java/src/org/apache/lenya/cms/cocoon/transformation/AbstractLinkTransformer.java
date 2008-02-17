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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.linking.LinkRewriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 * This transformer processes all links which are configured using
 * <code>&lt;transform/&gt;</code> elements:
 * </p>
 * <code><pre>
 *   &lt;map:transformer ... &gt;
 *     &lt;transform namespace=&quot;http://www.w3.org/1999/xhtml&quot; element=&quot;a&quot; attribute=&quot;href&quot;/&gt;
 *     &lt;transform namespace=&quot;...&quot; ... /&gt;
 *   &lt;/map:transformer&gt;
 * </pre></code>
 */
public abstract class AbstractLinkTransformer extends AbstractSAXTransformer {
    
    /**
     * Set of supported local names for quick pre-checks.
     */
    private Set localNames = new HashSet();
    
    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        Configuration[] transformConfigs = config.getChildren("transform");
        for (int i = 0; i < transformConfigs.length; i++) {
            String namespace = transformConfigs[i].getAttribute("namespace");
            String element = transformConfigs[i].getAttribute("element");
            String attribute = transformConfigs[i].getAttribute("attribute");
            AttributeConfiguration attrConfig = new AttributeConfiguration(namespace, element,
                    attribute);
            String key = getCacheKey(namespace, element);
            Set configs = (Set) this.namespaceLocalname2configSet.get(key);
            if (configs == null) {
                configs = new HashSet();
                this.localNames.add(element);
                this.namespaceLocalname2configSet.put(key, configs);
            }
            configs.add(attrConfig);
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

    /**
     * @param namespace The namespace URI.
     * @param localName The local name.
     * @param attrs The attributes.
     * @return A set of {@link AttributeConfiguration} objects.
     */
    protected Set getMatchingConfigurations(String namespace,
            String localName, Attributes attrs) {
        
        // pre-check for performance reasons
        if (!existsMatchingConfiguration(namespace, localName)) {
            return Collections.EMPTY_SET;
        }
        
        String key = getCacheKey(namespace, localName);
        
        // don't initialize yet for performance reasons
        Set configs = null;
        Set allConfigs = (Set) this.namespaceLocalname2configSet.get(key);
        for (Iterator i = allConfigs.iterator(); i.hasNext(); ) {
            AttributeConfiguration config = (AttributeConfiguration) i.next();
            if (config.matches(namespace, localName, attrs)) {
                if (configs == null) {
                    configs = new HashSet();
                }
                configs.add(config);
            }
        }
        if (configs == null) {
            configs = Collections.EMPTY_SET;
        }
        return configs;
    }
    
    /**
     * Cache to improve performance.
     */
    private Map namespaceLocalname2configSet = new HashMap();

    protected boolean existsMatchingConfiguration(String namespace, String localName) {
        // quick pre-check
        if (!this.localNames.contains(localName)) {
            return false;
        }
        
        // more expensive check
        String key = getCacheKey(namespace, localName);
        return this.namespaceLocalname2configSet.containsKey(key);
    }

    protected String getCacheKey(String namespace, String localName) {
        return namespace + " " + localName;
    }

    protected String indent = "";
    protected boolean ignoreLinkElement = false;
    private Stack ignoreLinkElementStack = new Stack();
    protected boolean useIgnore = false;

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

        Set configs = getMatchingConfigurations(uri, name, attrs);

        if (!configs.isEmpty()) {

            this.ignoreLinkElement = false;

            for (Iterator i = configs.iterator(); i.hasNext(); ) {
                AttributeConfiguration config = (AttributeConfiguration) i.next();
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

        // use existsMatching to match up with ednElement
        if (existsMatchingConfiguration(uri,name) && this.useIgnore) {
            if(this.ignoreLinkElement) { 
                ignoreLinkElementStack.push(Boolean.TRUE);
                return;
            } else { 
                ignoreLinkElementStack.push(Boolean.FALSE);
            }
        }
 
        if (newAttrs != null) {
            attrs = newAttrs;
        }
        super.startElement(uri, name, qname, attrs);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this.indent + "<" + qname + "> sent");
        }
    }

    /**
     * Handle a link in the source SAX stream.
     * @param linkUrl The link URL.
     * @param config The attribute configuration which matched the link.
     * @param newAttrs The new attributes which will be added to the result
     *        element.
     * @throws Exception if an error occurs.
     */
    protected void handleLink(String linkUrl, AttributeConfiguration config, AttributesImpl newAttrs)
            throws Exception {
        if (getLinkRewriter().matches(linkUrl)) {
            setAttribute(newAttrs, config.attribute, getLinkRewriter().rewrite(linkUrl));
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String name, String qname) throws SAXException {
        if (getLogger().isDebugEnabled()) {
            this.indent = this.indent.substring(2);
            getLogger().debug(this.indent + "</" + qname + ">");
        }
        if (existsMatchingConfiguration(uri, name) && this.useIgnore) { 
            if( ((Boolean)ignoreLinkElementStack.pop()).booleanValue()) { 
                this.ignoreLinkElement = false;
                return;
            }
        }
        
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this.indent + "</" + qname + "> sent");
        }
        super.endElement(uri, name, qname);
    }

    /**
     * Sets the value of a certain attribute.
     * 
     * @param attr The attributes.
     * @param name The attribute name.
     * @param value The value.
     * @throws IllegalArgumentException if the href attribute is not contained
     *         in this attributes.
     */
    protected void setAttribute(AttributesImpl attr, String name, String value) {
        int position = attr.getIndex(name);
        if (position == -1) {
            throw new IllegalArgumentException("The attribute [" + name + "] is not available!");
        }
        attr.setValue(position, value);
    }

    /**
     * @return The link rewriter used by this transformer.
     */
    protected abstract LinkRewriter getLinkRewriter();

}
