package org.wyona.cms.authoring;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Describe class <code>AbstractFilePublisher</code> here.
 *
 * @author <a href="mailto:christian.egli@wyona.com">Christian Egli</a>
 */
public abstract class AbstractFilePublisher extends AbstractPublisher {

    protected String absoluteAuthoringPath;
    protected String absoluteLivePath;
    protected String absoluteTreeAuthoringPath;
    protected String absoluteTreeLivePath;
    protected String docIds;

    public AbstractFilePublisher(String absoluteAuthoringPath,
				 String absoluteLivePath,
				 String absoluteTreeAuthoringPath,
				 String absoluteTreeLivePath,
				 String docIds) {
	this.absoluteAuthoringPath = absoluteAuthoringPath;
	this.absoluteLivePath = absoluteLivePath;
	this.absoluteTreeAuthoringPath = absoluteTreeAuthoringPath;
	this.absoluteTreeLivePath = absoluteTreeLivePath;
	this.docIds = docIds;
	    
    }
    
    /**
     * Describe <code>publish</code> method here.
     *
     */
    public abstract void publish();
    
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

