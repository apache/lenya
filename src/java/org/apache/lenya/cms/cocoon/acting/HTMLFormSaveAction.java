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

import org.xmldb.common.xml.queries.XPathQuery;
import org.xmldb.common.xml.queries.XPathQueryFactory;
import org.xmldb.common.xml.queries.XUpdateQuery;
import org.xmldb.xupdate.lexus.XUpdateQueryImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author Michael Wechner
 * @version $Id: HTMLFormSaveAction.java,v 1.18 2003/10/14 14:04:07 michi Exp $
 *
 * FIXME: org.apache.xpath.compiler.XPathParser seems to have problems when namespaces are not declared within the root element. Unfortunately the XSLTs (during Cocoon transformation) are moving the namespaces to the elements which use them! One hack might be to parse the tree for namespaces (Node.getNamespaceURI), collect them and add them to the document root element, before sending it through the org.apache.xpath.compiler.XPathParser (called by XPathAPI)
 *
 * FIXME: There seems to be another problem with default namespaces
 */
public class HTMLFormSaveAction extends AbstractConfigurableAction implements ThreadSafe {
    org.apache.log4j.Category log = org.apache.log4j.Category.getInstance(HTMLFormSaveAction.class);

    /**
     * Describe <code>configure</code> method here.
     *
     * @param conf a <code>Configuration</code> value
     *
     * @exception ConfigurationException if an error occurs
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
    }

    /**
     * Save blog entry to file
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

        Request request = ObjectModelHelper.getRequest(objectModel);

        if(request.getParameter("cancel") != null) {
            getLogger().info(".act(): Cancel editing");
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

                    Enumeration params = request.getParameterNames();
                    while (params.hasMoreElements()) {
                        String pname = (String) params.nextElement();
      
                        getLogger().debug(".act(): Parameter: " + pname + " (" + request.getParameter(pname)  + ")");

                        if (pname.indexOf("<xupdate:") == 0) {
                            String select = pname.substring(pname.indexOf("select") + 8);
                            select = select.substring(0, select.indexOf("\""));
                            log.debug(".act() Select Node: " + select);

                            // Check if node exists
                            xpath.setQString(select);
                            org.xmldb.common.xml.queries.XObject result = xpath.execute(document);
                            org.w3c.dom.NodeList selectionNodeList = result.nodeset();
                            if (selectionNodeList.getLength() == 0) {
                                log.warn(".act(): Node does not exist (might have been deleted during update): " + select);
                            } else {

                        if (pname.indexOf("xupdate:update") > 0) {
                            String xupdateUpdate = pname + request.getParameter(pname) + "</xupdate:update>";
                            if (pname.indexOf("<![CDATA[") > 0) {
                                xupdateUpdate = pname + request.getParameter(pname) + "]]></xupdate:update>";
                            }
                            log.error(".act() Update Node: " + xupdateUpdate);
                            xq.setQString("<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + xupdateUpdate + "</xupdate:modifications>");
                            xq.execute(document);
                        } else if (pname.indexOf("xupdate:append") > 0 && pname.endsWith(">")) { // no .x and .y from input type="image"
                            log.error(".act() Append Node: " + pname);
                            xq.setQString("<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname + "</xupdate:modifications>");
                            xq.execute(document);
                        } else if (pname.indexOf("xupdate:insert-before") > 0 && pname.endsWith(">")) { // no .x and .y from input type="image"
                            log.error(".act() Insert-Before Node: " + pname);
                            xq.setQString("<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname + "</xupdate:modifications>");
                            xq.execute(document);
                        } else if (pname.indexOf("xupdate:insert-after") > 0 && pname.endsWith(">")) { // no .x and .y from input type="image"
                            log.error(".act() Insert-After Node: " + pname);
                            xq.setQString("<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname + "</xupdate:modifications>");
                            xq.execute(document);
                        } else if (pname.indexOf("xupdate:remove") > 0 && pname.endsWith("/>")) { // no .x and .y from input type="image"
                            log.error(".act() Remove Node: " + pname);
                            xq.setQString("<?xml version=\"1.0\"?><xupdate:modifications xmlns:xupdate=\"http://www.xmldb.org/xupdate\">" + pname + "</xupdate:modifications>");
                            xq.execute(document);
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

                    if(request.getParameter("save") != null) {
                        getLogger().error(".act(): Save");
                        return null;
                    } else {
                        return new HashMap();
                    }
                } catch (NullPointerException e) {
                    getLogger().error(".act(): NullPointerException", e);
                    HashMap hmap = new HashMap();
                    hmap.put("message", "NullPointerException");
                    return hmap;
                } catch (Exception e) {
                    getLogger().error(".act(): Exception: " + e.getMessage(), e);
                    HashMap hmap = new HashMap();
                    hmap.put("message", e.getMessage());
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
}
