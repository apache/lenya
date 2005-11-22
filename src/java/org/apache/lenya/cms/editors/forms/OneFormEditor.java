/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.editors.forms;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.usecase.xml.UsecaseErrorHandler;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.Schema;
import org.apache.lenya.xml.ValidationUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * One form editor.
 * 
 * @version $Id$
 */
public class OneFormEditor extends DocumentUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        org.apache.lenya.cms.repository.Node[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!WorkflowUtil.canInvoke(this.manager,
                getSession(),
                getLogger(),
                getSourceDocument(),
                getEvent())) {
            addErrorMessage("error-workflow-document", new String[] { getEvent(),
                    getSourceDocument().getId() });
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        // Get namespaces
        String namespaces = removeRedundantNamespaces(getParameterAsString("namespaces"));
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(namespaces);
        }

        // Aggregate content

        Request request = ContextHelper.getRequest(this.context);
        String encoding = request.getCharacterEncoding();
        String content = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n"
                + addNamespaces(namespaces, getParameterAsString("content"));

        // Save file temporarily

        ModifiableSource xmlSource = null;
        SourceResolver resolver = null;
        Source indexSource = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            xmlSource = (ModifiableSource) resolver.resolveURI(getSourceDocument().getSourceURI());
            saveXMLFile(encoding, content, xmlSource);
            
            Document xmlDoc = null;

            try {
                xmlDoc = DocumentHelper.readDocument(xmlSource.getInputStream());
            } catch (SAXException e) {
                addErrorMessage("error-document-form", new String[] { e.getMessage() });
            }

            if (xmlDoc != null) {
                ResourceType resourceType = getSourceDocument().getResourceType();
                Schema schema = resourceType.getSchema();
                
                ValidationUtil.validate(this.manager, xmlDoc, schema, new UsecaseErrorHandler(this));
                
                if (!hasErrors()) {
                    WorkflowUtil.invoke(this.manager,
                            getSession(),
                            getLogger(),
                            getSourceDocument(),
                            getEvent());
                    
                    //re-index      
                    indexSource = resolver.resolveURI("cocoon://core/modules/lucene/lucene/index.xml");
                    InputSource xmlInputSource = org.apache.cocoon.components.source.SourceUtil.getInputSource(indexSource);
                }
            }

        } finally {
            if (resolver != null) {
                if (xmlSource != null) {
                    resolver.release(xmlSource);
                }
                if (indexSource != null) {
                    resolver.release(indexSource);
                }
                this.manager.release(resolver);
            }
        }
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
    private void saveXMLFile(String encoding, String content, ModifiableSource xmlSource)
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        FileOutputStream fileoutstream = null;
        Writer writer = null;

        try {
            writer = new OutputStreamWriter(xmlSource.getOutputStream(), encoding);
            writer.write(content, 0, content.length());
        } catch (FileNotFoundException e) {
            getLogger().error("File not found " + e.toString());
        } catch (UnsupportedEncodingException e) {
            getLogger().error("Encoding not supported " + e.toString());
        } catch (IOException e) {
            getLogger().error("IO error " + e.toString());
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

    protected String getEvent() {
        return "edit";
    }

}
