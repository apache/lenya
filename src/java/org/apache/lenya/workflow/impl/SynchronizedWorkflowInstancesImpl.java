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

/* $Id: SynchronizedWorkflowInstancesImpl.java,v 1.5 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.workflow.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.log4j.Category;

/**
 * An object of this class encapsulates a set of synchronized
 * workflow instances.
 */
public class SynchronizedWorkflowInstancesImpl implements SynchronizedWorkflowInstances {

    private static final Category log = Category.getInstance(SynchronizedWorkflowInstancesImpl.class);

    /**
     * Ctor.
     */
    public SynchronizedWorkflowInstancesImpl() {
    }

    /**
     * Ctor.
     * @param instances The set of workflow instances to synchronize.
     * @param mainInstance The main workflow instance to invoke for non-synchronized transitions.
     */
    public SynchronizedWorkflowInstancesImpl(
        WorkflowInstance[] instances,
        WorkflowInstance mainInstance) {
        setInstances(instances);
        setMainInstance(mainInstance);
    }

    /**
     * Sets the main workflow instance.
     * @param mainInstance The main workflow instance to invoke for non-synchronized transitions.
     */
    public void setMainInstance(WorkflowInstance mainInstance) {
        this.mainInstance = mainInstance;
    }

    private WorkflowInstance[] instances;
    private WorkflowInstance mainInstance;

    public void setInstances(WorkflowInstance[] instances) {
        this.instances = instances;
    }

    public WorkflowInstance[] getInstances() {
        return instances;
    }

    /**
     * Returns all executable events.
     * @see org.apache.lenya.workflow.WorkflowInstance#getExecutableEvents(org.apache.lenya.workflow.Situation)
     */
    public Event[] getExecutableEvents(Situation situation) throws WorkflowException {
        if (log.isDebugEnabled()) {
            log.debug("Resolving executable events");
        }

        WorkflowInstance[] instances = getInstances();
        if (instances.length == 0) {
            throw new WorkflowException("The set must contain at least one workflow instance!");
        }

        Event[] events = mainInstance.getExecutableEvents(situation);
        Set executableEvents = new HashSet(Arrays.asList(events));

        for (int i = 0; i < events.length; i++) {
            Event event = events[i];
            if (mainInstance.isSynchronized(event)) {

                boolean canFire = true;
                if (log.isDebugEnabled()) {
                    log.debug("    Transition for event [" + event + "] is synchronized.");
                }

                boolean sameState = true;
                State currentState = mainInstance.getCurrentState();
                int j = 0;
                while (j < instances.length && sameState) {
                    sameState = instances[j].getCurrentState().equals(currentState);
                    j++;
                }
                if (log.isDebugEnabled()) {
                    log.debug("    All instances are in the same state: [" + sameState + "]");
                }

                if (sameState) {
                    for (int k = 0; k < instances.length; k++) {
                        WorkflowInstanceImpl instance = (WorkflowInstanceImpl) instances[k];
                        if (instance != mainInstance && !instance.getNextTransition(event).canFire(situation, instance)) {
                            canFire = false;
                            if (log.isDebugEnabled()) {
                                log.debug("    Workflow instance [" + instance + "] can not fire.");
                            }
                        }
                    }
                } else {
                    canFire = false;
                }

                if (!canFire) {
                    executableEvents.remove(event);
                    if (log.isDebugEnabled()) {
                        log.debug("    Event [" + event + "] can not fire - removing from executable events.");
                    }
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("    Resolving executable events completed.");
        }

        return (Event[]) executableEvents.toArray(new Event[executableEvents.size()]);
    }

    /**
     * Invokes an event on all documents.
     * @see org.apache.lenya.workflow.WorkflowInstance#invoke(org.apache.lenya.workflow.Situation, org.apache.lenya.workflow.Event)
     */
    public void invoke(Situation situation, Event event) throws WorkflowException {
        
        if (mainInstance.isSynchronized(event)) {
            for (int i = 0; i < instances.length; i++) {
                instances[i].invoke(situation, event);
            }
        }
        else {
            mainInstance.invoke(situation, event);
        }
    }

}
