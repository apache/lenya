/*
 * CommandLineLogger.java
 *
 * Created on 26. März 2003, 14:05
 */

package org.lenya.util;


/**
 *
 * @author  hrt
 */
public class CommandLineLogger {
    
    /** Creates a new instance of CommandLineLogger */
    public CommandLineLogger(Class clazz) {
        className = clazz.getName();
    }
    
    private String className;
    
    protected String getClassName() {
        return className;
    }
    
    /**
     * Logs a message if debugging is enabled.
     */
    public void debug(String message) {
        if (DebugConfiguration.isDebug()) {
            log(message);
        }
    }
    
    /**
     * Logs a message.
     */
    public void log(String message) {
        System.out.println(getClassName() + ": " + message);
    }
    
    /**
     * Logs an exception.
     */
    public void log(Exception e) {
        log("", e);
    }
    
    /**
     * Logs an exception with a message.
     */
    public void log(String message, Exception e) {
        log(message + " ");
        e.printStackTrace(System.out);
    }
    
}
