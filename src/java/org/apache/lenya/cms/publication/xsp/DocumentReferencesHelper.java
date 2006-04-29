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

package org.apache.lenya.cms.publication.xsp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationHelper;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

/**
 * Helper class for finding references to the current document.
 */
public class DocumentReferencesHelper {

    private static final Logger log = Logger.getLogger(DocumentReferencesHelper.class);

    private PageEnvelope pageEnvelope = null;
    private Publication publication = null;
    private Document document = null;

    /**
     * Create a new DocumentReferencesHelper
     * 
     * @param objectModel the objectModel
     * 
     * @throws ProcessingException if the page envelope could not be created.
     */
    public DocumentReferencesHelper(Map objectModel)
        throws ProcessingException {
        try {
            this.pageEnvelope =
                PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
            this.publication = this.pageEnvelope.getPublication();
            this.document = this.pageEnvelope.getDocument();
        } catch (PageEnvelopeException e) {
            throw new ProcessingException(e);
        }
    }

    
    /**
     * Finds all internal links in the given file.
     * An internal link has the format 
     * /context-path/publication-id/foo
     * 
     * @param file
     * @return the webapp url of the link without the context prefix
     */
    protected String[] getInternalLinks(File file) 
        throws ParserConfigurationException, TransformerException, SAXException, IOException {
      
        ArrayList links = new ArrayList();
        org.w3c.dom.Document xmlDocument = DocumentHelper.readDocument(file);
        String[] xPaths = publication.getRewriteAttributeXPaths();

        for (int xPathIndex = 0; xPathIndex < xPaths.length; xPathIndex++) {
            NodeList nodes = XPathAPI.selectNodeList(xmlDocument, xPaths[xPathIndex]);
            for (int nodeIndex = 0; nodeIndex < nodes.getLength(); nodeIndex++) {
                Node node = nodes.item(nodeIndex);
                if (node.getNodeType() != Node.ATTRIBUTE_NODE) {
                    throw new RuntimeException("The XPath [" + xPaths[xPathIndex]
                            + "] may only match attribute nodes!");
                }
                Attr attribute = (Attr) node;
                final String url = attribute.getValue();

                if (url.startsWith(pageEnvelope.getContext() + "/" + publication.getId())) {
                    final String webappUrl = url.substring(pageEnvelope.getContext().length());
                    links.add(url);
                }
            }
        }
        return (String[])links.toArray(new String[links.size()]);
    }

    /**
     * Find a list of document-ids which have references to the current
     * document.
     * 
     * @return an <code>array</code> of documents if there are references, 
     * an empty <code>array</code> otherwise 
     * 
     * @throws ProcessingException if the search for references failed.
     */
    public Document[] getReferences(String area) throws ProcessingException {
        
        try {
            PublicationHelper pubHelper = new PublicationHelper(this.publication);
            DocumentBuilder builder = publication.getDocumentBuilder();
            Document[] documents = pubHelper.getAllDocuments(area);
            ArrayList targetDocuments = new ArrayList();

            for (int docIndex = 0; docIndex < documents.length; docIndex++) {
                String[] links = getInternalLinks(documents[docIndex].getFile());
                
                for (int linkIndex = 0; linkIndex < links.length; linkIndex++) {
                    if (builder.isDocument(publication, links[linkIndex])) {
                        Document targetDocument = builder.buildDocument(publication, links[linkIndex]);

                        if (targetDocument.equals(document)) {
                            if (log.isDebugEnabled()) {
                                log.debug("found link to " + document + " in " + documents[docIndex]);
                            }
                            
                            targetDocuments.add(documents[docIndex]);
                        }
                    }
                }
            }
            return (Document[])targetDocuments.toArray(new Document[targetDocuments.size()]);
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }
    

    /**
     * Find all internal references in the current document to documents which have
     * not been published yet.
     * 
     * @return an <code>array</code> of <code>Document</code> of references 
     * from the current document to documents which have not been published yet.
     *
     * @throws ProcessingException if the current document cannot be opened.
     */
    public Document[] getInternalReferences() throws ProcessingException {
        ArrayList unpublishedReferences = new ArrayList();
        DocumentBuilder builder = publication.getDocumentBuilder();

        try {
            SiteTree sitetree = publication.getTree(Publication.LIVE_AREA);
            String[] links = getInternalLinks(this.document.getFile());
            
            for (int linkIndex = 0; linkIndex < links.length; linkIndex++) {
                if (builder.isDocument(publication, links[linkIndex])) {
                    Document targetDocument = builder.buildDocument(publication, links[linkIndex]);

                    SiteTreeNode documentNode = sitetree.getNode(targetDocument.getId());

                    if (documentNode == null || documentNode.getLabel(targetDocument.getLanguage()) == null) {
                        // the document has not been published for the given language
                        if (log.isDebugEnabled()) {
                            log.debug("found reference to unpublished document: " + targetDocument);
                        }
                        unpublishedReferences.add(targetDocument);
                    }
                }
            }
            return (Document[])unpublishedReferences.toArray(new Document[unpublishedReferences.size()]);
        } catch (Exception e) {
            throw new ProcessingException(e);
        }   
    }        
}
