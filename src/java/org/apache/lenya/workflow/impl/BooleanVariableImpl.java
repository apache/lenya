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
    protected BooleanVariableImpl(String name) {
    	assert name != null;
    	this.name = name;
    }

    private String name;

    /**
     * @see org.apache.lenya.cms.workflow.BooleanVariable#getName()
     */
    public String getName() {
        return name;
    }

}
