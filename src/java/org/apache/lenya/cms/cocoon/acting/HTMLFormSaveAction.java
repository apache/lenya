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

package org.apache.lenya.cms.cocoon.acting;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.commons.lang.StringUtils;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.RelaxNG;
import org.apache.lenya.xml.XPath;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmldb.common.xml.queries.XObject;
import org.xmldb.common.xml.queries.XPathQuery;
import org.xmldb.common.xml.queries.XPathQueryConfigurationException;
import org.xmldb.common.xml.queries.XPathQueryFactory;
import org.xmldb.common.xml.queries.XUpdateQuery;
import org.xmldb.xupdate.lexus.XUpdateQueryImpl;

/**
 * FIXME: org.apache.xpath.compiler.XPathParser seems to have problems when namespaces are not
 * declared within the root element. Unfortunately the XSLTs (during Cocoon transformation) are
 * moving the namespaces to the elements which use them! One hack might be to parse the tree for
 * namespaces (Node.getNamespaceURI), collect them and add them to the document root element, before
 * sending it through the org.apache.xpath.compiler.XPathParser (called by XPathAPI)
 * FIXME: There seems to be another problem with default namespaces
 * WARNING: Internet Explorer sends X and Y coordinates for image buttons. These have to be treated
 * differently. Mozilla does not send these coordinates.
 */
public class HTMLFormSaveAction extends AbstractConfigurableAction implements ThreadSafe {

    class XUpdateAttributes {
        /**
         * <code>xupdateAttrExpr</code> The Xupdate expression
         */
        public String xupdateAttrExpr = "";
        /**
         * <code>tagID</code> The tag ID
         */
        public String tagID = "";

        /**
         * Set Xupdate attributes
         * @param _xupdateAttrExpr The xupdate expression
         * @param _tagID The tag id
         */
        public XUpdateAttributes(String _xupdateAttrExpr, String _tagID) {
            this.xupdateAttrExpr = _xupdateAttrExpr;
            this.tagID = _tagID;
        }
    }

