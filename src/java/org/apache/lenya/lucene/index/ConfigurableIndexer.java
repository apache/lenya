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

/* $Id: ConfigurableIndexer.java,v 1.13 2004/03/01 16:18:15 gregor Exp $  */

package org.apache.lenya.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.log4j.Category;
import org.w3c.dom.Element;

public class ConfigurableIndexer extends AbstractIndexer {
    Category log = Category.getInstance(ConfigurableIndexer.class);

    /**
     * Instantiate a Document Creator for creating Lucene Documents
     *
     * @param element <code>indexer</code> node
     *
     * @return DocumentCreator
     *
     * @throws Exception DOCUMENT ME!
     */
    public DocumentCreator createDocumentCreator(Element indexer, String configFileName) throws Exception {
        log.debug(".createDocumentCreatort(): Element name: " + indexer.getNodeName());

        // FIXME: concat these files ...
        String configurationFileName = new File(configFileName).getParent() + File.separator + getLuceneDocConfigFileName(indexer);
        File configurationFile = new File(configurationFileName);
        String stylesheet = getStylesheet(configurationFile);
        return new ConfigurableDocumentCreator(stylesheet);
    }

    public static final String CONFIGURATION_CREATOR_STYLESHEET = "org/apache/lenya/lucene/index/configuration2xslt.xsl";

    /**
     * Converts the configuration file to an XSLT stylesheet and returns a reader that reads this stylesheet.
     */
    protected String getStylesheet(File configurationFile) throws Exception {
        log.debug(".getStylesheet(): Configuration file: " + configurationFile.getAbsolutePath());

        URL configurationCreatorURL = ConfigurableIndexer.class.getClassLoader().getResource(CONFIGURATION_CREATOR_STYLESHEET);
        File configurationStylesheetFile = new File(new URI(configurationCreatorURL.toString()));
        org.w3c.dom.Document configurationDocument = DocumentHelper.readDocument(configurationFile);

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer configurationTransformer = tFactory.newTransformer(new StreamSource(configurationStylesheetFile));

        DOMSource source = new DOMSource(configurationDocument);
        Writer stylesheetWriter = new StringWriter();
        configurationTransformer.transform(source, new StreamResult(stylesheetWriter));

        log.debug(".getStylesheet(): Meta Stylesheet: " + stylesheetWriter.toString());

        return stylesheetWriter.toString();
    }

    /**
     * Returns the filter used to receive the indexable files.
     */
    public FileFilter getFilter(Element indexer, String configFileName) {
        String[] indexableExtensions = new String[1];
	    indexableExtensions[0] = getExtensions(indexer);
        return new AbstractIndexer.DefaultIndexFilter(indexableExtensions);
    }

    /**
     *
     */
    private String getLuceneDocConfigFileName(Element indexer) {
        String luceneDocConfigFileName = null;

        org.w3c.dom.NodeList nl = indexer.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node node = nl.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && node.getNodeName().equals("configuration")) {
                log.debug(".getLuceneDocConfigFileName(): Node configuration exists!");
                luceneDocConfigFileName = ((Element)node).getAttribute("src");
            }
        }
        if (luceneDocConfigFileName == null) {
            log.error(".getLuceneDocConfigFileName(): ERROR: Lucene Document Configuration is not specified (indexer/configuration/@src)");
        }
        log.debug(".getLuceneDocConfigFileName(): Lucene Document Configuration: " + luceneDocConfigFileName);
        return luceneDocConfigFileName;
    }

    /**
     *
     */
    private String getExtensions(Element indexer) {
        String extensions = null;

        org.w3c.dom.NodeList nl = indexer.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node node = nl.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && node.getNodeName().equals("extensions")) {
                log.debug("Node extensions exists!");
                extensions = ((Element)node).getAttribute("src");
            }
        }
        if (extensions == null) {
            log.error("Extensions have not been specified (indexer/extensions/@src)");
        }
        log.debug("Extensions: " + extensions);
        return extensions;
    }
}
