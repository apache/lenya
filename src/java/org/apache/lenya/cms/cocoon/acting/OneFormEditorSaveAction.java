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

package org.apache.lenya.cms.cocoon.acting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.excalibur.source.Source;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.RelaxNG;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *  
 */
public class OneFormEditorSaveAction extends AbstractConfigurableAction implements ThreadSafe {

    /**
     * Save data to temporary file.
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector,
     *      org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String,
     *      org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
            Parameters parameters) throws Exception {

        HttpRequest request = (HttpRequest) ObjectModelHelper.getRequest(objectModel);

        // Get namespaces
        String namespaces = removeRedundantNamespaces(request.getParameter("namespaces"));
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(namespaces);
        }

        // Aggregate content
        String encoding = request.getCharacterEncoding();
        String content = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n"
                + addNamespaces(namespaces, request.getParameter("content"));

        // Save file temporarily

        String xmlUri = parameters.getParameter("file");

        String schemaUri = parameters.getParameter("schema");
        Source schemaSource = null;
        Source xmlSource = null;
        try {

            xmlSource = resolver.resolveURI(xmlUri);
            saveXMLFile(encoding, content, xmlSource);

            try {
                DocumentHelper.readDocument(xmlSource.getInputStream());
            }
            catch (SAXException e) {
                getLogger().error("Wellformedness check failed: " + e.getMessage());
                Map hmap = new HashMap();
                hmap.put("message", "Document is not well-formed: " + e.getMessage());
                return hmap;
            }
            
            schemaSource = resolver.resolveURI(schemaUri);
            if (!schemaSource.exists()) {
                throw new IllegalArgumentException("The schema [" + schemaSource.getURI()
                        + "] does not exist.");
            }

            InputSource schemaInputSource = SourceUtil.getInputSource(schemaSource);
            InputSource xmlInputSource = SourceUtil.getInputSource(xmlSource);

            String message = RelaxNG.validate(schemaInputSource, xmlInputSource);
            if (message != null) {
                getLogger().error("RELAX NG Validation failed: " + message);
                HashMap hmap = new HashMap();
                hmap.put("message", "RELAX NG Validation failed: " + message);
                return hmap;
            }

        } finally {
            if (schemaSource != null) {
                resolver.release(schemaSource);
            }
            if (xmlSource != null) {
                resolver.release(xmlSource);
            }
        }

        return null;
    }

    /**
     * Save the XML file
     * @param encoding The encoding
     * @param content The content
     * @param xmlSource The source
     * @throws FileNotFoundException if the file was not found
     * @throws UnsupportedEncodingException if the encoding is not supported
     * @throws IOException if an IO error occurs
     */
    private void saveXMLFile(String encoding, String content, Source xmlSource)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        FileOutputStream fileoutstream = null;
        Writer writer = null;
        
        try {
            File xmlFile = org.apache.excalibur.source.SourceUtil.getFile(xmlSource);
            File parentFile = new File(xmlFile.getParent());
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            fileoutstream = new FileOutputStream(xmlFile);
            writer = new OutputStreamWriter(fileoutstream, encoding);
            writer.write(content, 0, content.length());
        } catch (FileNotFoundException e) {
            getLogger().error("File not found " +e.toString());
        } catch (UnsupportedEncodingException e) {
            getLogger().error("Encoding not supported " +e.toString());
        } catch (IOException e) {
            getLogger().error("IO error " +e.toString());
        } finally {
            // close all streams
            if (writer != null)
                writer.close();
            if (fileoutstream != null)
                fileoutstream.close();
        }
    }

    /**
     * Remove redundant namespaces
     * @param namespaces The namespaces to remove
     * @return The namespace string without the removed namespaces
     */
    private String removeRedundantNamespaces(String namespaces) {
        String[] namespace = namespaces.split(" ");

        String ns = "";
        for (int i = 0; i < namespace.length; i++) {
            if (ns.indexOf(namespace[i]) < 0) {
                ns = ns + " " + namespace[i];
            } else {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Redundant namespace: " + namespace[i]);
                }
            }
        }
        return ns;
    }

    /**
     * Add namespaces
     * @param namespaces The namespaces to add
     * @param content The content to add them to
     * @return The content with the added namespaces
     */
    private String addNamespaces(String namespaces, String content) {
        int i = content.indexOf(">");
        return content.substring(0, i) + " " + namespaces + content.substring(i);
    }
}