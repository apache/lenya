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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

import org.apache.excalibur.source.Source;

import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

import org.dom4j.io.SAXReader;

import java.io.File;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ParentChildCreatorAction extends AbstractComplementaryConfigurableAction
    implements Configurable {
    private String treeAuthoringPath = null;
    private String docsPath = null;
    private String doctypesPath = null;

    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);

        treeAuthoringPath = conf.getChild("tree-authoring").getAttribute("href");
        docsPath = conf.getChild("docs").getAttribute("href");
        doctypesPath = conf.getChild("doctypes").getAttribute("href");
        getLogger().debug("CONFIGURATION:\nAUTHORING PATH OF TREE=" + treeAuthoringPath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        Source input_source = resolver.resolveURI("");
        String sitemapParentPath = input_source.getURI();
        sitemapParentPath = sitemapParentPath.substring(5); // Remove "file:" protocol

        getLogger().debug(".act(): PARENT PATH OF SITEMAP: " + sitemapParentPath);

        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        // Get parameters
        String parentid = request.getParameter("parentid");
        if (parentid == null) {
            getLogger().warn("No parentid parameter defined! It might be necessary to specify a parentid request parameter.");
        }
        String childid = request.getParameter("childid");
        if (childid == null) {
            getLogger().error("No childid parameter defined! Please specify childid as request parameter.");
            throw new Exception("No childname defined!");
        }
        String childname = request.getParameter("childname");
        if (childname == null) {
            getLogger().error("No childname defined! Please specify childname as request parameter which is being used as label within a sitetree or topic map.");
            throw new Exception("No childname defined!");
        }
        String childtype = request.getParameter("childtype");
        if (childtype == null) {
            getLogger().error("No childtype defined! Please specify childtype as request parameter with value either \"branch\" or \"leaf\".");
            throw new Exception("No childname defined!");
        }
        short childType;

        if (childtype.equals("branch")) {
            childType = ParentChildCreatorInterface.BRANCH_NODE;
        } else if (childtype.equals("leaf")) {
            childType = ParentChildCreatorInterface.LEAF_NODE;
        } else {
            getLogger().error("No such child type: " + childtype);
            return null;
        }

        String doctype = request.getParameter("doctype");
        if (doctype == null) {
            getLogger().warn("No doctype defined! Please specify doctype as request parameter, which is being used to resolve the creator within doctypes.xconf. Otherwise the DefaultCreator class is being used (see below)!");
        }
        String language = request.getParameter("language");

        if (!validate(parentid, childid, childname, childtype, doctype)) {
            getLogger().error("Exception: Validation of parameters failed");

            return null;
        }

        // Get session
        Session session = request.getSession(true);

        if (session == null) {
            getLogger().error("No session object");

            return null;
        }

        // Get creator
        ParentChildCreatorInterface creator = null;
        String absoluteDoctypesPath = sitemapParentPath + doctypesPath;
        Document doctypesDoc = new SAXReader().read("file:" + absoluteDoctypesPath +
                "doctypes.xconf");
        Attribute creator_src = (Attribute) doctypesDoc.selectSingleNode("/doctypes/doc[@type='" +
                doctype + "']/creator/@src");

        if (creator_src != null) {
            getLogger().info(".act(): Creator found for \"" + doctype + "\": " +
                creator_src.getName() + " " + creator_src.getPath() + " " + creator_src.getValue());

            // now get the constructor that accepts the configuration
            Class creatorClass = Class.forName(creator_src.getValue());
            creator = (ParentChildCreatorInterface) creatorClass.newInstance();
        } else {
            getLogger().warn("No creator found for \"" + doctype +
                "\". DefaultParentChildreator will be taken.");
            creator = new org.apache.lenya.cms.authoring.DefaultCreator();
        }

        getLogger().debug(".act(): Creator : " + creator.getClass().getName());

        // Init creator
        // "Read" the configuration from the DOM node
        DefaultConfigurationBuilder defaultConfigBuilder = new DefaultConfigurationBuilder();
        Configuration[] docTypeConfigs = defaultConfigBuilder.buildFromFile(absoluteDoctypesPath +
                "doctypes.xconf").getChildren();

        Configuration doctypeConf = null;

        for (int i = 0; i < docTypeConfigs.length; i++) {
            String typeName = docTypeConfigs[i].getAttribute("type");

            if (typeName.equals(doctype)) {
                doctypeConf = docTypeConfigs[i].getChild("creator", false);
            }
        }

        creator.init(doctypeConf);

        // Transaction should actually be started here!
        String treefilename = sitemapParentPath + treeAuthoringPath;
        getLogger().debug(".act(): Filename of tree: " + treefilename);

        if (!new File(treefilename).exists()) {
            getLogger().warn("No sitetree or topic map: " + treefilename);
        } else {
            if (!updateTree(childtype, childType, childid, childname, parentid, doctype, creator, treefilename)) return null;
        }
        // Transaction should actually be finished here!


        // Create actual document
        // grab all the parameters from session, request params and
        // sitemap params
        HashMap allParameters = new HashMap();
        String[] names = parameters.getNames();

        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String value = null;

            try {
                value = parameters.getParameter(name);
            } catch (ParameterException pe) {
                value = null;
            }

            allParameters.put(name, value);
        }

        Enumeration requestParameters = request.getParameterNames();

        while (requestParameters.hasMoreElements()) {
            String requestParameterName = (String) requestParameters.nextElement();

            if (allParameters.containsKey(requestParameterName)) {
                // we do not allow name clashes
                throw new ProcessingException("Name clash in request parameter " +
                    "and sitemap parameter: " + requestParameterName);
            }

            allParameters.put(requestParameterName, request.getParameter(requestParameterName));
        }

        Enumeration sessionAttributeNames = session.getAttributeNames();

        while (sessionAttributeNames.hasMoreElements()) {
            String sessionAttributeName = (String) sessionAttributeNames.nextElement();

            if (allParameters.containsKey(sessionAttributeName)) {
                // we do not allow name clashes
                throw new ProcessingException("Name clash in session attribute " +
                    "and request parameter or sitemap parameter: " + sessionAttributeName);
            }

            allParameters.put(sessionAttributeName, session.getAttribute(sessionAttributeName));
        }

        try {
            creator.create(new File(absoluteDoctypesPath + "samples"),
                new File(sitemapParentPath + docsPath + parentid), childid, childType, childname, language,
                allParameters);
        } catch (Exception e) {
            getLogger().error("Creator threw exception: " + e);
        }

        // Redirect to referer
        String parent_uri = (String) session.getAttribute(
                "org.apache.lenya.cms.cocoon.acting.ParentChildCreatorAction.parent_uri");
        getLogger().info(".act(): Child added");

        HashMap actionMap = new HashMap();
        actionMap.put("parent_uri", parent_uri);
        session.removeAttribute(
            "org.apache.lenya.cms.cocoon.acting.ParentChildCreatorAction.parent_uri");

        return actionMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parentid DOCUMENT ME!
     * @param childid DOCUMENT ME!
     * @param childname DOCUMENT ME!
     * @param childtype DOCUMENT ME!
     * @param doctype DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean validate(String parentid, String childid, String childname, String childtype,
        String doctype) {
        getLogger().debug(".validate(): parentid=" + parentid + " ; childid=" + childid +
            " ; childname=" + childname + " ; childtype=" + childtype + " ; doctype=" + doctype);

        if ((childid.indexOf(" ") >= 0) || (childid.length() == 0)) {
            return false;
        }

        if (childname.length() == 0) {
            return false;
        }

        return true;
    }

    /**
     *
     */
    private boolean updateTree(String childtype, short childType, String childid, String childname, String parentid, String doctype, ParentChildCreatorInterface creator, String treefilename) throws Exception {
        Document doc = new SAXReader().read("file:" + treefilename);

        // Get parent element
        StringTokenizer st = new StringTokenizer(parentid, "/");
        String xpath_string = "/tree/branch"; // Trunk of tree

        while (st.hasMoreTokens()) {
            xpath_string = xpath_string + "/branch[@relURI='" + st.nextToken() + "']";
        }

        getLogger().debug("XPATH: " + xpath_string);

        XPath xpathSelector = DocumentHelper.createXPath(xpath_string);
        List nodes = xpathSelector.selectNodes(doc);

        if (nodes.isEmpty()) {
            getLogger().error(".act(): No nodes: " + xpath_string);
            getLogger().error(".act(): No child added!");

            return false;
        }

        Element parent_element = (Element) nodes.get(0);
        getLogger().debug("PARENT ELEMENT: " + parent_element.getPath());

        // Set child type: branch or leaf
        childType = creator.getChildType(childType);

        if (childType == ParentChildCreatorInterface.BRANCH_NODE) {
            childtype = "branch";
        } else {
            childtype = "leaf";
        }

        // Check if child already exists
        String newChildXPath = xpath_string + "/" + childtype;
        getLogger().debug("CHECK: " + newChildXPath);

        if (doc.selectSingleNode(newChildXPath + "[@relURI='" +
                    creator.generateTreeId(childid, childType) + "']") != null) {
            getLogger().error("Exception: XPath exists: " + newChildXPath + "[@relURI='" +
                creator.generateTreeId(childid, childType) + "']");
            getLogger().error("No child added");

            return false;
        }

        // Add node: branch or leaf
        parent_element.addElement(childtype)
                      .addAttribute("relURI", creator.generateTreeId(childid, childType))
                      .addAttribute("doctype", doctype).addAttribute("menuName",
            creator.getChildName(childname));
        getLogger().debug("Tree has been modified: " + doc.asXML());

        // Write new tree
        java.io.FileWriter fileWriter = new java.io.FileWriter(treefilename);
        doc.write(fileWriter);
        fileWriter.close();

        return true;
    }
}
