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

/* $Id: WorkflowInstance.java,v 1.10 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.workflow;

/**
 * <p>A workflow instance is an incarnation of a workflow schema. It consists of</p>
 * <ul>
 * <li>a current state,</li>
 * <li>a mapping which assigns values to all state variables.</li>
 * </ul>
 */
public interface WorkflowInstance {
    /**
     * Returns the workflow this instance belongs to.
     * @return A Workflow object.
     */
    Workflow getWorkflow();

    /**
     * Returns the current state of this WorkflowInstance.
     * 
     * @return the current state
     */
    State getCurrentState();

    /**
     * Returns the executable events in a certain situation.
     * @param situation The situation.
     * @return An array of events.
     * @throws WorkflowException when something went wrong.
     */
    Event[] getExecutableEvents(Situation situation) throws WorkflowException;

    /**
     * Indicates that the user invoked an event.
     * 
     * @param situation The situation in which the event was invoked.
     * @param event The event that was invoked.
     * @throws WorkflowException when something went wrong.
     */
    void invoke(Situation situation, Event event) throws WorkflowException;

    /**
     * Returns the current value of a variable.
     * @param variableName A variable name.
     * @return A boolean value.
     * @throws WorkflowException when the variable does not exist.
     */
    boolean getValue(String variableName) throws WorkflowException;

    /**
     * Adds a workflow listener.
     * @param listener The listener to add.
     */
    void addWorkflowListener(WorkflowListener listener);

    /**
     * Removes a workflow listener.
     * @param listener The listener to remove.
     */
    void removeWorkflowListener(WorkflowListener listener);

    /**
     * Returns if the transition for a certain event is synchronized.
     * @param event An event.
     * @return A boolean value.
     */
    boolean isSynchronized(Event event) throws WorkflowException;
}
