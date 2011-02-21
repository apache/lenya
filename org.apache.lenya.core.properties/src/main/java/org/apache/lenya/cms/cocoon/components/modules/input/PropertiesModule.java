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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.modules.input.DefaultsModule;
import org.apache.cocoon.components.modules.input.InputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.module.Module;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
//import org.apache.lenya.cms.publication.URLInformation;
//import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.utils.URLInformation;
import org.apache.lenya.utils.ServletHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Input module for accessing the base properties used in Lenya. The main values
 * are the locations of the <b>source </b> directories and of the <b>Lenya </b>
 * directories.
 */
public class PropertiesModule extends DefaultsModule implements InputModule {

    private Map pubId2roperties = new HashMap();

    private final static String LENYA_HOME = "context:/";
    private final static String DEFAULT_HOME_PROP = "lenya.home";
    private final static String PROPERTY_FILE_NAME = "lenya.properties.xml";
    private final static String PROPERTY_FILE_NAME_LOCAL = "local." + PROPERTY_FILE_NAME;
    
	// FIXME Use commons-configuration
    private Configuration globalProperties;
    private SourceResolver sourceResolver;
    private Map modules;

    protected Repository repository;

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        
        String attributeValue = getProperties(objectModel).getString(name);
        if (attributeValue == null) {
            String error = "Unable to get attribute value for " + name + ".\n"
                    + "Please make sure you defined " + name
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

    /**
     * Returns the properties for the current request. If the request refers to a publication,
     * the publication properties are considered. For global requests, only the global
     * properties are considered.
     * @param objectModel The current object model.
     * @return An AntProperties object.
     * @throws ConfigurationException if an error occurs.
     */
    protected Configuration getProperties(Map objectModel) throws ConfigurationException {
        Configuration properties;
        Request request = ObjectModelHelper.getRequest(objectModel);
        //florent 
        //String webappUrl = ServletHelper.getWebappURI(request);
        //URLInformation info = new URLInformation(webappUrl);
        URLInformation info = new URLInformation();
        String pubId = info.getPublicationId();
        Session session = this.repository.getSession(request);
        if (session.existsPublication(pubId)) {
            try {
                Publication pub = session.getPublication(pubId);
                properties = getPublicationProperties(pub);
            }
            catch (Exception e) {
                throw new ConfigurationException("Could not resolve properties for publication [" + pubId + "]: ", e);
            }
        }
        else {
            properties = this.globalProperties;
        }
        return properties;
    }

    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object value = getAttribute(name, modeConf, objectModel);
        Object[] values = { value };
        return values;
    }

    public Iterator getAttributeNames(org.apache.avalon.framework.configuration.Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Configuration properties = getProperties(objectModel);
        SortedSet matchset = new TreeSet();
        for (Iterator i = properties.getKeys(); i.hasNext(); ) {
            String key = (String) i.next();
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

        loadSystemProperties(globalProperties);

        // NOTE: the first values set get precedence, as in AntProperties
        // 
        // Order of precedence:
        // 1. Publication (lazy loaded in loadPublicationPropertiesIfNotDone())
        // 2. Lenya local
        // 3. Modules (all modules, not only the ones referenced in the
        // publication)
        // 4. Lenya
        
        // get the values from lenya.properties.xml, these are the default lenya values
        String lenyaPropsUri = LENYA_HOME + "/" + PROPERTY_FILE_NAME;
        Configuration lenyaProperties = loadXMLPropertiesFromURI(lenyaPropsUri);
        merge(this.globalProperties, lenyaProperties);
        
        // get the values from all modules
        Collection modules = this.modules.values();
        for (Iterator i = modules.iterator(); i.hasNext(); ) {
            Module module = (Module) i.next();
            String moduleBaseUri = module.getBaseUri();
            if (moduleBaseUri != null) {
                String modulePropsUri = moduleBaseUri + "/" + PROPERTY_FILE_NAME;
                Configuration moduleProperties = loadXMLPropertiesFromURI(modulePropsUri);
                merge(this.globalProperties, moduleProperties);
            }
        }
        
        // get the values from local.lenya.properties.xml
        String lenyaLocalPropsUri = LENYA_HOME + "/" + PROPERTY_FILE_NAME_LOCAL;
        Configuration localLenyaProperties = loadXMLPropertiesFromURI(lenyaLocalPropsUri);
        merge(this.globalProperties, localLenyaProperties);

        if (debugging())
            debug("Loaded project lenya.properties.xml:" + globalProperties);
    }

    /**
     * Sets all Lenya related home locations such as - LenyaHome - projectHome -
     * contextHome
     * 
     * @throws Exception
     */
    private void setHomes() throws Exception {
        this.globalProperties = new BaseConfiguration();
        this.globalProperties.setProperty(DEFAULT_HOME_PROP, LENYA_HOME);
    }

    /**
     * Override any properties for which a system property exists
     */
    private void loadSystemProperties(Configuration props) {
        for (Iterator i = props.getKeys(); i.hasNext();) {
            String propName = (String) i.next();
            String systemPropValue = System.getProperty(propName);
            if (systemPropValue != null) {
                overwriteProperty(props, propName, systemPropValue);
            }
        }
    }

    private void overwriteProperty(Configuration props, String propName, String propValue) {
        props.setProperty(propName, propValue);
    }

    /**
     * @param precedingProperties
     * @param uri
     * @param overwrite
     * @throws IOException
     * @throws MalformedURLException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private Configuration loadXMLPropertiesFromURI(String uri) throws Exception {

        Configuration properties = new BaseConfiguration();
        Source source = null;
        try {

            source = this.sourceResolver.resolveURI(uri);

            if (source.exists()) {

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(source.getURI());

                NodeList nl = document.getElementsByTagName("property");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element el = (Element) nl.item(i);
                        properties.setProperty(el.getAttribute("name"), el.getAttribute("value"));
                    }
                }
                if (debugging()) {
                    debug("Loaded:" + uri + properties.toString());
                }
            }
        } finally {
            if (source != null) {
                this.sourceResolver.release(source);
            }
        }
        return properties;
    }

    /**
     * Get the properties of the requested publication, including the global properties.
     */
    protected Configuration getPublicationProperties(Publication pub) throws Exception {
        
        String pubId = pub.getId();
        Configuration properties = (Configuration) this.pubId2roperties.get(pubId);
        if (properties == null) {
            properties = new BaseConfiguration();
            merge(properties, this.globalProperties);
            String uri = "context://lenya/pubs/" + pubId + "/" + PROPERTY_FILE_NAME;
            merge(properties, loadXMLPropertiesFromURI(uri));
            this.pubId2roperties.put(pubId, properties);
            if (debugging()) {
                debug("Loaded properties for publication [" + pubId + "]: " + properties);
            }
        }
        return properties;
    }

    /**
     * Inserts all key-value pairs from newProps into existingProps. Existing entries
     * will be replaced.
     * @param existingProps The existing properties.
     * @param newProps The new properties.
     */
    public void merge(Configuration existingProps, Configuration newProps) {
        for (Iterator i = newProps.getKeys(); i.hasNext(); ) {
            String key = (String) i.next();
            existingProps.setProperty(key, newProps.getProperty(key));
        }
    }

    /**
     * Rocket science
     */
    private final boolean debugging() {
        return getLogger().isDebugEnabled();
    }

    /**
     * Rocket science
     * 
     * @param debugString
     */
    private final void debug(String debugString) {
        getLogger().debug(debugString);
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    public void setModules(Map modules) {
        this.modules = modules;
    }

}
