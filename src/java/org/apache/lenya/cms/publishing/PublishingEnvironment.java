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

/* $Id: PublishingEnvironment.java,v 1.26 2004/03/01 16:18:18 gregor Exp $  */

package org.apache.lenya.cms.publishing;

import java.io.File;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;


public class PublishingEnvironment implements Configurable {
    private static Category log = Category.getInstance(PublishingEnvironment.class);
    public static final String CONFIGURATION_FILE = "config" + File.separator + "publishing" +
        File.separator + "publisher.xconf";
    public static final String PUBLICATION_PREFIX = "lenya" + File.separator + "pubs" +
        File.separator;
    public static final String PUBLICATION_PATH = "publication-path";
    public static final String PARAMETER_AUTHORING_PATH = "authoring-path";
    public static final String PARAMETER_TREE_AUTHORING_PATH = "tree-authoring-path";
    public static final String PARAMETER_LIVE_PATH = "live-path";
    public static final String PARAMETER_TREE_LIVE_PATH = "tree-live-path";
    public static final String PARAMETER_REPLICATION_PATH = "replication-path";
    public static final String PARAMETER_EXPORT_PATH = "export-path";
    public static final String PARAMETER_SUBSTITUTE_REGEXP = "substitute-regexp";
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
     * @param contextPath DOCUMENT ME!
     * @param publicationId DOCUMENT ME!
     */
    public PublishingEnvironment(String contextPath, String publicationId) {
        this(PublishingEnvironment.getPublicationPath(contextPath, publicationId));
        log.debug("Context Path and Publication Id: " + contextPath + "::" + publicationId);
    }

    /**
     * Creates a new PublishingEnvironment object.
     *
     * @param publicationPath DOCUMENT ME!
     */
    public PublishingEnvironment(String publicationPath) {
        setPublicationPath(publicationPath);

        String configurationFilePath = publicationPath + CONFIGURATION_FILE;

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
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws org.apache.avalon.framework.configuration.ConfigurationException DOCUMENT ME!
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
     * Returns the publication directory.
     *
     * @return DOCUMENT ME!
     */
    public String getPublicationPath() {
        return publicationPath;
    }

    /**
     * Returns the publication directory.
     */
    public File getPublicationDirectory() {
        return new File(getPublicationPath());
    }

    protected void setPublicationPath(String path) {
        publicationPath = path;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAuthoringPath() {
        return authoringPath;
    }

    protected void setAuthoringPath(String path) {
        authoringPath = path;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLivePath() {
        return livePath;
    }

    protected void setLivePath(String path) {
        livePath = path;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTreeAuthoringPath() {
        return treeAuthoringPath;
    }

    protected void setTreeAuthoringPath(String path) {
        treeAuthoringPath = path;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTreeLivePath() {
        return treeLivePath;
    }

    protected void setTreeLivePath(String path) {
        treeLivePath = path;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getReplicationDirectory() {
        return replicationDirectory;
    }

    protected void setReplicationDirectory(String directory) {
        replicationDirectory = directory;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getExportDirectory() {
        return exportDirectory;
    }

    protected void setExportDirectory(String directory) {
        exportDirectory = directory;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSubstituteExpression() {
        return substituteExpression;
    }

    protected void setSubstituteExpression(String substitute) {
        substituteExpression = substitute;
    }

    /**
     * Set replacement string, which was read from publisher.xconf
     */
    protected void setSubstituteReplacementExpression(String replacement) {
        substituteReplacement = replacement;
    }

    /**
     * Get the replacement string, which was read from publisher.xconf
     *
     * @return The replacement string
     */
    public String getSubstituteReplacement() {
        return substituteReplacement;
    }

    /**
     * DOCUMENT ME!
     *
     * @param servletContextPath DOCUMENT ME!
     * @param publicationId DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getPublicationPath(String servletContextPath, String publicationId) {
        if (!servletContextPath.endsWith(File.separator)) {
            servletContextPath += File.separator;
        }

        return servletContextPath + PUBLICATION_PREFIX + publicationId + File.separator;
    }
}
