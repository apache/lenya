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
 */

/* $Id: DefaultCreatorAction.java,v 1.12 2004/03/02 16:41:43 michi Exp $  */


package org.apache.lenya.cms.cocoon.acting;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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
import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;
import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.log4j.Category;


import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;


/**
 * DOCUMENT ME!
 */
public class DefaultCreatorAction extends AbstractComplementaryConfigurableAction implements Configurable {
    Category log = Category.getInstance(DefaultCreatorAction.class);

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

        docsPath = conf.getChild("docs").getAttribute("href");
        doctypesPath = conf.getChild("doctypes").getAttribute("href");
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
        Publication publication = PublicationFactory.getPublication(objectModel);

        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        // Get parameters
        //String parentid = request.getParameter("parentid");
        String parentid = request.getParameter("properties.create.parent-id");
        log.debug("properties.create.parent-id = " + parentid);

        //String childid = request.getParameter("childid");
        String childid = request.getParameter("properties.create.child-id");
        log.debug("properties.create.child-id = " + childid);

        //String childname = request.getParameter("childname");
        String childname = request.getParameter("properties.create.child-name");
        log.debug("properties.create.child-name = " + childname);

        //String childtype = request.getParameter("childtype");
        String childtype = request.getParameter("properties.create.child-type");
        log.debug("properties.create.childtype = " + childtype);



        short childType;
        if (childtype.equals("branch")) {
            childType = ParentChildCreatorInterface.BRANCH_NODE;
        } else if (childtype.equals("leaf")) {
            childType = ParentChildCreatorInterface.LEAF_NODE;
        } else {
            log.error("No such child type: " + childtype);
            return null;
        }



        //String doctype = request.getParameter("doctype");
        String doctype = request.getParameter("properties.create.doctype");
        log.debug("poperties.create.doctype = " + doctype);

        //String language = request.getParameter("language");
        String language = request.getParameter("properties.create.language");
        log.debug("poperties.create.language = " + language);
		



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
        String absoluteDoctypesPath = publication.getDirectory() + File.separator + doctypesPath;
        Document doctypesDoc = new SAXReader().read("file:" + absoluteDoctypesPath +
                "doctypes.xconf");
        Attribute creator_src = (Attribute) doctypesDoc.selectSingleNode("/doctypes/doc[@type='" +
                doctype + "']/creator/@src");

        if (creator_src != null) {
            log.info("Creator found for \"" + doctype + "\": " + creator_src.getName() + " " + creator_src.getPath() + " " + creator_src.getValue());

            // now get the constructor that accepts the configuration
            Class creatorClass = Class.forName(creator_src.getValue());
            creator = (ParentChildCreatorInterface) creatorClass.newInstance();
        } else {
            log.warn("No creator found for \"" + doctype + "\". DefaultBranchCreator will be taken.");
            creator = new org.apache.lenya.cms.authoring.DefaultBranchCreator();
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

        // add a node to the tree
        DefaultSiteTree siteTree = publication.getSiteTree(Publication.AUTHORING_AREA);
        Label[] labels = new Label[1];
        labels[0] = new Label(childname, language);
        siteTree.addNode(parentid, creator.generateTreeId(childid, childType), labels);

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
                new File(publication.getDirectory(), docsPath + parentid), childid, childType,
                childname, language, allParameters);
        } catch (Exception e) {
            log.error("Creator threw exception: " + e);
            return null;
        }

        // commit (sort of)
        siteTree.save();

        HashMap actionMap = new HashMap();

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
}
