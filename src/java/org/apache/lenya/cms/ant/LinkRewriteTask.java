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

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This task is used to rewrite internal links after a cut'n'paste operation, i.e.
 * after a document has changed its document-id. It finds all relevant documents
 * which have a link to the document that changed its document-id and changes this
 * link with the help of an xslt.
 */
public class LinkRewriteTask extends PublicationTask {

    private static final class XMLFilenameFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            File _file = new File(dir, name);
            return _file.isFile() && FileUtil.getExtension(name).equals("xml");
        }
    }

    private static final class DirectoryFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            File _file = new File(dir, name);
            return _file.isDirectory();
        }
    }

    private String baseDir;
    private String stylesheet;
    private String area;
    private String oldDocumentId;
    private String newDocumentId;

    /**
     * Get the area
     * @return the area
     */
    public String getArea() {
        return this.area;
    }

    /**
     * Set the area
     * @param _area the area
     */
    public void setArea(String _area) {
        this.area = _area;
    }

    /**
     * Get the new document-id.
     * @return the new document-id
     */
    public String getNewDocumentId() {
        return this.newDocumentId;
    }

    /**
     * Set the new document-id.
     * @param _newDocumentId the new document-id
     */
    public void setNewDocumentId(String _newDocumentId) {
        this.newDocumentId = _newDocumentId;
    }

    /**
     * Get the old document-id.
     * @return the old document-id
     */
    public String getOldDocumentId() {
        return this.oldDocumentId;
    }

    /**
     * Set the old document-id.
     * @param _oldDocumentId the old document-id
     */
    public void setOldDocumentId(String _oldDocumentId) {
        this.oldDocumentId = _oldDocumentId;
    }

    /**
     * Get the stylesheet.
     * @return the stylesheet
     */
    public String getStylesheet() {
        return this.stylesheet;
    }

    /**
     * Set the stylesheet.
     * @param _stylesheet the stylesheet that transforms the links
     */
    public void setStylesheet(String _stylesheet) {
        this.stylesheet = _stylesheet;
    }

    /**
     * Set the base dir where in which the link rewrite will take place.
     * @param _baseDir the base dir
     */
    public void setBaseDir(String _baseDir) {
        this.baseDir = _baseDir;
    }

    /**
     * Get the base dir.
     * @return the base dir
     */
    private String getBaseDir() {
        return this.baseDir;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        log("baseDir: " + getBaseDir());
        log("stylesheet: " + getStylesheet());
        try {
            replace(
                getBaseDir(),
                getStylesheet(),
                getArea(),
                getOldDocumentId(),
                getNewDocumentId());
        } catch (Exception e) {
            throw new BuildException(e);
        }

    }

    /**
     * Rewrites links
     * @param file The starting point for the link rewrite
     * @param transformer The transformer to use for the link rewrite
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private void replace_internal(File file, Transformer transformer)
        throws TransformerException, ParserConfigurationException, SAXException, IOException {

        FilenameFilter directoryFilter = new DirectoryFilter();

        FilenameFilter xmlFileFilter = new XMLFilenameFilter();

        log("root file: " + file.getCanonicalPath());
        assert(file.isDirectory());

        File[] children = file.listFiles(directoryFilter);
        for (int i = 0; i < children.length; i++) {
            replace_internal(children[i], transformer);
        }
        File[] xmlFiles = file.listFiles(xmlFileFilter);

        javax.xml.parsers.DocumentBuilder documentBuilder = DocumentHelper.createBuilder();

        for (int i = 0; i < xmlFiles.length; i++) {
            File tmpFile = File.createTempFile("linkRewrite", "tmp");
            FileOutputStream os = new FileOutputStream(tmpFile);
            log("transform " + xmlFiles[i].getCanonicalPath());

            Document document = documentBuilder.parse(xmlFiles[i]);
            DOMSource domSource = new DOMSource(document);
            
			StreamResult result = new StreamResult(os);
            transformer.transform(domSource, result);
            result.getOutputStream().close();

            if (!tmpFile.renameTo(xmlFiles[i])) {
			  FileUtil.copyFile(tmpFile, xmlFiles[i]);
			  FileUtil.forceDelete(tmpFile);
            }
        }
    }

    /**
     * Rewrites links by traversing a directory tree and applying a rewrite transformation
     * to XML files in the directory.
     * @param rootDirName The root directory for the rewrite
     * @param _stylesheet The stylesheet to use for rewriting
     * @param _area The area to use for rewriting
     * @param _oldDocumentId The old document id
     * @param _newDocumentId The new document id
     * 
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private void replace(
        String rootDirName,
        String _stylesheet,
        String _area,
        String _oldDocumentId,
        String _newDocumentId)
        throws TransformerException, ParserConfigurationException, SAXException, IOException {

        File rootDir = new File(rootDirName);
        TransformerFactory tFactory = TransformerFactory.newInstance();

        Transformer transformer = tFactory.newTransformer(new StreamSource(_stylesheet));

        Publication publication = getPublication();
        DocumentBuilder builder = publication.getDocumentBuilder();

        // replace all internal links
        String oldURL =
            getContextPrefix() + builder.buildCanonicalUrl(publication, _area, _oldDocumentId);
        String newURL =
            getContextPrefix() + builder.buildCanonicalUrl(publication, _area, _newDocumentId);

        log("Replace '" + oldURL + "' by '" + newURL + "'");
        transformer.setParameter("urlbefore", oldURL);
        transformer.setParameter("urlafter", newURL);

        replace_internal(rootDir, transformer);

        // now also do the replacement for all language versions
        String[] languages = publication.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            String language = languages[i];

            oldURL =
                getContextPrefix()
                    + builder.buildCanonicalUrl(publication, _area, _oldDocumentId, language);
            newURL =
                getContextPrefix()
                    + builder.buildCanonicalUrl(publication, _area, _newDocumentId, language);

            log("Replace '" + oldURL + "' by '" + newURL + "'");
            transformer.setParameter("urlbefore", oldURL);
            transformer.setParameter("urlafter", newURL);
            transformer.setParameter("language", language);

            replace_internal(rootDir, transformer);
        }
    }
}
