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
package org.apache.lenya.cms.migration;

import java.io.File;
import java.io.FileFilter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.ContentNode;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.RepositoryManager;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.SiteNode;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.tools.ant.BuildException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Migrate Lenya 1.4-dev content.
 */
public class Migrate14 {

    /**
     * Main method.
     * @param args The command line arguments: &lt;webapp-directory&gt;
     *            &lt;repository-factory-class&gt;
     */
    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: Migrate14 <webapp-directory> <repository-factory-class>");
        }

        Migrate14 migrate = new Migrate14();
        migrate.setWebappDirectory(args[0]);
        migrate.setRepositoryFactory(args[1]);
        migrate.execute();
    }

    private String webappDirectory;
    private String repositoryFactory;

    /**
     * @param webappDirectory The web application directory.
     */
    public void setWebappDirectory(String webappDirectory) {
        this.webappDirectory = webappDirectory;
    }

    /**
     * @return The web application directory.
     */
    public String getWebappDirectory() {
        return this.webappDirectory;
    }

    /**
     * @return The repository factory classname.
     */
    public String getRepositoryFactory() {
        return repositoryFactory;
    }

    /**
     * @param repositoryFactory The repository factory classname.
     */
    public void setRepositoryFactory(String repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    private Repository repo;
    private Session session;

    protected Repository getRepository() {
        return this.repo;
    }

    protected Session getSession() {
        return this.session;
    }

    static final String PUBLICATION_PREFIX = "lenya" + File.separator + "pubs";
    static final String CONFIGURATION_PATH = "config";
    static final String CONFIGURATION_FILE = CONFIGURATION_PATH + File.separator
            + "publication.xconf";

    public void execute() throws BuildException {

        try {
            this.repo = RepositoryManager.getRepository(getWebappDirectory(),
                    getRepositoryFactory());
            this.session = this.repo.createSession();

            File publicationsDirectory = new File(webappDirectory, PUBLICATION_PREFIX);
            File[] pubDirs = publicationsDirectory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory() && new File(file, CONFIGURATION_FILE).exists();
                }
            });

            for (int i = 0; i < pubDirs.length; i++) {
                importPublication(pubDirs[i]);
            }

            getSession().save();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            if (e.getCause() != null) {
                e.getCause().printStackTrace(System.err);
            }
            throw new BuildException(e);
        }

    }

    protected static final FileFilter directoryFilter = new FileFilter() {
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    protected void importPublication(File pubDir) throws RepositoryException {
        String pubId = pubDir.getName();
        Publication pub = getSession().getPublication(pubId);

        File contentDir = new File(pubDir, "content");
        File[] areaDirs = contentDir.listFiles(directoryFilter);
        for (int i = 0; i < areaDirs.length; i++) {
            importContent(areaDirs[i], pub);
        }
    }

    protected void importContent(File contentDir, Publication pub) throws RepositoryException {
        String area = contentDir.getName();
        Area repoArea = pub.getArea(area);
        importChildren(contentDir, repoArea, null);
    }

    protected void importNode(File docDir, Area area, SiteNode parent) throws RepositoryException {
        String nodeId = docDir.getName();
        String parentPath = "";
        if (parent != null) {
            parentPath = parent.getPath();
        }
        String documentPath = parentPath + "/" + nodeId;
        System.out.println("Importing [" + documentPath + "]");

        SiteNode siteNode;
        ContentNode contentNode = area.getContent().addNode(nodeId);
        if (parent == null) {
            siteNode = area.getSite().addChild(nodeId, contentNode);
        } else {
            siteNode = parent.addChild(nodeId, contentNode);
        }

        importDocuments(docDir, contentNode);

        importChildren(docDir, area, siteNode);
    }

    protected void importDocuments(File docDir, ContentNode contentNode) throws RepositoryException {
        File[] files = docDir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile() && file.getName().startsWith("index_")
                        && file.getName().endsWith(".xml");
            }
        });
        for (int i = 0; i < files.length; i++) {
            importDocument(files[i], contentNode);
        }
    }

    protected void importDocument(File file, ContentNode contentNode) throws RepositoryException {
        String fileName = file.getName();
        String suffix = fileName.substring("index_".length());
        String language = suffix.substring(0, suffix.length() - ".xml".length());
        System.out.println(" language [" + language + "]");
        
        Document document = contentNode.addDocument(language);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Source source = new StreamSource(file);
            Result result = new StreamResult(document.getOutputStream());
            transformer.transform(source, result);
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }

    }

    protected void importChildren(File docDir, Area area, SiteNode parentNode)
            throws RepositoryException {
        File[] childDirs = docDir.listFiles(directoryFilter);
        for (int i = 0; i < childDirs.length; i++) {
            importNode(childDirs[i], area, parentNode);
        }
    }
}
