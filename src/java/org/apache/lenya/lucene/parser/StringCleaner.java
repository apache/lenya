/*
 * StringCleaner.java
 *
 * Created on 21. März 2003, 11:31
 */

package org.apache.lenya.lucene.parser;

/**
 * A utility class to convert a string to text that can be included in an XML file.
 *
 * @author  hrt
 */
public class StringCleaner {
    
    /** Creates a new instance of StringCleaner */
    public StringCleaner() {
    }
    
    /**
     * Remove all non-word characters from a string.
     */
    public static String clean(String source) {
        
        //String result = source.replaceAll("[^\\w\\t\\n\\r\\ ]", "");
        
        String result = "<![CDATA[" + source + "]]>";
        return result;
    }
    
}
