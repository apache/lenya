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
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.publication.DefaultResourcesManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * The class <code>UploadAction</code> implements an action that allows for asset and content
 * upload. An upload consists of a file upload plus optionally a file creation for the meta data of
 * the asset.
 * 
 * Also @see org.apache.lenya.cms.authoring.UploadHelper
 */
public class UploadAction extends ServiceableAction {

    private Document document;
    private PageEnvelope pageEnvelope;

    /**
     * <code>UPLOADASSET_PARAM_NAME</code> Asset data parameter
     */
    public static final String UPLOADASSET_PARAM_NAME = "properties.asset.data";
    /**
     * <code>UPLOADASSET_PARAM_PREFIX</code> Prefix for all parameters
     */
    public static final String UPLOADASSET_PARAM_PREFIX = "properties.asset.";
    /**
     * <code>UPLOADASSET_RETURN_FILESIZE</code> The file size
     */
    public static final String UPLOADASSET_RETURN_FILESIZE = "file-size";
    /**
     * <code>UPLOADASSET_RETURN_MIMETYPE</code> The mime type
     */
    public static final String UPLOADASSET_RETURN_MIMETYPE = "mime-type";
    /**
     * <code>CONTENT_PREFIX</code> The content prefix
     */
    public static final String CONTENT_PREFIX = "content";
    /**
     * <code>FILE_NAME_REGEXP</code> The regular expression for valid asset names
     */
    public static final String FILE_NAME_REGEXP = "[-a-zA-Z0-9_.]+";
    /**
     * <code>DUBLIN_CORE_PARAMETERS</code> optional parameters for meta data according to dublin core
     */
    public static final String[] DUBLIN_CORE_PARAMETERS = { "title", "creator", "subject",
            "description", "publisher", "contributor", "date", "type", "format", "identifier",
            "source", "language", "relation", "coverage", "rights" };

    /**
     * Retrieve the file from the request and store it in the corresponding directory, optionally
     * create a meta file and optionally insert an image tag in the requesting document.
     * 
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     * 
     * @return a <code>Map</code> containing the referer or null if the name of the file to be
     *         uploaded contains characters that are not allowed (@see FILE_NAME_REGEXP).
     * 
     * @exception Exception if an error occurs
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
            Parameters parameters) throws Exception {

        Map results = new HashMap();
        Request request = ObjectModelHelper.getRequest(objectModel);
        DocumentIdentityMap map = new DocumentIdentityMap(this.manager);
        this.pageEnvelope = PageEnvelopeFactory.getInstance().getPageEnvelope(map, objectModel);
        this.document = this.pageEnvelope.getDocument();

        File assetFile;

        logRequestParameters(request);

        // determine if the upload is an asset or a content upload
        String uploadType = request.getParameter("uploadtype");

        // make asset upload the default if it is not specified
        if (uploadType == null) {
            uploadType = "asset";
        }

        Map dublinCoreParams = getDublinCoreParameters(request);

        // upload the file to the uploadDir
        Part part = (Part) request.get(UPLOADASSET_PARAM_NAME);

        String fileName = part.getFileName();
        if (!fileName.matches(FILE_NAME_REGEXP) || FileUtil.getExtension(fileName).equals("")) {
            // the file name contains characters which mean trouble
            // and are therefore not allowed.
            getLogger().warn("The filename [" + fileName + "] is not valid for an asset.");
            return null;
        }
        // convert spaces in the file name to underscores
        fileName = fileName.replace(' ', '_');
        String mimeType = part.getMimeType();
        int fileSize = part.getSize();

        results.put(UPLOADASSET_RETURN_MIMETYPE, mimeType);
        results.put(UPLOADASSET_RETURN_FILESIZE, new Integer(fileSize));

        dublinCoreParams.put("format", mimeType);
        dublinCoreParams.put("extent", Integer.toString(fileSize));

        if (uploadType.equals("asset")) {
            ResourcesManager resourcesMgr = new DefaultResourcesManager(this.document);
            assetFile = new File(resourcesMgr.getPath(), fileName);

            if (!resourcesMgr.getPath().exists()) {
                resourcesMgr.getPath().mkdirs();
            }

            // create an extra file containing the meta description for
            // the asset.
            File metaDataFile = new File(resourcesMgr.getPath(), fileName + ".meta");
            createMetaData(metaDataFile, dublinCoreParams);

        }
        // must be a content upload then
        else {
            assetFile = new File(this.document.getFile().getParent(), fileName);
            getLogger().debug("assetFile: " + assetFile);
        }

        saveAsset(assetFile, part);

        return Collections.unmodifiableMap(results);
    }

    /**
     * Saves the asset to a file.
     * 
     * @param assetFile The asset file.
     * @param part The part of the multipart request.
     * @throws Exception if an error occurs.
     */
    protected void saveAsset(File assetFile, Part part) throws Exception {
        if (!assetFile.exists()) {
            boolean created = assetFile.createNewFile();
            if (!created) {
                throw new RuntimeException("The file [" + assetFile + "] could not be created.");
            }
        }

        byte[] buf = new byte[4096];
        FileOutputStream out = new FileOutputStream(assetFile);
        try {
            InputStream in = part.getInputStream();
            int read = in.read(buf);

            while (read > 0) {
                out.write(buf, 0, read);
                read = in.read(buf);
            }
        } finally {
            out.close();
        }
    }

