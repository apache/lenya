/*
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.cocoon.acting;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.xml.DocumentHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xmldb.common.xml.queries.XObject;
import org.xmldb.common.xml.queries.XPathQuery;
import org.xmldb.common.xml.queries.XPathQueryFactory;
import org.xmldb.common.xml.queries.XUpdateQuery;
import org.xmldb.xupdate.lexus.XUpdateQueryImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.lenya.xml.RelaxNG;
import org.apache.lenya.xml.XPath;

/**
 * @author Michael Wechner
 * @version $Id: HTMLFormSaveAction.java,v 1.29 2003/12/04 16:10:50 michi Exp $
 *
 * FIXME: org.apache.xpath.compiler.XPathParser seems to have problems when namespaces are not declared within the root element. Unfortunately the XSLTs (during Cocoon transformation) are moving the namespaces to the elements which use them! One hack might be to parse the tree for namespaces (Node.getNamespaceURI), collect them and add them to the document root element, before sending it through the org.apache.xpath.compiler.XPathParser (called by XPathAPI)
 *
 * FIXME: There seems to be another problem with default namespaces
 */
public class HTMLFormSaveAction extends AbstractConfigurableAction implements ThreadSafe {
    org.apache.log4j.Category log = org.apache.log4j.Category.getInstance(HTMLFormSaveAction.class);

    class XUpdateAttributes {
        public String xupdateAttrExpr = "";
        public String tagID = "";

        public XUpdateAttributes(String xupdateAttrExpr, String tagID) {
            this.xupdateAttrExpr = xupdateAttrExpr;
            this.tagID = tagID;
        }
    }

