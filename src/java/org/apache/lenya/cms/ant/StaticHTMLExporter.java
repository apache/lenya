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
package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publishing.ExportException;

import org.apache.tools.ant.BuildException;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Ant task that exports a set of HTML URIs as static HTML files.
 * @author  andreas
 */
public class StaticHTMLExporter extends PublicationTask {
    /** Creates a new instance of StaticHTMLExporter */
    public StaticHTMLExporter() {
    }

    private String serverURL;

    /**
     * Returns the server URL.
     */
    protected URL getServer() throws MalformedURLException {
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
    public void export(URL serverURI, File publicationDirectory, String exportPath, String[] uris,
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
    public void execute() throws BuildException {
        try {
            log("Server URL: " + getServer());
            log("Publication Directory: " + getPublicationDirectory());
            log("Export directory: " + getPath());
            log("URIs: " + uris);
            log("Substitute expression: " + getExpression());
            log("Substitute replacement: " + getReplacement());

            export(getServer(), getPublicationDirectory(), getPath(), getUris(), getExpression(),
                getReplacement());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
