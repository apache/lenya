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

/* $Id: UploadAction.java,v 1.7 2004/03/15 14:58:28 michi Exp $  */

package org.apache.lenya.cms.cocoon.acting;

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

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * The class <code>UploadAction</code> implements an action that allows for
 * asset and content upload. An upload consists of a file upload plus optionally a file creation
 * for the meta data of the asset.
 *
 * Also see org.apache.lenya.cms.authoring.UploadHelper
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
            getLogger().debug(
                param
                    + ": "
                    + request.getParameter(param)
                    + " ["
                    + request.get(param)
                    + "]");
        }
        
        // determine if the upload is an asset or a content upload
        String uploadType = request.getParameter("uploadtype");

        // make asset upload the default if it is not specified
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
            getLogger().debug(
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

		// FIXME: write fileSize into dc meta data
        dublinCoreParams.put("format", mimeType);
        dublinCoreParams.put("extent", Integer.toString(fileSize));

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
			assetFile = new File(document.getFile().getParent(), part.getFileName());
            getLogger().debug("assetFile: "+assetFile);
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
