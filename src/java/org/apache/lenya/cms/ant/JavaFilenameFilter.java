package org.lenya.cms.ant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

/**
 * @author Michael Wechner
 */
public class JavaFilenameFilter implements FilenameFilter {

    /**
     *
     */
    public boolean accept(File dir, String name) {
        if (new File(dir, name).isFile()) {
            //System.out.println("JavaFilenameFilter.accept(): " + getExtension(name));
            if (getExtension(name).equals("java")) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     *
     */
    static public String getExtension(String filename) {
        StringTokenizer st = new StringTokenizer(filename,".");
        st.nextToken();
        String extension="";
        while (st.hasMoreTokens()) {
            extension = st.nextToken();
        }
        return extension;
    }
}