    /**
     * Logs the request parameters.
     * @param request The request.
     */
    protected void logRequestParameters(Request request) {
        for (Enumeration pEnum = request.getParameterNames(); pEnum.hasMoreElements();) {
            String param = (String) pEnum.nextElement();
            getLogger().debug(
                    param + ": " + request.getParameter(param) + " [" + request.get(param) + "]");
        }
    }

    /**
     * Retrieves optional parameters for the meta file which contains dublin core information from
     * the request.
     * @param request The request.
     * @return A map.
     */
    protected Map getDublinCoreParameters(Request request) {
        HashMap dublinCoreParams = new HashMap();
		String		key;
		String		value;
		Map.Entry	entry;
		
        for (int i = 0; i < DUBLIN_CORE_PARAMETERS.length; i++) {
            String paramName = DUBLIN_CORE_PARAMETERS[i];
            String paramValue = request.getParameter(UPLOADASSET_PARAM_PREFIX + paramName);

            if (paramValue == null) {
                paramValue = "";
            }

            dublinCoreParams.put(paramName, paramValue);
        }
        
        Iterator iter = dublinCoreParams.entrySet().iterator();
        while (iter.hasNext()) {
			entry 	= (Map.Entry)iter.next();
			key 	= (String)entry.getKey();
			value 	= (String)entry.getValue();
            getLogger().debug(key + ": " + value);
        }
        
        return dublinCoreParams;
    }

    /**
     * Create the meta data file given the dublin core parameters.
     * 
     * @param metaDataFile the file where the meta data file is to be created
     * @param dublinCoreParams a <code>Map</code> containing the dublin core values
     * @throws TransformerConfigurationException if an error occurs.
     * @throws TransformerException if an error occurs.
     * @throws IOException if an error occurs
     * @throws ParserConfigurationException if an error occurs.
     */
    protected void createMetaData(File metaDataFile, Map dublinCoreParams)
            throws TransformerConfigurationException, TransformerException, IOException,
            ParserConfigurationException {
        Map.Entry entry;
		String		key;
		String		value;

        assert (metaDataFile.getParentFile().exists());

        NamespaceHelper helper = new NamespaceHelper("http://purl.org/dc/elements/1.1/", "dc",
                "metadata");

        Element root = helper.getDocument().getDocumentElement();

        Iterator iter = dublinCoreParams.entrySet().iterator();

        while (iter.hasNext()) {
			entry 	= (Map.Entry)iter.next();
			key 	= (String)entry.getKey();
			value 	= (String)entry.getValue();
            root.appendChild(helper.createElement(key, value));
        }

        DocumentHelper.writeDocument(helper.getDocument(), metaDataFile);
    }
}