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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Asset;
import org.apache.lenya.cms.repo.AssetType;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.RepositoryManager;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.SiteNode;
import org.apache.lenya.cms.repo.Translation;
import org.apache.lenya.cms.repo.adapter.DublinCoreElements;
import org.apache.lenya.cms.repo.adapter.LenyaElements;
import org.apache.lenya.cms.repo.impl.AssetTypeImpl;
import org.apache.lenya.cms.repo.metadata.MetaData;
import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;
import org.apache.lenya.cms.repo.mock.AssetTypeResolverImpl;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.tools.ant.BuildException;
import org.w3c.dom.Element;

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

    /**
     * Executes the migration.
     * @throws BuildException if an error occurs.
     */
    public void execute() throws BuildException {

        try {
            this.repo = RepositoryManager.getRepository(getWebappDirectory(),
                    getRepositoryFactory());
            repo.setAssetTypeResolver(new AssetTypeResolverImpl());

            this.session = this.repo.login("john");

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
            getSession().logout();

            this.repo.shutdown();

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
        System.out.println("-----------------------------");
        System.out.println(" Importing publication [" + pubId + "]");
        System.out.println("-----------------------------");

        Publication pub;
        if (getSession().existsPublication(pubId)) {
            System.out.println("Publication already exists.");
            pub = getSession().getPublication(pubId);
        } else {
            System.out.println("Creating publication.");
            pub = getSession().addPublication(pubId);
        }

        File contentDir = new File(pubDir, "content");
        File[] areaDirs = contentDir.listFiles(directoryFilter);
        if (areaDirs != null) {
            for (int i = 0; i < areaDirs.length; i++) {
                importContent(areaDirs[i], pub);
            }
        }
    }

    protected void importContent(File contentDir, Publication pub) throws RepositoryException {
        String area = contentDir.getName();
        Area repoArea;
        if (pub.existsArea(area)) {
            repoArea = pub.getArea(area);
        } else {
            repoArea = pub.addArea(area);
        }
        importChildren(contentDir, repoArea, null);
    }

    protected void importNode(File docDir, Area area, SiteNode parent) throws RepositoryException {
        String nodeId = docDir.getName();
        String parentPath = "";
        if (parent != null) {
            parentPath = parent.getPath() + "/";
        }
        String documentPath = parentPath + nodeId;
        System.out.println("Importing [" + area + "] - [" + documentPath + "]");

        File[] metaFiles = docDir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile() && file.getName().startsWith("index_")
                        && file.getName().endsWith(".xml.meta");
            }
        });
        if (metaFiles.length > 0) {

            File metaFile = metaFiles[0];
            org.w3c.dom.Document xmlDoc;
            try {
                xmlDoc = DocumentHelper.readDocument(metaFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            NamespaceHelper helper = new NamespaceHelper(PageEnvelope.NAMESPACE, "", xmlDoc);
            Element metaElement = helper.getFirstChild(xmlDoc.getDocumentElement(), "meta");
            Element internalElement = helper.getFirstChild(metaElement, "internal");
            Element resourceTypeElement = helper.getFirstChild(internalElement, "resourceType");
            String resourceType = DocumentHelper.getSimpleElementText(resourceTypeElement);
            AssetType doctype;

            AssetTypeResolverImpl resolver = (AssetTypeResolverImpl) repo.getAssetTypeResolver();
            if (!resolver.canResolve(resourceType)) {
                doctype = new AssetTypeImpl(resourceType, null, false);
                resolver.register(doctype);
            } else {
                doctype = resolver.resolve(resourceType);
            }

            SiteNode siteNode = null;

            Asset contentNode = area.getContent().addAsset(doctype);
            if (parent == null) {
                if (area.getSite().hasChild(nodeId)) {
                    System.out.println("Site node [" + nodeId + "] already exists.");
                } else {
                    System.out.println("Adding site node [" + nodeId + "].");
                    siteNode = area.getSite().addChild(nodeId, contentNode);
                }
            } else {
                if (parent.hasChild(nodeId)) {
                    System.out.println("Site node [" + parent.getPath() + "/" + nodeId
                            + "] already exists.");
                } else {
                    System.out.println("Adding site node [" + parent.getPath() + "/" + nodeId
                            + "].");
                    siteNode = parent.addChild(nodeId, contentNode);
                }
            }

            importDocuments(docDir, contentNode);

            if (siteNode != null) {
                importChildren(docDir, area, siteNode);
            }
        }
    }

    protected void importDocuments(File docDir, Asset contentNode) throws RepositoryException {
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

    protected void importDocument(File file, Asset contentNode) throws RepositoryException {
        String fileName = file.getName();
        String suffix = fileName.substring("index_".length());
        String language = suffix.substring(0, suffix.length() - ".xml".length());
        System.out.println(" language [" + language + "]");

        Translation trans = contentNode.addTranslation(language, "Label", "application/xml");

        OutputStream out = trans.getOutputStream();
        FileInputStream in;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RepositoryException(e);
        }
        copy(in, out);

        File metaFile = new File(file.getAbsolutePath() + ".meta");
        try {
            org.w3c.dom.Document xmlDoc = DocumentHelper.readDocument(metaFile);
            importMetaData(trans,
                    xmlDoc,
                    PageEnvelope.NAMESPACE,
                    "internal",
                    LenyaElements.ELEMENT_SET,
                    LenyaElements.ELEMENTS);
            importMetaData(trans,
                    xmlDoc,
                    DublinCore.DC_NAMESPACE,
                    "dc",
                    DublinCoreElements.ELEMENT_SET,
                    DublinCoreElements.getElements());
            importMetaData(trans,
                    xmlDoc,
                    DublinCore.DCTERMS_NAMESPACE,
                    "dc",
                    DublinCoreElements.ELEMENT_SET,
                    DublinCoreElements.getElements());
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    protected void importMetaData(Translation trans, org.w3c.dom.Document xmlDoc,
            String namespaceUri, String parentElement, String elementSetName,
            org.apache.lenya.cms.repo.metadata.Element[] elements) throws RepositoryException {

        NamespaceHelper lenyaHelper = new NamespaceHelper(PageEnvelope.NAMESPACE, "", xmlDoc);
        Element metaElement = lenyaHelper.getFirstChild(xmlDoc.getDocumentElement(), "meta");
        Element parent = lenyaHelper.getFirstChild(metaElement, parentElement);

        NamespaceHelper helper = new NamespaceHelper(namespaceUri, "", xmlDoc);
        Element[] children = helper.getChildren(parent);
        for (int i = 0; i < children.length; i++) {
            String key = children[i].getLocalName();
            String value = DocumentHelper.getSimpleElementText(children[i]);
            System.out.println("  Setting meta data: [" + key + "] = [" + value + "]");

            MetaDataRegistry registry = this.repo.getMetaDataRegistry();
            if (!registry.isRegistered(elementSetName)) {
                registry.register(elementSetName, elements);
            }

            MetaData meta = trans.getMetaData(elementSetName);
            if (meta.getElementSet().getElement(key).isMultiple()) {
                meta.addValue(key, value);
            } else {
                meta.setValue(key, value);
            }
        }

    }

    protected static void copy(InputStream fis, OutputStream fos) {
        try {
            byte buffer[] = new byte[0xffff];
            int nbytes;

            while ((nbytes = fis.read(buffer)) != -1)
                fos.write(buffer, 0, nbytes);
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    System.out.println(e);
                }

            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                System.out.println(e);
            }
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
