/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.publishing;

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
            log.warn(
                "Did not load publishing configuration from publication.xconf (file is missing). " +
                "That means you can't access all PublishingEnvironment parameters and you should only" +
                "use the AntTask.");
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
