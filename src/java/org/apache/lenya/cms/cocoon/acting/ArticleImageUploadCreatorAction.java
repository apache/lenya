/*
 * $Id: ArticleImageUploadCreatorAction.java,v 1.27 2003/02/20 13:40:40 gregor Exp $
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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.acting.AbstractConfigurableAction;
import org.apache.cocoon.components.request.multipart.*;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Source;
import org.apache.cocoon.environment.SourceResolver;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;

import java.util.*;
import org.apache.cocoon.environment.ObjectModelHelper;


/**
 * The class <code>ArticleImageUploadCreatorAction</code> implements an action that allows for
 * image upload.
 *
 * @author <a href="mailto:christian.egli@wyona.org">Christian Egli</a>
 */
public class ArticleImageUploadCreatorAction extends AbstractConfigurableAction
    implements ThreadSafe {
    Properties default_properties = null;

    /**
     * The variable <code>recourcesRoot</code> is configured trough parameters to the action in the
     * sitemap. It defines the path to where images are uploaded.
     */
    protected String recourcesRoot = null;

    /**
     * The variable <code>metaRoot</code> is configured trough parameters to the action in the
     * sitemap. It defines where meta files which contain dublin core information for the uploaded
     * image are to be stored.
     */
    protected String metaRoot = null;

    /**
     * The variable <code>docsRoot</code> is configured trough parameters to the action in the
     * sitemap. It defines where the xml files are located. This is needed to find the path to the
     * original file that requested the upload where we need to insert a media tag.
     */
    protected String docsRoot = null;

    /**
     * The variable <code>insertImageBefore</code> is configured trough parameters to the action in
     * the sitemap. It defines whether the media tag is to be inserted before or after the xpath.
     * The values for the variables <code>insertBefore</code> and <code>insertAfter</code> come
     * from an optional request parameter which overwrites the configured behaviour.
     */
    protected boolean insertImageBefore = true;
    protected boolean insertBefore = false;
    protected boolean insertAfter = false;
    final String UPLOADFILE_PARAM_NAME = "uploadFile";
    final String IMAGEXPATH_PARAM_NAME = "xpath";
    final String DOCUMENTID_PARAM_NAME = "documentid";
    final String REFERER_PARAM_NAME = "referer";
    final String INSERTBEFORE_PARAM_NAME = "insertBefore";

    // optional parameters for meta data according to dublin core
    final String[] DUBLIN_CORE_PARAMETERS = {
        "title", "creator", "subject", "description", "publisher", "contributor", "date", "type",
        "format", "identifier", "source", "language", "relation", "coverage", "rights"
    };

    /**
     * Describe <code>configure</code> method here.
     *
     * @param conf a <code>Configuration</code> value
     *
     * @exception ConfigurationException if an error occurs
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);

        // The name of the uploaddir is specified as a parameter of
        // the action. The parameter is a child of the configuration.
        recourcesRoot = conf.getChild("resources-root").getAttribute("href");
        docsRoot = conf.getChild("docs-root").getAttribute("href");
        metaRoot = conf.getChild("meta-root").getAttribute("href");
        insertImageBefore = conf.getChild("insert-image-before").getAttributeAsBoolean("value", true);
        getLogger().debug("recourcesRoot:" + recourcesRoot);
        getLogger().debug("metaRoot:" + metaRoot);
        getLogger().debug("insertImageBefore:" + insertImageBefore);
    }

    /**
     * Describe <code>act</code> method here.
     *
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     *
     * @return a <code>Map</code> value
     *
     * @exception Exception if an error occurs
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
        Parameters parameters) throws Exception {
        HashMap results = new HashMap();
        Request request = ObjectModelHelper.getRequest(objectModel);
        Context context = ObjectModelHelper.getContext(objectModel);

        // find the absolute path (so we know where to put images and
        // meta data)
        Source inputSource = resolver.resolve("");
        String sitemapPath = inputSource.getSystemId();
        sitemapPath = sitemapPath.substring(5); // Remove "file:" protocol
        getLogger().debug("sitemapPath: " + sitemapPath);

        Properties properties = new Properties(default_properties);
        byte[] buf = new byte[4096];

        getLogger().debug("request: " + request);
        getLogger().debug("context: " + context);
        getLogger().debug("properties: " + properties);

        // pass the referer back to the sitemap so that it can do a
        // redirect back to it.
        String referer = request.getParameter(REFERER_PARAM_NAME);
        results.put(REFERER_PARAM_NAME, referer);
        getLogger().debug(REFERER_PARAM_NAME + ": " + referer);

        String imageXPath = request.getParameter(IMAGEXPATH_PARAM_NAME);
        String documentId = request.getParameter(DOCUMENTID_PARAM_NAME);
        String uploadFile = request.getParameter(UPLOADFILE_PARAM_NAME);
        String insert = request.getParameter(INSERTBEFORE_PARAM_NAME);

        getLogger().debug("imageXPath: " + imageXPath);
        getLogger().debug("documentId: " + documentId);
        getLogger().debug("uploadFile: " + uploadFile);
        getLogger().debug("insert: " + insert);

        // request parameters to indicate if the image should be
        // inserted before or after the given xpath, overwrites the
        // sitemap configuration
        insertBefore = false;
        insertAfter = false;

        if (insert != null) {
            if (insert.equals("true") || insert.equals("1")) {
                insertBefore = true;
            }

            if (insert.equals("false") || insert.equals("0")) {
                insertAfter = true;
            }
        }

        getLogger().debug("insertImageBefore: " + insertImageBefore);
        getLogger().debug("insertBefore: " + insertBefore);
        getLogger().debug("insertAfter: " + insertAfter);

        // optional parameters for the meta file which contains dublin
        // core information.
        HashMap dublinCoreParams = new HashMap();

        for (int i = 0; i < DUBLIN_CORE_PARAMETERS.length; i++) {
            String paramName = DUBLIN_CORE_PARAMETERS[i];
            String paramValue = request.getParameter(paramName);

            if (paramValue == null) {
                paramValue = "";
            }

            dublinCoreParams.put(paramName, paramValue);
        }

        Iterator iter = dublinCoreParams.keySet().iterator();

        while (iter.hasNext()) {
            String paramName = (String) iter.next();
            getLogger().debug(paramName + ": " + dublinCoreParams.get(paramName));
        }

        // if we can't find the uploadFile simply return, i.e. don't
        // do anything.
        if (uploadFile == null) {
            getLogger().debug("uploadFile is null");

            return null;
        }

        Object obj = request.get(UPLOADFILE_PARAM_NAME);
        getLogger().debug(obj.getClass().getName());

        // upload the file to the uploadDir
        if (obj instanceof FilePart) {
            getLogger().debug("Uploading file: " + ((FilePart) obj).getFileName());

            String identifier = (String) dublinCoreParams.get("identifier");
            String originalFileName = ((FilePart) obj).getFileName();
            String fileName = null;

            if (identifier.equals("")) {
                // if no identifier is specified we use the
                // originalFileName as the filename.
                fileName = originalFileName;
            } else {
                // due to some requirement we want the file extension
                // of the original file and want to add it to the
                // filename that the user provided through the
                // "identifier" parameter. 
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                fileName = identifier + extension;
            }

            getLogger().debug("fileName: " + fileName);

            // grab the mime type and add it to the dublin core meta
            // data as "format"
            // FIXME: put the proper mime type in here.
            // 	    String mimeType = ((FilePart)obj).getMimeType();
            String mimeType = "";

            if (mimeType != null) {
                dublinCoreParams.put("format", mimeType);
            }

            String imagePath = getImagePath(sitemapPath, recourcesRoot, documentId, fileName);

            getLogger().debug("sitemapPath: " + sitemapPath);
            getLogger().debug("imagePath: " + imagePath);

            File dir = (new File(imagePath)).getParentFile();

            if (!dir.exists()) {
                getLogger().info(".act(): Create directories: " + dir);
                dir.mkdirs();
            }

            if (obj instanceof FilePartFile) {
                ((FilePartFile) obj).getFile().renameTo(new File(imagePath));
            } else {
                FileOutputStream out = new FileOutputStream(imagePath);
                InputStream in = ((FilePart) obj).getInputStream();
                int read = in.read(buf);

                while (read > 0) {
                    out.write(buf, 0, read);
                    read = in.read(buf);
                }

                out.close();
            }

            // create an extra file containing the meta description for
            // the image.
            String metaDataFilePath = getMetaDataPath(sitemapPath, metaRoot, documentId,
                    fileName + ".meta");
            createMetaData(metaDataFilePath, dublinCoreParams);

            // insert <media> tags at the location sepecified by the
            // cpath in the original document (the referer)
            insertMediaTag(sitemapPath + docsRoot + File.separator + documentId, imageXPath,
                fileName, dublinCoreParams);
        } else {
            getLogger().debug("parameter " + UPLOADFILE_PARAM_NAME + " is not of type FilePart");

            return null;
        }

        return Collections.unmodifiableMap(results);
    }

    /**
     * Describe <code>createMetaData</code> method here.
     *
     * @param metaDataFilePathName a <code>String</code> value
     * @param dublinCoreParams a <code>HashMap</code> value
     *
     * @exception IOException if an error occurs
     */
    protected void createMetaData(String metaDataFilePathName, HashMap dublinCoreParams)
        throws IOException {
        getLogger().debug("metaDataFilePathName:" + metaDataFilePathName);

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("dc:metadata");

        Iterator iter = dublinCoreParams.keySet().iterator();

        while (iter.hasNext()) {
            String tagName = (String) iter.next();
            String tagValue = (String) dublinCoreParams.get(tagName);
            root.addElement(tagName).addText(tagValue);
        }

        File parentDir = (new File(metaDataFilePathName)).getParentFile();

        if (!parentDir.exists()) {
            getLogger().info(".act(): Create directories: " + parentDir);
            parentDir.mkdirs();
        }

        OutputStream out = new BufferedOutputStream(new FileOutputStream(metaDataFilePathName));

        XMLWriter writer = new XMLWriter(out, OutputFormat.createPrettyPrint());
        writer.write(document);
        writer.close();
    }

    /**
     * Insert &lt;media&gt; tags at the location specified by the xpath in the original document
     * (the requestingDocumentPath)
     *
     * @param requestingDocumentPath the xml document from where the image upload request
     *        originated.
     * @param imageXPath the xpath after which the image is to be inserted.
     * @param imagePath path name of the uploaded image
     * @param dublinCoreParams a HashMap of additional values according to Dublin Core.
     *
     * @exception DocumentException if an error occurs
     * @exception IOException if an error occurs
     */
    protected void insertMediaTag(String requestingDocumentPath, String imageXPath,
        String imagePath, HashMap dublinCoreParams) throws DocumentException, IOException {
        // insert <media> tags at the location specified by the xpath
        // in the original document (the referer)
        // read the document
        SAXReader reader = new SAXReader();

        Document document = reader.read(requestingDocumentPath);
        getLogger().debug("insertMediaTag:" + requestingDocumentPath);

        // create the media element
        Element mediaTag = DocumentHelper.createElement("media");
        mediaTag.addAttribute("media-type", "image");

        mediaTag.addElement("media-reference")
                .addAttribute("mime-type", (String) dublinCoreParams.get("format"))
                .addAttribute("source", imagePath)
                .addAttribute("alternate-text", (String) dublinCoreParams.get("title"))
                .addAttribute("copyright", (String) dublinCoreParams.get("rights"));

        if (((String) dublinCoreParams.get("description")).equals("")) {
            // FIXME: shouldn't there be some meaningful text in
            // here? How about another parameter to the action that
            // contains the caption text?
            mediaTag.addElement("media-caption").addText("No Caption");
        } else {
            mediaTag.addElement("media-caption").addText((String) dublinCoreParams.get(
                    "description"));
        }

        mediaTag.addElement("authorline").addText((String) dublinCoreParams.get("creator"));

        // find the node where the figure tag has to be inserted
        Node node = document.selectSingleNode(imageXPath);

        if (node == null) {
            // Hmm, we can't find the specified imageXPath in the
            // requesting document. Something is fishy. Log and return
            getLogger().error("Could not find xpath:" + imageXPath + " in document: " +
                requestingDocumentPath);

            return;
        }

        Element parent = node.getParent();

        if (parent == null) {
            // Hmm, the specified xpath doesn't seem to have a
            // parent. Where do we insert the media tag now? Instead
            // of making any assuptions we log and return without
            // adding the media tag.
            getLogger().error("The specified xpath:" + imageXPath +
                " doesn't have a parent. Don't know where" + " to insert the media tag.");

            return;
        }

        List list = parent.content();

        // The request parameters insertBefore and insertAfter
        // overwrite the configuration (insertImageBefore) 
        if (insertBefore || (insertImageBefore && !insertAfter)) {
            // insert the tag before the imageXPath
            list.add(parent.indexOf(node), mediaTag);
        } else {
            // insert the tag after the imageXPath
            list.add(parent.indexOf(node) + 1, mediaTag);
        }

        // write it back to the file
        OutputStream out = new BufferedOutputStream(new FileOutputStream(requestingDocumentPath));
        OutputFormat outputFormat = new OutputFormat();
        outputFormat.setNewlines(true);
        outputFormat.setTrimText(false);
        outputFormat.setExpandEmptyElements(false);

        XMLWriter writer = new XMLWriter(out, outputFormat);
        writer.write(document);
        writer.close();
    }

    /**
     * Figure out where the image is to be stored. The default implementation simply concatenates
     * the sitemapPath and recourcesRoot (which is defined in the sitemap) with the parent of
     * documentId and the filename. Derived classes might want to change this into more elaborate
     * schemes, i.e. store the image together with the document where the image is inserted.
     *
     * @param sitemapPath a <code>String</code> value
     * @param recourcesRoot a <code>String</code> value
     * @param documentId a <code>String</code> value
     * @param fileName a <code>String</code> value
     *
     * @return a <code>String</code> value
     */
    protected String getImagePath(String sitemapPath, String recourcesRoot, String documentId,
        String fileName) {
        return getResourcePath(sitemapPath, recourcesRoot, documentId, fileName);
    }

    /**
     * Figure out where the meta data file is to be stored. The default implementation simply
     * concatenates the sitemapPath and metaRoot (which is defined in the sitemap) with the parent
     * of documentId and the filename.
     *
     * @param sitemapPath a <code>String</code> value
     * @param metaRoot a <code>String</code> value
     * @param documentId a <code>String</code> value
     * @param fileName a <code>String</code> value
     *
     * @return a <code>String</code> value
     */
    protected String getMetaDataPath(String sitemapPath, String metaRoot, String documentId,
        String fileName) {
        return getResourcePath(sitemapPath, metaRoot, documentId, fileName);
    }

    /**
     * A simple helper method for the getFooPath methods, which simply concatenates the given
     * filenames. If a filename is null it is ignored.
     *
     * @param sitemapPath a <code>String</code> value
     * @param recourcesRoot a <code>String</code> value
     * @param documentId a <code>String</code> value
     * @param fileName a <code>String</code> value
     *
     * @return a <code>String</code> value
     */
    protected String getResourcePath(String sitemapPath, String recourcesRoot, String documentId,
        String fileName) {
        String path = sitemapPath + File.separator;
        path += (recourcesRoot + File.separator);

        String requestingDocumentPath = (new File(documentId)).getParent();

        // only add the path to the requesting document to the
        // imagePath if it is non-null, otherwise we get paths like
        // foo/null/bar.
        if (requestingDocumentPath != null) {
            path += (requestingDocumentPath + File.separator);
        }

        path += fileName;

        return path;
    }
}
