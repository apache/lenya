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

/* $Id: SynchronizedWorkflowInstances.java,v 1.3 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.workflow;


/**
 * <p>
 * Synchronized workflow instances.
 * </p>
 * 
 * <p>
 * A set of workflow instances with the same workflow schema can be synchronized.
 * If a transition in this schema is marked as synchronized, it can only be invoked
 * on all instances in the set at the same time.
 * </p>
 *
 * <p>
 * When a workflow event is invoked on a set of synchronized workflow instances,
 * the transition is invoked only if
 * </p>
 * <ul>
 * <li>all instances are in the source state of the transition, and</li>
 * <li>all conditions of the transition are complied for all instances.</li>
 * </ul>
 * 
 * <p>
 * Then the transition is invoked for all instances in the set.
 * </p>
 * <p>
 * A common usecase of this concept is the simultaneous publishing of
 * a set of documents (all language versions of a document, a section, ...).
 * </p>
 */
public interface SynchronizedWorkflowInstances {
    
    /**
     * Returns all executable events.
     * @see org.apache.lenya.workflow.WorkflowInstance#getExecutableEvents(org.apache.lenya.workflow.Situation)
     */
    Event[] getExecutableEvents(Situation situation) throws WorkflowException;
    
    /**
     * Invokes an event on all documents.
     * @see org.apache.lenya.workflow.WorkflowInstance#invoke(org.apache.lenya.workflow.Situation, org.apache.lenya.workflow.Event)
     */
    void invoke(Situation situation, Event event) throws WorkflowException;
}