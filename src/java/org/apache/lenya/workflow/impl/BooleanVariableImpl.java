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

/* $Id: BooleanVariableImpl.java,v 1.9 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.BooleanVariable;


/**
 * Implementation of a boolean variable.
 */
public class BooleanVariableImpl implements BooleanVariable {
    
    /**
     * Creates a new instance of BooleanVariableImpl.
     * @param variableName The variable name.
     * @param initialValue The initial value of the corresponding variable instances.
     */
    protected BooleanVariableImpl(String variableName, boolean initialValue) {
        name = variableName;

        this.initialValue = initialValue;
    }

    private String name;

    /**
     * @see org.apache.lenya.cms.workflow.BooleanVariable#getName()
     */
    public String getName() {
        return name;
    }

    private boolean initialValue;

    /**
     * @see org.apache.lenya.workflow.BooleanVariable#getInitialValue()
     */
    public boolean getInitialValue() {
        return initialValue;
    }
}
