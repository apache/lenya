/*
 * DocumentTypeBuilder.java
 *
 * Created on 9. April 2003, 10:11
 */

package org.apache.lenya.cms.publication;

import java.io.File;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A builder for document types.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public class DocumentTypeBuilder {
    
    /** Creates a new instance of DocumentTypeBuilder */
    public DocumentTypeBuilder() {
    }
    
    /**
     * The default document types configuration file, relative to the publication directory.
     */
    public static final String CONFIG_FILE
    	= "config/doctypes/doctypes.xconf".replace('/', File.separatorChar);
    
    /**
     * Builds a document type for a given name.
     * 
     * @param name A string value.
     * @param publication The publication the document type belongs to.
     * @return A document type object.
     */
    public DocumentType buildDocumentType(String name, Publication publication)
        throws DocumentTypeBuildException {
    	
    	File configFile = new File(publication.getDirectory(), CONFIG_FILE);
    	
    	try {
			Document document = DocumentHelper.readDocument(configFile);  
			DocumentHelper helper = new DocumentHelper();
			
			String xPath = "doctypes/doc[@type = '" + name + "']";
			Node doctypeNode = XPathAPI.selectSingleNode(document, xPath);
			
			// TODO add doctype initialization code
    	}
    	catch (Exception e) {
            throw new DocumentTypeBuildException(e);
    	}
    	
        DocumentType type = new DocumentType(name);
        return type;
    }

    
}
