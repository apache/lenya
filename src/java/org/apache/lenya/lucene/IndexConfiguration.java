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

package org.apache.lenya.lucene;

import java.io.File;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;

/**
 * @version $Id$
 */
public class IndexConfiguration {
    static Category log = Category.getInstance(IndexConfiguration.class);
    private String configurationFilePath;
    private String updateIndexType;
    private String indexDir;
    private String htdocsDumpDir;
    private Class indexerClass;

    /**
     * Creates a new IndexConfiguration object.
     * @param configurationFilePath The path of the configuration file.
     */
    public IndexConfiguration(String configurationFilePath) {
        this.configurationFilePath = configurationFilePath;

        try {
            File configFile = new File(configurationFilePath);
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration config = builder.buildFromFile(configFile);
            configure(config);
        } catch (Exception e) {
            log.error("Cannot load publishing configuration! ", e);
            System.err.println("Cannot load publishing configuration! ");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Main method.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: " + IndexConfiguration.class.getName() + " lucene.xconf");
            return;
        }

        IndexConfiguration ic = new IndexConfiguration(args[0]);
        String parameter;

        parameter = ic.getUpdateIndexType();
        System.out.println("Index type: " + parameter);

        parameter = ic.getIndexDir();
        System.out.println("Index dir: " + parameter);
        System.out.println("Index dir (resolved): " + ic.resolvePath(parameter));

        parameter = ic.getHTDocsDumpDir();
        System.out.println("htdocs_dump: " + parameter);
        System.out.println("htdocs_dump (resolved): " + ic.resolvePath(parameter));

        System.out.println("Indexer class: " + ic.getIndexerClass());
    }

    /**
     * Configures this object.
     * @param config The configuration.
     * @throws Exception if an error occurs.
     */
    protected void configure(Configuration config) throws Exception {
        updateIndexType = config.getChild("update-index").getAttribute("type");
        indexDir = config.getChild("index-dir").getAttribute("src");
        htdocsDumpDir = config.getChild("htdocs-dump-dir").getAttribute("src");
        String indexerClassName = config.getChild("indexer").getAttribute("class");
        indexerClass = Class.forName(indexerClassName);

        if (log.isDebugEnabled()) {
            log.debug("Configuring indexer:"
                    + "\nupdate index type:     " + updateIndexType
                    + "\nindex directory:       " + indexDir
                    + "\nhtdocs dump directory: " + htdocsDumpDir
                    + "\nindexer class:         " + indexerClass.getName());
        }

    }

    /**
     * Returns the update index type.
     * @return A string.
     */
    public String getUpdateIndexType() {
        return updateIndexType;
    }

    /**
     * Returns the directory where the index is stored.
     * @return A string.
     */
    public String getIndexDir() {
        return indexDir;
    }

    /**
     * Returns the directory where the HTML documents are dumped.
     * @return A string.
     */
    public String getHTDocsDumpDir() {
        return htdocsDumpDir;
    }

    /**
     * Returns the class of the indexer to use.
     * @return A class.
     */
    public Class getIndexerClass() {
        return indexerClass;
    }

    /**
     * Resolves a path. If the path is relative, it is resolved relatively
     * to the configuration directory.
     * @param path A string.
     * @return A string.
     */
    public String resolvePath(String path) {
        if (path.indexOf(File.separator) == 0) {
            return path;
        }
        return FileUtil.catPath(configurationFilePath, path);
    }
}