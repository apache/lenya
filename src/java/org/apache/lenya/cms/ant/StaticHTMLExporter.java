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
import org.apache.tools.ant.Task;

/**
 *
 * @author  andreas
 */
public class StaticHTMLExporter
    extends PublicationTask {
    
    /** Creates a new instance of StaticHTMLExporter */
    public StaticHTMLExporter() {
    }
    
    private String serverURL;
    
    protected URL getServer()
        throws MalformedURLException {
        return new URL(serverURL);
    }
    
    public void setServer(String serverURL) {
        this.serverURL = serverURL;
    }
    
    private String path;
    
    protected String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    private String uris;
    
    protected String[] getUris() {
        return uris.split(",");
    }
    
    public void setUris(String uris) {
        this.uris = uris;
    }
    
    private String expression;
    
    protected String getExpression() {
        return expression;
    }
    
    public void setExpression(String expression) {
        this.expression = expression;
    }
    
    private String replacement;
    
    protected String getReplacement() {
        return replacement;
    }
    
    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }
    
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
