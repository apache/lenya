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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeResolver;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * Workflow manager implementation.
 * 
 * @version $Id:$
 */
public class WorkflowManagerImpl extends AbstractLogEnabled implements WorkflowManager, Serviceable {

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowManager#invoke(org.apache.lenya.cms.publication.Document,
     *      java.lang.String, boolean)
     */
    public void invoke(Document document, String event, boolean force) throws WorkflowException {
        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            if (resolver.hasWorkflow(document)) {
                WorkflowInstance instance = resolver.getWorkflowInstance(document);
                Situation situation = resolver.getSituation();
                if (force && !instance.canInvoke(situation, event)) {
                    throw new WorkflowException("The event [" + event
                            + "] cannot be invoked on the document [" + document
                            + "] in the situation [" + situation + "]");
                }
                instance.invoke(situation, event);
            }
        } catch (ServiceException e) {
            throw new WorkflowException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowManager#invoke(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public void invoke(Document document, String event) throws WorkflowException {
        invoke(document, event, true);
    }

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowManager#invokeOnAll(org.apache.lenya.cms.publication.util.DocumentSet,
     *      java.lang.String, boolean)
     */
    public void invokeOnAll(DocumentSet documentSet, String event, boolean force)
            throws WorkflowException {
        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            Document[] documents = documentSet.getDocuments();
            for (int i = 0; i < documents.length; i++) {
                invoke(documents[i], event, force);
            }
        } catch (ServiceException e) {
            throw new WorkflowException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowManager#canInvoke(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public boolean canInvoke(Document document, String event) {
        WorkflowResolver resolver = null;
        boolean canInvoke = true;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            if (resolver.hasWorkflow(document)) {
                WorkflowInstance instance = resolver.getWorkflowInstance(document);
                Situation situation = resolver.getSituation();
                canInvoke = instance.canInvoke(situation, event);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
        return canInvoke;
    }

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowManager#canInvokeOnAll(org.apache.lenya.cms.publication.util.DocumentSet,
     *      java.lang.String)
     */
    public boolean canInvokeOnAll(DocumentSet documents, String event) {
        WorkflowResolver resolver = null;
        boolean canInvoke = true;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            Document[] documentArray = documents.getDocuments();
            for (int i = 0; i < documentArray.length; i++) {
                canInvoke = canInvoke && canInvoke(documentArray[i], event);
            }
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
        return canInvoke;
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * @throws WorkflowException
     * @see org.apache.lenya.cms.workflow.WorkflowManager#copyHistory(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copyHistory(Document source, Document target) throws WorkflowException {

        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            if (resolver.hasWorkflow(source)) {
                WorkflowInstance sourceInstance = resolver.getWorkflowInstance(source);
                WorkflowInstance destinationInstance = resolver.getWorkflowInstance(target);
                destinationInstance.getHistory().replaceWith(sourceInstance.getHistory());
            }
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowManager#moveHistory(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void moveHistory(Document sourceDocument, Document destinationDocument)
            throws WorkflowException {
        copyHistory(sourceDocument, destinationDocument);
        deleteHistory(sourceDocument);
    }

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowManager#deleteHistory(org.apache.lenya.cms.publication.Document)
     */
    public void deleteHistory(Document sourceDocument) throws WorkflowException {
        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            if (resolver.hasWorkflow(sourceDocument)) {
                WorkflowInstance sourceInstance = resolver.getWorkflowInstance(sourceDocument);
                sourceInstance.getHistory().delete();
            }
        } catch (ServiceException e) {
            throw new WorkflowException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowManager#initializeHistory(org.apache.lenya.cms.publication.Document)
     */
    public void initializeHistory(Document document) throws WorkflowException {
        DocumentTypeResolver doctypeResolver = null;
        WorkflowResolver workflowResolver = null;
        try {
            doctypeResolver = (DocumentTypeResolver) this.manager.lookup(DocumentTypeResolver.ROLE);
            workflowResolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);

            DocumentType doctype = doctypeResolver.resolve(document);

            if (doctype.hasWorkflow()) {
                Situation situation = workflowResolver.getSituation();
                workflowResolver.getWorkflowInstance(document).getHistory().initialize(situation);
            }

        } catch (ServiceException e) {
            throw new WorkflowException(e);
        } finally {
            if (doctypeResolver != null) {
                this.manager.release(doctypeResolver);
            }
        }
    }

}