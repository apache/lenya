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

/* $Id: DocumentIndexTransformer.java,v 1.7 2004/03/01 16:18:20 gregor Exp $  */

package org.apache.lenya.cms.cocoon.transformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This transformer lists the children of a document if the tag <namespaceURI:children> 
 * is present in this document. The list of the children is in the form :
 * <namespaceURI:children>
 *   <child href="....html>
 *     <ci:include src="..." element="included"/> 
 *   </child>
 *   ...
 * </namespaceURI:children>
 * Multiple language : if a child doesn't exist in the parent language, then the version 
 * in the default language will be considered. If it doesn't exist too, any other existent 
 * language will be considered.
 */
public class DocumentIndexTransformer extends AbstractSAXTransformer implements Parameterizable {

    private String namespace;
    private String cIncludeNamespace;

    public static final String CHILDREN_ELEMENT = "children";
    public static final String ABSTRACT_ATTRIBUTE = "abstract";
    
    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/documentindex/1.0";
    public static final String PREFIX = "index:";

    /** (non-Javadoc)
    	 * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
    	 */
    public void parameterize(Parameters parameters) throws ParameterException {
        this.namespace = parameters.getParameter("namespace", null);
        this.cIncludeNamespace = parameters.getParameter("cIncludeNamespace", null);
    }

    private Document document;

    private Publication publication;

    private String area;

    private DocumentBuilder builder;

    private SiteTree siteTree;

    /** (non-Javadoc)
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters)
        throws ProcessingException, SAXException, IOException {
        try {
            parameterize(parameters);

            PageEnvelope envelope = null;
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);

            setDocument(envelope.getDocument());
            setPublication(document.getPublication());
            setArea(document.getArea());
            setBuilder(document.getPublication().getDocumentBuilder());
            setSiteTree(publication.getSiteTree(area));

        } catch (Exception e) {
            throw new ProcessingException(e);
        }

    }

    /** (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String raw, Attributes attr)
        throws SAXException {

        if (uri != null
            && uri.equals(namespace)
            && cIncludeNamespace != null
            && localName.equals(CHILDREN_ELEMENT)) {
                
            if (getLogger().isInfoEnabled()) {
                getLogger().info("Inserting index");
            }

            String cIncludePrefix = "";
            if (!this.cIncludeNamespace.equals("")) {
                cIncludePrefix = "ci:";
            }

            String documentId = document.getId();
            String language = document.getLanguage();
            String defaultLanguage = publication.getDefaultLanguage();
            SiteTreeNode[] children = siteTree.getNode(documentId).getChildren();

            super.startElement(uri, localName, raw, attr);

            for (int i = 0; i < children.length; i++) {
                String childId = documentId + "/" + children[i].getId();

                //get child document with the same language than the parent document
                String url = builder.buildCanonicalUrl(publication, area, childId, language);
                Document doc;
                try {
                    doc = builder.buildDocument(publication, url);
                } catch (DocumentBuildException e) {
                    throw new SAXException(e);
                }
                File file = doc.getFile();

                if (!file.exists()) {
                    //get first the child document in the default language and then in any other existent language
                    getLogger().debug(
                        "There is no child file "
                            + file.getAbsolutePath()
                            + " in the same language as the parent document ["
                            + language
                            + "]");

                    //available language    
                    String[] availableLanguages = null;
                    try {
                        availableLanguages = doc.getLanguages();
                    } catch (DocumentException e) {
                        throw new SAXException(e);
                    }

                    List languages = new ArrayList();
                    for (int l = 0; l < availableLanguages.length; l++) {
                        if (availableLanguages[l].equals(language)) {
                            getLogger().debug(
                                "Do nothing because language was already tested: ["
                                    + availableLanguages[l]
                                    + "]");
                        } else if (availableLanguages[l].equals(defaultLanguage)) {
                            languages.add(0, availableLanguages[l]);
                        } else {
                            languages.add(availableLanguages[l]);
                        }
                    }

                    int j = 0;
                    while (!file.exists() && j < languages.size()) {
                        String newlanguage = (String) languages.get(j);
                        url = builder.buildCanonicalUrl(publication, area, childId, newlanguage);
                        try {
                            doc = builder.buildDocument(publication, url);
                        } catch (DocumentBuildException e) {
                            throw new SAXException(e);
                        }
                        file = doc.getFile();

                        j++;
                    }
                }

                if (file.exists()) {
                    //create the tags for the child
                    String path;
                    try {
                        path = file.getCanonicalPath();
                    } catch (IOException e) {
                        throw new SAXException(e);
                    }

                    AttributesImpl attribute = new AttributesImpl();
                    attribute.addAttribute("", "href", "href", "", url);
                    super.startElement(NAMESPACE, "child", PREFIX + "child", attribute);

                    AttributesImpl attributes = new AttributesImpl();
                    attributes.addAttribute("", "src", "src", "", path);
                    attributes.addAttribute("", "element", "element", "", "included");

                    super.startElement(
                        this.cIncludeNamespace,
                        "include",
                        cIncludePrefix + "include",
                        attributes);
                    super.endElement(this.cIncludeNamespace, "include", cIncludePrefix + "include");
                    super.endElement(NAMESPACE, "child", PREFIX + "child");
                } else {
                    //do nothing for this child
                    getLogger().warn("There are no existing file for the child with id " + childId);
                }

            }
        } else {
            super.startElement(uri, localName, raw, attr);
        }

    }

    /**
     * @return SiteTree The siteTree belonging to the area of the document
     */
    public SiteTree getSiteTree() {
        return siteTree;
    }

    /**
     * @param tree The siteTree of the area, which the document belongs.
     */
    public void setSiteTree(SiteTree tree) {
        siteTree = tree;
    }

    /**
     * @param string The area, which the document belongs.
     */
    public void setArea(String string) {
        area = string;
    }

    /**
     * @param builder The document builder.
     */
    public void setBuilder(DocumentBuilder builder) {
        this.builder = builder;
    }

    /**
     * @param document The document.
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * @param publication The publication, which the document belongs.
     */
    public void setPublication(Publication publication) {
        this.publication = publication;
    }

}
