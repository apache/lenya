/*
 * PublishingPaths.java
 *
 * Created on November 4, 2002, 1:09 PM
 */

package org.wyona.cms.publishing;

import java.io.File;
import java.net.URL;
import org.apache.avalon.framework.configuration.*;
import org.apache.log4j.Category;
import org.xml.sax.SAXException;

/**
 *
 * @author  ah
 */
public class PublishingEnvironment implements Configurable {

    static Category log = Category.getInstance(PublishingEnvironment.class);
    
    public static final String CONFIGURATION_FILE = "docs" + File.separator
                + "publication" + File.separator + "publication.xconf";

    public static final String PUBLICATION_PREFIX = "wyona" + File.separator
                + "cms" + File.separator + "pubs" + File.separator;

    public static final String PARAMETER_PUBLICATION_ID = "publicationID";
    public static final String PARAMETER_SOURCES = "sources";
    public static final String PARAMETER_URIS = "uris";
    
    public PublishingEnvironment(String contextPath, String publicationId) {
        this(PublishingEnvironment.getPublicationPath(contextPath, publicationId));
    }
    
    public PublishingEnvironment(String publicationPath) {
        setPublicationPath(publicationPath);
        String configurationFilePath = publicationPath + CONFIGURATION_FILE;
        File configurationFile = new File(configurationFilePath);
        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration configuration = builder.buildFromFile(configurationFile);
            configure(configuration);
        }
        catch (Exception e) {
            log.error("Cannot load publishing configuration! ", e);
        }
    }
    
    public void configure(
            org.apache.avalon.framework.configuration.Configuration configuration)
            throws org.apache.avalon.framework.configuration.ConfigurationException {
        
        // authoring
        setAuthoringPath(
                configuration.getChild("authoring").getChild("documents").getAttribute("href"));
        setTreeAuthoringPath(
                configuration.getChild("authoring").getChild("tree").getAttribute("href"));

        // live
        setLivePath(
                configuration.getChild("live").getChild("documents").getAttribute("href"));
        setTreeLivePath(
                configuration.getChild("live").getChild("tree").getAttribute("href"));

        // export
        setExportDirectory(configuration.getChild("export").getChild("destination").getAttribute("href"));
        setSubstituteExpression(configuration.getChild("export").getChild("substitution").getAttribute("regexp"));

        log.debug("CONFIGURATION:\nauthoring path=" +
                          getAuthoringPath() + "\nlive path=" +
                          getLivePath());
        log.debug("CONFIGURATION:\ntree authoring path=" +
                          getTreeAuthoringPath() + "\ntree live path=" +
                          getTreeLivePath());

        log.debug("CONFIGURATION:\nDirectory Prefix: href=" + getExportDirectory());
        log.debug("CONFIGURATION:\nPrefix Substitute: href=" + getSubstituteExpression());
    }
    
    private String publicationPath;
    private String authoringPath, livePath, treeAuthoringPath, treeLivePath;
    private String exportDirectory, substituteExpression;

    public String getPublicationPath() {
        return publicationPath;
    }
    
    protected void setPublicationPath(String path) {
        publicationPath = path;
    }
    
    public String getAuthoringPath() {
        return authoringPath;
    }
    
    protected void setAuthoringPath(String path) {
        authoringPath = path;
    }
    
    public String getLivePath() {
        return livePath;
    }
    
    protected void setLivePath(String path) {
        livePath = path;
    }
    
    public String getTreeAuthoringPath() {
        return treeAuthoringPath;
    }
    
    protected void setTreeAuthoringPath(String path) {
        treeAuthoringPath = path;
    }
    
    public String getTreeLivePath() {
        return treeLivePath;
    }
    
    protected void setTreeLivePath(String path) {
        treeLivePath = path;
    }
        
    public String getExportDirectory() {
        return exportDirectory;
    }
    
    protected void setExportDirectory(String directory) {
        exportDirectory = directory;
    }
    
    public String getSubstituteExpression() {
        return substituteExpression;
    }
    
    protected void setSubstituteExpression(String substitute) {
        substituteExpression = substitute;
    }
    
    public static String getPublicationPath(String servletContextPath, String publicationId) {
        return
                servletContextPath
                + PUBLICATION_PREFIX
                + publicationId
                + File.separator;
    }
    
}
