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
package org.apache.lenya.workflow;

/**
 * Workflow history.
 *
 * @version $Id:$
 */
public interface History {
    
    /**
     * @return The workflow instance this history belongs to.
     */
    WorkflowInstance getInstance();

    /**
     * Initializes the workflow history.
     * @param situation The situation.
     * @throws WorkflowException if an error occurs.
     */
    void initialize(Situation situation) throws WorkflowException;
    
    /**
     * Replace this workflow history with another history.
     * @param sourceHistory The source history.
     * @throws WorkflowException if an error occurs.
     */
    void replaceWith(History sourceHistory) throws WorkflowException;
    
    /**
     * @return If the workflow history is initialized.
     * @throws WorkflowException if an error occurs.
     */
    boolean isInitialized() throws WorkflowException;
    
    /**
     * Deletes the workflow history.
     * @throws WorkflowException if an error occurs.
     */
    void delete() throws WorkflowException;
    
    /**
     * @return The last state of the history.
     * @throws WorkflowException if the history is not yet initialized.
     */
    State getLastState() throws WorkflowException;
    
}
