/*
 * DebugConfiguration.java
 *
 * Created on 27. März 2003, 16:20
 */

package org.lenya.util;

/**
 *
 * @author  hrt
 */
public class DebugConfiguration {

    private static boolean isDebug = false;
    
    public static void setDebug(boolean debug) {
        isDebug = debug;
    }
    
    public static boolean isDebug() {
        return isDebug;
    }
    
}
