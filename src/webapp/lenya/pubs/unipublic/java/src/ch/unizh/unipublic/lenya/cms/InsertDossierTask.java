/*
 * InsertDossierTask.java
 *
 * Created on November 18, 2002, 3:17 PM
 */

package ch.unizh.unipublic.wyona.cms;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log4j.Category;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.wyona.cms.publishing.PublishingEnvironment;
import org.wyona.cms.task.AbstractTask;
import org.wyona.xml.DOM4JUtil;

/**
 *
 * @author  ah
 */
public class InsertDossierTask
    extends AbstractTask {

    static Category log=Category.getInstance(InsertDossierTask.class);
        
    public void insertDossier(String publicationPath, String authoringPath, String source) {
        try {
            
            // replace leading slash
            String dossierId = source.substring(1);
            
            StringTokenizer tokenizer = new StringTokenizer(dossierId, "/");
            tokenizer.nextToken();
            dossierId = tokenizer.nextToken() + "/" + tokenizer.nextToken();
            
            File dossierFile = new File(publicationPath + authoringPath + "/" + source);
            log.debug("\nDossier file: " + dossierFile.getPath());

            Document dossierDocument = new SAXReader().read(dossierFile);

            String headlinePath =
                "/dossier" +
                "/head" +
                "/title";

            DocumentFactory factory = DocumentFactory.getInstance();
            XPath headlineXPath = factory.createXPath(headlinePath);
            List nodes = headlineXPath.selectNodes(dossierDocument, headlineXPath);
            Element headlineElement = (Element) nodes.get(0);
            String title = headlineElement.getText();

            String dossiersFilename = publicationPath + authoringPath + "/frontpage/dossier.xml";

            log.debug("Dossiers file: " + dossiersFilename);
            Document dossiersDocument = new SAXReader().read("file:" + dossiersFilename);

            //insert the dossier at the top
            DocumentHelper documentHelper = new DocumentHelper();

            Element newDossierElement = 
                (Element) dossiersDocument.selectSingleNode("/dossiers/dossier[@id='"+dossierId+"']"); 

            if (newDossierElement != null) {
                log.info("the dossier " + dossierId + " is already on the frontpage");
                newDossierElement.setText(title);
            } else {
                newDossierElement = documentHelper.createElement("dossier");
                newDossierElement.setText(title);
                newDossierElement.setAttributeValue("id", dossierId);
                Element dossiersElement = (Element) dossiersDocument.selectSingleNode("/dossiers"); 
                List children = dossiersElement.elements();
                if (children.size() > 0)
                  children.add(0, newDossierElement);
                else
                  children.add(newDossierElement);
            }

            // write the headlines
            File parent = new File(new File(dossiersFilename).getParent());
            if(!parent.exists()){
                parent.mkdirs();
            }

            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(
                               new BufferedOutputStream(
                               new FileOutputStream(dossiersFilename)), format);
            writer.write(dossiersDocument);
            writer.close();
        } catch (Exception e) {
            log.error("Could not insert dossier: ", e);
        }                                                                                                                                     
    }
    
    /** Execute the task.
    *
    */
    public void execute(String contextPath) {
        try {
            String publicationId = getParameters().getParameter("publication-id");
            String publicationPath
            = PublishingEnvironment.getPublicationPath(contextPath, publicationId);

            Parameters taskParameters = new Parameters();

            PublishingEnvironment environment = new PublishingEnvironment(contextPath,
                publicationId);

            // read default parameters from PublishingEnvironment
            taskParameters.setParameter("authoring-path", environment.getAuthoringPath());

            taskParameters.merge(getParameters());
            parameterize(taskParameters);

            String sourcesString = getParameters().getParameter("sources");
            StringTokenizer st = new StringTokenizer(sourcesString,",");
            String sources[] = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                sources[i++] = st.nextToken();
            }

            insertDossier(
                publicationPath,
                getParameters().getParameter("authoring-path"),
                sources[0]);
	} catch (Exception e) {
	    log.error("Inserting dossier failed: ", e);
	}
    }
  
}
