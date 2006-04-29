/* $Id$  */

package org.apache.lenya.cms.task;

import org.apache.log4j.Logger;

import java.io.File;

import org.apache.lenya.lucene.index.Index;
import org.apache.lenya.lucene.index.Indexer;

public class LuceneTask extends AbstractTask {
    Logger log = Logger.getLogger(LuceneTask.class);

    /**
     * @see org.apache.lenya.cms.task.Task#execute(java.lang.String)
     */
    public void execute(String servletContextPath) throws ExecutionException {
        try {
            //outputParameters();

            log.debug("Servlet context path: " + servletContextPath);

            String publicationId = getParameters().getParameter("publication-id");
            log.debug("Publication ID: " + publicationId);

            String files = getParameters().getParameter("properties.files2index");
            log.debug("Files 2 index: " + files);

            String luceneConfig = getParameters().getParameter("config");
            log.debug("Lucene configuration: " + luceneConfig);

            String luceneConfigAbsolutePath = servletContextPath + File.separator + "lenya" + File.separator + "pubs" + File.separator + publicationId  + File.separator + luceneConfig;
            log.debug("Lucene configuration: " + luceneConfigAbsolutePath);

            Indexer indexer = Index.getIndexer(luceneConfigAbsolutePath);
	    indexer.indexDocument(new File(files));
        } catch (Exception e) {
            log.error("" + e);
        }
    }

    /**
     * Output parameters for debugging
     */
    public void outputParameters() throws Exception {
        String[] names = getParameters().getNames();
        for (int i = 0; i < names.length; i++) {
            log.error("Name: " + names[i]);
            log.error("Value: " + getParameters().getParameter(names[i]));
        }
    }
}
