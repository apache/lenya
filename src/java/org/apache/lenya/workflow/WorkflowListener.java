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

/* $Id: WorkflowListener.java,v 1.6 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.workflow;


/**
 * Workflow listener interface.
 */
public interface WorkflowListener {
    
    /**
     * This method is invoked when a transition has fired.
     * 
     * @param instance The workflow instance.
     * @param situation The situation before the transition has fired.
     * @param event The event that was invoked.
     * 
     * @throws WorkflowException if an error occured
     */
    void transitionFired(WorkflowInstance instance, Situation situation, Event event)
        throws WorkflowException;
}
