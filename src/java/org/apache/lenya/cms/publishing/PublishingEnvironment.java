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

/* $Id$  */

package org.apache.lenya.cms.publishing;

import java.io.File;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Logger;


/**
 * A class that holds various publishing parameters
 */
public class PublishingEnvironment implements Configurable {
    private static Logger log = Logger.getLogger(PublishingEnvironment.class);
    /**
     * <code>CONFIGURATION_FILE</code> The path to the publisher configuration file
     */
    public static final String CONFIGURATION_FILE = "config" + File.separator + "publishing" +
        File.separator + "publisher.xconf";
    /**
     * <code>PUBLICATION_PREFIX</code> The path to the publications from the webapp root
     */
    public static final String PUBLICATION_PREFIX = "lenya" + File.separator + "pubs" +
        File.separator;
    /**
     * <code>PUBLICATION_PATH</code> The publication path
     */
    public static final String PUBLICATION_PATH = "publication-path";
    /**
     * <code>PARAMETER_AUTHORING_PATH</code> The authoring path
     */
    public static final String PARAMETER_AUTHORING_PATH = "authoring-path";
    /**
     * <code>PARAMETER_TREE_AUTHORING_PATH</code> The path to the authoring tree
     */
    public static final String PARAMETER_TREE_AUTHORING_PATH = "tree-authoring-path";
    /**
     * <code>PARAMETER_LIVE_PATH</code> The live path
     */
    public static final String PARAMETER_LIVE_PATH = "live-path";
    /**
     * <code>PARAMETER_TREE_LIVE_PATH</code> The path to the live tree
     */
    public static final String PARAMETER_TREE_LIVE_PATH = "tree-live-path";
    /**
     * <code>PARAMETER_REPLICATION_PATH</code> The replication path
     */
    public static final String PARAMETER_REPLICATION_PATH = "replication-path";
    /**
     * <code>PARAMETER_EXPORT_PATH</code> The export path
     */
    public static final String PARAMETER_EXPORT_PATH = "export-path";
    /**
     * <code>PARAMETER_SUBSTITUTE_REGEXP</code> The regular expression to use during export
     */
    public static final String PARAMETER_SUBSTITUTE_REGEXP = "substitute-regexp";
    /**
     * <code>PARAMETER_SUBSTITUTE_REPLACEMENT</code> The replacement value for the regular expression
     */
    public static final String PARAMETER_SUBSTITUTE_REPLACEMENT = "substitute-replacement";
    private String publicationPath;
    private String replicationDirectory;
    private String authoringPath;
    private String livePath;
    private String treeAuthoringPath;
    private String treeLivePath;
    private String exportDirectory;
    private String substituteExpression;
    private String substituteReplacement;

    /**
     * Creates a new PublishingEnvironment object.
     *
     * @param contextPath The context path
     * @param publicationId The publication id
     */
    public PublishingEnvironment(String contextPath, String publicationId) {
        this(PublishingEnvironment.getPublicationPath(contextPath, publicationId));
        log.debug("Context Path and Publication Id: " + contextPath + "::" + publicationId);
    }

