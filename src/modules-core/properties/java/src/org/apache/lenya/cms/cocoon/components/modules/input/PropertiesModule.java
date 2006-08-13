/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.cms.cocoon.components.modules.input;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.modules.input.DefaultsModule;
import org.apache.cocoon.components.modules.input.InputModule;
import org.apache.commons.lang.SystemUtils;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.forrest.conf.AntProperties;
import org.apache.lenya.cms.module.ModuleManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Input module for accessing the base properties used in Lenya. The main values
 * are the locations of the <b>source </b> directories and of the <b>Lenya </b>
 * directories.
 */
public class PropertiesModule extends DefaultsModule implements InputModule,
        Initializable, ThreadSafe, Serviceable {
    private AntProperties filteringProperties;

    private SourceResolver m_resolver;

    private ModuleManager moduleManager;

    private final static String lenyaHome = "context:/";

    private final static String DEFAULT_HOME_PROP = "lenya.home";

    private final static String PROPERTY_NAME = "lenya.properties.xml";

    private final static String PROPERTY_NAME_LOCAL = "local." + PROPERTY_NAME;

    public Object getAttribute(String name, Configuration modeConf,
            Map objectModel) throws ConfigurationException {
        String attributeValue;

        attributeValue = filteringProperties.getProperty(name);
        if (attributeValue == null) {
            String error = "Unable to get attribute value for "
                    + name
                    + "\n"
                    + "Please make sure you defined "
                    + name
                    + " in lenya.properties.xml either in $LENYA_HOME or in the module that is requesting this property"
                    + "\n" 
                    + "If you see this message, most of the time you spotted a module bug "
                    + "(forget to define the default property). Please report it to our mailing list.";
            throw new ConfigurationException(
                    error);
        }

        if (debugging()) {
            debug(" - Requested:" + name);
            debug(" - Given:" + attributeValue);
        }

        return attributeValue;
    }

    public Object[] getAttributeValues(String name, Configuration modeConf,
            Map objectModel) throws ConfigurationException {
        Object[] attributeValues = super.getAttributeValues(name, modeConf,
                objectModel);
        for (int i = 0; i < attributeValues.length; i++) {
            attributeValues[i] = filteringProperties.filter(attributeValues[i]
                    .toString());
        }

        return attributeValues;
    }
    
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
    throws ConfigurationException {

SortedSet matchset = new TreeSet();
Enumeration enumeration = filteringProperties.keys();
while (enumeration.hasMoreElements()) {
    String key = (String) enumeration.nextElement();
    matchset.add(key);
}
Iterator iterator = super.getAttributeNames(modeConf, objectModel);
while (iterator.hasNext())
    matchset.add(iterator.next());
return matchset.iterator();
}

    public void initialize() throws Exception {

        // add all homes important to Lenya to the properties
        setHomes();
        
        loadSystemProperties(filteringProperties);

        // NOTE: the first values set get precedence, as in AntProperties
        String lenyaPropertiesStringURI = "";

        // get the values from local.lenya.properties.xml
        try {
            lenyaPropertiesStringURI = lenyaHome + SystemUtils.FILE_SEPARATOR
                    + PROPERTY_NAME_LOCAL;

            filteringProperties = loadXMLPropertiesFromURI(filteringProperties,
                    lenyaPropertiesStringURI);

        String[] module2src = moduleManager.getModuleIds();
        for (int i = 0; i < module2src.length; i++) {
            String id = module2src[i];
            Object value = moduleManager.getBaseURI(id);
            if (value != null) {
                lenyaPropertiesStringURI = value
                        + SystemUtils.FILE_SEPARATOR + PROPERTY_NAME;
                filteringProperties = loadXMLPropertiesFromURI(
                        filteringProperties, lenyaPropertiesStringURI);
            }
        }
        // get the values from lenya.properties.xml this are the default lenya
        // values
            lenyaPropertiesStringURI = lenyaHome + SystemUtils.FILE_SEPARATOR
                    + PROPERTY_NAME;

            filteringProperties = loadXMLPropertiesFromURI(filteringProperties,
                    lenyaPropertiesStringURI);
        } finally {
            if (debugging())
                debug("Loaded project lenya.properties.xml:" + filteringProperties);
        }
        
    }

    /**
     * Sets all Lenya related home locations such as - LenyaHome - projectHome -
     * contextHome
     * 
     * @throws Exception
     */
    private void setHomes() throws Exception {

        filteringProperties = new AntProperties();
        filteringProperties.setProperty(DEFAULT_HOME_PROP, lenyaHome);
    }

    /**
     * Override any properties for which a system property exists
     */
    private void loadSystemProperties(AntProperties props) {
        for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
            String propName = (String) e.nextElement();
            String systemPropValue = System.getProperty(propName);
            if (systemPropValue != null) {
                // AntProperties.setProperty doesn't let you override, so we
                // have to remove the property then add it again
                props.remove(propName);
                props.setProperty(propName, systemPropValue);
            }
        }
    }

    /**
     * @param propertiesStringURI
     * @throws IOException
     * @throws MalformedURLException
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws SourceNotFoundException
     */
    private AntProperties loadXMLPropertiesFromURI(
            AntProperties precedingProperties, String propertiesStringURI)
            throws MalformedURLException, IOException,
            ParserConfigurationException, SAXException {

        Source source = null;
        InputStream in = null;
        try {

            source = m_resolver.resolveURI(propertiesStringURI);

            if (source.exists()) {

                DocumentBuilderFactory factory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(source.getURI());

                NodeList nl = document.getElementsByTagName("property");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element el = (Element) nl.item(i);
                        filteringProperties.setProperty(
                                el.getAttribute("name"), el
                                        .getAttribute("value"));
                    }
                }

                if (debugging())
                    debug("Loaded:" + propertiesStringURI
                            + filteringProperties.toString());

            }

        } finally {
            if (source != null) {
                m_resolver.release(source);
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return filteringProperties;
    }

    public void service(ServiceManager manager) throws ServiceException {
        m_resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
        moduleManager = (ModuleManager) manager.lookup(ModuleManager.ROLE);
    }

    /**
     * Rocked science
     */
    private final boolean debugging() {
        return getLogger().isDebugEnabled();
    }

    /**
     * Rocked science
     * 
     * @param debugString
     */
    private final void debug(String debugString) {
        getLogger().debug(debugString);
    }

}
