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

/* $Id: XopusHandlerAction.java,v 1.39 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ConfigurableServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.util.IOUtils;
import org.apache.cocoon.util.PostInputStream;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.xml.dom.DOMParser;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.xml.DOMParserFactory;
import org.apache.lenya.xml.DOMWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Interfaces with Xopus: handles the requests and replies to them
 */
public class XopusHandlerAction extends ConfigurableServiceableAction {
    private String xmlRoot = null;
    private String xslRoot = null;
    private String xsdRoot = null;
    private String tempRoot = null;
    private Map relRootDirs = new HashMap();
    private String rcmlDirectory = null;
    private String backupDirectory = null;

    /**
     * Gets the configuration from the sitemap
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
        xmlRoot = conf.getChild("xml").getAttribute("href");
        xslRoot = conf.getChild("xsl").getAttribute("href");
        xsdRoot = conf.getChild("xsd").getAttribute("href");
        tempRoot = conf.getChild("temp").getAttribute("href");
        getLogger().debug(
            ".configure(): \n"
                + "Relative XML Root Directory: "
                + xmlRoot
                + "\n"
                + "Relative XSL Root Directory: "
                + xslRoot
                + "\n"
                + "Relative XSD Root Directory: "
                + xsdRoot
                + "\n"
                + "Relative Temp Directory: "
                + tempRoot);

        // Encode File types and their root directories, relative to the sitemap directory
        relRootDirs.put("xml", xmlRoot);
        relRootDirs.put("xsl", xslRoot);
        relRootDirs.put("xsd", xsdRoot);
        relRootDirs.put("temp", tempRoot);

        // Revision Control Parameters
        rcmlDirectory = conf.getChild("rcmlDirectory").getAttribute("href");
        backupDirectory = conf.getChild("backupDirectory").getAttribute("href");
    }

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param source DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ComponentException DOCUMENT ME!
     * @throws SAXException DOCUMENT ME!
     * @throws ProcessingException DOCUMENT ME!
     */
    public java.util.Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters params)
        throws IOException, SAXException, ProcessingException, ServiceException {
        // Get absolute path of sitemap directory
        Source input_source = resolver.resolveURI("");
        String sitemapPath = input_source.getURI();
        sitemapPath = sitemapPath.substring(5); // Remove "file:" protocol
        getLogger().debug(".act(): Absolute Sitemap Directory: " + sitemapPath);
        getLogger().debug(".act(): Absolute XML Root Directory: " + sitemapPath + xmlRoot);
        getLogger().debug(".act(): Absolute XSL Root Directory: " + sitemapPath + xslRoot);
        getLogger().debug(".act(): Absolute XSD Root Directory: " + sitemapPath + xsdRoot);
        getLogger().debug(".act(): Absolute Temp Root Directory: " + sitemapPath + tempRoot);

        // Get request object
        HttpRequest httpReq = (HttpRequest) objectModel.get(ObjectModelHelper.REQUEST_OBJECT);

        if (httpReq == null) {
            getLogger().error("Could not get HTTP_REQUEST_OBJECT from objectModel");

            return null;
        }

        int length = httpReq.getContentLength();
        PostInputStream reqContent = new PostInputStream(httpReq.getInputStream(), length);

        // construct DOM document from the request contents
        DOMParser parser = (DOMParser) this.manager.lookup(DOMParser.ROLE);
        InputSource saxSource = new InputSource(reqContent);
        Document requestDoc = parser.parseDocument(saxSource);

        // get the root element (should be "request") and its attributes ---> FixMe: Add error handling
        Element root = requestDoc.getDocumentElement();
        getLogger().debug(".act(): Root element (should be 'request'): " + root.getTagName());

        String reqId = root.getAttribute("id");
        getLogger().debug(".act(): Request ID: " + reqId);

        String reqType = root.getAttribute("type");
        getLogger().debug(".act(): Request Type: " + reqType);

        // get the first child element for root element (should be "data") and its attributes ---> FixMe: Add error handling
        Element data = (Element) root.getFirstChild();
        getLogger().debug(".act(): first child element (should be 'data'): " + data.getTagName());

        String reqFile = data.getAttribute("id");
        getLogger().debug(".act(): Requested File: " + reqFile);

        String fileType = data.getAttribute("type");
        getLogger().debug(".act(): Requested File's Type: " + fileType);

        // close the input stream
        reqContent.close();

        // Define Files
        File tempFileDir =
            new File(sitemapPath + relRootDirs.get("temp") + "/" + relRootDirs.get(fileType));

        if (!(tempFileDir.exists())) {
            tempFileDir.mkdir();
        }

        File tempFile = IOUtils.createFile(tempFileDir, reqFile);
        File permFile = new File(sitemapPath + relRootDirs.get(fileType) + "/" + reqFile);

        if (!permFile.exists()) {
            getLogger().error(".act(): No such file: " + permFile.getAbsolutePath());
            getLogger().error(
                ".act(): No such file: "
                    + sitemapPath
                    + "::"
                    + relRootDirs.get(fileType)
                    + "::"
                    + reqFile);

            return null;
        }

        // make a temporary copy of the file to be edited
        if ("xml".equals(fileType) && "open".equals(reqType)) {
            FileUtil.copyFile(permFile, tempFile);
            getLogger().debug(".act(): PERMANENT FILE: " + permFile.getAbsolutePath());
            getLogger().debug(".act(): TEMPORARY FILE: " + tempFile.getAbsolutePath());
        }

        // set sitemap params for response routing
        Map sitemapParams = new HashMap();
        sitemapParams.put("reqId", reqId);
        sitemapParams.put("reqType", reqType);
        sitemapParams.put("reqFile", reqFile);
        sitemapParams.put("fileType", fileType);

        if ("xml".equals(fileType) && ("open".equals(reqType) || "save".equals(reqType))) {
            sitemapParams.put(
                "reqFilePath",
                (String) relRootDirs.get("temp")
                    + "/"
                    + (String) relRootDirs.get(fileType)
                    + "/"
                    + reqFile);
            getLogger().debug(
                ".act(): File to be edited (in temp dir): " + sitemapParams.get("reqFilePath"));
        } else {
            sitemapParams.put("reqFilePath", (String) relRootDirs.get(fileType) + "/" + reqFile);
        }

        // The xopus sitemap will return the XML
        if ("open".equals(reqType)) {
            return sitemapParams;
        }

        // save to temporary file, if needed
        if ("save".equals(reqType) || "checkin".equals(reqType)) {
            getLogger().debug(".act(): Write to temp file: " + tempFile);

            try {
                Element contentNode = (Element) data.getFirstChild();
                DOMParserFactory dpf = new DOMParserFactory();

                // Create a new document, where the actual content starts at the root element, which is the inner part of requestDoc
                Document contentDocument = dpf.getDocument();
                contentDocument.appendChild(
                    dpf.cloneNode(contentDocument, contentNode, true));
                new DOMWriter(new FileOutputStream(tempFile)).printWithoutFormatting(
                    contentDocument);
            } catch (Exception e) {
                getLogger().error(".act(): Exception during writing to temp file", e);
            }
        }

        // save to permanent file, if needed
        if ("checkin".equals(reqType)) {
            getLogger().debug(".act(): Save to permanent file: " + permFile);

            RevisionController rc =
                new RevisionController(
                    sitemapPath + rcmlDirectory,
                    sitemapPath + backupDirectory,
                    sitemapPath);

            try {
                Session session = httpReq.getSession(false);

                if (session == null) {
                    throw new Exception("No session");
                }

                Identity identity = (Identity) session.getAttribute(Identity.class.getName());
                org.apache.lenya.ac.Identity identityTwo =
                    (org.apache.lenya.ac.Identity) session.getAttribute(Identity.class.getName());
                String username = null;
                if (identity != null) {
                    User user = identity.getUser();
                    if (user != null) {
                        username = user.getId();
                    }
                } else if (identityTwo != null) {
                    username = identityTwo.getUser().getId();
                } else {
                    getLogger().error(".act(): No identity!");
                }
                getLogger().debug(".act(): Checkin: " + reqFile + "::" + username);
                rc.reservedCheckIn(xmlRoot + reqFile, username, true);
                FileUtil.copyFile(tempFile, permFile);
            } catch (Exception e) {
                getLogger().error(".act(): Exception during checkin of " + xmlRoot + reqFile, e);
                return null;
            }
        }

        return sitemapParams;
    }
}
