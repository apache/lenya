/*
 * PreParser.java
 *
 * Created on 30. März 2003, 13:37
 */

package org.lenya.lucene.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.lenya.util.CommandLineLogger;

/**
 * The Java HTML parser cannot handle self-closing text.
 * This class converts all "/>" strings to ">" to avoid this problem.
 *
 * @author  nobby
 */
public class PreParser {
    
    /** Creates a new instance of PreParser */
    public PreParser() {
        new CommandLineLogger(getClass()).log("creating new object");
    }

    /**
     * Parses HTML from a reader.
     */
    public Reader parse(Reader reader)
            throws IOException {
        
        StringBuffer buffer = new StringBuffer();
        boolean pending = false;
        
        char chars[] = new char[1];
        while (reader.read(chars) != -1) {
            
            int lastPosition = buffer.length() - 1;
            if (chars[0] == '>' && buffer.charAt(lastPosition) == '/') {
                buffer.deleteCharAt(lastPosition);
            }
            buffer.append(chars[0]);
        }
        
        return new StringReader(buffer.toString());
    }
    
}
