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
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.lenya.cms.jcr.mapping.Path;
import org.apache.lenya.cms.jcr.mapping.RepositoryFacade;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.RepositoryException;

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
    }

    private JCRRepository repository;

    public Repository getRepository() {
        return this.repository;
    }

    private Map area2facade = new HashMap();

    protected RepositoryFacade getRepositoryFacade(String area) throws RepositoryException {
        RepositoryFacade facade = (RepositoryFacade) this.area2facade.get(area);
        if (facade == null) {

            try {
                // boolean create = false;

                WorkspaceImpl defaultWorkspace = getDefaultWorkspace();
                String[] workspaces = defaultWorkspace.getAccessibleWorkspaceNames();
                if (!Arrays.asList(workspaces).contains(area)) {
                    defaultWorkspace.createWorkspace(area);
                    // create = true;
                }

                Session session = ((JCRRepository) getRepository()).getRepository()
                        .login(new SimpleCredentials("john", "".toCharArray()), area);
                facade = new RepositoryFacade(this,
                        session,
                        getRepository().getDocumentTypeRegistry(),
                        getRepository().getMetaDataRegistry());

                // if (create) {
                NamespaceRegistry registry = session.getWorkspace().getNamespaceRegistry();
                String uri = "http://apache.org/cocoon/lenya/jcr/1.0";
                if (!Arrays.asList(registry.getURIs()).contains(uri)) {
                    registry.registerNamespace("lenya", uri);
                }
                uri = "http://apache.org/cocoon/lenya/jcr/nodetype/1.0";
                if (!Arrays.asList(registry.getURIs()).contains(uri)) {
                    registry.registerNamespace("lnt", uri);
                }
                // }
                this.area2facade.put(area, facade);
            } catch (javax.jcr.RepositoryException e) {
                throw new RepositoryException(e);
            }
        }

        return facade;
    }

    protected WorkspaceImpl getDefaultWorkspace() throws LoginException, RepositoryException {
        WorkspaceImpl defaultWorkspace;
        try {
            Session defaultWorkspaceSession = ((JCRRepository) getRepository()).getRepository()
                    .login(new SimpleCredentials("john", "".toCharArray()));
            defaultWorkspace = (WorkspaceImpl) defaultWorkspaceSession.getWorkspace();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }

        return defaultWorkspace;
    }

    public void save() throws org.apache.lenya.cms.repo.RepositoryException {
        try {
            for (Iterator i = this.area2facade.keySet().iterator(); i.hasNext();) {
                String area = (String) i.next();
                RepositoryFacade facade = (RepositoryFacade) this.area2facade.get(area);
                facade.getSession().save();
            }
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
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

                    Session session = getRepositoryFacade(workspaces[i]).getSession();
                    Node rootNode = session.getRootNode();

                    for (NodeIterator pubNodes = rootNode.getNodes(AreaProxy.NODE_NAME); pubNodes.hasNext();) {
                        Node node = pubNodes.nextNode();
                        String pubId = node.getProperty(AreaProxy.ID_PROPERTY).getString();
                        Publication pub = (Publication) this.publications.get(pubId);
                        if (pub == null) {
                            pub = new JCRPublication(this, pubId);
                            this.publications.put(pubId, pub);
                        }
                        pubIds.add(pubId);
                    }
                }

            } catch (javax.jcr.RepositoryException e) {
                throw new RepositoryException(e);
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
                    + "]�already exists.");
        } else {
            Publication pub = new JCRPublication(this, id);
            this.publications.put(id, pub);
            return pub;
        }
    }

    protected AreaProxy addArea(JCRPublication publication, String area)
            throws org.apache.lenya.cms.repo.RepositoryException {

        RepositoryFacade facade = getRepositoryFacade(area);
        return (AreaProxy) facade.addByProperty(new Path(),
                AreaProxy.NODE_TYPE,
                AreaProxy.class.getName(),
                AreaProxy.NODE_NAME,
                AreaProxy.ID_PROPERTY,
                publication.getPublicationId());
    }

    protected AreaProxy[] getAreas(Publication publication) throws RepositoryException {

        Set areas = new HashSet();
        String[] workspaces;
        try {
            workspaces = getDefaultWorkspace().getAccessibleWorkspaceNames();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
        for (int i = 0; i < workspaces.length; i++) {
            RepositoryFacade facade = getRepositoryFacade(workspaces[i]);
            Path path = AreaProxy.getPath(publication.getPublicationId());
            if (facade.containsProxy(path)) {
                AreaProxy area = (AreaProxy) facade.getProxy(path);
                areas.add(area);
            }
        }
        return (AreaProxy[]) areas.toArray(new AreaProxy[areas.size()]);
    }

    protected AreaProxy getArea(Publication publication, String area) throws RepositoryException {
        RepositoryFacade facade = getRepositoryFacade(area);
        Path path = AreaProxy.getPath(publication.getPublicationId());
        return (AreaProxy) facade.getProxy(path);
    }

    protected boolean existsArea(Publication publication, String area)
            throws org.apache.lenya.cms.repo.RepositoryException {
        RepositoryFacade facade = getRepositoryFacade(area);
        Path path = AreaProxy.getPath(publication.getPublicationId());
        return facade.containsProxy(path);
    }

    public boolean existsPublication(String id)
            throws org.apache.lenya.cms.repo.RepositoryException {
        initPublications();
        return this.publications.containsKey(id);
    }

}