/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.commons.io.IOUtils;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.linking.LinkConverter;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.cms.workflow.usecases.UsecaseWorkflowHelper;
import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.Schema;
import org.apache.lenya.xml.ValidationUtil;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * One form editor.
 * 
 * @version $Id$
 */
public class OneFormEditor extends DocumentUsecase implements ErrorHandler {

    protected static final String PARAM_VALIDATION_ERRORS = "validationErrors";
    protected static final String PARAM_CONTENT = "content";
    protected static final String DEFAULT_ENCODING = "utf-8";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        org.apache.lenya.cms.publication.Document doc = getSourceDocument();
        Set nodes = new HashSet();
        if (doc != null) {
            nodes.add(doc.getRepositoryNode());
        }
        return (org.apache.lenya.cms.repository.Node[]) nodes
                .toArray(new org.apache.lenya.cms.repository.Node[nodes.size()]);
    }

    protected void prepareView() throws Exception {
        super.prepareView();

        StringWriter writer = new StringWriter();
        IOUtils.copy(getSourceDocument().getInputStream(), writer, DEFAULT_ENCODING);
        String xmlString = writer.toString();
        setParameter(PARAM_CONTENT, xmlString);
        validate(xmlString, DEFAULT_ENCODING);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!hasErrors()) {
            UsecaseWorkflowHelper.checkWorkflow(this.manager, this, getEvent(),
                    getSourceDocument(), getLogger());
            if (!ServletHelper.isUploadEnabled(this.manager)) {
                addErrorMessage("upload-disabled");
            }
        }
        setParameter("executable", Boolean.valueOf(!hasErrors()));
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        saveDocument(getXml(getContent(), getRequestEncoding()));
    }

    protected String getRequestEncoding() {
        Request request = ContextHelper.getRequest(this.context);
        return request.getCharacterEncoding();
    }

    protected String getContent() {
        return getParameterAsString(PARAM_CONTENT);
    }

    public void advance() throws UsecaseException {
        clearErrorMessages();
        try {
            String content = getContent();
            String encoding = getRequestEncoding();
            Document xml = getXml(content, encoding);
            if (xml != null) {
                validate(content, encoding);
            }
            if (!hasErrors()) {
                IOUtils.copy(new StringReader(content), getSourceDocument().getOutputStream());
                deleteParameter(PARAM_CONTENT);
            }
        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        if (hasErrors()) {
            return;
        }
        String encoding = getRequestEncoding();
        Document xml = getXml(getContent(), encoding);
        if (xml != null) {
            validate(getContent(), encoding);
        }
    }

    protected void validate(String xmlString, String encoding) throws Exception {
        ResourceType resourceType = getSourceDocument().getResourceType();
        Schema schema = resourceType.getSchema();
        if (schema == null) {
            getLogger().info(
                    "No schema declared for resource type [" + resourceType.getName()
                            + "], skipping validation.");
        } else {
            deleteParameter(PARAM_VALIDATION_ERRORS);
            byte bytes[] = xmlString.getBytes(encoding);
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            StreamSource source = new StreamSource(stream);
            ValidationUtil.validate(this.manager, source, schema, this);
            if (!getValidationErrors().isEmpty()) {
                addErrorMessage("editors.validationFailed");
            }
        }
    }

    protected Document getXml(String xmlString, String encoding)
            throws ParserConfigurationException, IOException {
        try {
            return DocumentHelper.readDocument(xmlString, encoding);
        } catch (SAXException e) {
            addErrorMessage("error-document-form", new String[] { e.getMessage() });
            return null;
        }
    }

    /**
     * Save the content to the document source. After saving, the XML is validated. If validation
     * errors occur, the usecase transaction is rolled back, so the changes are not persistent. If
     * the validation succeeded, the workflow event is invoked.
     * 
     * @param content The content to save.
     * @throws Exception if an error occurs.
     */
    protected void saveDocument(Document content) throws Exception {
        saveXMLFile(content, getSourceDocument());

        WorkflowUtil.invoke(this.manager, getSession(), getLogger(), getSourceDocument(),
                getEvent());
    }

    /**
     * Save the XML file
     * 
     * @param content The content
     * @param document The source
     */
    protected void saveXMLFile(Document content, org.apache.lenya.cms.publication.Document document) {
        try {
            SourceUtil.writeDOM(content, document.getOutputStream());
            LinkConverter converter = new LinkConverter(this.manager, getLogger());
            converter.convertUrlsToUuids(document, false);
        } catch (Exception e) {
            addErrorMessage(e.getMessage());
        }
    }

    protected String getEvent() {
        return "edit";
    }

    public static class ValidationError {

        protected static final int SEVERITY_WARNING = 0;
        protected static final int SEVERITY_ERROR = 1;
        protected static final int SEVERITY_FATAL = 2;

        private int severity;

        public int getLine() {
            return this.line;
        }

        public int getColumn() {
            return this.column;
        }

        public String getMessage() {
            return this.message;
        }

        private int line;
        private int column;
        private String message;

        public ValidationError(int severity, SAXParseException e) {
            this.message = e.getMessage();
            this.line = e.getLineNumber();
            this.column = e.getColumnNumber();
            this.severity = severity;
        }

    }

    protected List getValidationErrors() {
        List errors = (List) getParameter(PARAM_VALIDATION_ERRORS);
        if (errors == null) {
            errors = new ArrayList();
            setParameter(PARAM_VALIDATION_ERRORS, errors);
        }
        return errors;
    }

    public void error(SAXParseException e) throws SAXException {
        getValidationErrors().add(new ValidationError(ValidationError.SEVERITY_ERROR, e));
    }

    public void fatalError(SAXParseException e) throws SAXException {
        getValidationErrors().add(new ValidationError(ValidationError.SEVERITY_FATAL, e));
    }

    public void warning(SAXParseException e) throws SAXException {
        getValidationErrors().add(new ValidationError(ValidationError.SEVERITY_WARNING, e));
    }

}