    /**
     * Creates a new PublishingEnvironment object.
     *
     * @param _publicationPath The publication path
     */
    public PublishingEnvironment(String _publicationPath) {
        setPublicationPath(_publicationPath);

        String configurationFilePath = _publicationPath + CONFIGURATION_FILE;

        File configurationFile = new File(configurationFilePath);

        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration configuration = builder.buildFromFile(configurationFile);
            configure(configuration);
        } catch (Exception e) {
            log.info(
                "Did not load publishing configuration from publisher.xconf (No such file or directory: " + configurationFile  + "). " +
                "That means you can't access all PublishingEnvironment parameters and you should only " +
                "use the AntTask. But don't panic, this file has been DEPRECATED.");
        }
    }

    /**
     * Initializes the class from a configuration file
     *
     * @param configuration The configuration file
     *
     * @throws org.apache.avalon.framework.configuration.ConfigurationException if an error occurs
     */
    public void configure(org.apache.avalon.framework.configuration.Configuration configuration)
        throws org.apache.avalon.framework.configuration.ConfigurationException {
        // authoring
        setAuthoringPath(configuration.getChild("authoring").getChild("documents").getAttribute("href"));
        setTreeAuthoringPath(configuration.getChild("authoring").getChild("tree").getAttribute("href"));

        // replication
        setReplicationDirectory(configuration.getChild("replication").getChild("pending-documents")
                                             .getAttribute("href"));

        // live
        setLivePath(configuration.getChild("live").getChild("documents").getAttribute("href"));
        setTreeLivePath(configuration.getChild("live").getChild("tree").getAttribute("href"));

        // export
        setExportDirectory(configuration.getChild("export").getChild("destination").getAttribute("href"));
        setSubstituteExpression(configuration.getChild("export").getChild("substitution")
                                             .getAttribute("regexp"));
        setSubstituteReplacementExpression(configuration.getChild("export").getChild("substitution")
                                                        .getAttribute("replacement"));

        log.debug("CONFIGURATION:\nauthoring path=" + getAuthoringPath() + "\nlive path=" +
            getLivePath());
        log.debug("CONFIGURATION:\ntree authoring path=" + getTreeAuthoringPath() +
            "\ntree live path=" + getTreeLivePath());

        log.debug("CONFIGURATION:\nDirectory Prefix: href=" + getExportDirectory());
        log.debug("CONFIGURATION:\nPrefix Substitute: href=" + getSubstituteExpression());

        log.debug("CONFIGURATION:\nReplication Directory: href=" + getReplicationDirectory());
    }

    /**
     * Returns the publication path.
     *
     * @return The publication path
     */
    public String getPublicationPath() {
        return this.publicationPath;
    }

    /**
     * Returns the publication directory.
     * @return The publication directory
     */
    public File getPublicationDirectory() {
        return new File(getPublicationPath());
    }

    protected void setPublicationPath(String path) {
        this.publicationPath = path;
    }

    /**
     * Returns the authoring path
     *
     * @return The authoring path
     */
    public String getAuthoringPath() {
        return this.authoringPath;
    }

    protected void setAuthoringPath(String path) {
        this.authoringPath = path;
    }

    /**
     * Returns the live path
     *
     * @return The live path
     */
    public String getLivePath() {
        return this.livePath;
    }

    protected void setLivePath(String path) {
        this.livePath = path;
    }

    /**
     * Returns the path to the authoring tree
     *
     * @return The path to the authoring tree
     */
    public String getTreeAuthoringPath() {
        return this.treeAuthoringPath;
    }

    protected void setTreeAuthoringPath(String path) {
        this.treeAuthoringPath = path;
    }

    /**
     * Returns the path to the live tree
     *
     * @return The path to the live tree
     */
    public String getTreeLivePath() {
        return this.treeLivePath;
    }

    protected void setTreeLivePath(String path) {
        this.treeLivePath = path;
    }

    /**
     * Returns the path to the replication directory
     *
     * @return The path to the replication directory
     */
    public String getReplicationDirectory() {
        return this.replicationDirectory;
    }

    protected void setReplicationDirectory(String directory) {
        this.replicationDirectory = directory;
    }

    /**
     * Returns the path to the export directory
     *
     * @return The path to the export directory
     */
    public String getExportDirectory() {
        return this.exportDirectory;
    }

    protected void setExportDirectory(String directory) {
        this.exportDirectory = directory;
    }

    /**
     * Returns the substitution regular expression used in the export
     *
     * @return The substitution regular expression
     */
    public String getSubstituteExpression() {
        return this.substituteExpression;
    }

    protected void setSubstituteExpression(String substitute) {
        this.substituteExpression = substitute;
    }

    /**
     * Set replacement string, which was read from publisher.xconf
     * @param replacement The replacement string
     */
    protected void setSubstituteReplacementExpression(String replacement) {
        this.substituteReplacement = replacement;
    }

    /**
     * Get the replacement string, which was read from publisher.xconf
     *
     * @return The replacement string
     */
    public String getSubstituteReplacement() {
        return this.substituteReplacement;
    }

    /**
     * Returns the path to the publication given the servlet context path and the publication id
     *
     * @param servletContextPath The servlet context path
     * @param publicationId The publication id
     *
     * @return The path to the publication
     */
    public static String getPublicationPath(String servletContextPath, String publicationId) {
        if (!servletContextPath.endsWith(File.separator)) {
            servletContextPath += File.separator;
        }

        return servletContextPath + PUBLICATION_PREFIX + publicationId + File.separator;
    }
}
