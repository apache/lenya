/*
 * Publisher.java
 *
 * Created on November 1, 2002, 3:27 PM
 */

package org.wyona.cms.publishing;

import java.net.URL;
import org.apache.log4j.Logger;

/**
 * A Publisher is used to copy XML sources from the authoring server
 * to the pending server.
 * @author <a href="mailto:andreas.hartmann@wyona.com">Andreas Hartmann</a>
 */
public interface Publisher {
    
    void publish(
        String publicationPath,
        String authoringPath,
        String treeAuthoringPath,
        String livePath,
        String treeLivePath,
        String[] sources) throws Exception;
}
