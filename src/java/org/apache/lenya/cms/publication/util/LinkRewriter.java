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
package org.apache.lenya.cms.publication.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Rewrite the links in a publication. This is used after renaming / moving a
 * document.
 * 
 * @version $Id:$
 */
public class LinkRewriter {

    private FileFilter directoryFilter = new FileFilter() {

        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    private FileFilter xmlFileFilter = new FileFilter() {

        public boolean accept(File file) {
            return file.isFile() && FilenameUtils.getExtension(file.getName()).equals("xml");
        }
    };

    /**
     * Ctor.
     */
    public LinkRewriter() {
    }

    /**
     * Rewrites the links to a document and all its descendants, including all
     * language versions.
     * @param originalTargetDocument The original target document.
     * @param newTargetDocument The new target document.
     * @param contextPath The servlet context path.
     */
    public void rewriteLinks(Document originalTargetDocument, Document newTargetDocument,
            String contextPath) {

        Publication publication = originalTargetDocument.getPublication();
        String area = originalTargetDocument.getArea();
        File[] files = getDocumentFiles(publication, area);

        DocumentBuilder builder = publication.getDocumentBuilder();

        try {
            for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
                org.w3c.dom.Document xmlDocument = DocumentHelper.readDocument(files[fileIndex]);
                boolean linksRewritten = false;

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

                        if (url.startsWith(contextPath + "/" + publication.getId())) {
                            final String webappUrl = url.substring(contextPath.length());
                            
                            if (builder.isDocument(publication, webappUrl)) {
                                Document targetDocument = builder.buildDocument(publication, webappUrl);

                                if (matches(targetDocument, originalTargetDocument)) {
                                    String newTargetUrl = getNewTargetURL(targetDocument,
                                            originalTargetDocument,
                                            newTargetDocument);
                                    attribute.setValue(contextPath + newTargetUrl);
                                    linksRewritten = true;
                                }
                            }
                        }
                    }
                }

                if (linksRewritten) {
                    DocumentHelper.writeDocument(xmlDocument, files[fileIndex]);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if targetDocument refers to originalTargetDocument, to one of its
     * language versions, to one of its descendants, or to a language version of
     * one of the descendants.
     * @param targetDocument The target document.
     * @param originalTargetDocument The original target document.
     * @return A boolean value.
     */
    protected boolean matches(Document targetDocument, Document originalTargetDocument) {
        String matchString = originalTargetDocument.getId() + "/";
        String testString = targetDocument.getId() + "/";
        return testString.startsWith(matchString);
    }

    /**
     * Rewrites a document.
     * @param targetDocument The target document to rewrite.
     * @param originalTargetDocument The original target document.
     * @param newTargetDocument The new target document.
     * @return A string.
     */
    protected String getNewTargetURL(Document targetDocument, Document originalTargetDocument,
            Document newTargetDocument) {
        String originalId = originalTargetDocument.getId();
        String targetId = targetDocument.getId();
        String childString = targetId.substring(originalId.length());

        DocumentBuilder builder = targetDocument.getPublication().getDocumentBuilder();
        String newTargetUrl = builder.buildCanonicalUrl(newTargetDocument.getPublication(),
                newTargetDocument.getArea(),
                newTargetDocument.getId() + childString,
                targetDocument.getLanguage());

        return newTargetUrl;
    }

    /**
     * Returns all XML files in a specific area.
     * @param publication The publication.
     * @param area The area.
     * @return An array of files.
     */
    protected File[] getDocumentFiles(Publication publication, String area) {
        File directory = publication.getContentDirectory(area);
        List files = getDocumentFiles(directory);
        return (File[]) files.toArray(new File[files.size()]);
    }

    /**
     * Returns all XML files in a specific directory.
     * @param directory The directory.
     * @return A list of files.
     */
    protected List getDocumentFiles(File directory) {

        List list = new ArrayList();

        File[] directories = directory.listFiles(directoryFilter);
        for (int i = 0; i < directories.length; i++) {
            list.addAll(getDocumentFiles(directories[i]));
        }
        File[] xmlFiles = directory.listFiles(xmlFileFilter);
        list.addAll(Arrays.asList(xmlFiles));
        return list;
    }

}