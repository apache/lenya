/*
 * StaticHTMLExporter.java
 *
 * Created on November 4, 2002, 6:10 PM
 */

package org.wyona.cms.publishing;

import java.io.File;
import java.net.URL;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log4j.Category;
import org.wyona.cms.task.Task;

/**
 * This Exporter uses WGet to download HTML files from URIs and saves them.
 * The Task parameters are:
 * <code><strong>server-uri</strong></code>: the server uri<br/>
 * <code><strong>server-port</strong></code>: the server port<br/>
 * <code><strong>publication-id</strong></code>: the publication id<br/>
 * <code><strong>export-path-prefix</strong></code>: the path to save the files to<br/>
 * <code><strong>uris</strong></code>: a comma-separated list of uris to download (without server + port)<br/>
 * <code><strong>substitute-regexp</strong></code>: a regular expression to substitute a part of the path<br/>
 *
 * @author  <a href="mailto:andreas.hartmann@wyona.com">Andreas Hartmann</a>
 */
public class StaticHTMLExporter extends AbstractExporter {
    static Category log = Category.getInstance(StaticHTMLExporter.class);

    public static final String PARAMETER_URIS = "uris";
/**
 *
 */
    public void export(
        URL serverURI,
        int serverPort,
        String publicationPath,
        String exportPath,
        String uris[],
        String substituteExpression) throws Exception {
                
        try {

            String exportDirectory = publicationPath + exportPath;

            org.wyona.net.WGet wget=new org.wyona.net.WGet();
            log.info(".export(): Export directory: " + exportDirectory);
            wget.setDirectoryPrefix(exportDirectory);

            String fullServerURI = serverURI + ":" + serverPort;

            for (int i = 0; i < uris.length; i++) {
                URL uri = new URL(fullServerURI + uris[i]);
                log.info(".export(): Export static HTML: " + uri);
                byte[] response = wget.download(uri, substituteExpression);
                //wget.saveToFile(url.getFile(),response);
            }
        }
        catch(Exception e){
            log.error("Export failed: ", e);
        }
    }
    
    public void execute(String contextPath) {

        try {
            
            String publicationId = getParameters().getParameter(PARAMETER_PUBLICATION_ID);

            Parameters taskParameters = new Parameters();

            PublishingEnvironment environment = new PublishingEnvironment(contextPath,
                publicationId);

            // read default parameters from PublishingEnvironment
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_EXPORT_PATH, environment.getExportDirectory());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_SUBSTITUTE_REGEXP, environment.getSubstituteExpression());

            taskParameters.merge(getParameters());
            parameterize(taskParameters);

            String publicationPath
                = PublishingEnvironment.getPublicationPath(contextPath, publicationId);

            int serverPort = getParameters().getParameterAsInteger(PARAMETER_SERVER_PORT);
            log.debug(".execute(): Server Port: "+serverPort);
            String serverURI = getParameters().getParameter(PARAMETER_SERVER_URI);

            String urisString = getParameters().getParameter(PARAMETER_URIS);
            StringTokenizer st = new StringTokenizer(urisString, ",");
            String uris[] = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                uris[i++] = st.nextToken();
            }
            
            export(
                new URL(serverURI),
                serverPort,
                publicationPath,
                getParameters().getParameter(PublishingEnvironment.PARAMETER_EXPORT_PATH),
                uris,
                getParameters().getParameter(PublishingEnvironment.PARAMETER_SUBSTITUTE_REGEXP));
        }
        catch(Exception e){
            log.error("Export failed: ", e);
        }
        
    }
    
}
