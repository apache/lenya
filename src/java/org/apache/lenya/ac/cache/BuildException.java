/*
 * Created on Aug 13, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.lenya.ac.cache;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BuildException extends Exception {

    /**
     * Ctor.
     */
    public BuildException() {
        super();
    }

    /**
     * Ctor.
     * @param message The message.
     */
    public BuildException(String message) {
        super(message);
    }

    /**
     * Ctor.
     * @param message The message.
     * @param cause The cause.
     */
    public BuildException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Ctor.
     * @param cause The cause.
     */
    public BuildException(Throwable cause) {
        super(cause);
    }

}
