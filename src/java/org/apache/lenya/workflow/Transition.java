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

/* $Id$  */

package org.apache.lenya.workflow;


/**
 * <p>
 * A transition describes the switching of a workflow instance
 * from one state to another. A transition has
 * </p>
 * 
 * <ul>
 * <li>a source state,</li>
 * <li>a destination state,</li>
 * <li>an event,</li>
 * <li>a set of conditions,</li>
 * <li>a set of assignments.</li>
 * </ul>
 * 
 * <p>
 * Additionally, a transition can be marked as synchronized.
 * </p>
 */
public interface Transition {
	
    /**
     * Returns the event of this transition.
     * @return the event
     */
    String getEvent();
    
    /**
     * @return The source state.
     */
    String getSource();
    
    /**
     * @return The destination state.
     */
    String getDestination();

    /**
     * Returns the actions of this transition.
     * @return the actions
     */
    Action[] getActions();
    
    /**
     * @return The conditions.
     */
    Condition[] getConditions();

    /**
     * Returns if this transition is synchronized.
     * @return A boolean value.
     */
    boolean isSynchronized();
}
