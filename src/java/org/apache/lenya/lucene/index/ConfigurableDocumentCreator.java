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

/* $Id: ConfigurableDocumentCreator.java,v 1.10 2004/03/01 16:18:15 gregor Exp $  */

package org.apache.lenya.lucene.index;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.lenya.lucene.parser.HTMLParser;
import org.apache.lenya.lucene.parser.HTMLParserFactory;
import org.apache.lenya.lucene.parser.StringCleaner;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Category;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class ConfigurableDocumentCreator extends AbstractDocumentCreator {
    Category log = Category.getInstance(ConfigurableDocumentCreator.class);
  
    public static final String LUCENE_NAMESPACE = "http://apache.org/cocoon/lenya/lucene/1.0";
    public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

    /**
     * Creates a new ConfigurableDocumentCreator object.
     *
     * @param stylesheet DOCUMENT ME!
     */
    public ConfigurableDocumentCreator(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    private String stylesheet;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStylesheet() {
        return stylesheet;
    }

    /**
     * Transform source document into lucene document and generate a Lucene Document instance
     *
     * @param file DOCUMENT ME!
     * @param htdocsDumpDir DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(File file, File htdocsDumpDir) throws Exception {
        log.debug(".getDocument() : indexing " + file.getAbsolutePath());
        try {

            org.w3c.dom.Document sourceDocument = null;
            DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
            parserFactory.setValidating(false);
            parserFactory.setNamespaceAware(true);
            parserFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder mybuilder = parserFactory.newDocumentBuilder();
            sourceDocument = mybuilder.parse(file.getAbsolutePath());


// FIXME: What is this good for: <?xml version="1.0"?><body>...</body>
/*
            NamespaceHelper documentHelper = new NamespaceHelper(XHTML_NAMESPACE, "xhtml", "html");
            org.w3c.dom.Document sourceDocument = documentHelper.getDocument();

            Element rootNode = sourceDocument.getDocumentElement();

            String bodyText = getBodyText(file);
            Element bodyElement = documentHelper.createElement("body", bodyText);
            rootNode.appendChild(bodyElement);
*/




            DOMSource documentSource = new DOMSource(sourceDocument);
            Writer documentWriter = new StringWriter();

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer documentTransformer = tFactory.newTransformer(new StreamSource(new StringReader(getStylesheet())));
            documentTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            documentTransformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

            String fileName = file.getName();

            if (fileName.endsWith(".pdf.txt")) {
                fileName = fileName.substring(0, fileName.lastIndexOf(".txt"));
            }

            documentTransformer.setParameter("filename", fileName);
            documentTransformer.transform(documentSource, new StreamResult(documentWriter));

            // DEBUG: debug lucene documents
            //dumpLuceneDocument(file, documentWriter);

            DocumentBuilder builder = DocumentHelper.createBuilder();
            org.w3c.dom.Document luceneDocument = builder.parse(new InputSource(new StringReader(documentWriter.toString())));

            NamespaceHelper helper = new NamespaceHelper(LUCENE_NAMESPACE, "luc", luceneDocument);
            Element root = luceneDocument.getDocumentElement();
            Element[] fieldElements = helper.getChildren(root, "field");

            Document document = super.getDocument(file, htdocsDumpDir);

            Class[] parameterTypes = { String.class, String.class };

            for (int i = 0; i < fieldElements.length; i++) {
                String name = fieldElements[i].getAttribute("name");
                String type = fieldElements[i].getAttribute("type");
                String text = getText(fieldElements[i]);

                Method method = Field.class.getMethod(type, parameterTypes);

                String[] args = { name, text };

                Field field = (Field) method.invoke(null, args);
                document.add(field);

            }

            return document;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Writes the lucene XML document to a file.
     */
    protected void dumpLuceneDocument(File file, Writer writer) throws IOException {
        log.debug(".dumpLuceneDocument(): Dump document: " + file.getAbsolutePath());

        File luceneDocumentFile = new File(file.getAbsolutePath() + ".xluc");
        luceneDocumentFile.createNewFile();

        FileWriter fileWriter = new FileWriter(luceneDocumentFile);
        fileWriter.write(writer.toString());
        fileWriter.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getText(Node node) {
        StringBuffer result = new StringBuffer();

        if (!node.hasChildNodes()) {
            return "";
        }

        NodeList list = node.getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            Node subnode = list.item(i);

            if (subnode.getNodeType() == Node.TEXT_NODE) {
                result.append(subnode.getNodeValue());
            } else if (subnode.getNodeType() == Node.CDATA_SECTION_NODE) {
                result.append(subnode.getNodeValue());
            } else if (subnode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
                // Recurse into the subtree for text
                // (and ignore comments)
                result.append(getText(subnode));
            }
        }

        return result.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public static String getBodyText(File file) throws Exception {
        HTMLParser parser = HTMLParserFactory.newInstance(file);
        parser.parse(file);

        Reader reader = parser.getReader();
        Writer writer = new StringWriter();

        int c;

        while ((c = reader.read()) != -1)
            writer.write(c);

        String content = writer.toString();
        reader.close();
        writer.close();

        content = StringCleaner.clean(content);

        return content;
    }
}
