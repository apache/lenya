/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.editors.forms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.RelaxNG;
import org.apache.lenya.xml.XPath;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmldb.common.xml.queries.XPathQueryConfigurationException;
import org.xmldb.common.xml.queries.XPathQueryFactory;
import org.xmldb.common.xml.queries.XUpdateQuery;
import org.xmldb.common.xml.queries.xalan2.XPathQueryFactoryImpl;
import org.xmldb.xupdate.lexus.XUpdateQueryImpl;

/**
 * Multiple forms editor usecase.
 * 
 * @version $Id$
 */
public class FormsEditor extends DocumentUsecase {

    private static final class XUpdateAttributes {
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
    
    protected static final String WORKFLOW_INVOKED = "private.workflowInvoked";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getObjectsToLock()
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {
        Transactionable[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!WorkflowUtil.canInvoke(this.manager, getLogger(), getSourceDocument(), getEvent())) {
            addErrorMessage("error-workflow-document", new String[] { getEvent(),
                    getSourceDocument().getId() });
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() throws UsecaseException {
        super.advance();

        String unnumberTagsXslUri = "context://lenya/usecases/edit/forms/unnumberTags.xsl";
        String numberTagsXslUri = "context://lenya/usecases/edit/forms/numberTags.xsl";

        ModifiableSource xmlSource = null;
        Source schemaSource = null;
        Source unnumberTagsXslSource = null;
        Source numberTagsXslSource = null;

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            xmlSource = (ModifiableSource) resolver.resolveURI(getSourceDocument().getSourceURI());

            ResourceType resourceType = getSourceDocument().getResourceType();
            String schemaUri = resourceType.getSchemaDefinitionSourceURI();
            schemaSource = resolver.resolveURI(schemaUri);

            unnumberTagsXslSource = resolver.resolveURI(unnumberTagsXslUri);
            numberTagsXslSource = resolver.resolveURI(numberTagsXslUri);

            if (getParameterAsString("cancel") != null) {
                getLogger().warn("Editing has been canceled");
                // modifiableXmlSource.delete();
                return;
            }

            save(resolver, xmlSource, schemaSource, unnumberTagsXslSource, numberTagsXslSource);
            
            if (!getParameterAsBoolean(WORKFLOW_INVOKED, false)) {
                WorkflowUtil.invoke(this.manager, getLogger(), getSourceDocument(), getEvent());
                setParameter(WORKFLOW_INVOKED, Boolean.valueOf(true));
            }
            
        } catch (final Exception e) {
            throw new UsecaseException(e);
        } finally {
            if (resolver != null) {
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
                this.manager.release(resolver);
            }
        }
    }

    /**
     * Save the Form
     * @param resolver
     * @param xmlSource
     * @param schemaSource
     * @param unnumberTagsXslSource
     * @param numberTagsXslSource
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
    private void save(SourceResolver resolver, ModifiableSource xmlSource, Source schemaSource,
            Source unnumberTagsXslSource, Source numberTagsXslSource) throws ProcessingException,
            FactoryConfigurationError, ParserConfigurationException, IOException, SAXException,
            XPathQueryConfigurationException, Exception, MalformedURLException,
            TransformerConfigurationException, TransformerException {
        if (!xmlSource.exists()) {
            throw new ProcessingException("The source [" + xmlSource.getURI() + "] does not exist.");
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Save modifications to [" + xmlSource.getURI() + "]");
        }

        DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
        parserFactory.setValidating(false);
        parserFactory.setNamespaceAware(true);
        parserFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = parserFactory.newDocumentBuilder();

        InputSource xmlInputSource = SourceUtil.getInputSource(xmlSource);
        Document document = builder.parse(xmlInputSource);
        System.setProperty(XPathQueryFactory.class.getName(), XPathQueryFactoryImpl.class.getName());

        XUpdateQuery xUpdateQuery = new XUpdateQueryImpl();

        String editSelect = processElements(document, xUpdateQuery);
        setParameter("editSelect", editSelect);

        // validate against relax ng after the updates
        if (!schemaSource.exists()) {
            throw new ProcessingException("Schema [" + schemaSource.getURI() + "] does not exist.");
        }

        Source validationSource = null;
        Source unnumberTagsSource = null;

        try {
            String validationUri = xmlSource.getURI() + ".validate";
            validationSource = resolver.resolveURI(validationUri);
            checkModifiability(validationSource);

            String unnumberTagsUri = xmlSource.getURI() + ".validate.unnumber";
            unnumberTagsSource = resolver.resolveURI(unnumberTagsUri);
            checkModifiability(unnumberTagsSource);

            String message = validateDocument(document, schemaSource, unnumberTagsXslSource);

            if (message != null) {
                throw new UsecaseException("RELAX NG Validation failed: " + message);
            }
        } finally {
            if (validationSource != null) {
                resolver.release(validationSource);
            }
            if (unnumberTagsSource != null) {
                resolver.release(unnumberTagsSource);
            }
        }

        Document renumberedDocument = renumberDocument(document,
                unnumberTagsXslSource,
                numberTagsXslSource);
        writeDocument(renumberedDocument, xmlSource);
    }

    /**
     * Process elements
     * @param document
     * @param xq
     * @return A string.
     * @throws Exception
     */
    private String processElements(Document document, XUpdateQuery xq) throws Exception {
        String editSelect = null;
        String[] paramNames = getParameterNames();
        for (int paramIndex = 0; paramIndex < paramNames.length; paramIndex++) {
            String pname = paramNames[paramIndex];
            getLogger().debug("Parameter: " + pname + " = " + getParameterAsString(pname));

            // Extract the xpath to edit
            if (pname.indexOf("edit[") >= 0) {
                if (pname.endsWith("].x")) {
                    editSelect = pname.substring(5, pname.length() - 3);
                    getLogger().debug("Edit: " + editSelect);
                }
                deleteParameter(pname);
            }

            // Make sure we are dealing with an xupdate statement,
            // else skip
            if (pname.startsWith("<xupdate:")) {
                String select = pname.substring(pname.indexOf("select") + 8);
                select = select.substring(0, select.indexOf("\""));
                getLogger().debug("Select Node: " + select);

                // Check if node exists
                PrefixResolver resolver = new FormPrefixResolver(document.getDocumentElement());
                XObject xObject = XPathAPI.eval(document.getDocumentElement(), select, resolver);
                NodeList nodes = xObject.nodelist();
                if (nodes.getLength() == 0) {
                    getLogger().debug(".act(): Node does not exist (might have been deleted during update): "
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
                            xupdateModifications = updateCDATA(pname, true);
                        } else {
                            xupdateModifications = update(pname, select, nodes.item(0), true);
                        }
                    } else if (pname.indexOf("xupdate:update") > 0) {
                        getLogger().debug("UPDATE Node: " + pname);
                        // CDATA updates need to be handled
                        // seperately
                        if (pname.indexOf("<![CDATA[") > 0) {
                            xupdateModifications = updateCDATA(pname, false);
                        } else {
                            xupdateModifications = update(pname, select, nodes.item(0), false);
                        }
                    } else if (pname.indexOf("xupdate:append") > 0 && pname.endsWith(">.x")) {
                        xupdateModifications = append(pname.substring(0, pname.length() - 2));
                        // insert-before: in case of select/option
                    } else if (pname.indexOf("xupdate:insert-before") > 0 && pname.endsWith("/>")) {
                        if (!getParameterAsString(pname).equals("null")) {
                            xupdateModifications = insertBefore(getParameterAsString(pname));
                        }
                        // insert-before: in case of image
                    } else if (pname.indexOf("xupdate:insert-before") > 0 && pname.endsWith(">.x")) {
                        xupdateModifications = insertBefore(pname.substring(0, pname.length() - 2));
                        // insert-after: in case of select/option
                    } else if (pname.indexOf("xupdate:insert-after") > 0 && pname.endsWith("/>")) {
                        if (!getParameterAsString(pname).equals("null")) {
                            xupdateModifications = insertAfter(getParameterAsString(pname));
                        }
                        // insert-after: in case of image
                    } else if (pname.indexOf("xupdate:insert-after") > 0 && pname.endsWith(">.x")) {
                        xupdateModifications = insertAfter(pname.substring(0, pname.length() - 2));
                    } else if (pname.indexOf("xupdate:remove") > 0 && pname.endsWith("/>.x")) {
                        xupdateModifications = remove(pname.substring(0, pname.length() - 2));
                    } else if (pname.endsWith(">.y")) {
                        getLogger().debug("Don't handle this: " + pname);
                    } else {
                        getLogger().debug("Don't handle this either: " + pname);
                    }

                    // Get hidden namespaces
                    String namespaces = getParameterAsString("namespaces");

                    // Add XML declaration
                    // NOTE: select/option is generating parameter
                    // which should be considered as null
                    if (xupdateModifications != null) {
                        xupdateModifications = "<?xml version=\"1.0\"?>"
                                + addHiddenNamespaces(namespaces, xupdateModifications);
                    }

                    // now run the assembled xupdate query
                    if (xupdateModifications != null) {
                        getLogger().info("Execute XUpdate Modifications: " + xupdateModifications);
                        xq.setQString(xupdateModifications);
                        xq.execute(document);
                    } else {
                        getLogger().debug("Parameter did not match any xupdate command: " + pname);
                    }
                }
                deleteParameter(pname);
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

        StringBuffer buf = new StringBuffer();
        String xupdateString = "";
        String tagID = "";
        org.w3c.dom.NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                org.w3c.dom.Attr attribute = (org.w3c.dom.Attr) attributes.item(i);
                getLogger().debug(".getAttributes(): " + attribute.getName() + " "
                        + attribute.getValue());
                if (!attribute.getName().equals("tagID")) {
                    String namespace = attribute.getNamespaceURI();
                    getLogger().debug(".getAttributes(): Namespace: " + namespace);
                    String namespaceAttribute = "";
                    if (namespace != null) {
                        namespaceAttribute = " namespace=\"" + namespace + "\"";
                    }
                    buf.append("<xupdate:attribute name=\"" + attribute.getName() + "\""
                            + namespaceAttribute + ">" + attribute.getValue()
                            + "</xupdate:attribute>");
                } else {
                    buf.append("<xupdate:attribute name=\"tagID\">temp</xupdate:attribute>");
                    tagID = attribute.getValue();
                }
            }
            xupdateString = buf.toString();
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
     * @param pname Name of the parent element
     * @param select The attribute to update
     * @param nodeToUpdate The node to update
     * @param parent If true then parent element is part of update and attributes need to be updated
     *            resp. added or deleted
     * @return the Xupdate statement
     */
    private String update(String pname, String select, Node nodeToUpdate, boolean parent) {
        getLogger().debug("Update node: " + select);

        // deal with attribute values here..
        if (nodeToUpdate.getNodeType() == Node.ATTRIBUTE_NODE) {
            getLogger().debug("Update attribute: " + select);

            String xupdateUpdate = pname + getParameterAsString(pname) + "</xupdate:update>";
            return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                    + xupdateUpdate + "</xupdate:modifications>";
            /*
             * And deal with mixed content here.. NOTE: Lexus has trouble with mixed content. As
             * Workaround we insert-after the new node, remove the original node and replace the
             * temporary tagID by the original tagID.
             */
        }

        getLogger().debug("Update element: " + select);

        String namespace = nodeToUpdate.getNamespaceURI();
        String namespaceAttribute = "";
        if (namespace != null) {
            namespaceAttribute = " namespace=\"" + namespace + "\"";
        }
        // NOTE: getAttributes adds the attribute tagID with value "temp",
        // which will be replaced further down
        XUpdateAttributes xa = getAttributes(nodeToUpdate);
        String xupdateInsertAfter = null;
        if (parent) {
            xa = getAttributes(getParameterAsString(pname), xa.tagID);
            xupdateInsertAfter = "<xupdate:insert-after select=\"" + select
                    + " \"><xupdate:element name=\"" + new XPath(select).getNameWithoutPredicates()
                    + "\"" + namespaceAttribute + ">" + xa.xupdateAttrExpr
                    + removeParent(getParameterAsString(pname))
                    + "</xupdate:element></xupdate:insert-after>";
        } else {
            xupdateInsertAfter = "<xupdate:insert-after select=\"" + select
                    + " \"><xupdate:element name=\"" + new XPath(select).getNameWithoutPredicates()
                    + "\"" + namespaceAttribute + ">" + xa.xupdateAttrExpr
                    + getParameterAsString(pname) + "</xupdate:element></xupdate:insert-after>";
        }
        getLogger().debug(".update(): Update Node (insert-after): " + xupdateInsertAfter);

        String xupdateRemove = "<xupdate:remove select=\"" + select + " \"/>";
        getLogger().debug(".update(): Update Node (remove): " + xupdateRemove);

        String xupdateUpdateAttribute = "<xupdate:update select=\""
                + new XPath(select).removePredicates(select) + "[@tagID='temp']/@tagID" + " \">"
                + xa.tagID + "</xupdate:update>";
        getLogger().debug(".update(): Update Node (update tagID attribute): "
                + xupdateUpdateAttribute);

        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + xupdateInsertAfter + xupdateRemove + xupdateUpdateAttribute
                + "</xupdate:modifications>";
    }

    /**
     * xupdate:update CDATA
     * @param pname The name of the parent element
     * @param parent if true then attributes of parent will also be updated
     * @return The Xupdate expression
     */
    private String updateCDATA(String pname, boolean parent) {
        String xupdateUpdate = pname + getParameterAsString(pname) + "]]></xupdate:update>";
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
     * @param document The document.
     * @param schema The schema source.
     * @param unnumberTagsXsl The source of the unnumber XSL stylesheet.
     * @return A string. FIXME: return codes?
     * @throws UsecaseException if an error occurs.
     */
    private String validateDocument(Document document, Source schema, Source unnumberTagsXsl)
            throws UsecaseException {

        try {
            javax.xml.transform.Source xmlSource = new DOMSource(document);
            javax.xml.transform.Source unnumberTagsXslSource = new StreamSource(unnumberTagsXsl.getInputStream());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            StreamResult unnumberXmlResult = new StreamResult(out);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(unnumberTagsXslSource);
            transformer.transform(xmlSource, unnumberXmlResult);

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            InputSource unnumberXmlInputSource = new InputSource(in);
            InputSource schemaInputSource = SourceUtil.getInputSource(schema);

            return RelaxNG.validate(schemaInputSource, unnumberXmlInputSource);

        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * Renumber the tags within a document. Each tag gets a unique number used in Xupdate
     * expressions.
     * @param doc The document to renumber
     * @param unnumberTagsXSL The XSL stylesheet to remove the tagID attribute
     * @param numberTagsXSL The XSL stylesheet to add the tagID attribute
     * @return The renumbered document
     * @throws UsecaseException
     */
    private Document renumberDocument(Document doc, Source unnumberTagsXSL, Source numberTagsXSL)
            throws UsecaseException {

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
        } catch (final Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * Remove parent element
     * @param xmlSnippet The XML snippet to remove the parent from
     * @return The XML snippet with the parent removed
     */
    private String removeParent(String xmlSnippet) {
        String xmlSnippetWithoutParent = xmlSnippet;
        xmlSnippetWithoutParent = xmlSnippetWithoutParent.substring(xmlSnippetWithoutParent.indexOf(">") + 1);
        xmlSnippetWithoutParent = StringUtils.reverse(xmlSnippetWithoutParent);
        xmlSnippetWithoutParent = xmlSnippetWithoutParent.substring(xmlSnippetWithoutParent.indexOf("<") + 1);
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

    /**
     * Prefix resolver which uses the usecase parameters like
     * "namespace.xhtml=http://www.w3.org/1999/xhtml" to resolve prefixes.
     */
    public class FormPrefixResolver extends PrefixResolverDefault {

        /**
         * Ctor.
         * @param context The context node.
         */
        public FormPrefixResolver(Node context) {
            super(context);
        }

        /**
         * @see org.apache.xml.utils.PrefixResolver#getNamespaceForPrefix(java.lang.String,
         *      org.w3c.dom.Node)
         */
        public String getNamespaceForPrefix(String prefix, Node context) {
            String uri = super.getNamespaceForPrefix(prefix, context);
            if (uri == null) {
                uri = FormsEditor.this.getParameterAsString("namespace." + prefix);
            }
            return uri;
        }

    }

    protected String getEvent() {
        return "edit";
    }

}
