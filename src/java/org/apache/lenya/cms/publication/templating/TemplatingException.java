/*
 * Created on 12.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.publication.templating;

/**
 * @author nobby
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TemplatingException extends RuntimeException {

    /**
     * Ctor.
     */
    public TemplatingException() {
        super();
    }
    
    /**
     * Ctor.
     * @param message The message.
     */
    public TemplatingException(String message) {
        super(message);
    }
    
    /**
     * Ctor.
     * @param message The message.
     * @param cause The cause.
     */
    public TemplatingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Ctor.
     * @param cause The cause.
     */
    public TemplatingException(Throwable cause) {
        super(cause);
    }
    
}
