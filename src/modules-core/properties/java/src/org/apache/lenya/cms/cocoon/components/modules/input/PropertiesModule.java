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
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashSet;
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
import org.apache.excalibur.source.SourceResolver;
import org.apache.forrest.conf.AntProperties;
import org.apache.lenya.cms.publication.Publication;	
import org.apache.lenya.cms.publication.PublicationUtil;
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
    
    private HashSet pubInit;
    
    private AntProperties filteringProperties;

    private SourceResolver m_resolver;

    private ModuleManager moduleManager;
    
    private ServiceManager serviceManager;

    private final static String lenyaHome = "context:/";

    private final static String DEFAULT_HOME_PROP = "lenya.home";

    private final static String PROPERTY_NAME = "lenya.properties.xml";

    private final static String PROPERTY_NAME_LOCAL = "local." + PROPERTY_NAME;

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        String attributeValue;

        loadPublicationPropertiesIfNotDone(objectModel);
        attributeValue = filteringProperties.getProperty(name);
        if (attributeValue == null) {
            String error = "Unable to get attribute value for "
                + name
                + ".\n"
                + "Please make sure you defined "
                + name
                + " in lenya.properties.xml either in $LENYA_HOME, $PUB_HOME or "
                + "in the module that is requesting this property.\n"
                + "If you see this message, most of the time you spotted a module bug "
                + "(forget to define the default property). Please report it to "
                + "our mailing list.";           
            throw new ConfigurationException(error);
        }

        if (debugging()) {
            debug(" - Requested:" + name);
            debug(" - Given:" + attributeValue);
        }

        return attributeValue;
    }

    public Object[] getAttributeValues(String name, Configuration modeConf,
            Map objectModel) throws ConfigurationException {
        loadPublicationPropertiesIfNotDone(objectModel);
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
        loadPublicationPropertiesIfNotDone(objectModel);
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
        pubInit = new HashSet();
        
        // add all homes important to Lenya to the properties
        setHomes();

        loadSystemProperties(filteringProperties);

        // NOTE: the first values set get precedence, as in AntProperties
        // 
        // Order of precedence:        
        // 1. Publication (lazy loaded in loadPublicationPropertiesIfNotDone())
        // 2. Lenya local
        // 3. Modules (all modules, not only the ones referenced in the publication)
        // 4. Lenya
        //
        String lenyaPropertiesStringURI = "";

        try {
            // get the values from local.lenya.properties.xml
            lenyaPropertiesStringURI = lenyaHome + SystemUtils.FILE_SEPARATOR
                    + PROPERTY_NAME_LOCAL;
            filteringProperties = loadXMLPropertiesFromURI(filteringProperties,
                    lenyaPropertiesStringURI, false);

            // get the values from all modules
            String[] module2src = moduleManager.getModuleIds();
            for (int i = 0; i < module2src.length; i++) {
                String id = module2src[i];
                Object value = moduleManager.getBaseURI(id);
                if (value != null) {
                    lenyaPropertiesStringURI = value + SystemUtils.FILE_SEPARATOR
                            + PROPERTY_NAME;
                    filteringProperties = loadXMLPropertiesFromURI(
                            filteringProperties, lenyaPropertiesStringURI, false);
                }
            }
            // get the values from lenya.properties.xml this are the default
            // lenya values
            lenyaPropertiesStringURI = lenyaHome + SystemUtils.FILE_SEPARATOR
                    + PROPERTY_NAME;
            filteringProperties = loadXMLPropertiesFromURI(filteringProperties,
                    lenyaPropertiesStringURI, false);
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
                overwriteProperty(props, propName, systemPropValue);
            }
        }
    }

    private void overwriteProperty(AntProperties props, String propName, String propValue) {
        //
        // AntProperties.setProperty doesn't let you override, so we
        // have to remove the property then add it again
        props.remove(propName);
        props.setProperty(propName, propValue);
    }

    /**
     * @param precedingProperties
     * @param propertiesStringURI
     * @param overwrite
     * @throws IOException
     * @throws MalformedURLException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private AntProperties loadXMLPropertiesFromURI(AntProperties precedingProperties, 
            String propertiesStringURI, boolean overwrite)
            throws MalformedURLException, IOException,
            ParserConfigurationException, SAXException {

        Source source = null;
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
                        if (overwrite == true) {
                            overwriteProperty(filteringProperties, el.getAttribute("name"), 
                                              el.getAttribute("value"));
                        } else {
                            filteringProperties.setProperty(el.getAttribute("name"), 
                                                            el.getAttribute("value"));
                        }
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
        }

        return filteringProperties;
    }

    /**
     * Get the properties from the requested publication
     */
    private void loadPublicationPropertiesIfNotDone(Map objectModel) 
            throws ConfigurationException {
        Publication pub;
        String pubId;

        try {
            pub = PublicationUtil.getPublication(serviceManager, objectModel);
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage());
        }
        pubId = pub.getId();
        if (pubInit.contains(pubId)) {
            return;
        }
        try {
            filteringProperties = loadXMLPropertiesFromURI(filteringProperties,
                                  PROPERTY_NAME, true);
        } catch (IOException e) {       
          getLogger().warn("Could not load properties from pub \""+pubId+"\".\n"+e);
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage());
        }
        pubInit.add(pubId);
    }

    public void service(ServiceManager manager) throws ServiceException {        
        this.serviceManager = manager;
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
