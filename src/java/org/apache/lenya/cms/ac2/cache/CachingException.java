/*
 * Created on Aug 13, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.lenya.cms.ac2.cache;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CachingException extends Exception {

    /**
     * Ctor.
     */
    public CachingException() {
        super();
    }

    /**
     * Ctor.
     * @param message The message.
     */
    public CachingException(String message) {
        super(message);
    }

    /**
     * Ctor.
     * @param message The message.
     * @param cause The cause.
     */
    public CachingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Ctor.
     * @param cause The cause.
     */
    public CachingException(Throwable cause) {
        super(cause);
    }

}
