/*
 * BooleanVariableInstanceImpl.java
 *
 * Created on 27. Mai 2003, 12:37
 */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.BooleanVariableInstance;

/**
 *
 * @author  andreas
 */
public class BooleanVariableInstanceImpl implements BooleanVariableInstance {

    private boolean value;

    /** Creates a new instance of BooleanVariableInstanceImpl */
    protected BooleanVariableInstanceImpl() {
    }

    /**
     * @see org.apache.lenya.cms.workflow.BooleanVariableInstance#getValue()
     */
    public boolean getValue() {
        return value;
    }

    /**
     * @see org.apache.lenya.cms.workflow.BooleanVariableInstance#setValue(boolean)
     */
    public void setValue(boolean value) {
        this.value = value;
    }

}
