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
package org.apache.lenya.defaultpub.cms.publication.templating;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.MutableConfiguration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.search.components.IndexManager;
import org.apache.cocoon.components.search.components.impl.IndexManagerImpl;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.impl.FileSource;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationManager; // import org.apache.lenya.cms.publication.PublicationConfiguration;
import org.apache.lenya.util.Assert;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Instantiate the publication.
 */
public class Instantiator extends AbstractLogEnabled implements
        org.apache.lenya.cms.publication.templating.Instantiator, Serviceable, Parameterizable {

    protected static final String[] sourcesToCopy = { "config/publication.xml",
            "config/access-control/access-control.xml", "config/access-control/policies/",
            "config/access-control/usecase-policies.xml", "config/workflow/workflow.xml",
            "config/" + IndexManagerImpl.INDEX_CONF_FILE };

    protected static final String ACCREDITABLES_DIRECTORY = "config/access-control/passwd/";

    // the following stuff should actually come from PublicationConfiguration,
    // but there's currently no way to get at it.
    // the correct solution suggested by andreas is not to meddle with config
    // files here at all, but instead implement
    // appropriate setter functions in the Publication class. postponed to after
    // 2.0.
    // thus, don't waste too much effort on this file, it's all an intermediate
    // hack.
    private static final String CONFIGURATION_FILE = "config/publication.xml";
    private static final String CONFIGURATION_NAMESPACE = "http://apache.org/cocoon/lenya/publication/1.1";
    private static final String ACCESS_CONTROL_FILE = "config/access-control/access-control.xml";
    private static final String ELEMENT_NAME = "name";
    private static final String ELEMENT_TEMPLATE = "template";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ELEMENT_RESOURCE_TYPES = "resource-types";// *
    private static final String ELEMENT_RESOURCE_TYPE = "resource-type";// *
    private static final String ELEMENT_MODULES = "modules";// *
    private static final String ELEMENT_MODULE = "module";// *

    private ServiceManager manager;
    private boolean shareAccreditables;

    /**
     * @see org.apache.lenya.cms.publication.templating.Instantiator#instantiate(org.apache.lenya.cms.publication.Publication,
     *      java.lang.String, java.lang.String)
     */
    public void instantiate(Publication template, String newPublicationId, String name)
            throws Exception {

        Assert.notNull("template", template);
        Assert.notNull("publication ID", newPublicationId);
        Assert.notNull("name", name);

        if (name.equals("")) {
            name = newPublicationId;
        }

        SourceResolver resolver = null;
        Source publicationsSource = null;
        PublicationManager pubManager = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            publicationsSource = resolver.resolveURI("context://"
                    + Publication.PUBLICATION_PREFIX_URI);
            String publicationsUri = publicationsSource.getURI();

            List sources = new ArrayList(Arrays.asList(sourcesToCopy));

            if (!this.shareAccreditables) {
                sources.add(ACCREDITABLES_DIRECTORY);
            }

            for (Iterator i = sources.iterator(); i.hasNext();) {

                String source = (String) i.next();
                if (source.endsWith("/")) {
                    copyDirSource(template, newPublicationId, resolver, publicationsUri, source);
                } else {
                    copySource(template, newPublicationId, resolver, publicationsUri, source);
                }
            }

            updateMetaData(resolver, newPublicationId, name, publicationsUri);

            configureSearchIndex(resolver, template, newPublicationId, publicationsUri);

            updateConfiguration(resolver, template, newPublicationId, publicationsUri);

            updateAccessControl(resolver, template, newPublicationId, publicationsUri);

            pubManager = (PublicationManager) this.manager.lookup(PublicationManager.ROLE);
            pubManager.addPublication(newPublicationId);

        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
                if (publicationsSource != null) {
                    resolver.release(publicationsSource);
                }
            }
            if (pubManager != null) {
                this.manager.release(pubManager);
            }
        }

    }

    protected void updateMetaData(SourceResolver resolver, String newPublicationId, String name,
            String publicationsUri) throws MalformedURLException, IOException,
            ParserConfigurationException, SAXException, SourceNotFoundException,
            TransformerConfigurationException, TransformerException {
        ModifiableSource metaSource = null;
        try {
            metaSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + newPublicationId + "/" + CONFIGURATION_FILE);
            Document metaDoc = DocumentHelper.readDocument(metaSource.getInputStream());
            NamespaceHelper helper = new NamespaceHelper(CONFIGURATION_NAMESPACE, "", metaDoc);
            Element nameElement = helper.getFirstChild(metaDoc.getDocumentElement(), ELEMENT_NAME);
            DocumentHelper.setSimpleElementText(nameElement, name);

            save(metaDoc, metaSource);
        } finally {
            if (resolver != null) {
                if (metaSource != null) {
                    resolver.release(metaSource);
                }
            }
        }
    }

    protected void updateConfiguration(SourceResolver resolver, Publication template,
            String newPublicationId, String publicationsUri) throws MalformedURLException,
            IOException, SAXException, ConfigurationException, SourceNotFoundException {
        ModifiableSource configSource = null;
        try {

            configSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + newPublicationId + "/" + CONFIGURATION_FILE);

            final boolean ENABLE_XML_NAMESPACES = true;
            DefaultConfiguration config = (DefaultConfiguration) new DefaultConfigurationBuilder(
                    ENABLE_XML_NAMESPACES).build(configSource.getInputStream());
            addTemplateConfiguration(template, config);

            removeChildren(config.getMutableChild(ELEMENT_MODULES), ELEMENT_MODULE);
            removeChildren(config.getMutableChild(ELEMENT_RESOURCE_TYPES), ELEMENT_RESOURCE_TYPE);

            OutputStream oStream = configSource.getOutputStream();
            new DefaultConfigurationSerializer().serialize(oStream, config);
            if (oStream != null) {
                oStream.flush();
                try {
                    oStream.close();
                } catch (Throwable t) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Exception closing output stream: ", t);
                    }
                    throw new RuntimeException("Could not write document: ", t);
                }
            }
        } finally {
            if (resolver != null) {
                if (configSource != null) {
                    resolver.release(configSource);
                }
            }
        }
    }

    protected void addTemplateConfiguration(Publication template, DefaultConfiguration config)
            throws ConfigurationException {
        Configuration[] templateConfigs = config.getChildren(ELEMENT_TEMPLATE);
        for (int i = 0; i < templateConfigs.length; i++) {
            config.removeChild(templateConfigs[i]);
        }
        DefaultConfiguration templateConfig = new DefaultConfiguration(ELEMENT_TEMPLATE, null,
                CONFIGURATION_NAMESPACE, "");
        templateConfig.setAttribute(ATTRIBUTE_ID, template.getId());
        config.addChild(templateConfig);
    }

    protected void removeChildren(MutableConfiguration config, String name)
            throws ConfigurationException {
        MutableConfiguration[] moduleConfigs = config.getMutableChildren(name);
        for (int i = 0; i < moduleConfigs.length; i++) {
            config.removeChild(moduleConfigs[i]);
        }
    }

    protected void configureSearchIndex(SourceResolver resolver, Publication template,
            String newPublicationId, String publicationsUri) throws MalformedURLException,
            IOException, ParserConfigurationException, SAXException, SourceNotFoundException,
            TransformerConfigurationException, TransformerException, ServiceException,
            ConfigurationException {
        ModifiableSource indexSource = null;
        IndexManager indexManager = null;
        try {

            // RGE: Soc addition
            // First, patch the xconf patchfile with the new publication name

            String indexDir = "lenya/pubs/" + newPublicationId + "/work/lucene/index";

            indexSource = (ModifiableSource) resolver
                    .resolveURI(publicationsUri
                            + "/"
                            + newPublicationId
                            + "/config/"
                            + org.apache.cocoon.components.search.components.impl.IndexManagerImpl.INDEX_CONF_FILE);
            Document indexDoc = DocumentHelper.readDocument(indexSource.getInputStream());
            Element[] indexElement = DocumentHelper.getChildren(indexDoc.getDocumentElement(),
                    null, "index");

            for (int i = 0; i < indexElement.length; i++) {
                String id = indexElement[i].getAttribute("id");
                String area = id.split("-")[1];
                indexElement[i].setAttribute("id", newPublicationId + "-" + area);
                indexElement[i].setAttribute("directory", indexDir + "/" + area + "/index");
            }

            save(indexDoc, indexSource);

            // Second, configure the index and add it to the IndexManager

            indexManager = (IndexManager) this.manager.lookup(IndexManager.ROLE);

            indexManager.addIndexes(indexSource);

            // TODO: release all objects!

            // RGE: End Soc addition

        } finally {
            if (indexManager != null) {
                this.manager.release(indexManager);
            }
            if (resolver != null) {
                this.manager.release(resolver);
                if (indexSource != null) {
                    resolver.release(indexSource);
                }
            }
        }
    }

    protected void copySource(Publication template, String publicationId, SourceResolver resolver,
            String publicationsUri, String source) throws MalformedURLException, IOException {
        Source templateSource = null;
        ModifiableSource targetSource = null;
        try {
            templateSource = resolver.resolveURI(publicationsUri + "/" + template.getId() + "/"
                    + source);
            targetSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + publicationId + "/" + source);

            org.apache.lenya.cms.cocoon.source.SourceUtil.copy(templateSource, targetSource, false);
        } finally {
            if (templateSource != null) {
                resolver.release(templateSource);
            }
            if (targetSource != null) {
                resolver.release(targetSource);
            }
        }
    }

    protected void copyDirSource(Publication template, String publicationId,
            SourceResolver resolver, String publicationsUri, String source)
            throws MalformedURLException, IOException {
        FileSource directory = new FileSource(publicationsUri + "/" + template.getId() + "/"
                + source);
        Collection files = directory.getChildren();
        for (Iterator iter = files.iterator(); iter.hasNext();) {
            FileSource filesource = (FileSource) iter.next();
            if (filesource.isCollection()) {
                copyDirSource(template, publicationId, resolver, publicationsUri, source + "/"
                        + filesource.getName());
            } else {
                copySource(template, publicationId, resolver, publicationsUri, source + "/"
                        + filesource.getName());
            }
        }
    }

    protected void updateAccessControl(SourceResolver resolver, Publication template,
            String newPublicationId, String publicationsUri) throws MalformedURLException,
            IOException, SAXException, ConfigurationException, SourceNotFoundException {
        ModifiableSource configSource = null;
        try {

            configSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + newPublicationId + "/" + ACCESS_CONTROL_FILE);

            final boolean enableXmlNamespaces = true;
            DefaultConfiguration config = (DefaultConfiguration) new DefaultConfigurationBuilder(
                    enableXmlNamespaces).build(configSource.getInputStream());
            DefaultConfiguration acreditableDirectory = (DefaultConfiguration) config.getChild(
                    "accreditable-manager", false).getChild("parameter", false);

            if (!this.shareAccreditables) {
                acreditableDirectory.setAttribute("value", "context:///lenya/pubs/"
                        + newPublicationId + "/config/access-control/passwd");
            }

            DefaultConfiguration policyDirectory = (DefaultConfiguration) config.getChild(
                    "policy-manager", false).getChild("policy-manager", false).getChild(
                    "parameter", false);
            policyDirectory.setAttribute("value", "context:///lenya/pubs/" + newPublicationId
                    + "/config/access-control/policies");

            saveConfiguration(config, configSource);
        } finally {
            if (resolver != null) {
                if (configSource != null) {
                    resolver.release(configSource);
                }
            }
        }
    }

    protected void saveConfiguration(DefaultConfiguration config, ModifiableSource source)
            throws IOException, SAXException, ConfigurationException {
        OutputStream oStream = source.getOutputStream();
        new DefaultConfigurationSerializer().serialize(oStream, config);
        if (oStream != null) {
            oStream.flush();
            try {
                oStream.close();
            } catch (Throwable t) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Exception closing output stream: ", t);
                }
                throw new RuntimeException("Could not write document: ", t);
            }
        }
    }

    protected void save(Document metaDoc, ModifiableSource metaSource) throws IOException,
            TransformerConfigurationException, TransformerException {
        OutputStream oStream = metaSource.getOutputStream();
        DocumentHelper.writeDocument(metaDoc, new OutputStreamWriter(oStream));
        if (oStream != null) {
            oStream.flush();
            try {
                oStream.close();
            } catch (Throwable t) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Exception closing output stream: ", t);
                }
                throw new RuntimeException("Could not write document: ", t);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void parameterize(Parameters params) throws ParameterException {
        this.shareAccreditables = params.getParameterAsBoolean("shareAccreditables", true);
    }

}
