/*
 * Created on 03.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.usecase;

import java.util.Map;

import org.apache.cocoon.components.ContextHelper;
import org.apache.lenya.cms.cocoon.workflow.WorkflowHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * @author nobby
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkflowUsecase extends AbstractUsecase {

    private Situation situation;

    /**
     * Returns the workflow situation.
     * @return A situation.
     */
    protected Situation getSituation() {
        return this.situation;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
     */
    protected void doInitialize() {
        super.doInitialize();
        Map objectModel = ContextHelper.getObjectModel(getContext());
        try {
            this.situation = WorkflowHelper.buildSituation(objectModel);
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Triggers a workflow event on a document.
     * @param event The event.
     * @param document The document.
     */
    protected void triggerWorkflow(String event, Document document) {
        WorkflowFactory factory = WorkflowFactory.newInstance();
        try {
            WorkflowInstance instance = factory.buildInstance(document);
            Event[] events = instance.getExecutableEvents(getSituation());
            Event executableEvent = null;
            for (int i = 0; i < events.length; i++) {
                if (events[i].getName().equals(event)) {
                    executableEvent = events[i];
                }
            }

            if (executableEvent == null) {
                throw new RuntimeException("The event [" + event
                        + "] is not executable on document [" + document + "]");
            }
            instance.invoke(getSituation(), executableEvent);
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

}
