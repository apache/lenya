/*
 * StaticHTMLExporter.java
 *
 * Created on 6. Mai 2003, 18:00
 */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.lenya.cms.publishing.ExportException;
import org.apache.tools.ant.BuildException;

/**
 * Ant task that exports a set of HTML URIs as static HTML files.
 * @author  andreas
 */
public class StaticHTMLExporter
    extends PublicationTask {
    
    /** Creates a new instance of StaticHTMLExporter */
    public StaticHTMLExporter() {
    }
    
    private String serverURL;
    
    /**
     * Returns the server URL.
     */
    protected URL getServer()
        throws MalformedURLException {
        return new URL(serverURL);
    }
    
    /**
     * Sets the server URL.
     */
    public void setServer(String serverURL) {
        this.serverURL = serverURL;
    }
    
    private String path;
    
    /**
     * Returns the path to the exported files.
     */
    protected String getPath() {
        return path;
    }
    
    /**
     * Sets the path to the exported files.
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    private String uris;
    
    /**
     * Returns the URIs to export.
     */
    protected String[] getUris() {
        return uris.split(",");
    }
    
    /**
     * Sets the URIs to export.
     */
    public void setUris(String uris) {
        this.uris = uris;
    }
    
    private String expression;

    /**
     * Returns the expression in the URI to be replaced.
     */
    protected String getExpression() {
        return expression;
    }
    
    /**
     * Sets the expression in the URI to be replaced.
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }
    
    private String replacement;
    
    /**
     * Returns the string in the URI that replaces the expression.
     */
    protected String getReplacement() {
        return replacement;
    }
    
    /**
     * Sets the string in the URI that replaces the expression.
     */
    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }
    
    /**
     * Exports a set of URIs as a HTML file.
     * @param serverURI The server to download the file from.
     * @param publicationDirectory The directory of the publication.
     * @param exportPath The path to export the files to (relative to publicationDirectory).
     * @param uris The URIs to export (relative to the publication URI).
     * @param substituteExpression A part of the complete URIs to be substituted.
     * @param substituteReplacement A string to replace substituteExpression.
     */
    public void export(URL serverURI, File publicationDirectory, String exportPath,
        String[] uris, String substituteExpression, String substituteReplacement)
        throws ExportException {
        try {
            File exportDirectory;
            if (new File(exportPath).isAbsolute()) {
                exportDirectory = new File(exportPath);
            }
            else {
                exportDirectory = new File(publicationDirectory, exportPath);
            }
            
            if (!exportDirectory.exists()) {
                exportDirectory.mkdirs();
            }

            org.apache.lenya.net.WGet wget = new org.apache.lenya.net.WGet();
            wget.setDirectoryPrefix(exportDirectory.getAbsolutePath());

            String fullServerURI = serverURI.toString();

            for (int i = 0; i < uris.length; i++) {
                URL uri = new URL(fullServerURI + uris[i]);
                wget.download(uri, substituteExpression, substituteReplacement);
                log("Exported URI: " + uri);
            }
        } catch (Exception e) {
            throw new ExportException(e);
        }
    }
    
    /**
     * Executes the task.
     */
    public void execute()
        throws BuildException {
            
        try {
            log("Server URL: " + getServer());
            log("Publication Directory: " + getPublicationDirectory());
            log("Export directory: " + getPath());
            log("URIs: " + uris);
            log("Substitute expression: " + getExpression());
            log("Substitute replacement: " + getReplacement());

            export(
                getServer(),
                getPublicationDirectory(),
                getPath(),
                getUris(),
                getExpression(),
                getReplacement());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

}
