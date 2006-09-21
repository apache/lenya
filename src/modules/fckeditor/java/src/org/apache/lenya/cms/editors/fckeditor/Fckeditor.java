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
package org.apache.lenya.cms.editors.fckeditor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Properties;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.xml.XMLUtils;
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
import org.xml.sax.SAXException;
import org.w3c.tidy.Tidy;

/**
 * Fckeditor Usecase
 * 
 */
public class Fckeditor extends DocumentUsecase {
    
    public static final String TIDY_CONFIG="tidyConfig";
    public static final String XSLT_CLEAN_FORMAT="xslt-clean";
    
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        org.apache.lenya.cms.repository.Node[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        
        Request request = ContextHelper.getRequest(this.context);
        String requesturi = request.getRequestURI();
        String host = "http://"+request.getServerName()+":"+request.getServerPort();
        setParameter("host",host);
        setParameter("requesturi",requesturi);
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
                    getSourceDocument().toString() });
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
        saveDocument(encoding, content);
    }

    /**
     * Save the content to the document source. After saving, the XML is validated. If validation
     * errors occur, the usecase transaction is rolled back, so the changes are not persistent. If
     * the validation succeeded, the workflow event is invoked.
     * @param encoding The encoding to use.
     * @param content The content to save.
     * @throws Exception if an error occurs.
     */
    protected void saveDocument(String encoding, String content) throws Exception {
        ModifiableSource xmlSource = null;
        SourceResolver resolver = null;
        Source indexSource = null;
        Source tidySource = null;
        ModifiableSource xsltSource = null;
        Properties properties = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            xmlSource = (ModifiableSource) resolver.resolveURI(getSourceDocument().getSourceURI());
            saveXMLFile(encoding, content, xmlSource);
            
            Document xmlDoc = null;
            
            // Setup an instance of Tidy.
            Tidy tidy = new Tidy();
            
            String tidyProps = this.getParameterAsString(TIDY_CONFIG, null);
            if (tidyProps != null) {
                tidySource = resolver.resolveURI(tidyProps);
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                        "Loading configuration from " + tidySource.getURI());
                }
                properties = new Properties();
                properties.load(tidySource.getInputStream());
            

                if (properties == null) {
                    tidy.setXHTML(true);
                } else {
                    tidy.setConfigurationFromProps(properties);
                }
            
                //Set Jtidy warnings on-off
                tidy.setShowWarnings(getLogger().isWarnEnabled());
                //Set Jtidy final result summary on-off
                tidy.setQuiet(!getLogger().isInfoEnabled());
                //Set Jtidy infos to a String (will be logged) instead of System.out
                StringWriter stringWriter = new StringWriter();
                PrintWriter errorWriter = new PrintWriter(stringWriter);
                tidy.setErrout(errorWriter);

                xmlDoc = tidy.parseDOM(xmlSource.getInputStream(), null);
            
                // FIXME: Jtidy doesn't warn or strip duplicate attributes in same
                // tag; stripping.
                XMLUtils.stripDuplicateAttributes(xmlDoc, null);
            
                StringWriter output = new StringWriter();
                StreamResult strResult = new StreamResult(output);
                TransformerFactory tfac = TransformerFactory.newInstance();
                try {
                    Transformer t = tfac.newTransformer();
                    t.setOutputProperty(OutputKeys.ENCODING, encoding);
                    t.setOutputProperty(OutputKeys.INDENT, "yes");
                    t.setOutputProperty(OutputKeys.METHOD, "xml");
                    t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                    t.transform(new DOMSource(xmlDoc.getDocumentElement()), strResult);
                
                    content = strResult.getWriter().toString();
                } catch (Exception e) {
                    addErrorMessage(e.getMessage());
                }
                
                saveXMLFile(encoding, content, xmlSource);
            } else {
                try {
                  xmlDoc = DocumentHelper.readDocument(xmlSource.getInputStream());
                } catch (SAXException e) {
                    addErrorMessage("error-document-form", new String[] { e.getMessage() });
                }
            }
            
            //Try to clean the xml using xslt
            ResourceType resType = getSourceDocument().getResourceType();
            String[] formats = resType.getFormats();
            if (Arrays.asList(formats).contains(XSLT_CLEAN_FORMAT)) {
                StringWriter output = new StringWriter();
                StreamResult strResult = new StreamResult(output);
                TransformerFactory tfac = TransformerFactory.newInstance();
                try {
                    xsltSource = (ModifiableSource) resolver.resolveURI(resType.getFormatURI(XSLT_CLEAN_FORMAT));
                    Transformer t = tfac.newTransformer(new StreamSource(xsltSource.getInputStream()));
                    t.setOutputProperty(OutputKeys.ENCODING, encoding);
                    t.setOutputProperty(OutputKeys.INDENT, "yes");
                    t.setOutputProperty(OutputKeys.METHOD, "xml");
                    t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                    xmlDoc = DocumentHelper.readDocument(xmlSource.getInputStream());
                    t.transform(new DOMSource(xmlDoc.getDocumentElement()), strResult);
                
                    content = strResult.getWriter().toString();
                } catch (Exception e) {
                    addErrorMessage(e.getMessage());
                }
                
                saveXMLFile(encoding, content, xmlSource);
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
                if (tidySource != null) {
                    resolver.release(tidySource);
                }
                if (xsltSource != null) {
                    resolver.release(xsltSource);
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
