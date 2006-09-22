/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

/* 
  since there is no really tinymce-specific code in here, perhaps it should
  be made fully generic and moved to the editors module.
*/

package org.apache.lenya.cms.editors.tinymce;

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
import org.xml.sax.SAXException;

/**
 * TinyMce Usecase
 *
 */
public class TinyMce extends DocumentUsecase {

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
        setParameter("host", "http://"
            + request.getServerName()
            + ":" +request.getServerPort()
        );
        setParameter("requesturi",request.getRequestURI());
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
                 getEvent())) 
        {
             addErrorMessage(
                 "error-workflow-document", 
                 new String[] { 
                     getEvent(),
                     getSourceDocument().getId() 
                 }
             );
        }
        addInfoMessage("This is a usecase InfoMessage.");
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        // Get namespaces
        String namespaces = getParameterAsString("tinymce.namespaces");
        if (namespaces == null) {
            namespaces = new String();
        } else {
             namespaces = removeDuplicates(namespaces);
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(namespaces);
        }

        // Aggregate content
        Request request = ContextHelper.getRequest(this.context);
        String encoding = request.getCharacterEncoding();
// bad: hardcoded header. needs work.
        String content = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\" " + namespaces + ">\n"
                + "  <head><title></title></head>\n"
                + "  <body>\n"
                + getParameterAsString("tinymce.content") 
                + "  </body>\n"
                + "</html>\n";

        //content = content.replaceAll("[\r\n]", "");

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(content);
        }

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
            throws FileNotFoundException, UnsupportedEncodingException, IOException  {
//        FileOutputStream fileoutstream = null;
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
  //          if (fileoutstream != null)
  //              fileoutstream.close();
        }
    }

    /**
     * Remove duplicates
     * @param list A list of string tokens (separated by spaces) to check for duplicate tokens.
     * @return The list of string tokens with duplicates removed
     */
    private String removeDuplicates(String tokenList) {
        String[] tokens = tokenList.split(" ");

        String s = "";
        for (int i = 0; i < tokens.length; i++) {
            if (s.indexOf(tokens[i]) < 0) {
                s = s + " " + tokens[i];
            } else {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Duplicate token: " + tokens[i]);
                }
            }
        }
        return s;
    }

  protected String getEvent() {
      return "edit";
  }
  
}
