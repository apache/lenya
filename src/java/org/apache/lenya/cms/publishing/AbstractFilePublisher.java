package org.wyona.cms.publishing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Describe class <code>AbstractFilePublisher</code> here.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.com">Andreas Hartmann</a>
 */
public abstract class AbstractFilePublisher
    extends AbstractPublisher {

    /**
     * Utility function to copy a source file to destination
     *
     * @param source a <code>File</code> value
     * @param destination a <code>File</code> value
     * @return true if the copy was succesfull, false otherwise
     * @exception IOException if an error occurs
     */
    protected void copyFile(File source, File destination)
	throws IOException, FileNotFoundException {
	
	if (!source.exists()) {
 	    throw new FileNotFoundException();
	}
	    
	File parentDestination = new File(destination.getParent());
	if (!parentDestination.exists()) {
	    parentDestination.mkdirs();
	}
	org.apache.avalon.excalibur.io.FileUtil.copyFile(source,destination);
    }
    
}

