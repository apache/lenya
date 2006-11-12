/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.commons.lang.StringUtils;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.RelaxNG;
import org.apache.lenya.xml.XPath;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xmldb.common.xml.queries.XObject;
import org.xmldb.common.xml.queries.XPathQuery;
import org.xmldb.common.xml.queries.XPathQueryFactory;
import org.xmldb.common.xml.queries.XUpdateQuery;
import org.xmldb.xupdate.lexus.XUpdateQueryImpl;

/**
 * FIXME: org.apache.xpath.compiler.XPathParser seems to have problems when
 * namespaces are not declared within the root element. Unfortunately the XSLTs
 * (during Cocoon transformation) are moving the namespaces to the elements
 * which use them! One hack might be to parse the tree for namespaces
 * (Node.getNamespaceURI), collect them and add them to the document root
 * element, before sending it through the org.apache.xpath.compiler.XPathParser
 * (called by XPathAPI)
 * 
 * FIXME: There seems to be another problem with default namespaces
 * 
 * WARNING: Internet Explorer sends X and Y coordinates for image buttons. These
 * have to be treated differently. Mozilla does not send these coordinates.
 */
public class HTMLFormSaveAction extends AbstractConfigurableAction implements ThreadSafe {

    static class XUpdateAttributes {
        public String xupdateAttrExpr = "";
        public String tagID = "";

        public XUpdateAttributes(String xupdateAttrExpr, String tagID) {
            this.xupdateAttrExpr = xupdateAttrExpr;
            this.tagID = tagID;
        }
    }

    /**
     * Save data to temporary file
     * 
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     * 
     * @return a <code>Map</code> value
     * 
     * @exception Exception if an error occurs
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
            Parameters parameters) throws Exception {
        File sitemap = new File(new URL(resolver.resolveURI("").getURI()).getFile());
        File file = new File(sitemap.getAbsolutePath() + File.separator
                + parameters.getParameter("file"));
        File schema = new File(sitemap.getAbsolutePath() + File.separator
                + parameters.getParameter("schema"));
        File unnumberTagsXSL = new File(sitemap.getAbsolutePath() + File.separator
                + parameters.getParameter("unnumberTagsXSL"));
        File numberTagsXSL = new File(sitemap.getAbsolutePath() + File.separator
                + parameters.getParameter("numberTagsXSL"));

        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request.getParameter("cancel") != null) {
            getLogger().warn(".act(): Editing has been canceled");
            file.delete();
            return null;
        } else {
            if (file.isFile()) {
                getLogger().debug(".act(): Save modifications to " + file.getAbsolutePath());

                try {
                    Document document = null;
                    DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
                    parserFactory.setValidating(false);
                    parserFactory.setNamespaceAware(true);
                    parserFactory.setIgnoringElementContentWhitespace(true);
                    DocumentBuilder builder = parserFactory.newDocumentBuilder();
                    document = builder.parse(file.getAbsolutePath());
                    System.setProperty("org.xmldb.common.xml.queries.XPathQueryFactory",
                            "org.xmldb.common.xml.queries.xalan2.XPathQueryFactoryImpl");

                    XPathQuery xpath = XPathQueryFactory.newInstance().newXPathQuery();
                    XUpdateQuery xq = new XUpdateQueryImpl();

                    String editSelect = null;
                    String insertSelect = null;
                    Enumeration params = request.getParameterNames();
                    while (params.hasMoreElements()) {
                        String pname = (String) params.nextElement();
                        getLogger().debug("Parameter: " + pname + " ("
                                + request.getParameter(pname) + ")");

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
                                getLogger()
                                        .debug(".act(): Node does not exist (might have been deleted during update): "
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
                                        xupdateModifications = update(request,
                                                pname,
                                                select,
                                                selectionNodeList,
                                                true);
                                    }
                                } else if (pname.indexOf("xupdate:update") > 0) {
                                    getLogger().debug("UPDATE Node: " + pname);
                                    // CDATA updates need to be handled
                                    // seperately
                                    if (pname.indexOf("<![CDATA[") > 0) {
                                        xupdateModifications = updateCDATA(request, pname, false);
                                    } else {
                                        xupdateModifications = update(request,
                                                pname,
                                                select,
                                                selectionNodeList,
                                                false);
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
                                        insertSelect = pname.substring(31,pname.length() - 3);
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
                                        insertSelect = pname.substring(30,pname.length() - 3);
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
                                    insertSelect = pname.substring(24,pname.length() - 3);
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
                                    getLogger().info("Execute XUpdate Modifications: "
                                            + xupdateModifications);
                                    xq.setQString(xupdateModifications);
                                    xq.execute(document);
                                } else {
                                    getLogger()
                                            .debug("Parameter did not match any xupdate command: "
                                                    + pname);
                                }
                            }
                        }
                    }

                    //  Uncomment this for debugging
                    /*
                     * java.io.StringWriter writer = new java.io.StringWriter();
                     * org.apache.xml.serialize.OutputFormat OutFormat = new
                     * org.apache.xml.serialize.OutputFormat("xml", "UTF-8",
                     * true); org.apache.xml.serialize.XMLSerializer serializer =
                     * new org.apache.xml.serialize.XMLSerializer(writer,
                     * OutFormat);
                     * serializer.asDOMSerializer().serialize((Document)
                     * document); log.error(".act(): XUpdate Result:
                     * \n"+writer.toString());
                     */

