/*
 * Exporter.java
 *
 * Created on November 4, 2002, 6:08 PM
 */

package org.wyona.cms.publishing;

import java.net.URL;

/**
 * An Exporter is used to copy files from the pending to the live server.
 * @author  ah
 */
public interface Exporter {
    
    public void export(
        URL serverURI,
        int serverPort,
        String publicationPath,
        String exportPathPrefix,
        String uris[],
        String substituteExpression) throws Exception;
    
}
