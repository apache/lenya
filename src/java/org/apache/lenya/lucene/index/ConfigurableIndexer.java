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

package org.apache.lenya.lucene.index;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A configurable indexer
 */
public class ConfigurableIndexer extends AbstractIndexer {
    private static final Logger _log = Logger.getLogger(ConfigurableIndexer.class);

    /**
     * Instantiate a Document Creator for creating Lucene Documents
     * @param _indexer <code>indexer</code> node
     * @param _configFileName The name of the configuration file
     * @return DocumentCreator The document creator
     * @throws IOException if an error occurs
     */
    public DocumentCreator createDocumentCreator(Element _indexer, String _configFileName) throws IOException {
            _log.debug(".createDocumentCreatort(): Element name: " + _indexer.getNodeName());

            // FIXME: concat these files ...
            String configurationFileName = new File(_configFileName).getParent() + File.separator + getLuceneDocConfigFileName(_indexer);
            File configurationFile = new File(configurationFileName);
            String stylesheet = getStylesheet(configurationFile);
            return new ConfigurableDocumentCreator(stylesheet);
    }

    /**
     * <code>CONFIGURATION_CREATOR_STYLESHEET</code> Path to the configuration stylesheet
     */
    public static final String CONFIGURATION_CREATOR_STYLESHEET = "org/apache/lenya/lucene/index/configuration2xslt.xsl";

    /**
     * Converts the configuration file to an XSLT stylesheet and returns a reader that reads this stylesheet.
     * @param configurationFile The configuration file
     * @return The writer as string
     * @throws IOException if an error occurs
     */
    protected String getStylesheet(File configurationFile) throws IOException {
        Writer stylesheetWriter;
        try {
            _log.debug(".getStylesheet(): Configuration file: " + configurationFile.getAbsolutePath());

            URL configurationCreatorURL = ConfigurableIndexer.class.getClassLoader().getResource(CONFIGURATION_CREATOR_STYLESHEET);
            File configurationStylesheetFile = new File(new URI(configurationCreatorURL.toString()));
            Document configurationDocument = DocumentHelper.readDocument(configurationFile);

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer configurationTransformer = tFactory.newTransformer(new StreamSource(configurationStylesheetFile));

            DOMSource source = new DOMSource(configurationDocument);
            stylesheetWriter = new StringWriter();
            configurationTransformer.transform(source, new StreamResult(stylesheetWriter));

            // Show meta stylesheet which has been created by configuration2xslt.xsl
            _log.debug(".getStylesheet(): Meta Stylesheet: " + stylesheetWriter.toString());
        } catch (final TransformerConfigurationException e) {
            log.error("Transformer configuration error " +e.toString());
            throw new IOException(e.toString());
        } catch (final URISyntaxException e) {
            log.error("URI syntax error " +e.toString());
            throw new IOException(e.toString());
        } catch (final ParserConfigurationException e) {
            log.error("Parser configuration error " +e.toString());
            throw new IOException(e.toString());
        } catch (final SAXException e) {
            log.error("SAX error " +e.toString());
            throw new IOException(e.toString());
        } catch (final IOException e) {
            log.error("IO error " +e.toString());
            throw new IOException(e.toString());
        } catch (final TransformerFactoryConfigurationError e) {
            log.error("Transformer factory error " +e.toString());
            throw new IOException(e.toString());
        } catch (final TransformerException e) {
            log.error("Transformer error " +e.toString());
            throw new IOException(e.toString());
        }

        return stylesheetWriter.toString();
    }

    /**
     * Returns the filter used to receive the indexable files.
     * @param _indexer The indexer
     * @param _configFileName The configuration file name
     * @return A file filter
     */
    public FileFilter getFilter(Element _indexer, String _configFileName) {
        if (extensionsExists(_indexer)) {
            String[] indexableExtensions = new String[1];
	    indexableExtensions[0] = getExtensions(_indexer);
            return new AbstractIndexer.DefaultIndexFilter(indexableExtensions);
        } else if (filterExists(_indexer)) {
            return getFilterFromConfiguration(_indexer);
        }

        return new AbstractIndexer.DefaultIndexFilter(); 
    }

    /**
     * Returns the filename of the Lucence configuration file
     * @param _indexer The indexer
     * @return The filename
     */
    private String getLuceneDocConfigFileName(Element _indexer) {
        String luceneDocConfigFileName = null;

        NodeList nl = _indexer.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("configuration")) {
                _log.debug(".getLuceneDocConfigFileName(): Node configuration exists!");
                luceneDocConfigFileName = ((Element)node).getAttribute("src");
            }
        }
        if (luceneDocConfigFileName == null) {
            _log.error(".getLuceneDocConfigFileName(): ERROR: Lucene Document Configuration is not specified (indexer/configuration/@src)");
        }
        _log.debug(".getLuceneDocConfigFileName(): Lucene Document Configuration: " + luceneDocConfigFileName);
        return luceneDocConfigFileName;
    }

    /**
     * Returns the configured extensions of this Indexer
     * @param _indexer The indexer
     * @return The extensions
     */
    private String getExtensions(Element _indexer) {
        String extensions = null;

        NodeList nl = _indexer.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("extensions")) {
                _log.debug("Node extensions exists!");
                extensions = ((Element)node).getAttribute("src");
            }
        }
        if (extensions == null) {
            _log.error("Extensions have not been specified (indexer/extensions/@src)");
        }
        _log.debug("Extensions: " + extensions);
        return extensions;
    }

    /**
     * Returns the file filter from the configuration of this indexer
     * @param _indexer The indexer
     * @return The file filter
     */
    private FileFilter getFilterFromConfiguration(Element _indexer) {
        String className = null;

        NodeList nl = _indexer.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("filter")) {
                _log.debug("Node filter exists!");
                className = ((Element)node).getAttribute("class");
            }
        }
        if (className == null) {
            _log.error("Class name has not been specified (indexer/filter/@class)");
            return null;
        }
        _log.debug("Class name: " + className);
        try {
            return (FileFilter)Class.forName(className).newInstance();
        } catch(Exception e) {
            _log.error("" + e);
        }
        return null;
    }

    /**
     * Check if node <extensions src="..."/> exists
     * @param _indexer The indexer to check
     * @return Whether extensions node exists
     */
    private boolean extensionsExists(Element _indexer) {
        NodeList nl = _indexer.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("extensions")) {
                _log.debug("Node <extensions src=\"...\"/> exist");
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a file filter exists
     * @param _indexer The indexer to check
     * @return Whether a file filter exists
     */
    private boolean filterExists(Element _indexer) {
        NodeList nl = _indexer.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("filter")) {
                _log.debug("Node filter exists!");
                return true;
            }
        }
        return false;
    }
}