    /**
     * Save data to temporary file
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     * @return a <code>Map</code> value
     * @exception Exception if an error occurs
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
            Parameters parameters) throws Exception {

        String xmlUri = parameters.getParameter("file");
        String schemaUri = parameters.getParameter("schema");
        String unnumberTagsXslUri = parameters.getParameter("unnumberTagsXSL");
        String numberTagsXslUri = parameters.getParameter("numberTagsXSL");

        Request request = ObjectModelHelper.getRequest(objectModel);

        Source xmlSource = null;
        Source schemaSource = null;
        Source unnumberTagsXslSource = null;
        Source numberTagsXslSource = null;

        xmlSource = resolver.resolveURI(xmlUri);
        schemaSource = resolver.resolveURI(schemaUri);
        unnumberTagsXslSource = resolver.resolveURI(unnumberTagsXslUri);
        numberTagsXslSource = resolver.resolveURI(numberTagsXslUri);

        if (!(xmlSource instanceof ModifiableSource)) {
            throw new ProcessingException("Source [" + xmlSource + "] is not writeable.");
        }

        ModifiableSource modifiableXmlSource = (ModifiableSource) xmlSource;

        if (request.getParameter("cancel") != null) {
            getLogger().warn(".act(): Editing has been canceled");
            modifiableXmlSource.delete();
            return null;
        }

        try {
            return save(resolver, request, xmlSource, schemaSource, unnumberTagsXslSource, 
                    numberTagsXslSource, modifiableXmlSource);
        } catch (final ProcessingException e) {
            getLogger().error("Exception", e);
            HashMap hmap = new HashMap();
            hmap.put("message", "Exception");
            return hmap;
        } catch (MalformedURLException e) {
            getLogger().error("Exception", e);
            HashMap hmap = new HashMap();
            hmap.put("message", "Exception");
            return hmap;
        } catch (TransformerConfigurationException e) {
            getLogger().error("Exception", e);
            HashMap hmap = new HashMap();
            hmap.put("message", "Exception");
            return hmap;
        } catch (FactoryConfigurationError e) {
            getLogger().error("Exception", e);
            HashMap hmap = new HashMap();
            hmap.put("message", "Exception");
            return hmap;
        } catch (ParserConfigurationException e) {
            getLogger().error("Exception", e);
            HashMap hmap = new HashMap();
            hmap.put("message", "Exception");
            return hmap;
        } catch (IOException e) {
            getLogger().error("Exception", e);
            HashMap hmap = new HashMap();
            hmap.put("message", "Exception");
            return hmap;
        } catch (SAXException e) {
            getLogger().error("Exception", e);
            HashMap hmap = new HashMap();
            hmap.put("message", "Exception");
            return hmap;
        } catch (XPathQueryConfigurationException e) {
            getLogger().error("Exception", e);
            HashMap hmap = new HashMap();
            hmap.put("message", "Exception");
            return hmap;
        } catch (TransformerException e) {
            getLogger().error("Exception", e);
            HashMap hmap = new HashMap();
            hmap.put("message", "Exception");
            return hmap;
        } catch (Exception e) {
            getLogger().error("Exception", e);
            HashMap hmap = new HashMap();
            hmap.put("message", "Exception");
            return hmap;
        } finally {
            if (xmlSource != null) {
                resolver.release(xmlSource);
            }
            if (schemaSource != null) {
                resolver.release(schemaSource);
            }
            if (unnumberTagsXslSource != null) {
                resolver.release(unnumberTagsXslSource);
            }
            if (numberTagsXslSource != null) {
                resolver.release(numberTagsXslSource);
            }
            }

    }

    /**
     * Save the Form
     * @param resolver
     * @param request
     * @param xmlSource
     * @param schemaSource
     * @param unnumberTagsXslSource
     * @param numberTagsXslSource
     * @param modifiableXmlSource
     * @return
     * @throws ProcessingException
     * @throws FactoryConfigurationError
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XPathQueryConfigurationException
     * @throws Exception
     * @throws MalformedURLException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    private Map save(SourceResolver resolver, Request request, Source xmlSource, Source schemaSource, 
            Source unnumberTagsXslSource, Source numberTagsXslSource, ModifiableSource modifiableXmlSource) 
    		throws ProcessingException, FactoryConfigurationError, ParserConfigurationException, 
    		IOException, SAXException, XPathQueryConfigurationException, Exception, 
    		MalformedURLException, TransformerConfigurationException, TransformerException {
        if (!xmlSource.exists()) {
            throw new ProcessingException("The source [" + xmlSource.getURI()
                    + "] does not exist.");
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Save modifications to [" + xmlSource.getURI() + "]");
        }

        Document document = null;
        DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
        parserFactory.setValidating(false);
        parserFactory.setNamespaceAware(true);
        parserFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = parserFactory.newDocumentBuilder();

        InputSource xmlInputSource = SourceUtil.getInputSource(xmlSource);
        document = builder.parse(xmlInputSource);
        System.setProperty("org.xmldb.common.xml.queries.XPathQueryFactory",
                "org.xmldb.common.xml.queries.xalan2.XPathQueryFactoryImpl");

        XPathQuery xpath = XPathQueryFactory.newInstance().newXPathQuery();
        XUpdateQuery xq = new XUpdateQueryImpl();

        String editSelect = null;
        Enumeration params = request.getParameterNames();
        editSelect = processElements(request, document, xpath, xq, editSelect, params);

        // validate against relax ng after the updates
        if (!schemaSource.exists()) {
            throw new ProcessingException("Schema [" + schemaSource.getURI()
                    + "] does not exist.");
        }

        Source validationSource = null;
        Source unnumberTagsSource = null;

        try {
            String validationUri = modifiableXmlSource.getURI() + ".validate";
            validationSource = resolver.resolveURI(validationUri);
            checkModifiability(validationSource);

            String unnumberTagsUri = modifiableXmlSource.getURI() + ".validate.unnumber";
            unnumberTagsSource = resolver.resolveURI(unnumberTagsUri);
            checkModifiability(unnumberTagsSource);

            writeDocument(document, (ModifiableSource) validationSource);

            String message = validateDocument(schemaSource, validationSource,
                    (ModifiableSource) unnumberTagsSource, unnumberTagsXslSource);

            if (message != null) {
                getLogger().error("RELAX NG Validation failed: " + message);
                HashMap hmap = new HashMap();
                hmap.put("message", "RELAX NG Validation failed: " + message);
                return hmap;
            }
        } finally {
            if (validationSource != null) {
                resolver.release(validationSource);
            }
            if (unnumberTagsSource != null) {
                resolver.release(unnumberTagsSource);
            }
        }

        Document renumberedDocument = renumberDocument(document, unnumberTagsXslSource,
                numberTagsXslSource);
        writeDocument(document, modifiableXmlSource);

        // check to see if we save and exit
        if (request.getParameter("save") != null) {
            getLogger().info(".act(): Save");
            return null;
        }
        /*
         * We don't exit
         */
        HashMap hmap = new HashMap();
        if (editSelect != null) {
            hmap.put("editSelect", editSelect);
        }
        return hmap;
    }

    /**
     * Process elements
     * @param request
     * @param document
     * @param xpath
     * @param xq
     * @param editSelect
     * @param params
     * @return
     * @throws Exception
     */
    private String processElements(Request request, Document document, XPathQuery xpath, XUpdateQuery xq, 
            String editSelect, Enumeration params) throws Exception {
        while (params.hasMoreElements()) {
            String pname = (String) params.nextElement();
            getLogger().debug(
                    "Parameter: " + pname + " (" + request.getParameter(pname) + ")");

            // Extract the xpath to edit
            if (editSelect == null && pname.indexOf("edit[") >= 0
                    && pname.endsWith("].x")) {
                editSelect = pname.substring(5, pname.length() - 3);
                getLogger().debug("Edit: " + editSelect);
            }

            // Make sure we are dealing with an xupdate statement,
            // else skip
            if (pname.indexOf("<xupdate:") == 0) {
                String select = pname.substring(pname.indexOf("select") + 8);
                select = select.substring(0, select.indexOf("\""));
                getLogger().debug(".act() Select Node: " + select);

                // Check if node exists
                xpath.setQString(select);
                XObject result = xpath.execute(document);
                NodeList selectionNodeList = result.nodeset();
                if (selectionNodeList.getLength() == 0) {
                    getLogger().debug(
                            ".act(): Node does not exist (might have been deleted during update): "
                                    + select);
                } else {
                    String xupdateModifications = null;
                    // now check for the different xupdate
                    // statements, and handle appropriately
                    if (pname.indexOf("xupdate:update-parent") > 0) {
                        getLogger().debug("UPDATE PARENT Node: " + pname);
                        // CDATA updates need to be handled
                        // seperately
                        if (pname.indexOf("<![CDATA[") > 0) {
                            xupdateModifications = updateCDATA(request, pname, true);
                        } else {
                            xupdateModifications = update(request, pname, select,
                                    selectionNodeList, true);
                        }
                    } else if (pname.indexOf("xupdate:update") > 0) {
                        getLogger().debug("UPDATE Node: " + pname);
                        // CDATA updates need to be handled
                        // seperately
                        if (pname.indexOf("<![CDATA[") > 0) {
                            xupdateModifications = updateCDATA(request, pname, false);
                        } else {
                            xupdateModifications = update(request, pname, select,
                                    selectionNodeList, false);
                        }
                    } else if (pname.indexOf("xupdate:append") > 0
                            && pname.endsWith(">.x")) {
                        xupdateModifications = append(pname.substring(0,
                                pname.length() - 2));
                        // insert-before: in case of select/option
                    } else if (pname.indexOf("xupdate:insert-before") > 0
                            && pname.endsWith("/>")) {
                        if (!request.getParameter(pname).equals("null")) {
                            xupdateModifications = insertBefore(request
                                    .getParameter(pname));
                        }
                        // insert-before: in case of image
                    } else if (pname.indexOf("xupdate:insert-before") > 0
                            && pname.endsWith(">.x")) {
                        xupdateModifications = insertBefore(pname.substring(0, pname
                                .length() - 2));
                        // insert-after: in case of select/option
                    } else if (pname.indexOf("xupdate:insert-after") > 0
                            && pname.endsWith("/>")) {
                        if (!request.getParameter(pname).equals("null")) {
                            xupdateModifications = insertAfter(request
                                    .getParameter(pname));
                        }
                        // insert-after: in case of image
                    } else if (pname.indexOf("xupdate:insert-after") > 0
                            && pname.endsWith(">.x")) {
                        xupdateModifications = insertAfter(pname.substring(0, pname
                                .length() - 2));
                    } else if (pname.indexOf("xupdate:remove") > 0
                            && pname.endsWith("/>.x")) {
                        xupdateModifications = remove(pname.substring(0,
                                pname.length() - 2));
                    } else if (pname.endsWith(">.y")) {
                        getLogger().debug("Don't handle this: " + pname);
                    } else {
                        getLogger().debug("Don't handle this either: " + pname);
                    }

                    // Get hidden namespaces
                    String namespaces = request.getParameter("namespaces");

                    // Add XML declaration
                    // NOTE: select/option is generating parameter
                    // which should be considered as null
                    if (xupdateModifications != null) {
                        xupdateModifications = "<?xml version=\"1.0\"?>"
                                + addHiddenNamespaces(namespaces, xupdateModifications);
                    }

                    // now run the assembled xupdate query
                    if (xupdateModifications != null) {
                        getLogger().info(
                                "Execute XUpdate Modifications: "
                                        + xupdateModifications);
                        xq.setQString(xupdateModifications);
                        xq.execute(document);
                    } else {
                        getLogger()
                                .debug(
                                        "Parameter did not match any xupdate command: "
                                                + pname);
                    }
                }
            }
        }
        return editSelect;
    }

    /**
     * Writes a document to a modifiable source.
     * @param document The document.
     * @param source The source.
     * @throws IOException if an error occurs.
     * @throws TransformerConfigurationException if an error occurs.
     * @throws TransformerException if an error occurs.
     * @throws ProcessingException if an error occurs.
     */
    protected void writeDocument(Document document, ModifiableSource source) throws IOException,
            TransformerConfigurationException, TransformerException, ProcessingException {
        OutputStream oStream = source.getOutputStream();
        Writer writer = new OutputStreamWriter(oStream);
        DocumentHelper.writeDocument(document, writer);
        if (oStream != null) {
            oStream.flush();
            try {
                oStream.close();
            } catch (Throwable t) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Exception closing output stream: ", t);
                }
                throw new ProcessingException("Could not write document: ", t);
            }
        }
        if (!source.exists()) {
            throw new ProcessingException("Could not write source [" + source.getURI() + "]");
        }
    }

    /**
     * Checks if a source is modifiable.
     * @param source The source.
     * @throws ProcessingException if the source is not modifiable.
     */
    protected void checkModifiability(Source source) throws ProcessingException {
        if (!(source instanceof ModifiableSource)) {
            throw new ProcessingException("Cannot write to source [" + source.getURI() + "]");
        }
    }

    /**
     * Get attributes from original node
     * @param node Original node
     * @return An XupdateAttributes class holding the attributes
     */
    private XUpdateAttributes getAttributes(Node node) {

        String xupdateString = "";
        String tagID = "";
        org.w3c.dom.NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                org.w3c.dom.Attr attribute = (org.w3c.dom.Attr) attributes.item(i);
                getLogger().debug(
                        ".getAttributes(): " + attribute.getName() + " " + attribute.getValue());
                if (!attribute.getName().equals("tagID")) {
                    String namespace = attribute.getNamespaceURI();
                    getLogger().debug(".getAttributes(): Namespace: " + namespace);
                    String namespaceAttribute = "";
                    if (namespace != null) {
                        namespaceAttribute = " namespace=\"" + namespace + "\"";
                    }
                    xupdateString = xupdateString + "<xupdate:attribute name=\""
                            + attribute.getName() + "\"" + namespaceAttribute + ">"
                            + attribute.getValue() + "</xupdate:attribute>";
                } else {
                    xupdateString = xupdateString
                            + "<xupdate:attribute name=\"tagID\">temp</xupdate:attribute>";
                    tagID = attribute.getValue();
                }
            }
        } else {
            xupdateString = "";
        }
        getLogger().debug("Attributes: " + xupdateString);

        return new XUpdateAttributes(xupdateString, tagID);
    }

    /**
     * Get attributes from actual update
     * @param update The actual update
     * @param tagID The tag id to get the updates for
     * @return An XupdateAttributes class holding the attributes
     */
    private XUpdateAttributes getAttributes(String update, String tagID) {
        getLogger().debug(update);

        String xupdateString = "<xupdate:attribute name=\"tagID\">temp</xupdate:attribute>";

        String[] attributes = update.substring(0, update.indexOf(">")).split(" ");
        for (int i = 1; i < attributes.length; i++) {
            // TODO: beware of white spaces
            int index = attributes[i].indexOf("=");
            if (index > 0) {
                String name = attributes[i].substring(0, index);
                String value = attributes[i].substring(index + 2, attributes[i].length() - 1);
                if (name.indexOf("xmlns") < 0) {
                    xupdateString = xupdateString + "<xupdate:attribute name=\"" + name + "\">"
                            + value + "</xupdate:attribute>";
                }
            }
        }

        getLogger().debug("Attributes: " + xupdateString);

        return new XUpdateAttributes(xupdateString, tagID);
    }

    /**
     * xupdate:update
     * @param request The request
     * @param pname Name of the parent element
     * @param select The attribute to update
     * @param selectionNodeList The nodes to update
     * @param parent If true then parent element is part of update and attributes need to be updated
     *            resp. added or deleted
     * @return the Xupdate statement
     */
    private String update(Request request, String pname, String select, NodeList selectionNodeList,
            boolean parent) {
        getLogger().debug("Update node: " + select);

        Node nodeToCopy = selectionNodeList.item(0);
        // deal with attribute values here..
        if (nodeToCopy.getNodeType() == Node.ATTRIBUTE_NODE) {
            getLogger().debug("Update attribute: " + select);

            String xupdateUpdate = pname + request.getParameter(pname) + "</xupdate:update>";
            return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                    + xupdateUpdate + "</xupdate:modifications>";
            /*
             * And deal with mixed content here.. NOTE: Lexus has trouble with mixed content. As
             * Workaround we insert-after the new node, remove the original node and replace the
             * temporary tagID by the original tagID.
             */
        } 

        getLogger().debug("Update element: " + select);

        String namespace = nodeToCopy.getNamespaceURI();
        String namespaceAttribute = "";
        if (namespace != null) {
            namespaceAttribute = " namespace=\"" + namespace + "\"";
        }
        // NOTE: getAttributes adds the attribute tagID with value "temp",
        // which will be replaced further down
        XUpdateAttributes xa = getAttributes(nodeToCopy);
        String xupdateInsertAfter = null;
        if (parent) {
            xa = getAttributes(request.getParameter(pname), xa.tagID);
            xupdateInsertAfter = "<xupdate:insert-after select=\"" + select
                    + " \"><xupdate:element name=\""
                    + new XPath(select).getNameWithoutPredicates() + "\"" + namespaceAttribute
                    + ">" + xa.xupdateAttrExpr + removeParent(request.getParameter(pname))
                    + "</xupdate:element></xupdate:insert-after>";
        } else {
            xupdateInsertAfter = "<xupdate:insert-after select=\"" + select
                    + " \"><xupdate:element name=\""
                    + new XPath(select).getNameWithoutPredicates() + "\"" + namespaceAttribute
                    + ">" + xa.xupdateAttrExpr + request.getParameter(pname)
                    + "</xupdate:element></xupdate:insert-after>";
        }
        getLogger().debug(".update(): Update Node (insert-after): " + xupdateInsertAfter);

        String xupdateRemove = "<xupdate:remove select=\"" + select + " \"/>";
        getLogger().debug(".update(): Update Node (remove): " + xupdateRemove);

        String xupdateUpdateAttribute = "<xupdate:update select=\""
                + new XPath(select).removePredicates(select) + "[@tagID='temp']/@tagID"
                + " \">" + xa.tagID + "</xupdate:update>";
        getLogger().debug(
                ".update(): Update Node (update tagID attribute): " + xupdateUpdateAttribute);

        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + xupdateInsertAfter + xupdateRemove + xupdateUpdateAttribute
                + "</xupdate:modifications>";
    }

    /**
     * xupdate:update CDATA
     * @param request The request
     * @param pname The name of the parent element
     * @param parent if true then attributes of parent will also be updated
     * @return The Xupdate expression
     */
    private String updateCDATA(Request request, String pname, boolean parent) {
        String xupdateUpdate = pname + request.getParameter(pname) + "]]></xupdate:update>";
        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + xupdateUpdate + "</xupdate:modifications>";
    }

    /**
     * xupdate:append
     * @param pname The node to append to
     * @return The Xupdate statement
     */
    private String append(String pname) {
        getLogger().debug(".append() APPEND Node: " + pname);
        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname
                + "</xupdate:modifications>";
    }

    /**
     * xupdate:insert-before
     * @param pname The node to insert before
     * @return The Xupdate statement
     */
    private String insertBefore(String pname) {
        getLogger().debug(".insertBefore() INSERT-BEFORE Node: " + pname);
        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname
                + "</xupdate:modifications>";
    }

    /**
     * xupdate:insert-after
     * @param pname The node to insert after
     * @return The Xupdate statement
     */
    private String insertAfter(String pname) {
        getLogger().debug(".insertAfter() INSERT-AFTER Node: " + pname);
        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname
                + "</xupdate:modifications>";
    }

    /**
     * xupdate:remove
     * @param pname The node to remove
     * @return The Xupdate statement
     */
    private String remove(String pname) {
        getLogger().debug(".remove() REMOVE Node: " + pname);
        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname
                + "</xupdate:modifications>";
    }

    /**
     * Validates a document.
     * @param schema The schema source.
     * @param xml The input XML source.
     * @param unnumberXml The source of the temporary unnumbered XML.
     * @param unnumberTagsXsl The source of the unnumber XSL stylesheet.
     * @return A string. FIXME: return codes?
     */
    private String validateDocument(Source schema, Source xml, ModifiableSource unnumberXml,
            Source unnumberTagsXsl) {

        try {
            StreamSource xmlSource = new StreamSource(xml.getInputStream());
            StreamSource unnumberTagsXslSource = new StreamSource(unnumberTagsXsl.getInputStream());
            
            OutputStream outputStream = unnumberXml.getOutputStream();
            StreamResult unnumberXmlResult = new StreamResult(outputStream);
            
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(unnumberTagsXslSource);
            transformer.transform(xmlSource, unnumberXmlResult);
            
            if (outputStream != null) {
                outputStream.flush();
                try {
                    outputStream.close();
                } catch (Throwable t) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Exception closing output stream: ", t);
                    }
                    throw new ProcessingException("Could not write document: ", t);
                }
            }

            InputSource schemaInputSource = SourceUtil.getInputSource(schema);
            InputSource unnumberXmlInputSource = SourceUtil.getInputSource(unnumberXml);

            return RelaxNG.validate(schemaInputSource, unnumberXmlInputSource);
        } catch (final SourceNotFoundException e) {
            getLogger().error("Validating failed:", e);
            return e.getMessage();
        } catch (final TransformerConfigurationException e) {
            getLogger().error("Validating failed:", e);
            return e.getMessage();
        } catch (final ProcessingException e) {
            getLogger().error("Validating failed:", e);
            return e.getMessage();
        } catch (final IOException e) {
            getLogger().error("Validating failed:", e);
            return e.getMessage();
        } catch (final TransformerFactoryConfigurationError e) {
            getLogger().error("Validating failed:", e);
            return e.getMessage();
        } catch (final TransformerException e) {
            getLogger().error("Validating failed:", e);
            return e.getMessage();
        }

    }

    /**
     * Renumber the tags within a document. Each tag gets a unique number used in Xupdate expressions.
     * @param doc The document to renumber
     * @param unnumberTagsXSL The XSL stylesheet to remove the tagID attribute
     * @param numberTagsXSL The XSL stylesheet to add the tagID attribute
     * @return The renumbered document
     */
    private Document renumberDocument(Document doc, Source unnumberTagsXSL, Source numberTagsXSL) {

        try {
            DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
            parserFactory.setValidating(false);
            parserFactory.setNamespaceAware(true);
            parserFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = parserFactory.newDocumentBuilder();

            TransformerFactory tf = TransformerFactory.newInstance();

            // Remove tagIDs
            Transformer ut = tf.newTransformer(new StreamSource(unnumberTagsXSL.getInputStream()));
            Document unnumberedDocument = builder.newDocument();
            ut.transform(new DOMSource(doc), new DOMResult(unnumberedDocument));

            // Add tagIDs
            Transformer nt = tf.newTransformer(new StreamSource(numberTagsXSL.getInputStream()));
            Document renumberedDocument = builder.newDocument();
            nt.transform(new DOMSource(unnumberedDocument), new DOMResult(renumberedDocument));

            return renumberedDocument;
        } catch (final SourceNotFoundException e) {
            getLogger().error("" + e);
        } catch (final TransformerConfigurationException e) {
            getLogger().error("" + e);
        } catch (final FactoryConfigurationError e) {
            getLogger().error("" + e);
        } catch (final ParserConfigurationException e) {
            getLogger().error("" + e);
        } catch (final TransformerFactoryConfigurationError e) {
            getLogger().error("" + e);
        } catch (final IOException e) {
            getLogger().error("" + e);
        } catch (final TransformerException e) {
            getLogger().error("" + e);
        }

        return null;
    }

    /**
     * Remove parent element
     * @param xmlSnippet The XML snippet to remove the parent from
     * @return The XML snippet with the parent removed
     */
    private String removeParent(String xmlSnippet) {
        String xmlSnippetWithoutParent = xmlSnippet;
        xmlSnippetWithoutParent = xmlSnippetWithoutParent.substring(xmlSnippetWithoutParent
                .indexOf(">") + 1);
        xmlSnippetWithoutParent = StringUtils.reverse(xmlSnippetWithoutParent);
        xmlSnippetWithoutParent = xmlSnippetWithoutParent.substring(xmlSnippetWithoutParent
                .indexOf("<") + 1);
        xmlSnippetWithoutParent = StringUtils.reverse(xmlSnippetWithoutParent);
        return xmlSnippetWithoutParent;
    }

    /**
     * Add namespaces to xupdate statement
     * @param namespaces The namespaces to add
     * @param xupdateModifications The Xupdate statement to add namespaces to
     * @return The Xupdate statement with the added namespaces
     */
    private String addHiddenNamespaces(String namespaces, String xupdateModifications) {
        getLogger().debug("Namespaces: " + namespaces);

        if (namespaces == null) {
            getLogger().debug("No additional namespaces");
            return xupdateModifications;
        }

        String[] namespace = namespaces.split(" ");
        String ns = "";
        for (int i = 0; i < namespace.length; i++) {
            if ((ns.indexOf(namespace[i]) < 0) && (xupdateModifications.indexOf(namespace[i]) < 0)) {
                ns = ns + " " + namespace[i];
            } else {
                getLogger().debug("Redundant namespace: " + namespace[i]);
            }
        }

        int endOfFirstNode = xupdateModifications.indexOf(">");
        return xupdateModifications.substring(0, endOfFirstNode) + " " + ns
                + xupdateModifications.substring(endOfFirstNode);
    }
}