/*
$Id: ParentChildCreatorAction.java,v 1.34 2003/07/31 01:15:18 michi Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
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


/**
 * Describe class <code>ParentChildCreatorAction</code> here.
 *
 * @author Michael Wechner
 * @version 2002.02.27
 */
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
        String childid = request.getParameter("childid");
        String childname = request.getParameter("childname");
        String childtype = request.getParameter("childtype");
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
            getLogger().warn(".act(): No creator found for \"" + doctype +
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
            getLogger().error(".act(): No tree: " + treefilename);
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
            getLogger().error(".act(): Creator threw exception: " + e);
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
