package org.wyona.cms.authoring;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.log4j.Category;

/**
 * Describe class <code>DefaultFilePublisher</code> here.
 *
 * @author <a href="mailto:christian.egli@wyona.com">Christian Egli</a>
 */
public class DefaultFilePublisher extends AbstractFilePublisher {
    
    static Category log = Category.getInstance(DefaultFilePublisher.class);

    public DefaultFilePublisher(String absoluteAuthoringPath,
				String absoluteLivePath,
				String absoluteTreeAuthoringPath,
				String absoluteTreeLivePath,
				String docIds) {
	super(absoluteAuthoringPath, absoluteLivePath,
	      absoluteTreeAuthoringPath, absoluteTreeLivePath, docIds);
    }
    
    /**
     * Default implementation of <code>publish</code> which simply
     * copies the files from the absoluteAuthoringPath to the
     * absoluteLivePath.
     */
    public void publish() {
	log.debug("DefaultFilePublisher.publish() has been called.");

	StringTokenizer st = new StringTokenizer(docIds,",");
	while (st.hasMoreTokens()) {
	    String docId = st.nextToken();
	    File sourceFile = new File(absoluteAuthoringPath + docId);
	    File destinationFile = new File(absoluteLivePath + docId);
	    try {
		copyFile(sourceFile, destinationFile);
		log.debug("Document published: " + sourceFile +
			  " " + destinationFile);
	    } catch (FileNotFoundException fnfe) {
		log.error("Document not published: Source file (" +
			  sourceFile + ") not found");
	    } catch (IOException ioe) {
		log.error("Document not published: " +
			  sourceFile + " " + destinationFile);
	    }
	}
	
	// Update (copy) tree
	try {
	    copyFile(new File(absoluteTreeAuthoringPath),
		     new File(absoluteTreeLivePath));
	    log.debug("COPY\ntree source=" + absoluteTreeAuthoringPath +
		      "\ntree destination=" + absoluteTreeLivePath);
	    log.debug("Tree published");
	} catch (IOException ioe) {
	    log.error("Tree not published: " +
		      absoluteTreeAuthoringPath + " " + absoluteTreeLivePath);
	}
    }
}
