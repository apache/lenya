/*
 * $Id: PublishingEnvironment.java,v 1.7 2003/03/04 19:44:44 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.cms.publishing;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @author ah
 * @author Michael Wechner
 */
public class PublishingEnvironment implements Configurable {
    static Category log = Category.getInstance(PublishingEnvironment.class);
    public static final String CONFIGURATION_FILE = "docs" + File.separator + "publication" +
        File.separator + "publication.xconf";
    public static final String PUBLICATION_PREFIX = "lenya" + File.separator + "cms" +
        File.separator + "pubs" + File.separator;
    public static final String PARAMETER_AUTHORING_PATH = "authoring-path";
    public static final String PARAMETER_TREE_AUTHORING_PATH = "tree-authoring-path";
    public static final String PARAMETER_LIVE_PATH = "live-path";
    public static final String PARAMETER_TREE_LIVE_PATH = "tree-live-path";
    public static final String PARAMETER_REPLICATION_PATH = "replication-path";
    public static final String PARAMETER_EXPORT_PATH = "export-path";
    public static final String PARAMETER_SUBSTITUTE_REGEXP = "substitute-regexp";
    private String publicationPath;
    private String replicationDirectory;
    private String authoringPath;
    private String livePath;
    private String treeAuthoringPath;
    private String treeLivePath;
    private String exportDirectory;
    private String substituteExpression;

    /**
     * Creates a new PublishingEnvironment object.
     *
     * @param contextPath DOCUMENT ME!
     * @param publicationId DOCUMENT ME!
     */
    public PublishingEnvironment(String contextPath, String publicationId) {
        this(PublishingEnvironment.getPublicationPath(contextPath, publicationId));
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
            log.error("Cannot load publishing configuration! ", e);
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

        log.debug("CONFIGURATION:\nauthoring path=" + getAuthoringPath() + "\nlive path=" +
            getLivePath());
        log.debug("CONFIGURATION:\ntree authoring path=" + getTreeAuthoringPath() +
            "\ntree live path=" + getTreeLivePath());

        log.debug("CONFIGURATION:\nDirectory Prefix: href=" + getExportDirectory());
        log.debug("CONFIGURATION:\nPrefix Substitute: href=" + getSubstituteExpression());

        log.debug("CONFIGURATION:\nReplication Directory: href=" + getReplicationDirectory());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPublicationPath() {
        return publicationPath;
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
     * DOCUMENT ME!
     *
     * @param servletContextPath DOCUMENT ME!
     * @param publicationId DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getPublicationPath(String servletContextPath, String publicationId) {
        return servletContextPath + PUBLICATION_PREFIX + publicationId + File.separator;
    }
}
