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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.cocoon.util.PostInputStream;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.cocoon.xml.dom.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.*;
import java.util.*;
import java.util.Enumeration;
import java.net.URL;


/**
 * @author Michael Wechner
 * @version $Id: HTMLFormSaveAction.java,v 1.4 2003/08/12 17:07:50 michi Exp $
 */
public class HTMLFormSaveAction extends AbstractConfigurableAction implements ThreadSafe {

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

        if(request.getParameter("save") != null) {
            if(file.isFile()) {
                getLogger().error(".act(): Save modifications to " + file.getAbsolutePath());

                try {
                    Document document = DocumentHelper.readDocument(file);

                    Enumeration params = request.getParameterNames();
                    while (params.hasMoreElements()) {
                        String name = (String) params.nextElement();
                        getLogger().debug(".act(): Parameter: " + name + " (" + request.getParameter(name)  + ")");
                        if (name.indexOf("element.") == 0) {
                            String xpath = name.substring(8, name.indexOf("["));
                            String tagID = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
                            xpath = xpath + "[@tagID=\"" + tagID + "\"]";
                            getLogger().error(".act(): XPath: " + xpath);
                            setNodeValue(document, request.getParameter(name), xpath);
                        }
                    }
                    DocumentHelper.writeDocument(document, file);
                } catch (Exception e) {
                    getLogger().error(".act(): Exception: " + e.getMessage(), e);
                    return new HashMap();
                }
            } else {
                getLogger().error(".act(): No such file: " + file.getAbsolutePath());
                return new HashMap();
            }
        } else if(request.getParameter("insert") != null) {
            getLogger().error(".act(): Insert");
            Enumeration params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String name = (String) params.nextElement();
                getLogger().error(".act(): Parameter: " + name + " (" + request.getParameter(name)  + ")");
            }
        } else if(request.getParameter("delete") != null) {
            getLogger().error(".act(): Delete");
            Enumeration params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String name = (String) params.nextElement();
                getLogger().error(".act(): Parameter: " + name + " (" + request.getParameter(name)  + ")");
            }
        } else { // cancel
            getLogger().error(".act(): Cancel editing");
        }

        return null;
    }

    /**
     *
     */
    private void setNodeValue(Document document, String value, String xpath) throws Exception {
        Node node = DOMUtil.getSingleNode(document.getDocumentElement(), xpath);
        getLogger().error(".act(): value = " + DOMUtil.getValueOfNode(node));
        DOMUtil.setValueOfNode(node, value);
        getLogger().error(".act(): value = " + DOMUtil.getValueOfNode(node));
    }
}