    /**
     * Describe <code>configure</code> method here.
     *
     * @param conf a <code>Configuration</code> value
     *
     * @exception ConfigurationException if an error occurs
     */
/*
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
    }
*/

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
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source, Parameters parameters) throws Exception {
        File sitemap = new File(new URL(resolver.resolveURI("").getURI()).getFile());
        File file = new File(sitemap.getAbsolutePath() + File.separator + parameters.getParameter("file"));
        File schema = new File(sitemap.getAbsolutePath() + File.separator + parameters.getParameter("schema"));
        File unnumberTagsXSL = new File(sitemap.getAbsolutePath() + File.separator + parameters.getParameter("unnumberTagsXSL"));


        Request request = ObjectModelHelper.getRequest(objectModel);



        if(request.getParameter("cancel") != null) {
            getLogger().warn(".act(): Editing has been canceled");
            // FIXME: document needs to be restored from original document
            return null;
        } else {
            if(file.isFile()) {
                getLogger().debug(".act(): Save modifications to " + file.getAbsolutePath());

                try {
                    //Document document = DocumentHelper.readDocument(file);

                    Document document = null;
                    DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
                    parserFactory.setValidating(false);
                    parserFactory.setNamespaceAware(true);
                    parserFactory.setIgnoringElementContentWhitespace(true);
                    DocumentBuilder builder = parserFactory.newDocumentBuilder();
                    document = builder.parse(file.getAbsolutePath());
                    System.setProperty("org.xmldb.common.xml.queries.XPathQueryFactory", "org.xmldb.common.xml.queries.xalan2.XPathQueryFactoryImpl");

                    XPathQuery xpath = XPathQueryFactory.newInstance().newXPathQuery();
                    XUpdateQuery xq = new XUpdateQueryImpl();

                    String editSelect = null;
                    Enumeration params = request.getParameterNames();
                    while (params.hasMoreElements()) {
                        String pname = (String) params.nextElement();
                        log.debug("Parameter: " + pname + " (" + request.getParameter(pname)  + ")");

                        if (editSelect == null && pname.indexOf("edit[") >= 0 && pname.endsWith("].x")) {
                            editSelect = pname.substring(5, pname.length() - 3);
                           log.debug("Edit: " + editSelect);
                        }

                        if (pname.indexOf("<xupdate:") == 0) {
                            String select = pname.substring(pname.indexOf("select") + 8);
                            select = select.substring(0, select.indexOf("\""));
                            log.debug(".act() Select Node: " + select);

                            // Check if node exists
                            xpath.setQString(select);
                            XObject result = xpath.execute(document);
                            NodeList selectionNodeList = result.nodeset();
                            if (selectionNodeList.getLength() == 0) {
                                log.warn(".act(): Node does not exist (might have been deleted during update): " + select);
                            } else {
                                String xupdateModifications = null;
                                if (pname.indexOf("xupdate:update") > 0) {
                                    log.debug(".act(): UPDATE Node: " + pname);
                                    if (pname.indexOf("<![CDATA[") > 0) {
                                        xupdateModifications = updateCDATA(request, pname);
                                    } else {
                                        xupdateModifications = update(request, pname, select, selectionNodeList);
                                    }
                                } else if (pname.indexOf("xupdate:append") > 0 && pname.endsWith(">.x")) {
                                // FIXME: Internet Explorer does not send the name without the coordinates if input type is equals image. Mozilla does.
                                //} else if (pname.indexOf("xupdate:append") > 0 && pname.endsWith(">")) {
                                    // no .x and .y from input type="image"
                                    xupdateModifications = append(pname.substring(0, pname.length()-2));
                                    //xupdateModifications = append(pname);
                                } else if (pname.indexOf("xupdate:insert-before") > 0 && pname.endsWith(">.x")) {
                                //} else if (pname.indexOf("xupdate:insert-before") > 0 && pname.endsWith(">")) {
                                    // no .x and .y from input type="image"
                                    xupdateModifications = insertBefore(pname.substring(0, pname.length()-2));
                                    //xupdateModifications = insertBefore(pname);
                                } else if (pname.indexOf("xupdate:insert-after") > 0 && pname.endsWith("/>")) {
                                    // select:option
                                    if (!request.getParameter(pname).equals("null")) {
                                        xupdateModifications = insertAfter(request.getParameter(pname));
                                    }
                                } else if (pname.indexOf("xupdate:insert-after") > 0 && pname.endsWith(">.x")) {
                                //} else if (pname.indexOf("xupdate:insert-after") > 0 && pname.endsWith(">")) {
                                    // no .x and .y from input type="image"
                                    xupdateModifications = insertAfter(pname.substring(0, pname.length()-2));
                                    //xupdateModifications = insertAfter(pname);
                                } else if (pname.indexOf("xupdate:remove") > 0 && pname.endsWith("/>.x")) {
                                //} else if (pname.indexOf("xupdate:remove") > 0 && pname.endsWith("/>")) {
                                    // no .x and .y from input type="image"
                                    xupdateModifications = remove(pname.substring(0, pname.length()-2));
                                    //xupdateModifications = remove(pname);
                                }

                                if (xupdateModifications != null) {
                                    log.debug(".act(): MODIFICATIONS: " + xupdateModifications);
                                    xq.setQString(xupdateModifications);
                                    xq.execute(document);
                                } else {
                                    log.debug(".act(): Parameter did not match any xupdate command: " + pname);
                                }
                            } // Check select
                    } // Check <xupdate:
                    } // while




/*
                    java.io.StringWriter writer = new java.io.StringWriter();
                    org.apache.xml.serialize.OutputFormat OutFormat = new org.apache.xml.serialize.OutputFormat("xml", "UTF-8", true);
                    org.apache.xml.serialize.XMLSerializer serializer = new org.apache.xml.serialize.XMLSerializer(writer, OutFormat);
                    serializer.asDOMSerializer().serialize((Document) document);
                    log.error(".act(): XUpdate Result: \n"+writer.toString());
*/


                    DocumentHelper.writeDocument(document, file);


		    if (schema.isFile()) {
			String message = validateDocument(schema, file, unnumberTagsXSL);
                        if (message != null) {
                            log.error("RELAX NG Validation failed: " + message);
                            HashMap hmap = new HashMap();
                            hmap.put("message", "RELAX NG Validation failed: " + message);
                            return hmap;
                        }
                    } else {
                        log.warn("No such schema: " + schema.getAbsolutePath());
                    }

                    if(request.getParameter("save") != null) {
                        getLogger().info(".act(): Save");
                        return null;
                    } else {
                        // Loop while editing
                        HashMap hmap = new HashMap();
                        if (editSelect != null) {
                            hmap.put("editSelect", editSelect);
                        }
                        return hmap;
                    }
                } catch (NullPointerException e) {
                    getLogger().error(".act(): NullPointerException", e);
                    HashMap hmap = new HashMap();
                    hmap.put("message", "NullPointerException");
                    return hmap;
                } catch (Exception e) {
                    getLogger().error(".act(): Exception: " + e.getMessage(), e);
                    HashMap hmap = new HashMap();
                    if (e.getMessage() != null) {
                        hmap.put("message", e.getMessage());
                    } else {
                        hmap.put("message", "No message (" + e.getClass().getName()  + ")");
                    }
                    return hmap;
                }
            } else {
                getLogger().error(".act(): No such file: " + file.getAbsolutePath());
                HashMap hmap = new HashMap();
                hmap.put("message", "No such file: " + file.getAbsolutePath());
                return hmap;
            }
        }
    }
    /**
     * Get attributes
     */
    private XUpdateAttributes getAttributes(Node node) {
        String xupdateString = "";
        String tagID = "";
        org.w3c.dom.NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0;i < attributes.getLength();i++) {
              org.w3c.dom.Attr attribute = (org.w3c.dom.Attr)attributes.item(i);
              log.debug(".getAttributes(): " + attribute.getName() + " " + attribute.getValue());
              if (!attribute.getName().equals("tagID")) {
                  String namespace = attribute.getNamespaceURI();
                  log.debug(".getAttributes(): Namespace: " + namespace);
                  String namespaceAttribute = "";
                  if (namespace != null) {
                      namespaceAttribute = " namespace=\"" + namespace + "\"";
                  }
                  xupdateString = xupdateString + "<xupdate:attribute name=\"" + attribute.getName()  + "\"" + namespaceAttribute  + ">" + attribute.getValue()  + "</xupdate:attribute>";
              } else {
                  xupdateString = xupdateString + "<xupdate:attribute name=\"tagID\">temp</xupdate:attribute>";
                  tagID = attribute.getValue();
              }
            }
        } else {
            xupdateString = "";
        }
        log.debug(".getAttributes(): " + xupdateString);

        return new XUpdateAttributes(xupdateString, tagID);
    }
 
    /**
     * xupdate:update
     */
    private String update(Request request, String pname, String select, NodeList selectionNodeList) {
        // FIXME: Lexus seems to have trouble with mixed content. As Workaround we insert-after new and remove original
        Node nodeToCopy = selectionNodeList.item(0);
        String namespace = nodeToCopy.getNamespaceURI();
        log.debug(".update(): Update Node (namespace): " + namespace);
        String namespaceAttribute = "";
        if (namespace != null) {
            namespaceAttribute = " namespace=\"" + namespace + "\"";
        }
        // WARNING: getAttributes adds the attribute tagID with value "temp"
        XUpdateAttributes xa = getAttributes(nodeToCopy);
        String xupdateInsertAfter = "<xupdate:insert-after select=\"" + select  + " \"><xupdate:element name=\"" + new XPath(select).getNameWithoutPredicates()  + "\"" + namespaceAttribute  + ">" + xa.xupdateAttrExpr + request.getParameter(pname)  + "</xupdate:element></xupdate:insert-after>";
        log.debug(".update(): Update Node (insert-after): " + xupdateInsertAfter);
        String xupdateRemove = "<xupdate:remove select=\"" + select  + " \"/>";
        log.debug(".update(): Update Node (remove): " + xupdateRemove);
        String xupdateUpdateAttribute = "<xupdate:update select=\"" + new XPath(select).removePredicates(select) + "[@tagID='temp']/@tagID"  + " \">" + xa.tagID  + "</xupdate:update>";
        log.debug(".update(): Update Node (update tagID attribute): " + xupdateUpdateAttribute);
        return "<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + xupdateInsertAfter + xupdateRemove + xupdateUpdateAttribute  + "</xupdate:modifications>";

/*
        String xupdateUpdate = pname + request.getParameter(pname) + "</xupdate:update>";
        return "<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + xupdateUpdate + "</xupdate:modifications>";
*/
    }
 
    /**
     * xupdate:update CDATA
     */
    private String updateCDATA(Request request, String pname) {
        String xupdateUpdate = pname + request.getParameter(pname) + "]]></xupdate:update>";
        return "<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + xupdateUpdate + "</xupdate:modifications>";
    }
 
    /**
     * xupdate:append
     */
    private String append(String pname) {
        log.debug(".append() APPEND Node: " + pname);
        return "<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname + "</xupdate:modifications>";
    }
 
    /**
     * xupdate:insert-before
     */
    private String insertBefore(String pname) {
        log.debug(".insertBefore() INSERT-BEFORE Node: " + pname);
        return "<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname + "</xupdate:modifications>";
    }
 
    /**
     * xupdate:insert-after
     */
    private String insertAfter(String pname) {
        log.debug(".insertAfter() INSERT-AFTER Node: " + pname);
        return "<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname + "</xupdate:modifications>";
    }
 
    /**
     * xupdate:remove
     */
    private String remove(String pname) {
        log.debug(".remove() REMOVE Node: " + pname);
        return "<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname + "</xupdate:modifications>";
    }
 
    /**
     * Validate document
     */
    private String validateDocument(File schema, File file, File unnumberTagsXSL) {
        try {
            // Remove tagIDs
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer(new StreamSource(unnumberTagsXSL));
            t.transform(new StreamSource(file), new StreamResult(new File(file.getAbsolutePath() + ".validate")));

            // Validate
            return RelaxNG.validate(schema, new File(file.getAbsolutePath() + ".validate"));
        } catch (Exception e) {
            log.error(e);
            return "" + e;
        }
    }
}
