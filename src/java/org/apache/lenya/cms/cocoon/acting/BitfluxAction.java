/*
$Id: BitfluxAction.java,v 1.16 2003/07/23 13:21:31 gregor Exp $
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

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ConfigurableComposerAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.http.HttpRequest;
import org.apache.cocoon.serialization.Serializer;
import org.apache.cocoon.util.IOUtils;
import org.apache.cocoon.util.PostInputStream;
import org.apache.cocoon.xml.dom.DOMStreamer;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.xml.dom.DOMParser;

import org.apache.lenya.cms.ac.Identity;
import org.apache.lenya.cms.rc.RevisionController;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;


/**
 * Interfaces with Bitflux editor: handles the requests and replies to them
 *
 * @author Michael Wechner
 * @version 2003.1.6
 */
public class BitfluxAction extends ConfigurableComposerAction {
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
        getLogger().debug("CONFIGURATION:\n" + "Relative XML Root Directory: " + xmlRoot + "\n" +
            "Relative XSL Root Directory: " + xslRoot + "\n" + "Relative XSD Root Directory: " +
            xsdRoot + "\n" + "Relative Temp Directory: " + tempRoot);

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
    public java.util.Map act(Redirector redirector, SourceResolver resolver, Map objectModel,
        String source, Parameters params)
        throws IOException, ComponentException, SAXException, ProcessingException {
        // Get relative filename to save
        String relativeFilename = null;

        if (params.isParameter("save-filename")) {
            relativeFilename = params.getParameter("save-filename", null);
            getLogger().debug(".act(): Filename: " + params.getParameter("save-filename", null));
        }

        // Get absolute path of sitemap directory
        Source input_source = resolver.resolveURI("");
        String sitemapPath = input_source.getURI();
        sitemapPath = sitemapPath.substring(5); // Remove "file:" protocol
        getLogger().debug("Absolute SITEMAP Directory: " + sitemapPath);
        getLogger().debug("Absolute XML Root Directory: " + sitemapPath + xmlRoot);
        getLogger().debug("Absolute XSL Root Directory: " + sitemapPath + xslRoot);
        getLogger().debug("Absolute XSD Root Directory: " + sitemapPath + xsdRoot);
        getLogger().debug("Absolute Temp Root Directory: " + sitemapPath + tempRoot);

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
        getLogger().debug("root element (should be 'request'): " + root.getTagName());

        String reqType = root.getAttribute("type");
        getLogger().debug("Request Type: " + reqType);

        // get the first child element for root element (should be "data") and its attributes ---> FixMe: Add error handling
        Element data = (Element) root.getFirstChild();
        getLogger().debug("first child element (should be 'data'): " + data.getTagName());

        String reqFile = data.getAttribute("id");
        String fileType = data.getAttribute("type");
        getLogger().debug("Requested File's Type: " + fileType);

        // close the input stream
        reqContent.close();

        // Define temp dir
        File tempFileDir = new File(sitemapPath + relRootDirs.get("temp") + "/" +
                relRootDirs.get(fileType));

        if (!(tempFileDir.exists())) {
            tempFileDir.mkdir();
        }

        // Define Filenames
        File tempFile = IOUtils.createFile(tempFileDir, relativeFilename);
        File permFile = new File(sitemapPath + relRootDirs.get(fileType) + "/" + relativeFilename);
        getLogger().debug("PERMANENT FILE: " + permFile.getAbsolutePath());
        getLogger().debug("TEMPORARY FILE: " + tempFile.getAbsolutePath());

        // make a temporary copy of the file to be edited
        if ("xml".equals(fileType) && "open".equals(reqType)) {
            FileUtil.copyFile(permFile, tempFile);
        }

        Map sitemapParams = new HashMap();

        // save to temporary file, if needed
        if ("save".equals(reqType) || "checkin".equals(reqType)) {
            //FIXME(): remove hard coding
            final OutputStream os = new FileOutputStream(tempFile);
            ComponentSelector selector = (ComponentSelector) super.manager.lookup(Serializer.ROLE +
                    "Selector");

            //FIXME: remove hardcoding stuff
            Serializer serializer = (Serializer) selector.select("xml");
            serializer.setOutputStream(os);
            serializer.startDocument();

            DOMStreamer domStreamer = new DOMStreamer(serializer);
            Element contentNode = (Element) data.getFirstChild();
            domStreamer.stream(contentNode);
            serializer.endDocument();
            getLogger().debug("NODE IS: \n" + contentNode.toString());
            selector.release(serializer);
            super.manager.release(selector);
            os.flush();
            os.close();
        }

        // save to permanent file, if needed
        if ("checkin".equals(reqType)) {
            getLogger().debug(".act(): Save to permanent file: " + permFile);

            RevisionController rc = new RevisionController(sitemapPath + rcmlDirectory,
                    sitemapPath + backupDirectory, sitemapPath);

            try {
                Session session = httpReq.getSession(false);

                if (session == null) {
                    throw new Exception("No session");
                }

                Identity identity = (Identity) session.getAttribute(
                        "org.apache.lenya.cms.ac.Identity");
                rc.reservedCheckIn(xmlRoot + "/" + relativeFilename, identity.getUsername(), true);

                FileUtil.copyFile(tempFile, permFile);
            } catch (Exception e) {
                getLogger().error(".act(): Exception during checkin of " + xmlRoot + "/" +
                    relativeFilename + " (" + e + ")");

                return null;
            }
        }

        return sitemapParams;
    }
}
