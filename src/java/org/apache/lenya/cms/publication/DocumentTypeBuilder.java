/*
 * DocumentTypeBuilder.java
 *
 * Created on 9. April 2003, 10:11
 */

package org.lenya.cms.publication;

import java.io.File;
import org.lenya.cms.workflow.Workflow;
import org.lenya.cms.workflow.impl.WorkflowBuildException;
import org.lenya.cms.workflow.impl.WorkflowBuilder;
import org.lenya.cms.workflow.impl.WorkflowBuilderFactory;
import org.lenya.xml.DocumentHelper;
import org.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author  andreas
 */
public class DocumentTypeBuilder {
    
    /** Creates a new instance of DocumentTypeBuilder */
    public DocumentTypeBuilder() {
    }
    
    public static final String NAMESPACE = "http://www.lenya.org/2003/doctype";
    public static final String DEFAULT_PREFIX = "dt";
    
    public static final String WORKFLOW_ELEMENT = "workflow";
    public static final String SRC_ATTRIBUTE = "src";
    
    public DocumentType buildDocumentType(File file, Publication publication)
        throws DocumentTypeBuildException {
        DocumentType type;
        
        try {
            Document document = DocumentHelper.readDocument(file);
            type = buildDocumentType(document, publication);
        }
        catch (Exception e) {
            throw new DocumentTypeBuildException(e);
        }
        
        return type;
    }

    public static final String WORKFLOW_DIRECTORY
        = "config/workflow/".replace('/', File.separatorChar);
    
    
    protected DocumentType buildDocumentType(Document document, Publication publication)
        throws DocumentTypeBuildException {
            
        NamespaceHelper helper = new NamespaceHelper(NAMESPACE, DEFAULT_PREFIX, document);
        Element root = document.getDocumentElement();
           
        Element workflowElement
            = (Element) root.getElementsByTagNameNS(NAMESPACE, WORKFLOW_ELEMENT).item(0);
        
        String source = workflowElement.getAttribute(SRC_ATTRIBUTE);
        assert source != null;
        
        File publicationDirectory = publication.getEnvironment().getPublicationDirectory();
        String fileName = WORKFLOW_DIRECTORY + source;
        File workflowFile = new File(publicationDirectory, fileName);
        
        WorkflowBuilderFactory factory = new WorkflowBuilderFactory();
        WorkflowBuilder builder = factory.createBuilder();
        Workflow workflow;
        
        try {
            workflow = builder.buildWorkflow(workflowFile);
        }
        catch (WorkflowBuildException e) {
            throw new DocumentTypeBuildException(e);
        }
        
        DocumentType type = new DocumentType(workflow);
        return type;
    }
    
}
