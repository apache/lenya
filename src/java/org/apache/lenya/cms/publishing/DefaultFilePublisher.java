package org.wyona.cms.publishing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.log4j.Category;
import org.wyona.cms.task.Task;

/**
 * Describe class <code>DefaultFilePublisher</code> here.
 * The following task parameters must be provided:<br/>
 * <code><strong>publication-id</strong></code>: the absolute path of this publication<br/>
 * <code><strong>authoring-path</strong></code>: the authoring path<br/>
 * <code><strong>tree-authoring-path</strong></code>: the location of the <code>tree.xml</code> file<br/>
 * <code><strong>live-path</strong></code>: the live path<br/>
 * <code><strong>tree-live-path</strong></code>: the location of the <code>tree.xml</code> file<br/>
 * <code><strong>sources</strong></code>: a comma-separated list of files to publish<br/>
 *
 * @author <a href="mailto:andreas.hartmann@wyona.com">Andreas Hartmann</a>
 */
public class DefaultFilePublisher
    extends AbstractFilePublisher {
    
    static Category log = Category.getInstance(DefaultFilePublisher.class);
    public static final String PARAMETER_SOURCES = "sources";

    /**
     * Default implementation of <code>publish</code> which simply
     * copies the files from the absoluteAuthoringPath to the
     * absoluteLivePath.
     */
    public void publish(
        String publicationPath,
        String authoringPath,
        String treeAuthoringPath,
        String livePath,
        String treeLivePath,
        String[] sources) {

	log.debug("PUBLICATION: " + publicationPath);
	log.debug("CONFIGURATION:\nauthoring path=" + authoringPath + "\nlive path=" + livePath);
	
	// Set absolute paths
	String absoluteAuthoringPath = publicationPath + authoringPath;
	String absoluteTreeAuthoringPath = publicationPath + treeAuthoringPath;
	String absoluteLivePath = publicationPath + livePath;
	String absoluteTreeLivePath = publicationPath + treeLivePath;

        log.debug("DefaultFilePublisher.publish() has been called.");

        for (int index=0; index < sources.length; index++) {
	    File sourceFile = new File(absoluteAuthoringPath + sources[index]);
	    File destinationFile = new File(absoluteLivePath + sources[index]);
	    try {
		copyFile(sourceFile, destinationFile);
		log.debug("Document published: " + sourceFile + " " + destinationFile);
	    } catch (FileNotFoundException fnfe) {
		log.error("Document not published: Source file (" + sourceFile + ") not found");
	    } catch (IOException ioe) {
		log.error("Document not published: " + sourceFile + " " + destinationFile);
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
		      absoluteTreeAuthoringPath + " " +
                      absoluteTreeLivePath);
	}
    }
    
    public void execute(String contextPath) {

        try {
            String publicationId = getParameters().getParameter(PARAMETER_PUBLICATION_ID);

            Parameters taskParameters = new Parameters();

            PublishingEnvironment environment = new PublishingEnvironment(contextPath,
                publicationId);

            // read default parameters from PublishingEnvironment
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_AUTHORING_PATH, environment.getAuthoringPath());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_TREE_AUTHORING_PATH, environment.getTreeAuthoringPath());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_LIVE_PATH, environment.getLivePath());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_TREE_LIVE_PATH, environment.getTreeLivePath());

            taskParameters.merge(getParameters());
            parameterize(taskParameters);

            String sourcesString = getParameters().getParameter(PARAMETER_SOURCES);
            StringTokenizer st = new StringTokenizer(sourcesString,",");
            String sources[] = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                sources[i++] = st.nextToken();
            }

            publish(
                PublishingEnvironment.getPublicationPath(contextPath, publicationId),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_AUTHORING_PATH),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_TREE_AUTHORING_PATH),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_LIVE_PATH),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_TREE_LIVE_PATH),
                sources);
	} catch (Exception e) {
	    log.error("Publishing failed: ", e);
	}
    }
    
}
