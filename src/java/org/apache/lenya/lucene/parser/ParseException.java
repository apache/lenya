/*
 * ParseException.java
 *
 * Created on 21. März 2003, 11:16
 */

package org.apache.lenya.lucene.parser;

/**
 *
 * @author  hrt
 */
public class ParseException
        extends Exception {
    
    /** Creates a new instance of ParseException */
    public ParseException() {
    }
    
    /**
     * Creates a new ParseException object.
     *
     * @param message the error message
     * @param cause the cause of the exception
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new ParseException object.
     *
     * @param message the error message
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Creates a new ParseException object.
     *
     * @param cause the cause of the exception
     */
    public ParseException(Throwable cause) {
        super(cause);
    }

}
