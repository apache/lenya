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

/* $Id: ServletHelper.java 741654 2009-02-06 17:55:28Z andreas $  */

package org.apache.lenya.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.xpath.XPathAPI;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Servlet utility class
 * 
 */
public final class ServletHelper {

    /**
     * Ctor.
     */
    private ServletHelper() {
        // do nothing
    }

    public static HttpServletRequest getRequest(){
    	HttpServletRequest request = null;
    	
    	RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    	if (requestAttributes instanceof ServletRequestAttributes) {
    		request = ((ServletRequestAttributes)requestAttributes).getRequest();
    	}
    	return request;
    }	
    /**
     * Return the webapp url (with a / at the beginning)
     */
    public static String getCurrentURI(){
    	String currentURI = "";
    	HttpServletRequest request = getRequest();
    	currentURI = request.getRequestURI(); // ==> /default/authoring/
    	return currentURI;
    }

    /**
     * Converts the request parameters to a map. If a key is mapped to multiple parameters, a string
     * array is used as the value.
     * @param request The request.
     * @return A map.
     * 
     * @TODO : write a test and see if very useful (code duplication ?)
     */
    public static Map getParameterMap() {
    	HttpServletRequest request = getRequest();
        Map requestParameters = new HashMap();
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String[] values = request.getParameterValues(key);
            Object value;
            if (values.length == 1) {
                value = values[0];
            } else {
                value = values;
            }
            requestParameters.put(key, value);
        }
        return requestParameters;
    }

    private static Boolean uploadEnabled = null;

    /**
     * Returns the value of enable-uploads in web.xml
     * @return true if enable upload is true or not set in web.xml, else false
     */
    public static synchronized boolean isUploadEnabled() throws SourceNotFoundException,
            ParserConfigurationException, SAXException, IOException, TransformerException {

        if (ServletHelper.uploadEnabled == null) {

            Node node;
            String webappUrl = "context://WEB-INF/web.xml";
            SourceResolver resolver = (SourceResolver) WebAppContextUtils
                    .getCurrentWebApplicationContext().getBean(SourceResolver.ROLE);
            Document document = SourceUtil.readDOM(webappUrl, resolver);
            Element root = document.getDocumentElement();
            node = XPathAPI.selectSingleNode(root,
                    "/web-app/servlet/init-param[param-name='enable-uploads']/param-value/text()");

            if (node == null) {
                ServletHelper.uploadEnabled = Boolean.FALSE;
            } else {
                boolean enabled = node.getNodeValue().equals("true");
                ServletHelper.uploadEnabled = Boolean.valueOf(enabled);
            }
        }
        return ServletHelper.uploadEnabled.booleanValue();
    }
}
