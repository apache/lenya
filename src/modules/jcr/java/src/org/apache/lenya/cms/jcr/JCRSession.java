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
package org.apache.lenya.cms.jcr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jcr.LoginException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.lenya.cms.repo.Publication;

/**
 * Repository session.
 */
public class JCRSession implements org.apache.lenya.cms.repo.Session {

    /**
     * Ctor.
     * @param repository The repository facade.
     */
    public JCRSession(JCRRepository repository) {
        this.repository = repository;

        this.publicationNodeBuilder = new JCRPublicationNodeBuilder(this);
        this.publicationNodeManager = new NodeWrapperManager(this, this.publicationNodeBuilder);
    }

    private JCRRepository repository;

    protected JCRRepository getRepository() {
        return this.repository;
    }

    private Map area2session = new HashMap();

    private NodeWrapperManager publicationNodeManager;
    private JCRPublicationNodeBuilder publicationNodeBuilder;

    protected Session getSession(String area) throws RepositoryException {
        Session session = (Session) this.area2session.get(area);
        if (session == null) {

            boolean create = false;

            WorkspaceImpl defaultWorkspace = getDefaultWorkspace();
            String[] workspaces = defaultWorkspace.getAccessibleWorkspaceNames();
            if (!Arrays.asList(workspaces).contains(area)) {
                defaultWorkspace.createWorkspace(area);
                create = true;
            }

            session = getRepository().getRepository().login(new SimpleCredentials("john",
                    "".toCharArray()),
                    area);

            if (create) {
                NamespaceRegistry registry = session.getWorkspace().getNamespaceRegistry();
                String uri = "http://apache.org/cocoon/lenya/jcr/1.0";
                if (!Arrays.asList(registry.getURIs()).contains(uri)) {
                    registry.registerNamespace("lenya", uri);
                }
                uri = "http://apache.org/cocoon/lenya/jcr/nodetype/1.0";
                if (!Arrays.asList(registry.getURIs()).contains(uri)) {
                    registry.registerNamespace("lnt", uri);
                }
            }
            this.area2session.put(area, session);
        }

        return session;
    }

    protected WorkspaceImpl getDefaultWorkspace() throws LoginException, RepositoryException {
        Session defaultWorkspaceSession = getRepository().getRepository()
                .login(new SimpleCredentials("john", "".toCharArray()));

        WorkspaceImpl defaultWorkspace = (WorkspaceImpl) defaultWorkspaceSession.getWorkspace();
        return defaultWorkspace;
    }

    public void save() throws org.apache.lenya.cms.repo.RepositoryException {
        try {
            for (Iterator i = this.area2session.keySet().iterator(); i.hasNext();) {
                String area = (String) i.next();
                Session session = (Session) this.area2session.get(area);
                session.save();
            }
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.repo.RepositoryException(e);
        }
    }

    protected void initPublications() throws org.apache.lenya.cms.repo.RepositoryException {

        if (this.publications == null) {
            this.publications = new HashMap();
            try {
                Set pubIds = new HashSet();

                WorkspaceImpl defaultWorkspace = getDefaultWorkspace();
                String[] workspaces = defaultWorkspace.getAccessibleWorkspaceNames();

                for (int i = 0; i < workspaces.length; i++) {

                    Session session = getSession(workspaces[i]);
                    Node rootNode = session.getRootNode();

                    for (NodeIterator pubNodes = rootNode.getNodes(JCRPublicationNodeBuilder.NODE_NAME); pubNodes.hasNext();) {
                        Node node = pubNodes.nextNode();

                        String pubId = node.getProperty(JCRPublicationNodeBuilder.ID_ATTRIBUTE)
                                .getString();
                        Publication pub = (Publication) this.publications.get(pubId);
                        if (pub == null) {
                            pub = new JCRPublication(this, pubId);
                            this.publications.put(pubId, pub);
                        }
                        pubIds.add(pubId);
                    }
                }

            } catch (RepositoryException e) {
                throw new org.apache.lenya.cms.repo.RepositoryException(e);
            }
        }
    }

    private Map publications;

    public Publication getPublication(String id)
            throws org.apache.lenya.cms.repo.RepositoryException {

        initPublications();
        if (this.publications.keySet().contains(id)) {
            return (Publication) this.publications.get(id);
        } else {
            throw new org.apache.lenya.cms.repo.RepositoryException("The publication [" + id
                    + "] does not exist.");
        }
    }
    
    public Publication addPublication(String id)
            throws org.apache.lenya.cms.repo.RepositoryException {
        initPublications();
        if (this.publications.keySet().contains("id")) {
            throw new org.apache.lenya.cms.repo.RepositoryException("The publication [" + id
                    + "]Êalready exists.");
        } else {
            Publication pub = new JCRPublication(this, id);
            this.publications.put(id, pub);
            return pub;
        }
    }

    protected JCRPublicationNode addArea(JCRPublication publication, String area)
            throws org.apache.lenya.cms.repo.RepositoryException {

        BuilderParameters params = this.publicationNodeBuilder.createParameters(publication.getPublicationId(),
                area);
        if (this.publicationNodeBuilder.existsNode(this, params)) {
            throw new org.apache.lenya.cms.repo.RepositoryException("The node already exists!");
        } else {
            return (JCRPublicationNode) this.publicationNodeManager.addNode(publication.getPublicationId()
                    + ":" + area,
                    params);
        }
    }
    
    protected JCRPublicationNode[] getAreas(Publication publication)
            throws org.apache.lenya.cms.repo.RepositoryException {

        Set pubNodes = new HashSet();
        String[] keys = this.publicationNodeManager.getKeys(null);
        for (int i = 0; i < keys.length; i++) {
            String[] steps = keys[i].split(":");
            String pubId = steps[0];
            if (pubId.equals(publication.getPublicationId())) {
                String area = steps[1];
                BuilderParameters params = this.publicationNodeBuilder.createParameters(publication.getPublicationId(),
                        area);
                pubNodes.add(this.publicationNodeManager.getNode(keys[i], params));
            }
        }
        return (JCRPublicationNode[]) pubNodes.toArray(new JCRPublicationNode[pubNodes.size()]);
    }

    protected JCRPublicationNode getArea(Publication publication, String area)
            throws org.apache.lenya.cms.repo.RepositoryException {
        BuilderParameters params = this.publicationNodeBuilder.createParameters(publication.getPublicationId(),
                area);
        return (JCRPublicationNode) this.publicationNodeManager.getNode(publication.getPublicationId(),
                params);
    }
    
    protected boolean existsArea(Publication publication, String area) throws org.apache.lenya.cms.repo.RepositoryException {
        BuilderParameters params = this.publicationNodeBuilder.createParameters(publication.getPublicationId(),
                area);
        return this.publicationNodeBuilder.existsNode(this, params);
    }

    public boolean existsPublication(String id) throws org.apache.lenya.cms.repo.RepositoryException {
        initPublications();
        return this.publications.containsKey(id);
    }

}
