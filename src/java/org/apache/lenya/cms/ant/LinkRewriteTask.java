/*
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
 * 
 * @author Christian Egli
 * 
 * @version $Id: LinkRewriteTask.java,v 1.3 2003/12/08 13:56:55 andreas Exp $
 *
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

            transformer.transform(domSource, new StreamResult(os));

            if (!tmpFile.renameTo(xmlFiles[i])) {
                throw new IOException(
                    "Could not move "
                        + tmpFile.getCanonicalPath()
                        + " to "
                        + xmlFiles[i].getCanonicalPath());
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
