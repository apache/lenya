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

/* $Id$  */

package org.apache.lenya.workflow.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.State;
import org.apache.lenya.workflow.SynchronizedWorkflowInstances;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * An object of this class encapsulates a set of synchronized workflow
 * instances.
 */
public class SynchronizedWorkflowInstancesImpl extends AbstractLogEnabled implements
        SynchronizedWorkflowInstances {

    /**
     * Ctor.
     */
    public SynchronizedWorkflowInstancesImpl() {
    }

    /**
     * Ctor.
     * @param instances The set of workflow instances to synchronize.
     * @param mainInstance The main workflow instance to invoke for
     *            non-synchronized transitions.
     */
    public SynchronizedWorkflowInstancesImpl(WorkflowInstance[] instances,
            WorkflowInstance mainInstance) {
        setInstances(instances);
        setMainInstance(mainInstance);
    }

    /**
     * Sets the main workflow instance.
     * @param mainInstance The main workflow instance to invoke for
     *            non-synchronized transitions.
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
    public String[] getExecutableEvents(Situation situation) throws WorkflowException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving executable events");
        }

        WorkflowInstance[] instances = getInstances();
        if (instances.length == 0) {
            throw new WorkflowException("The set must contain at least one workflow instance!");
        }

        String[] events = mainInstance.getExecutableEvents(situation);
        Set executableEvents = new HashSet(Arrays.asList(events));

        for (int i = 0; i < events.length; i++) {
            String event = events[i];
            if (mainInstance.isSynchronized(event)) {

                boolean canFire = true;
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("    Transition for event [" + event + "] is synchronized.");
                }

                boolean sameState = true;
                State currentState = mainInstance.getCurrentState();
                int j = 0;
                while (j < instances.length && sameState) {
                    sameState = instances[j].getCurrentState().equals(currentState);
                    j++;
                }
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("    All instances are in the same state: [" + sameState + "]");
                }

                if (sameState) {
                    for (int k = 0; k < instances.length; k++) {
                        WorkflowInstanceImpl instance = (WorkflowInstanceImpl) instances[k];
                        if (instance != mainInstance
                                && !instance.getNextTransition(event).canFire(situation, instance)) {
                            canFire = false;
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("    Workflow instance [" + instance + "] can not fire.");
                            }
                        }
                    }
                } else {
                    canFire = false;
                }

                if (!canFire) {
                    executableEvents.remove(event);
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("    Event [" + event
                                + "] can not fire - removing from executable events.");
                    }
                }
            }
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("    Resolving executable events completed.");
        }

        return (String[]) executableEvents.toArray(new String[executableEvents.size()]);
    }

    /**
     * Invokes an event on all documents.
     * @see org.apache.lenya.workflow.WorkflowInstance#invoke(org.apache.lenya.workflow.Situation,
     *      java.lang.String)
     */
    public void invoke(Situation situation, String event) throws WorkflowException {

        if (this.mainInstance.isSynchronized(event)) {
            for (int i = 0; i < this.instances.length; i++) {
                this.instances[i].invoke(situation, event);
            }
        } else {
            this.mainInstance.invoke(situation, event);
        }
    }

}