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

/* $Id: BooleanVariable.java,v 1.7 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.workflow;


/**
 * <p>Boolean state variable.</p>
 * <p>
 * A workflow schema can contain a set of state variables.
 * For each instance, the state variables hold certain values.
 * Values can be assigned during transitions, so a variable can
 * change its value when a transition fires. Currently,
 * the workflow supports only boolean state variables.
 * </p>
 */
public interface BooleanVariable {
	
    /**
     * Returns the name of this variable.
     * @return the name
     */
    String getName();

    /**
     * Returns the initial value of this variable.
     * @return A boolean value.
     */
    boolean getInitialValue();
}
