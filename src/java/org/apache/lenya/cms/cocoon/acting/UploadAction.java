/*
$Id: UploadAction.java,v 1.1 2003/10/21 15:05:32 gregor Exp $
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
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.servlet.multipart.Part;

import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * The class <code>UploadAction</code> implements an action that allows for
 * asset and content upload. An upload consists of a file upload plus optionally a file creation
 * for the meta data of the asset.
 *
 * @author <a href="mailto:egli@apache.org">Christian Egli</a>
 */
public class UploadAction extends AbstractConfigurableAction {
	
	private Document document;
	private PageEnvelope pageEnvelope;

    public static final String UPLOADASSET_PARAM_NAME = "properties.asset.data";
    public static final String UPLOADASSET_PARAM_PREFIX = "properties.asset.";

    public static final String UPLOADASSET_RETURN_FILESIZE = "file-size";
    public static final String UPLOADASSET_RETURN_MIMETYPE = "mime-type";

	public static final String CONTENT_PREFIX = "content";

    public static String FILE_NAME_REGEXP = "[-a-zA-Z0-9_.]+";

    // optional parameters for meta data according to dublin core
    public static final String[] DUBLIN_CORE_PARAMETERS =
        {
            "title",
            "creator",
            "subject",
            "description",
            "publisher",
            "contributor",
            "date",
            "type",
            "format",
            "identifier",
            "source",
            "language",
            "relation",
            "coverage",
            "rights" };

    /**
     * Retrieve the file from the request and store it in the
     * corresponding directory, optionally create a meta file and
     * optionally insert an image tag in the requesting document.
     *
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     *
     * @return a <code>Map</code> containing the referer or null if the 
     * name of the file to be uploaded contains characters that are not allowed
     * (@see FILE_NAME_REGEXP).
     *
     * @exception Exception if an error occurs
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws Exception {

        HashMap results = new HashMap();

        Request request = ObjectModelHelper.getRequest(objectModel);

        pageEnvelope =
            PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        
        document = pageEnvelope.getDocument();

        byte[] buf = new byte[4096];
        File assetFile;

        for (Enumeration enum = request.getParameterNames();
            enum.hasMoreElements();
            ) {
            String param = (String)enum.nextElement();
            getLogger().info(
                param
                    + ": "
                    + request.getParameter(param)
                    + " ["
                    + request.get(param)
                    + "]");
        }
        
        // determine if the upload is an asset or a content upload
        String uploadType = request.getParameter("uploadtype");

		if (uploadType == null) {
			uploadType = "asset";
		}

        // optional parameters for the meta file which contains dublin
        // core information.
        HashMap dublinCoreParams = new HashMap();

        for (int i = 0; i < DUBLIN_CORE_PARAMETERS.length; i++) {
            String paramName = DUBLIN_CORE_PARAMETERS[i];
            String paramValue =
                request.getParameter(UPLOADASSET_PARAM_PREFIX + paramName);

            if (paramValue == null) {
                paramValue = "";
            }

            dublinCoreParams.put(paramName, paramValue);
        }

        Iterator iter = dublinCoreParams.keySet().iterator();

        while (iter.hasNext()) {
            String paramName = (String)iter.next();
            getLogger().info(
                paramName + ": " + dublinCoreParams.get(paramName));
        }

        // upload the file to the uploadDir
        Part part = (Part)request.get(UPLOADASSET_PARAM_NAME);

        String fileName = part.getFileName();
        if (!fileName.matches(FILE_NAME_REGEXP)
            || FileUtil.getExtension(fileName).equals("")) {
            // the file name contains characters which mean trouble 
            // and are therefore not allowed.
            return null;
        }
        String mimeType = part.getMimeType();
        int fileSize = part.getSize();

        results.put(UPLOADASSET_RETURN_MIMETYPE, mimeType);
        results.put(UPLOADASSET_RETURN_FILESIZE, new Integer(fileSize));

        dublinCoreParams.put("format", mimeType);
        dublinCoreParams.put("extent", Integer.toString(fileSize));

        // FIXME: write fileSize into dc meta data

        if (uploadType.equals("asset")) {
        	ResourcesManager resourcesMgr =
            	new ResourcesManager(document);
        	    assetFile = new File(resourcesMgr.getPath(), part.getFileName());

        	if (!resourcesMgr.getPath().exists()) {
            	resourcesMgr.getPath().mkdirs();
        	}

	        // create an extra file containing the meta description for
			// the asset.
			File metaDataFile =
				new File(resourcesMgr.getPath(), fileName + ".meta");
			createMetaData(metaDataFile, dublinCoreParams);

        } 
        // must be a content upload then
        else {
			assetFile = new File(document.getPublication().getDirectory()+getPathFromPublication().replace('/', File.separatorChar), part.getFileName());
            getLogger().info("assetFile: "+assetFile);
        }
        
		assetFile.createNewFile();
        
        FileOutputStream out = new FileOutputStream(assetFile);
        InputStream in = part.getInputStream();
        int read = in.read(buf);

        while (read > 0) {
            out.write(buf, 0, read);
            read = in.read(buf);
        }

        return Collections.unmodifiableMap(results);
    }

	/**
	 * Get the path to the resources.
	 * 
	 * @return the path to the resources
	 */
	private String getPathFromPublication() {
		return File.separatorChar + CONTENT_PREFIX + File.separatorChar + document.getArea() + document.getId() + File.separatorChar;
	}

    /**
     * Create the meta data file given the dublin core parameters.
     *
     * @param metaDataFile the file where the meta data file is to be created
     * @param dublinCoreParams a <code>Map</code> containing the dublin core values
     *
     * @exception IOException if an error occurs
     */
    protected void createMetaData(File metaDataFile, HashMap dublinCoreParams)
        throws
            TransformerConfigurationException,
            TransformerException,
            IOException,
            ParserConfigurationException {

        assert(metaDataFile.getParentFile().exists());

        NamespaceHelper helper =
            new NamespaceHelper(
                "http://purl.org/dc/elements/1.1/",
                "dc",
                "metadata");

		Element root = helper.getDocument().getDocumentElement();
		
        Iterator iter = dublinCoreParams.keySet().iterator();

        while (iter.hasNext()) {
            String tagName = (String)iter.next();
            String tagValue = (String)dublinCoreParams.get(tagName);
            root.appendChild(helper.createElement(tagName, tagValue));
        }

        DocumentHelper.writeDocument(helper.getDocument(), metaDataFile);
    }
}
