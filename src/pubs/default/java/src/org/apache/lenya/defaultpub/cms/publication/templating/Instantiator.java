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
package org.apache.lenya.defaultpub.cms.publication.templating;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.lang.Boolean;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.impl.FileSource;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import org.apache.cocoon.components.search.Index;
import org.apache.cocoon.components.search.IndexException;
import org.apache.cocoon.components.search.IndexStructure;
import org.apache.cocoon.components.search.components.AnalyzerManager;
import org.apache.cocoon.components.search.components.IndexManager;
import org.apache.cocoon.components.search.fieldmodel.DateFieldDefinition;
import org.apache.cocoon.components.search.fieldmodel.FieldDefinition;
import org.apache.cocoon.components.search.utils.SourceHelper;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Instantiate the publication.
 * 
 * @version $Id$
 */
public class Instantiator extends AbstractLogEnabled implements
        org.apache.lenya.cms.publication.templating.Instantiator, Serviceable {

    protected static final String[] sourcesToCopy = { "publication.xml",
            "config/publication.xconf", "config/ac/ac.xconf",
            "config/ac/policies/", "config/ac/usecase-policies.xml", "config/workflow/workflow.xml",
            "config/" + org.apache.cocoon.components.search.components.impl.IndexManagerImpl.INDEX_CONF_FILE};

    /*
     * "config/cocoon-xconf/index_manager_index.xconf", "config/cocoon-xconf/index_manager.xconf",
     */

    /**
     * @see org.apache.lenya.cms.publication.templating.Instantiator#instantiate(org.apache.lenya.cms.publication.Publication,
     *      java.lang.String, java.lang.String)
     */
    public void instantiate(Publication template, String newPublicationId, String name)
            throws Exception {

        SourceResolver resolver = null;
        Source publicationsSource = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            publicationsSource = resolver.resolveURI("context://"
                    + Publication.PUBLICATION_PREFIX_URI);
            String publicationsUri = publicationsSource.getURI();

            for (int i = 0; i < sourcesToCopy.length; i++) {

                String source = sourcesToCopy[i];
                if (source.endsWith("/")) {
                    copyDirSource(template, newPublicationId, resolver, publicationsUri, source);
                } else {
                    copySource(template, newPublicationId, resolver, publicationsUri, source);
                }
            }

            updateMetaData(resolver, newPublicationId, name, publicationsUri);

            configureSearchIndex(resolver, template, newPublicationId, publicationsUri);

            updateConfiguration(resolver, template, newPublicationId, publicationsUri);

        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
                if (publicationsSource != null) {
                    resolver.release(publicationsSource);
                }
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
                    + newPublicationId + "/publication.xml");
            Document metaDoc = DocumentHelper.readDocument(metaSource.getInputStream());
            NamespaceHelper helper = new NamespaceHelper("http://apache.org/cocoon/lenya/publication/1.0",
                    "lenya",
                    metaDoc);
            Element nameElement = helper.getFirstChild(metaDoc.getDocumentElement(), "name");
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
                    + newPublicationId + "/config/publication.xconf");
            DefaultConfiguration config = (DefaultConfiguration) new DefaultConfigurationBuilder().build(configSource.getInputStream());
            DefaultConfiguration templatesConfig = (DefaultConfiguration) config.getChild("templates",
                    false);
            if (templatesConfig == null) {
                templatesConfig = new DefaultConfiguration("templates");
                config.addChild(templatesConfig);
            } else {
                Configuration[] templateConfigs = templatesConfig.getChildren("template");
                for (int i = 0; i < templateConfigs.length; i++) {
                    templatesConfig.removeChild(templateConfigs[i]);
                }
            }
            DefaultConfiguration templateConfig = new DefaultConfiguration("template");
            templateConfig.setAttribute("id", template.getId());
            templatesConfig.addChild(templateConfig);
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

            indexSource = (ModifiableSource) resolver.resolveURI(publicationsUri + "/"
                    + newPublicationId + "/config/lucene_index.xconf");
            Document indexDoc = DocumentHelper.readDocument(indexSource.getInputStream());
            NamespaceHelper indexHelper = new NamespaceHelper(null, "xconf", indexDoc);

            Element[] indexElement = indexHelper.getChildren(indexDoc.getDocumentElement(), "index");

            indexElement[0].setAttribute("id", newPublicationId + "-live");
            indexElement[0].setAttribute("directory", indexDir + "/live/index");
            indexElement[1].setAttribute("id", newPublicationId + "-authoring");
            indexElement[1].setAttribute("directory", indexDir + "/authoring/index");

            save(indexDoc, indexSource);

            // Second, configure the index and add it to the IndexManager

            indexManager = (IndexManager) manager.lookup(IndexManager.ROLE);

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

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
