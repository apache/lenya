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

package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.cms.site.tree.TreeSiteManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This transformer lists the children of a document if the tag <namespaceURI:children>is present in
 * this document. The list of the children is in the form :<namespaceURI:children><child
 * href="....html> <ci:include src="..." element="included"/> </child> ... </namespaceURI:children>
 * Multiple language : if a child doesn't exist in the parent language, then the version in the
 * default language will be considered. If it doesn't exist too, any other existent language will be
 * considered.
 */
public class DocumentIndexTransformer extends AbstractSAXTransformer implements Parameterizable {

    private String namespace;
    private String cIncludeNamespace;

    /**
     * <code>CHILDREN_ELEMENT</code> The children element
     */
    public static final String CHILDREN_ELEMENT = "children";
    /**
     * <code>ABSTRACT_ATTRIBUTE</code> The abstract attribute
     */
    public static final String ABSTRACT_ATTRIBUTE = "abstract";
    /**
     * <code>NAMESPACE</code> The document index namespace
     */
    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/documentindex/1.0";
    /**
     * <code>PREFIX</code> The namespace prefix
     */
    public static final String PREFIX = "index:";

    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters _parameters) throws ParameterException {
        this.namespace = _parameters.getParameter("namespace", null);
        this.cIncludeNamespace = _parameters.getParameter("cIncludeNamespace", null);
    }

    private Document document;
    private Publication publication;
    private String area;
    private SiteTree siteTree;
    private DocumentIdentityMap identityMap;

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver,
     *      java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver _resolver, Map _objectModel, String src, Parameters _parameters)
            throws ProcessingException, SAXException, IOException {

        TreeSiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            super.setup(_resolver, _objectModel, src, _parameters);

            parameterize(_parameters);

            PageEnvelope envelope = null;
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            this.publication = factory.getPublication(_objectModel);
            this.identityMap = new DocumentIdentityMap(this.manager, getLogger());
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(this.identityMap,
                    _objectModel);

            setDocument(envelope.getDocument());
            setArea(this.document.getArea());

            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (TreeSiteManager) selector.select(this.publication.getSiteManagerHint());

            setSiteTree(siteManager.getTree(this.identityMap, this.publication, this.area));
        } catch (final ProcessingException e) {
            throw e;
        } catch (final Exception e) {
            throw new ProcessingException(e);
        }

    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String,
     *      java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String raw, Attributes attr)
            throws SAXException {

        try {
            if (uri != null && uri.equals(this.namespace) && this.cIncludeNamespace != null
                    && localName.equals(CHILDREN_ELEMENT)) {

                if (getLogger().isInfoEnabled()) {
                    getLogger().info("Inserting index");
                }

                String cIncludePrefix = "";
                if (!this.cIncludeNamespace.equals("")) {
                    cIncludePrefix = "ci:";
                }

                String documentId = this.document.getId();
                String language = this.document.getLanguage();
                String defaultLanguage = this.publication.getDefaultLanguage();
                SiteTreeNode[] children = this.siteTree.getNode(documentId).getChildren();

                super.startElement(uri, localName, raw, attr);

                for (int i = 0; i < children.length; i++) {
                    String childId = documentId + "/" + children[i].getId();

                    //get child document with the same language than the parent
                    // document
                    Document doc;
                    try {
                        doc = this.identityMap.get(this.publication, this.area, childId, language);
                    } catch (DocumentBuildException e) {
                        throw new SAXException(e);
                    }
                    String url = doc.getCanonicalWebappURL();

                    if (!doc.exists()) {
                        //get first the child document in the default language
                        // and then in any
                        // other
                        // existent language
                        getLogger().debug("There is no child document [" + doc
                                + "] in the same language as the parent document [" + language
                                + "]");

                        //available language
                        String[] availableLanguages = null;
                        try {
                            availableLanguages = doc.getLanguages();
                        } catch (final DocumentException e) {
                            throw new SAXException(e);
                        }

                        List languages = new ArrayList();
                        for (int l = 0; l < availableLanguages.length; l++) {
                            if (availableLanguages[l].equals(language)) {
                                getLogger()
                                        .debug("Do nothing because language was already tested: ["
                                                + availableLanguages[l] + "]");
                            } else if (availableLanguages[l].equals(defaultLanguage)) {
                                languages.add(0, availableLanguages[l]);
                            } else {
                                languages.add(availableLanguages[l]);
                            }
                        }

                        int j = 0;
                        while (!doc.exists() && j < languages.size()) {
                            String newlanguage = (String) languages.get(j);
                            try {
                                doc = this.identityMap.get(this.publication,
                                        this.area,
                                        childId,
                                        newlanguage);
                            } catch (final DocumentBuildException e) {
                                throw new SAXException(e);
                            }
                            url = doc.getCanonicalWebappURL();

                            j++;
                        }
                    }

                    if (doc.exists()) {
                        //create the tags for the child
                        String sourceUri = doc.getSourceURI();

                        AttributesImpl attribute = new AttributesImpl();
                        attribute.addAttribute("", "href", "href", "", url);
                        super.startElement(NAMESPACE, "child", PREFIX + "child", attribute);

                        AttributesImpl attributes = new AttributesImpl();
                        attributes.addAttribute("", "src", "src", "", sourceUri);
                        attributes.addAttribute("", "element", "element", "", "included");

                        super.startElement(this.cIncludeNamespace, "include", cIncludePrefix
                                + "include", attributes);
                        super.endElement(this.cIncludeNamespace, "include", cIncludePrefix
                                + "include");
                        super.endElement(NAMESPACE, "child", PREFIX + "child");
                    } else {
                        //do nothing for this child
                        getLogger().warn("There are no existing file for the child with id "
                                + childId);
                    }

                }
            } else {
                super.startElement(uri, localName, raw, attr);
            }
        } catch (final DocumentException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @return SiteTree The siteTree belonging to the area of the document
     */
    public SiteTree getSiteTree() {
        return this.siteTree;
    }

    /**
     * @param tree The siteTree of the area, which the document belongs.
     */
    public void setSiteTree(SiteTree tree) {
        this.siteTree = tree;
    }

    /**
     * @param string The area, which the document belongs.
     */
    public void setArea(String string) {
        this.area = string;
    }

    /**
     * @param _document The document.
     */
    public void setDocument(Document _document) {
        this.document = _document;
    }

    /**
     * @param _publication The publication, which the document belongs.
     */
    public void setPublication(Publication _publication) {
        this.publication = _publication;
    }

}