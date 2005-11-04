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
import java.util.Arrays;

import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.security.SimpleLoginModule;
import org.apache.lenya.cms.jcr.JCRArea;
import org.apache.lenya.cms.jcr.JCRPublication;
import org.apache.lenya.cms.jcr.RepositoryFacade;
import org.apache.lenya.cms.jcr.RepositoryFactory;
import org.apache.lenya.cms.jcr.RepositorySession;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationImpl;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Migrate Lenya 1.4-dev content.
 */
public class Migrate14 {

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

    private RepositoryFacade repo;
    private RepositorySession session;
    private Node contentNode;

    protected RepositoryFacade getRepository() {
        return this.repo;
    }

    protected RepositorySession getSession() {
        return this.session;
    }

    public void execute() throws BuildException {

        try {
            this.repo = new RepositoryFacade(getWebappDirectory(), getRepositoryFactory());
            this.session = this.repo.createSession();

            File publicationsDirectory = new File(webappDirectory, Publication.PUBLICATION_PREFIX);
            File[] pubDirs = publicationsDirectory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory()
                            && new File(file, PublicationImpl.CONFIGURATION_FILE).exists();
                }
            });

            for (int i = 0; i < pubDirs.length; i++) {
                importPublication(pubDirs[i]);
            }

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
        JCRPublication pub = new JCRPublication(getSession(), pubId);

        File contentDir = new File(pubDir, "content");
        File[] areaDirs = contentDir.listFiles(directoryFilter);
        for (int i = 0; i < areaDirs.length; i++) {
            importContent(areaDirs[i], pub);
        }
    }

    protected void importContent(File contentDir, JCRPublication pub) throws RepositoryException {
        String area = contentDir.getName();
        JCRArea jcrArea = pub.getArea(area);
        
        System.out.println("pub node:  " + jcrArea.getPublicationNode().getPath());
        System.out.println("site node: " + jcrArea.getSiteNode().getPath());
        
        importChildren(contentDir, jcrArea.getSiteNode());
    }

    protected void importDocument(File docDir, Node parent)
            throws RepositoryException {
        String nodeId = docDir.getName();
        String documentId = parent.getPath() + "/" + nodeId;
        System.out.println("Importing [" + documentId + "]");

        Node node = parent.addNode(nodeId);

        importChildren(docDir, node);
    }

    protected void importChildren(File docDir, Node parent)
            throws RepositoryException {
        File[] childDirs = docDir.listFiles(directoryFilter);
        for (int i = 0; i < childDirs.length; i++) {
            importDocument(childDirs[i], parent);
        }
    }
}