                    // validate against relax ng after the updates
                    if (schema.isFile()) {
                        DocumentHelper.writeDocument(document, new File(file.getCanonicalPath()
                                + ".validate"));
                        String message = validateDocument(schema, new File(file.getCanonicalPath()
                                + ".validate"), unnumberTagsXSL);
                        if (message != null) {
                            getLogger().error("RELAX NG Validation failed: " + message);
                            HashMap hmap = new HashMap();
                            hmap.put("message", "RELAX NG Validation failed: " + message);
                            return hmap;
                        }
                    } else {
                        getLogger().warn("No such schema: " + schema.getAbsolutePath());
                    }

                    Document renumberedDocument = renumberDocument(document,
                            unnumberTagsXSL,
                            numberTagsXSL);
                    DocumentHelper.writeDocument(renumberedDocument, file);

                    // check to see if we save and exit
                    if (request.getParameter("save") != null) {
                        getLogger().info(".act(): Save");
                        return null;
                    } else {
                        /*
                         * We don't exit
                         */
                        HashMap hmap = new HashMap();
                        if (editSelect != null) {
                            hmap.put("editSelect", editSelect);
                        }
                        if (insertSelect != null) {
                            hmap.put("insertSelect", insertSelect);
                        }
                        
                        return hmap;
                    }
                } catch (NullPointerException e) {
                    getLogger().error("NullPointerException", e);
                    HashMap hmap = new HashMap();
                    hmap.put("message", "NullPointerException");
                    return hmap;
                } catch (Exception e) {
                    getLogger().error("Exception: " + e.getMessage(), e);
                    HashMap hmap = new HashMap();
                    if (e.getMessage() != null) {
                        hmap.put("message", e.getMessage());
                    } else {
                        hmap.put("message", "No message (" + e.getClass().getName() + ")");
                    }
                    return hmap;
                }
            } else {
                getLogger().error("No such file: " + file.getAbsolutePath());
                HashMap hmap = new HashMap();
                hmap.put("message", "No such file: " + file.getAbsolutePath());
                return hmap;
            }
        }
    }

    /**
     * Get attributes from original node
     * 
     * @param node Original node
     */
    private XUpdateAttributes getAttributes(Node node) {
        StringBuffer xUpdate = new StringBuffer();
        String tagID = "";
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr attribute = (Attr) attributes.item(i);
                getLogger().debug(".getAttributes(): " + attribute.getName() + " "
                        + attribute.getValue());
                if (!attribute.getName().equals("tagID")) {
                    String namespace = attribute.getNamespaceURI();
                    getLogger().debug(".getAttributes(): Namespace: " + namespace);
                    String namespaceAttribute = "";
                    if (namespace != null) {
                        namespaceAttribute = " namespace=\"" + namespace + "\"";
                    }
                    xUpdate.append("<xupdate:attribute name=\"")
                            .append(attribute.getName()).append("\"").append(namespaceAttribute).append(">")
                            .append(attribute.getValue()).append("</xupdate:attribute>");
                } else {
                    xUpdate.append("<xupdate:attribute name=\"tagID\">temp</xupdate:attribute>");
                    tagID = attribute.getValue();
                }
            }
        }
        getLogger().debug("Attributes: " + xUpdate);
        return new XUpdateAttributes(xUpdate.toString(), tagID);
    }

    /**
     * Get attributes from actual update
     * 
     * @param update The actual update
     */
    private XUpdateAttributes getAttributes(String update, String tagID) {
        getLogger().debug(update);

        StringBuffer xUpdate = new StringBuffer("<xupdate:attribute name=\"tagID\">temp</xupdate:attribute>");

        String[] attributes = update.substring(0, update.indexOf(">")).split(" ");
        for (int i = 1; i < attributes.length; i++) {
            // TODO: beware of white spaces
            int index = attributes[i].indexOf("=");
            if (index > 0) {
                String name = attributes[i].substring(0, index);
                String value = attributes[i].substring(index + 2, attributes[i].length() - 1);
                if (name.indexOf("xmlns") < 0) {
                    xUpdate.append("<xupdate:attribute name=\"").append(name).append("\">")
                            .append(value).append("</xupdate:attribute>");
                }
            }
        }
        getLogger().debug("Attributes: " + xUpdate);
        return new XUpdateAttributes(xUpdate.toString(), tagID);
    }

    /**
     * xupdate:update
     * 
     * @param parent If true then parent element is part of update and
     *            attributes need to be updated resp. added or deleted
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
             * And deal with mixed content here.. NOTE: Lexus has trouble with
             * mixed content. As Workaround we insert-after the new node, remove
             * the original node and replace the temporary tagID by the original
             * tagID.
             */
        } else {
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
            getLogger().debug(".update(): Update Node (update tagID attribute): "
                    + xupdateUpdateAttribute);

            return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                    + xupdateInsertAfter + xupdateRemove + xupdateUpdateAttribute
                    + "</xupdate:modifications>";
        }
    }

    /**
     * xupdate:update CDATA
     * 
     * @param parent if true then attributes of parent will also be updated
     */
    private String updateCDATA(Request request, String pname, boolean parent) {
        String xupdateUpdate = pname + request.getParameter(pname) + "]]></xupdate:update>";
        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
                + xupdateUpdate + "</xupdate:modifications>";
    }

    /**
     * xupdate:append
     */
    private String append(String pname) {
        getLogger().debug(".append() APPEND Node: " + pname);
        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname
                + "</xupdate:modifications>";
    }

    /**
     * xupdate:insert-before
     */
    private String insertBefore(String pname) {
        getLogger().debug(".insertBefore() INSERT-BEFORE Node: " + pname);
        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname
                + "</xupdate:modifications>";
    }

    /**
     * xupdate:insert-after
     */
    private String insertAfter(String pname) {
        getLogger().debug(".insertAfter() INSERT-AFTER Node: " + pname);
        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname
                + "</xupdate:modifications>";
    }

    /**
     * xupdate:remove
     */
    private String remove(String pname) {
        getLogger().debug(".remove() REMOVE Node: " + pname);
        return "<xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname
                + "</xupdate:modifications>";
    }

    /**
     * Validate document
     */
    private String validateDocument(File schema, File file, File unnumberTagsXSL) {
        try {
            // Remove tagIDs
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer(new StreamSource(unnumberTagsXSL));
            t.transform(new StreamSource(file), new StreamResult(new File(file.getAbsolutePath()
                    + ".unnumber")));

            // Validate
            return RelaxNG.validate(schema, new File(file.getAbsolutePath() + ".unnumber"));
        } catch (Exception e) {
            getLogger().error("Validating failed:", e);
            return "" + e;
        }
    }

    /**
     * Renumber document
     */
    private Document renumberDocument(Document doc, File unnumberTagsXSL, File numberTagsXSL) {

        try {
            DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
            parserFactory.setValidating(false);
            parserFactory.setNamespaceAware(true);
            parserFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = parserFactory.newDocumentBuilder();

            TransformerFactory tf = TransformerFactory.newInstance();

            // Remove tagIDs
            Transformer ut = tf.newTransformer(new StreamSource(unnumberTagsXSL));
            Document unnumberedDocument = builder.newDocument();
            ut.transform(new DOMSource(doc), new DOMResult(unnumberedDocument));

            // Add tagIDs
            Transformer nt = tf.newTransformer(new StreamSource(numberTagsXSL));
            Document renumberedDocument = builder.newDocument();
            nt.transform(new DOMSource(unnumberedDocument), new DOMResult(renumberedDocument));

            return renumberedDocument;
        } catch (Exception e) {
            getLogger().error("" + e);
        }

        return null;
    }

    /**
     * Remove parent element
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
     * Add namespaces to xupdate
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
