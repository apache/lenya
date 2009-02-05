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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.search.analyzer.ConfigurableAnalyzer;
import org.apache.cocoon.components.search.components.AnalyzerManager;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lucene.analysis.Analyzer;

/**
 * Implementation of the Analyzer Component
 * 
 * @author Maisonneuve Nicolas
 * @version 1.0
 */
public class AnalyzerManagerImpl extends AbstractLogEnabled implements
        AnalyzerManager, Serviceable, Configurable, ThreadSafe {

    /**
     * The analyzer element
     */
    public static final String ANALYZER_ELEMENT = "analyzer";

    /**
     * the id of the analyzer
     */
    public static final String ID_ATT = "id";

    /**
     * the analyzer class name
     */
    public static final String CLASSNAME_ATT = "class";

    /**
     * (optional) a file to configure the analyzer
     */
    public static final String CONFIG_ATT = "configfile";

    /**
     * Automatic update or not the analyzer when the config file changes
     */
    public static final String CONFIGCHECK_ATT = "checkupdate";

    /**
     * Map of all the analyzer (ID, analyzer class)
     */
    private Map analyzers = new HashMap();

    private ServiceManager manager;

    public boolean exist(String id) {
        return this.analyzers.containsKey(id);
    }

    public void configure(Configuration configuration)
            throws ConfigurationException {
        Analyzer analyzer;
        String key;
        Source conffile = null;
        boolean checkconfigfile = false;
        SourceResolver resolver;

        Configuration[] confAnalyzer = configuration
                .getChildren(ANALYZER_ELEMENT);
        if (confAnalyzer.length == 0) {
            throw new ConfigurationException("tag " + ANALYZER_ELEMENT
                    + " expected ");
        }
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
        } catch (ServiceException e) {
            throw new ConfigurationException(" source resolver error", e);
        }

        for (int i = 0; i < confAnalyzer.length; i++) {

            // KEY
            key = confAnalyzer[i].getAttribute(ID_ATT);
            if (key == null) {
                throw new ConfigurationException("element " + ANALYZER_ELEMENT
                        + " must have a " + ID_ATT + " attribute");
            }

            // CLASS
            String classname = confAnalyzer[i].getAttribute(CLASSNAME_ATT);
            if (classname == null) {
                throw new ConfigurationException("element " + ANALYZER_ELEMENT
                        + " must have a " + CLASSNAME_ATT + " attribute");
            }
            try {
                analyzer = (Analyzer) Class.forName(classname).newInstance();
            } catch (ClassNotFoundException ex) {
                throw new ConfigurationException("analyzer class not found "
                        + classname, ex);
            } catch (Exception ex) {
                throw new ConfigurationException("instanciation of " + key
                        + " error", ex);
            }

            if (analyzer instanceof LogEnabled) {
                this.setupLogger(analyzer);
            }

            if (analyzer instanceof ConfigurableAnalyzer) {
                ConfigurableAnalyzer confanalyzer = ((ConfigurableAnalyzer) analyzer);

                // CONFIGFILE
                String conffilename = confAnalyzer[i].getAttribute(CONFIG_ATT);

                if (conffilename == null || conffilename.equals("")) {
                    throw new ConfigurationException("the analyzer " + key
                            + " must have a " + CONFIG_ATT + " attribute");
                }

                try {
                    conffile = resolver.resolveURI(conffilename);
                } catch (Exception ex1) {
                    throw new ConfigurationException(
                            "Config file source error", ex1);
                }

                // CHECKUPDATE
                checkconfigfile = confAnalyzer[i].getAttributeAsBoolean(
                        CONFIGCHECK_ATT, false);

                confanalyzer.setAnalyerManager(this);
                confanalyzer.setConfigFile(conffile);
                confanalyzer.setEnableCheckFile(checkconfigfile);
            }
            this.put(key, analyzer);
        }

        manager.release(resolver);
        getLogger().info("AnalyzerManager configured.");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.AnalyzerManager#put(java.lang.String,
     *      org.apache.lucene.analysis.Analyzer)
     */
    public void put(String id, Analyzer analyzer) {
        this.analyzers.put(id, analyzer);
        this.getLogger().info(
                "add analyzer id: " + id + " with class "
                        + analyzer.getClass().getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.AnalyzerManager#remove(java.lang.String)
     */
    public void remove(String id) {
        this.analyzers.remove(id);
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("remove analyzer id: " + id);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.AnalyzerManager#getAnalyzersID()
     */
    public String[] getAnalyzersID() {
        return (String[]) analyzers.keySet().toArray(
                new String[analyzers.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.components.search.components.AnalyzerManager#getAnalyzer(java.lang.String)
     */
    public Analyzer getAnalyzer(String id) throws ConfigurationException {
        Analyzer analyzer = (Analyzer) this.analyzers.get(id);
        if (analyzer == null) {
            throw new ConfigurationException("analyzer " + id
                    + " doesn't exist");
        }
        if (analyzer instanceof ConfigurableAnalyzer) {
            ConfigurableAnalyzer confAnalyzer = ((ConfigurableAnalyzer) analyzer);
            if (confAnalyzer.enableCheckFile()) {
                confAnalyzer.reconfigure();
            }
        }
        return analyzer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
