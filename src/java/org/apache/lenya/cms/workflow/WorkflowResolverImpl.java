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
package org.apache.lenya.cms.workflow;

import java.io.File;
import java.util.Map;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuildException;
import org.apache.lenya.cms.publication.DocumentTypeResolver;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.util.LanguageVersions;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.impl.SynchronizedWorkflowInstancesImpl;
import org.apache.lenya.workflow.impl.WorkflowBuilder;
import org.apache.lenya.workflow.impl.WorkflowImpl;

/**
 * Default implementation of the workflow resolver component.
 * 
 * @version $Id:$
 */
public class WorkflowResolverImpl extends AbstractLogEnabled implements WorkflowResolver,
        Contextualizable, Serviceable {

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowResolver#getWorkflowInstance(org.apache.lenya.cms.publication.Document)
     */
    public WorkflowInstance getWorkflowInstance(Document document) throws WorkflowException {
        WorkflowInstance instance = new WorkflowDocument(buildWorkflow(document), document);
        ContainerUtil.enableLogging(instance, getLogger());
        return instance;
    }

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowResolver#getSituation()
     */
    public Situation getSituation() {
        Request request = ObjectModelHelper.getRequest(this.objectModel);
        Session session = request.getSession(false);

        Situation situation = null;
        if (session != null) {
            Identity identity = (Identity) session.getAttribute(Identity.class.getName());

            User user = identity.getUser();
            String userId = null;
            if (user != null) {
                userId = user.getId();
            }

            Machine machine = identity.getMachine();
            String machineIp = null;
            if (machine != null) {
                machineIp = machine.getIp();
            }

            Role[] roles;
            try {
                roles = PolicyAuthorizer.getRoles(request);
            } catch (AccessControlException e) {
                throw new RuntimeException(e);
            }
            String[] roleIds = new String[roles.length];
            for (int i = 0; i < roles.length; i++) {
                roleIds[i] = roles[i].getId();
            }

            situation = new CMSSituation(roleIds, userId, machineIp);
        }
        return situation;
    }

    private Map objectModel;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.objectModel = ContextHelper.getObjectModel(context);
    }

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowResolver#hasWorkflow(org.apache.lenya.cms.publication.Document)
     */
    public boolean hasWorkflow(Document document) {
        try {
            return getWorkflowInstance(document).getHistory().isInitialized();
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    protected static final String WORKFLOW_DIRECTORY = "config/workflow".replace('/',
            File.separatorChar);

    /**
     * Builds a workflow for a given document.
     * @param document The document.
     * @return A workflow object or <code>null</code> if there is no workflow
     *         assigned to the document's document type.
     */
    protected WorkflowImpl buildWorkflow(Document document) {

        DocumentTypeResolver doctypeResolver = null;
        WorkflowImpl workflow = null;

        try {
            doctypeResolver = (DocumentTypeResolver) this.manager.lookup(DocumentTypeResolver.ROLE);
            DocumentType doctype = doctypeResolver.resolve(document);

            if (doctype.hasWorkflow()) {
                String workflowFileName = doctype.getWorkflowFileName();

                PublicationFactory factory = PublicationFactory.getInstance(getLogger());
                Publication publication = factory.getPublication(this.objectModel);

                File workflowDirectory = new File(publication.getDirectory(), WORKFLOW_DIRECTORY);
                File workflowFile = new File(workflowDirectory, workflowFileName);
                WorkflowBuilder builder = new WorkflowBuilder(getLogger());
                workflow = builder.buildWorkflow(workflowFileName, workflowFile);
            }
        } catch (final ServiceException e) {
            throw new RuntimeException(e);
        } catch (final DocumentTypeBuildException e) {
            throw new RuntimeException(e);
        } catch (final PublicationException e) {
            throw new RuntimeException(e);
        } catch (final WorkflowException e) {
            throw new RuntimeException(e);
        } finally {
            if (doctypeResolver != null) {
                this.manager.release(doctypeResolver);
            }
        }

        return workflow;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowResolver#getSynchronizedInstance(org.apache.lenya.cms.publication.Document)
     */
    public SynchronizedWorkflowInstances getSynchronizedInstance(Document document)
            throws WorkflowException {
        assert document != null;
        LanguageVersions versions;
        try {
            versions = new LanguageVersions(document);
        } catch (DocumentException e) {
            throw new WorkflowException(e);
        }

        Document[] documents = versions.getDocuments();
        WorkflowInstance[] instances = new WorkflowInstance[documents.length];
        for (int i = 0; i < documents.length; i++) {
            instances[i] = getWorkflowInstance(documents[i]);
        }

        SynchronizedWorkflowInstances set = new SynchronizedWorkflowInstancesImpl(instances,
                getWorkflowInstance(document));
        ContainerUtil.enableLogging(set, getLogger());
        return set;
    }
}