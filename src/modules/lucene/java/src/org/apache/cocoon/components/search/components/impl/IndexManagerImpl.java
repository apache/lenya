/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.components.search.components.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.search.Index;
import org.apache.cocoon.components.search.IndexException;
import org.apache.cocoon.components.search.IndexStructure;
import org.apache.cocoon.components.search.components.AnalyzerManager;
import org.apache.cocoon.components.search.components.IndexManager;
import org.apache.cocoon.components.search.fieldmodel.DateFieldDefinition;
import org.apache.cocoon.components.search.fieldmodel.FieldDefinition;
import org.apache.cocoon.components.search.utils.SourceHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.metadata.Element;
import org.apache.lenya.cms.metadata.ElementSet;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataRegistry;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationManager;
import org.apache.lenya.modules.lucene.MetaDataFieldRegistry;

/**
 * Index Manager Component. Configure and Manage the differents indexes.
 * 
 * @author Maisonneuve Nicolas
 * @version 1.0
 */
public class IndexManagerImpl extends AbstractLogEnabled implements IndexManager, Serviceable,
        ThreadSafe, Configurable {

    /**
     * indexer element
     */
    public static final String INDEXER_ELEMENT = "indexer";

    /**
     * indexer element
     */
    public static final String INDEXER_ROLE_ATTRIBUTE = "role";

    /**
     * set of indexes
     */
    public static final String INDEXES_ELEMENT = "indexes";

    /**
     * Index declaration element
     */
    public static final String INDEX_ELEMENT = "index";

    /**
     * default analyzer of a index
     */
    public static final String INDEX_DEFAULTANALZER_ATTRIBUTE = "analyzer";

    /**
     * directory where the index is stored
     */
    public static final String INDEX_DIRECTORY_ATTRIBUTE = "directory";

    /**
     * Index Structure element
     */
    public static final String STRUCTURE_ELEMENT = "structure";

    /**
     * Field declaration element
     */
    public static final String FIELD_ELEMENT = "field";

    /**
     * field name
     */
    public static final String ID_ATTRIBUTE = "id";

    /**
     * type of the field: "text, "keyword", "date" (see
     * 
     * @see org.apache.cocoon.components.search.fieldmodel.FieldDefinition
     *      class)
     */
    public static final String TYPE_ATTRIBUTE = "type";

    /**
     * store information or not (true/false)
     */
    public static final String STORE_ATTRIBUTE = "storetext";

    /**
     * The date Format when the field type is a date
     */
    public static final String DATEFORMAT_ATTRIBUTE = "dateformat";

    /**
     * The name of the index configuration file.
     */
    public static final String INDEX_CONF_FILE = "search/lucene_index.xml";

    /**
     * check the config file each time the getIndex is called to update if
     * necessary the configuration
     */
    // public static final String CHECK_ATTRIBUTE = "check";
    /**
     * Source of the index configuration file
     */
    // public static final String CONFIG_ATTRIBUTE = "config";
    /**
     * Check or not the configuration file (automatic update if the file is
     * changed)
     */
    // private boolean check;
    /**
     * Index configuration file
     */
    // private Source configfile;
    private ServiceManager manager;

    private Map indexMap;

    protected Map indexes() {
        if (this.indexMap == null) {
            this.indexMap = new HashMap();
            loadIndexes();
        }
        return this.indexMap;
    }

    private String indexerRole = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.IndexManager#contains(java.lang.String)
     */
    public boolean contains(String id) {
        if (id != null) {
            return this.indexes().get(id) != null;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.IndexManager#getIndex(java.lang.String)
     */
    public Index getIndex(String id) throws IndexException {

        if (id == null || id.equals("")) {
            throw new IndexException(" index with no name was called");
        }

        Index index = (Index) this.indexes().get(id);
        if (index == null) {
            throw new IndexException("Index " + id + " doesn't exist. Check if configuration "
                    + INDEX_CONF_FILE + " exists for this publication!");
        }

        return index;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.IndexManager#addIndex(org.apache.cocoon.components.search.Index)
     */
    public void addIndex(Index base) {
        this.indexes().put(base.getID(), base);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.IndexManager#remove(java.lang.String)
     */
    public void remove(String id) {
        this.indexes().remove(id);
    }

    protected void loadIndexes() {
        // configure the index manager:

        // now check all publications and add their indexes:
        PublicationManager pubManager = null;
        SourceResolver resolver = null;
        Source confSource = null;
        ContextUtility util = null;
        try {
            util = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
            Request request = util.getRequest();
            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            pubManager = (PublicationManager) this.manager.lookup(PublicationManager.ROLE);
            Publication[] publications = pubManager.getPublications(factory);
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            for (int i = 0; i < publications.length; i++) {
                String uri = "context://" + Publication.PUBLICATION_PREFIX_URI + "/"
                        + publications[i].getId() + "/" + Publication.CONFIGURATION_PATH + "/"
                        + INDEX_CONF_FILE;
                confSource = resolver.resolveURI(uri);
                if (confSource.exists()) {
                    addIndexes(confSource);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Config file error", e);
        } catch (ServiceException e) {
            throw new RuntimeException("PublicationManager lookup error", e);
        } finally {
            if (pubManager != null) {
                this.manager.release(pubManager);
            }
            if (resolver != null) {
                if (confSource != null) {
                    resolver.release(confSource);
                }
                this.manager.release(resolver);
            }
            if (util != null) {
                this.manager.release(util);
            }
        }

        getLogger().info("Search Engine - Index Manager configured.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException {
        this.indexerRole = configuration.getChild(INDEXER_ELEMENT).getAttribute(
                INDEXER_ROLE_ATTRIBUTE);
    }

    /**
     * Adds indexes from the given configuration file to the index manager.
     * @param confSource
     */
    public void addIndexes(Source confSource) {
        try {
            Configuration indexConfiguration = SourceHelper.build(confSource);
            addIndexes(indexConfiguration);
        } catch (ConfigurationException e) {
            throw new RuntimeException("Error with configuration file " + confSource.getURI(), e);
        }
    }

    /**
     * Adds indexes from the given configuration object to the index manager.
     * @param configuration
     * @throws ConfigurationException
     */
    private void addIndexes(Configuration configuration) throws ConfigurationException {
        AnalyzerManager analyzerManager = null;
        MetaDataFieldRegistry registry = null;

        Configuration[] confs = configuration.getChildren(INDEX_ELEMENT);

        if (confs.length == 0) {
            throw new ConfigurationException("no index is defined !");
        }
        try {
            analyzerManager = (AnalyzerManager) this.manager.lookup(AnalyzerManager.ROLE);
            registry = (MetaDataFieldRegistry) this.manager.lookup(MetaDataFieldRegistry.ROLE);

            // configure each index
            for (int i = 0; i < confs.length; i++) {
                String id = confs[i].getAttribute(ID_ATTRIBUTE);
                String analyzerid = confs[i].getAttribute(INDEX_DEFAULTANALZER_ATTRIBUTE, null);
                if (analyzerid != null && !analyzerManager.exist(analyzerid)) {
                    throw new ConfigurationException("Analyzer " + analyzerid + " no found");
                }

                String directory = confs[i].getAttribute(INDEX_DIRECTORY_ATTRIBUTE);

                Configuration[] fields = confs[i].getChild(STRUCTURE_ELEMENT).getChildren(
                        FIELD_ELEMENT);

                IndexStructure docdecl = new IndexStructure();

                addMetaDataFieldDefinitions(registry, docdecl);
                
                FieldDefinition uuidDef = FieldDefinition.create("uuid", FieldDefinition.KEYWORD);
                uuidDef.setStore(true);
                docdecl.addFieldDef(uuidDef);

                FieldDefinition langDef = FieldDefinition.create("language", FieldDefinition.KEYWORD);
                langDef.setStore(true);
                docdecl.addFieldDef(langDef);

                for (int j = 0; j < fields.length; j++) {

                    FieldDefinition fielddecl;

                    // field id attribute
                    String id_field = fields[j].getAttribute(ID_ATTRIBUTE);

                    // field type attribute
                    String typeS = fields[j].getAttribute(TYPE_ATTRIBUTE, "");
                    int type = FieldDefinition.stringTotype(typeS);
                    try {
                        fielddecl = FieldDefinition.create(id_field, type);
                    } catch (IllegalArgumentException e) {
                        throw new ConfigurationException("field " + id_field + " type " + typeS, e);
                    }

                    // field store attribute
                    boolean store;
                    if (fielddecl.getType() == FieldDefinition.TEXT) {
                        store = fields[j].getAttributeAsBoolean(STORE_ATTRIBUTE, false);
                    } else {
                        store = fields[j].getAttributeAsBoolean(STORE_ATTRIBUTE, true);
                    }
                    fielddecl.setStore(store);

                    // field dateformat attribute
                    if (fielddecl.getType() == FieldDefinition.DATE) {
                        String dateformat_field = fields[j].getAttribute(DATEFORMAT_ATTRIBUTE);
                        ((DateFieldDefinition) fielddecl).setDateFormat(new SimpleDateFormat(
                                dateformat_field));
                    }

                    this.getLogger().debug("field added: " + fielddecl);
                    docdecl.addFieldDef(fielddecl);
                }

                Index index = new Index();
                index.setID(id);
                index.setIndexer(indexerRole);

                // if the directory path is relative, prepend context path:
                if (!directory.startsWith(File.separator)) {
                    directory = getServletContextPath() + File.separator + directory;
                }

                if (index.setDirectory(directory)) {
                    this.getLogger().warn("directory " + directory + " was locked ");
                }
                if (analyzerid != null) {
                    index.setDefaultAnalyzerID(analyzerid);
                }
                index.setStructure(docdecl);
                index.setManager(manager);

                this.addIndex(index);
                this.getLogger()
                        .info("add index  " + index.getID() + " for directory " + directory);
            }
        } catch (ServiceException e) {
            throw new ConfigurationException("AnalyzerManager lookup error", e);
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        } finally {
            if (analyzerManager != null) {
                this.manager.release(analyzerManager);
            }
            if (registry != null) {
                this.manager.release(registry);
            }
        }
    }

    protected void addMetaDataFieldDefinitions(MetaDataFieldRegistry registry,
            IndexStructure indexStructure) throws MetaDataException {
        String[] fieldNames = registry.getFieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            FieldDefinition fieldDef = FieldDefinition.create(fieldNames[i], FieldDefinition.TEXT);
            fieldDef.setStore(false);
            indexStructure.addFieldDef(fieldDef);
        }
    }

    public static String getUniversalName(String namespace, String elementName) {
        return "{" + namespace + "}" + elementName;
    }

    /**
     * @return The servlet context path.
     * @throws Exception if an error occurs.
     */
    public String getServletContextPath() throws Exception {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context:///");
            return SourceUtil.getFile(source).getCanonicalPath();
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.cocoon.components.search.components.IndexManager#getIndex()
     */
    public Index[] getIndex() {
        return (Index[]) this.indexes().values().toArray(new Index[indexes().size()]);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
