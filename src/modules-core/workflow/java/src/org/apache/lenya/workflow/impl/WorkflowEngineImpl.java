/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.workflow.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.workflow.Action;
import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.Transition;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowEngine;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.Workflowable;

/**
 * Workflow engine implementation.
 * 
 * @version $Id$
 */
public class WorkflowEngineImpl implements WorkflowEngine {

    /**
     * @see org.apache.lenya.workflow.WorkflowEngine#canInvoke(org.apache.lenya.workflow.Workflowable,
     *      org.apache.lenya.workflow.Workflow, java.lang.String)
     */
    public boolean canInvoke(Workflowable workflowable, Workflow workflow, String event)
            throws WorkflowException {
        List firingTransitions = getFiringTransitions(workflowable, workflow, event);
        return firingTransitions.size() == 1;
    }

    /**
     * @see org.apache.lenya.workflow.WorkflowEngine#invoke(org.apache.lenya.workflow.Workflowable,
     *      org.apache.lenya.workflow.Workflow, java.lang.String)
     */
    public void invoke(Workflowable workflowable, Workflow workflow, String event)
            throws WorkflowException {

        Transition firingTransition = null;
        List firingTransitions = getFiringTransitions(workflowable, workflow, event);

        if (firingTransitions.size() == 0) {
            throw new WorkflowException("No transition can fire!");
        } else if (firingTransitions.size() > 1) {
            throw new WorkflowException("More than one transitions can fire!");
        } else {
            firingTransition = (Transition) firingTransitions.get(0);
        }

        String destination = firingTransition.getDestination();

        Version newVersion = createNewVersion(workflowable, workflow, event, destination);

        Action[] actions = firingTransition.getActions();
        for (int i = 0; i < actions.length; i++) {
            actions[i].execute(newVersion);
        }

        workflowable.newVersion(workflow, newVersion);
    }

    /**
     * Creates a new version.
     * @param workflowable The workflowable.
     * @param workflow The workflow.
     * @param event The event.
     * @param destination The destination.
     * @return A version.
     * @throws WorkflowException if an error occurs.
     */
    protected Version createNewVersion(Workflowable workflowable, Workflow workflow, String event,
            String destination) throws WorkflowException {
        Version latestVersion = workflowable.getLatestVersion();
        Version newVersion = new VersionImpl(event, destination);
        String[] variableNames = workflow.getVariableNames();
        for (int i = 0; i < variableNames.length; i++) {
            String name = variableNames[i];
            boolean value;
            if (latestVersion == null) {
                value = workflow.getInitialValue(name);
            } else {
                value = latestVersion.getValue(name);
            }
            newVersion.setValue(name, value);
        }
        return newVersion;
    }

    /**
     * Returns the transitions that would fire in a certain situation.
     * @param workflowable The workflowable.
     * @param workflow The workflow.
     * @param event The event.
     * @return A list of transitions.
     * @throws WorkflowException if an error occurs.
     */
    protected List getFiringTransitions(Workflowable workflowable, Workflow workflow, String event)
            throws WorkflowException {
        Version lastVersion = workflowable.getLatestVersion();

        String currentState;
        if (lastVersion == null) {
            currentState = workflow.getInitialState();
        } else {
            currentState = lastVersion.getState();
        }

        Transition[] transitions = workflow.getLeavingTransitions(currentState);
        List firingTransitions = new ArrayList();

        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].getEvent().equals(event)
                    && canFire(transitions[i], workflow, workflowable)) {
                firingTransitions.add(transitions[i]);
            }
        }
        return firingTransitions;
    }

    /**
     * Checks if a transition can fire.
     * @param transition The transition.
     * @param workflow The workflow.
     * @param workflowable The workflowable.
     * @return A boolean value.
     * @throws WorkflowException if an error occurs.
     */
    public boolean canFire(Transition transition, Workflow workflow, Workflowable workflowable)
            throws WorkflowException {
        Condition[] _conditions = transition.getConditions();
        boolean canFire = true;

        int i = 0;
        while (canFire && i < _conditions.length) {
            canFire = canFire && _conditions[i].isComplied(workflow, workflowable);
            i++;
        }

        return canFire;
    }

}