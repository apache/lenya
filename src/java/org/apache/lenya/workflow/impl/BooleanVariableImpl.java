/*
 * BooleanVariableImpl.java
 *
 * Created on 27. Mai 2003, 12:37
 */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.BooleanVariable;

/**
 *
 * @author  andreas
 */
public class BooleanVariableImpl implements BooleanVariable {

    /** Creates a new instance of BooleanVariableImpl */
    protected BooleanVariableImpl(String variableName, boolean initialValue) {
    	assert variableName != null;
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

    /* (non-Javadoc)
     * @see org.apache.lenya.workflow.BooleanVariable#getInitialValue()
     */
    public boolean getInitialValue() {
        return initialValue;
    }

}
