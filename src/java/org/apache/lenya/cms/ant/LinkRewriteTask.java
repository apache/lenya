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

/* $Id: LinkRewriteTask.java,v 1.5 2004/03/03 12:56:30 gregor Exp $  */

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

    private String baseDir;
    private String stylesheet;
    private String area;
    private String oldDocumentId;
    private String newDocumentId;

    /**
     * Get the area
     * 
     * @return the area
     */
    public String getArea() {
        return area;
    }

    /**
     * Set the area
     * 
     * @param area the area
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * Get the new document-id.
     * 
     * @return the new document-id
     */
    public String getNewDocumentId() {
        return newDocumentId;
    }

    /**
     * Set the new document-id.
     * 
     * @param newDocumentId the new document-id
     */
    public void setNewDocumentId(String newDocumentId) {
        this.newDocumentId = newDocumentId;
    }

    /**
     * Get the old document-id.
     * 
     * @return the old document-id
     */
    public String getOldDocumentId() {
        return oldDocumentId;
    }

    /**
     * Set the old document-id.
     * 
     * @param oldDocumentId the old document-id
     */
    public void setOldDocumentId(String oldDocumentId) {
        this.oldDocumentId = oldDocumentId;
    }

    /**
     * Get the stylesheet.
     * 
     * @return the stylesheet
     */
    public String getStylesheet() {
        return stylesheet;
    }

    /**
     * Set the stylesheet.
     * 
     * @param stylesheet the stylesheet that transforms the links
     */
    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    /**
     * Set the base dir where in which the link rewrite will take place.
     * 
     * @param baseDir the base dir
     */
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Get the base dir.
     * 
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
     * 
     * @param file
     * @param transformer
     * @param oldDocumentId
     * @param newDocumentId
     * @throws FileNotFoundException
     * @throws TransformerException
     */
    private void replace_internal(File file, Transformer transformer)
        throws TransformerException, ParserConfigurationException, SAXException, IOException {

        FilenameFilter directoryFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                File file = new File(dir, name);
                return file.isDirectory();
            }
        };

        FilenameFilter xmlFileFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                File file = new File(dir, name);
                return file.isFile() && FileUtil.getExtension(name).equals("xml");
            }
        };

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
     * 
     * @param rootDirName
     * @param stylesheet
     * @param oldDcoumentId
     * @param newDocumentId
     * 
     * @throws FileNotFoundException
     * @throws TransformerException
     */
    private void replace(
        String rootDirName,
        String stylesheet,
        String area,
        String oldDcoumentId,
        String newDocumentId)
        throws TransformerException, ParserConfigurationException, SAXException, IOException {

        File rootDir = new File(rootDirName);
        TransformerFactory tFactory = TransformerFactory.newInstance();

        Transformer transformer = tFactory.newTransformer(new StreamSource(stylesheet));

        Publication publication = getPublication();
        DocumentBuilder builder = publication.getDocumentBuilder();

        // replace all internal links
        String oldURL =
            getContextPrefix() + builder.buildCanonicalUrl(publication, area, oldDcoumentId);
        String newURL =
            getContextPrefix() + builder.buildCanonicalUrl(publication, area, newDocumentId);

        log("Replace '" + oldURL + "' by '" + newURL + "'");
        transformer.setParameter("idbefore", oldURL);
        transformer.setParameter("idafter", newURL);

        replace_internal(rootDir, transformer);

        // now also do the replacement for all language versions
        String[] languages = publication.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            String language = languages[i];

            oldURL =
                getContextPrefix()
                    + builder.buildCanonicalUrl(publication, area, oldDcoumentId, language);
            newURL =
                getContextPrefix()
                    + builder.buildCanonicalUrl(publication, area, newDocumentId, language);

            log("Replace '" + oldURL + "' by '" + newURL + "'");
            transformer.setParameter("idbefore", oldURL);
            transformer.setParameter("idafter", newURL);

            replace_internal(rootDir, transformer);
        }
    }
}
