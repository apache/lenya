/*
 * BooleanVariableInstance.java
 *
 * Created on 27. Mai 2003, 12:36
 */

package org.apache.lenya.workflow;

/**
 *
 * @author  andreas
 */
public interface BooleanVariableInstance {

    /**
     * Sets the value of this variable.
     * @param value A boolean value.
     */
    void setValue(boolean value);

    /**
     * Returns the value of this variable.
     * @return A boolean value.
     */
    boolean getValue();
}
