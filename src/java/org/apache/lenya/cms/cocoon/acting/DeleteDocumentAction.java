/*
 * $Id: DeleteDocumentAction.java,v 1.4 2003/02/13 19:17:35 andreas Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.util.Tokenizer;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

import org.dom4j.io.SAXReader;

import org.wyona.cms.authoring.AbstractParentChildCreator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.cocoon.environment.ObjectModelHelper;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2002.02.27
 */
public class DeleteDocumentAction extends AbstractComplementaryConfigurableAction
    implements Configurable {
    private String treeAuthoringPath = null;
    private String docsPath = null;

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
        org.apache.cocoon.environment.Source input_source = resolver.resolve("");
        String sitemapParentPath = input_source.getSystemId();
        sitemapParentPath = sitemapParentPath.substring(5); // Remove "file:" protocoll
        getLogger().debug("PARENT PATH OF SITEMAP: " + sitemapParentPath);

        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        if (3 > 1) {
            return null;
        }

        /*
            // Get parameters
            String parentid=request.getParameter("parentid");
            String childid=request.getParameter("childid");
            String childname=request.getParameter("childname");
            String childtype=request.getParameter("childtype");
            short childType;
            if(childtype.equals("branch")){
              childType=AbstractParentChildCreator.BRANCH_NODE;
              }
            else if(childtype.equals("leaf")){
              childType=AbstractParentChildCreator.LEAF_NODE;
              }
            else{
              getLogger().error("No such child type: "+childtype);
              return null;
              }
            String doctype=request.getParameter("doctype");
            if(!validate(parentid,childid,childname,childtype,doctype)){
              getLogger().error("Exception: Validation of parameters failed");
              return null;
              }

            // Get session
            Session session=request.getSession(true);
            if(session == null){
              getLogger().error("No session object");
              return null;
            }

            // Get creator
            AbstractParentChildCreator creator=null;
            String absoluteDoctypesPath=sitemapParentPath+doctypesPath;
            Document doctypesDoc=new SAXReader().read("file:"+absoluteDoctypesPath+"doctypes.xconf");
            Attribute creator_src=(Attribute)doctypesDoc.selectSingleNode("/doctypes/doc[@type='"+doctype+"']/creator/@src");
            if(creator_src != null){
              getLogger().error("CREATOR: "+creator_src.getName()+" "+creator_src.getPath()+" "+creator_src.getValue());
              creator=(AbstractParentChildCreator)Class.forName(creator_src.getValue()).newInstance();
              }
            else{
              getLogger().error("CREATOR: DefaultCreator");
              creator=(AbstractParentChildCreator)Class.forName("org.wyona.cms.authoring.DefaultParentChildCreator").newInstance();
              }

            // Transaction should actually be started here!

            // Read tree
            String treefilename=sitemapParentPath+treeAuthoringPath;
            getLogger().debug("FILENAME OF TREE: "+treefilename);
            if(!new File(treefilename).exists()){
              return null;
            }
            //InputStream in=intercept(new FileInputStream(treefilename));
            Document doc=new SAXReader().read("file:"+treefilename);

            // Get parent element
            StringTokenizer st=new StringTokenizer(parentid,"/");
            String xpath_string="/tree/branch"; // Trunk of tree
            while(st.hasMoreTokens()){
              xpath_string=xpath_string+"/branch[@relURI='"+st.nextToken()+"']";
              }
            getLogger().debug("XPATH: "+xpath_string);
            XPath xpathSelector=DocumentHelper.createXPath(xpath_string);
            List nodes=xpathSelector.selectNodes(doc);
            if(nodes.isEmpty()){
              getLogger().error("No child added");
              return null;
              }
            Element parent_element=(Element)nodes.get(0);
            getLogger().debug("PARENT ELEMENT: "+parent_element.getPath());

            // Set child type: branch or leaf
            childType=creator.getChildType(childType);
            if(childType==AbstractParentChildCreator.BRANCH_NODE){
              childtype="branch";
              }
            else{
              childtype="leaf";
              }

            // Check if child already exists
            String newChildXPath=xpath_string+"/"+childtype;
            getLogger().debug("CHECK: "+newChildXPath);
            if(doc.selectSingleNode(newChildXPath+"[@relURI='"+creator.generateTreeId(childid,childType)+"']") != null){
              getLogger().error("Exception: XPath exists: "+newChildXPath+"[@relURI='"+creator.generateTreeId(childid,childType)+"']");
              getLogger().error("No child added");
              return null;
              }

            // Add node: branch or leaf
            parent_element.addElement(childtype).addAttribute("relURI",creator.generateTreeId(childid,childType)).addAttribute("doctype",doctype).addAttribute("menuName",childname);
            getLogger().debug("Tree has been modified: "+doc.asXML());

            // Write new tree
            java.io.FileWriter fileWriter = new java.io.FileWriter(treefilename);
            doc.write(fileWriter);
            fileWriter.close();

            // Transaction should actually be finished here!

            // Create actual document
            creator.create(new File(absoluteDoctypesPath+"samples"),new File(sitemapParentPath+docsPath+parentid),childid,childType);

            // Redirect to referer
            if(7 > 1){
              String parent_uri=(String)session.getAttribute("org.wyona.cms.cocoon.acting.ParentChildCreatorAction.parent_uri");
              getLogger().error("Child added");
              HashMap actionMap=new HashMap();
              actionMap.put("parent_uri",parent_uri);
              session.removeAttribute("org.wyona.cms.cocoon.acting.ParentChildCreatorAction.parent_uri");
              return actionMap;
              }
            getLogger().error("No child added");
        */
        return null;
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
     * Write input stream to Logger (for debugging)
     *
     * @param in an <code>InputStream</code> value
     *
     * @return an <code>InputStream</code> value
     *
     * @exception Exception if an error occurs
     */
    private InputStream intercept(InputStream in) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes_read;
        ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();

        while ((bytes_read = in.read(buffer)) != -1) {
            bufferOut.write(buffer, 0, bytes_read);
        }

        getLogger().debug("Intercepted Input Stream:\n\n" + bufferOut.toString());

        return new ByteArrayInputStream(bufferOut.toByteArray());
    }

    /**
     * Write output stream to Logger (for debugging)
     *
     * @param out an <code>OutputStream</code> value
     *
     * @return an <code>OutputStream</code> value
     *
     * @exception Exception if an error occurs
     */
    private OutputStream intercept(OutputStream out) throws Exception {
        return null;
    }
}
