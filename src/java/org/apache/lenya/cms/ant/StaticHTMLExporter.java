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

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.lenya.cms.publishing.ExportException;
import org.apache.tools.ant.BuildException;


/**
 * Ant task that exports a set of HTML URIs as static HTML files.
 */
public class StaticHTMLExporter extends PublicationTask {
    /** Creates a new instance of StaticHTMLExporter */
    public StaticHTMLExporter() {
	    // do nothing
    }

    private String serverURL;

    /**
     * Returns the server URL.
     * @return The server URL
     * @throws MalformedURLException if the Server URL is not valid
     */
    protected URL getServer() throws MalformedURLException {
        return new URL(this.serverURL);
    }

    /**
     * Sets the server URL.
     * @param _serverURL The server URL
     */
    public void setServer(String _serverURL) {
        this.serverURL = _serverURL;
    }

    private String path;

    /**
     * Returns the path to the exported files.
     * @return The path
     */
    protected String getPath() {
        return this.path;
    }

    /**
     * Sets the path to the exported files.
     * @param _path The path
     */
    public void setPath(String _path) {
        this.path = _path;
    }

    private String uris;

    /**
     * Returns the URIs to export.
     * @return The URIs
     */
    protected String[] getUris() {
        return this.uris.split(",");
    }

    /**
     * Sets the URIs to export.
     * @param _uris The URIs
     */
    public void setUris(String _uris) {
        this.uris = _uris;
    }

    private String expression;

    /**
     * Returns the expression in the URI to be replaced.
     * @return The expression
     */
    protected String getExpression() {
        return this.expression;
    }

    /**
     * Sets the expression in the URI to be replaced.
     * @param _expression The expression
     */
    public void setExpression(String _expression) {
        this.expression = _expression;
    }

    private String replacement;

    /**
     * Returns the string in the URI that replaces the expression.
     * @return The replacement string
     */
    protected String getReplacement() {
        return this.replacement;
    }

    /**
     * Sets the string in the URI that replaces the expression.
     * @param _replacement The replacement string
     */
    public void setReplacement(String _replacement) {
        this.replacement = _replacement;
    }

    /**
     * Exports a set of URIs as a HTML file.
     * @param serverURI The server to download the file from.
     * @param publicationDirectory The directory of the publication.
     * @param exportPath The path to export the files to (relative to publicationDirectory).
     * @param _uris The URIs to export (relative to the publication URI).
     * @param substituteExpression A part of the complete URIs to be substituted.
     * @param substituteReplacement A string to replace substituteExpression.
     * @throws ExportException if an error occurs
     */
    public void export(URL serverURI, File publicationDirectory, String exportPath, String[] _uris,
        String substituteExpression, String substituteReplacement)
        throws ExportException {
        try {
            File exportDirectory;

            if (new File(exportPath).isAbsolute()) {
                exportDirectory = new File(exportPath);
            } else {
                exportDirectory = new File(publicationDirectory, exportPath);
            }

            if (!exportDirectory.exists()) {
                exportDirectory.mkdirs();
            }

            org.apache.lenya.net.WGet wget = new org.apache.lenya.net.WGet();
            wget.setDirectoryPrefix(exportDirectory.getAbsolutePath());

            String fullServerURI = serverURI.toString();

            for (int i = 0; i < _uris.length; i++) {
                URL uri = new URL(fullServerURI + _uris[i]);
                wget.download(uri, substituteExpression, substituteReplacement);
                log("Exported URI: " + uri);
            }
        } catch (final MalformedURLException e) {
            log("Malformed URL " +e.toString());
            throw new ExportException(e);
        } catch (final IOException e) {
            log("IO error " +e.toString());
            throw new ExportException(e);
        }
    }

    /**
     * Executes the task.
     * @throws BuildException if an error occurs
     */
    public void execute() throws BuildException {

        try {
            log("Server URL: " + getServer());
            log("Publication Directory: " + getPublicationDirectory());
            log("Export directory: " + getPath());
            log("URIs: " + this.uris);
            log("Substitute expression: " + getExpression());
            log("Substitute replacement: " + getReplacement());

            export(getServer(), getPublicationDirectory(), getPath(), getUris(), getExpression(),
                getReplacement());
        } catch (final MalformedURLException e) {
            throw new BuildException(e);
        } catch (final ExportException e) {
            throw new BuildException(e);
        }

    }
}
