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

import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.DefaultDocument;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowEngine;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.impl.WorkflowEngineImpl;

/**
 * Workflow manager implementation.
 * 
 * @version $Id:$
 */
public class WorkflowManagerImpl extends AbstractLogEnabled implements WorkflowManager, Serviceable, Poolable {

    /**
     * @see org.apache.lenya.cms.workflow.WorkflowManager#invoke(org.apache.lenya.cms.publication.Document,
     *      java.lang.String, boolean)
     */
    public void invoke(Document document, String event, boolean force) throws WorkflowException {
        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            if (resolver.hasWorkflow(document)) {
                WorkflowEngine engine = new WorkflowEngineImpl();
                Situation situation = resolver.getSituation();
                Workflow workflow = resolver.getWorkflowSchema(document);
                if (force && !engine.canInvoke(document, workflow, situation, event)) {
                    throw new WorkflowException("The event [" + event
                            + "] cannot be invoked on the document [" + document
                            + "] in the situation [" + situation + "]");
                }
                engine.invoke(document, workflow, situation, event);
                
                ((DefaultDocument) document).getHistory().save();
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
     * @see org.apache.lenya.cms.workflow.WorkflowManager#invoke(org.apache.lenya.cms.publication.util.DocumentSet,
     *      java.lang.String, boolean)
     */
    public void invoke(DocumentSet documentSet, String event, boolean force)
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
                Workflow workflow = resolver.getWorkflowSchema(document);
                WorkflowEngine engine = new WorkflowEngineImpl();
                Situation situation = resolver.getSituation();
                canInvoke = engine.canInvoke(document, workflow, situation, event);
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
     * @see org.apache.lenya.cms.workflow.WorkflowManager#canInvoke(org.apache.lenya.cms.publication.util.DocumentSet,
     *      java.lang.String)
     */
    public boolean canInvoke(DocumentSet documents, String event) {
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
        SourceResolver sourceResolver = null;
        Source sourceHistory = null;
        Source targetHistory = null;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            if (resolver.hasWorkflow(source)) {
                
                sourceResolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                
                String sourceUri = ((DefaultDocument) source).getHistorySourceURI();
                sourceHistory = sourceResolver.resolveURI(sourceUri);
                
                if (sourceHistory.exists()) {
                    String targetUri = ((DefaultDocument) target).getHistorySourceURI();
                    targetHistory = sourceResolver.resolveURI(targetUri);
                    SourceUtil.copy(sourceHistory, (ModifiableSource) targetHistory, true);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
            if (sourceResolver != null) {
                if (sourceHistory != null) {
                    sourceResolver.release(sourceHistory);
                }
                if (targetHistory != null) {
                    sourceResolver.release(targetHistory);
                }
                this.manager.release(sourceResolver);
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
        SourceResolver sourceResolver = null;
        Source historySource = null;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            if (resolver.hasWorkflow(sourceDocument)) {
                sourceResolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                String uri = ((DefaultDocument) sourceDocument).getHistorySourceURI();
                historySource = sourceResolver.resolveURI(uri);
                ((ModifiableSource) historySource).delete();
            }
        } catch (Exception e) {
            throw new WorkflowException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
            if (sourceResolver != null) {
                if (historySource != null) {
                    sourceResolver.release(historySource);
                }
                this.manager.release(sourceResolver);
            }
        }
    }

}
