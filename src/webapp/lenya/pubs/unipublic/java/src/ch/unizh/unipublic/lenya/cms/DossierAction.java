/*
 * DossierAction.java
 *
 * Created on November 14, 2002, 3:29 PM
 */

package ch.unizh.unipublic.wyona.cms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.log4j.Category;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.wyona.cms.publishing.PublishingEnvironment;

/**
 *
 * @author  ah
 */
public class DossierAction
    extends AbstractComplementaryConfigurableAction {
    
    static Category log = Category.getInstance(DossierAction.class);
    
    public static final String ARTICLE_FILE = "article-file";
    public static final String DOSSIER_ID = "dossier-id";
  
    public java.util.Map act(
            Redirector redirector,
            SourceResolver sourceResolver,
            Map objectModel,
            String str,
            Parameters parameters) throws Exception {
                
        log.debug(
            "\n--------------------------" +
            "\n- DossierAction invoked" +
            "\n--------------------------");
        
	// Get Source
	org.apache.cocoon.environment.Source inputSource = sourceResolver.resolve("");
	String publicationPath = inputSource.getSystemId();
	// Remove "file:" protocol
	publicationPath = publicationPath.substring(5); 
        
        if (publicationPath.endsWith("/"))
            publicationPath = publicationPath.substring(0, publicationPath.length()-1);

        int lastSlashIndex = publicationPath.lastIndexOf("/");
        String publicationId = publicationPath.substring(lastSlashIndex + 1);

        publicationPath = publicationPath.substring(0, lastSlashIndex + 1);

        String publicationPrefix = PublishingEnvironment.PUBLICATION_PREFIX;
        
//        String contextPath = publicationPath.replaceFirst(publicationPrefix, "");
        String contextPath = publicationPath.substring(
            0, publicationPath.length() - publicationPrefix.length());
        
        Request request = ObjectModelHelper.getRequest(objectModel);

	if (request == null) {
	    getLogger().error ("No request object");
	    return null;
	}
	
        publicationPath = PublishingEnvironment.getPublicationPath(contextPath, publicationId);
        PublishingEnvironment environment = new PublishingEnvironment(publicationPath);
        
        // set parameters using the request parameters
        log.debug("\n<parameters>");
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            log.debug("\n  Parameter: " + name + " = " + request.getParameter(name));
        }
        log.debug("\n</parameters>");
        
        String articlePathParameter = request.getParameter(ARTICLE_FILE);
        String newDossierId = request.getParameter(DOSSIER_ID);
        
        String articlePath =
            publicationPath +
            environment.getAuthoringPath() +
            File.separator +
            articlePathParameter;
        
        String dossierBasePath =
            publicationPath +
            environment.getAuthoringPath() +
            File.separator +
            "dossiers" +
            File.separator;
        
        String newDossierPath = dossierBasePath + newDossierId + File.separator + "index.xml";
        
        File articleFile = new File(articlePath);
        log.debug("Article file: " + articleFile.getPath());
        
        SAXReader reader = new SAXReader();
        Document articleDocument = reader.read(articleFile);

        DocumentFactory factory = DocumentFactory.getInstance();
        
        try {
            
            //------------------------------------
            // article file
            //------------------------------------
            
            StringTokenizer tokenizer = new StringTokenizer(articlePathParameter, "/");
            String articleChannel = tokenizer.nextToken();
            String articleSection = tokenizer.nextToken();
            String articleYear = tokenizer.nextToken();
            String articleDirectory = tokenizer.nextToken();
            
            // create backup
            backupFile(articlePath);

            // on we go
            
            XPath articleXPath = factory.createXPath("/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/nitf/body/body.head");
            List parentNodes = articleXPath.selectNodes(articleDocument);
            Element parent = null;
            if (parentNodes.size() > 0) {
                parent = (Element) parentNodes.get(0);
                log.debug(
                    "\n--------------------------" +
                    "\n- Parent element: " + parent.getName() +
                    "\n--------------------------");

            }
            else {
                log.error(
                    "\n--------------------------" +
                    "\n- Parent node not found!" +
                    "\n--------------------------");
            }
            
            Element dossierElement = parent.element("dossier");

            String oldDossierId = "none";
            if (dossierElement != null)
                oldDossierId = dossierElement.attribute("id").getStringValue();
            
            boolean replace = !oldDossierId.equals(newDossierId);
            
            // ----------------------------------------------------
            // do we need to replace it at all?
            // ----------------------------------------------------
            if (replace) {
                
                // ----------------------------------------------------
                // does a previous dossier entry exist?
                // ----------------------------------------------------
                if (dossierElement != null) {

                    parent.remove(dossierElement);

                    // ----------------------------------------------------
                    // remove from old dossier file
                    // ----------------------------------------------------
                    
                    String oldDossierPath = dossierBasePath + oldDossierId + File.separator + "index.xml";
                    File oldDossierFile = new File(oldDossierPath);
                    
                    Document oldDossierDocument = reader.read(oldDossierFile);

                    XPath dossierXPath = factory.createXPath("/dossier/articles");
                    List oldArticlesElements = dossierXPath.selectNodes(oldDossierDocument);
                    Element oldArticlesElement = null;
                    if (oldArticlesElements.size() > 0) {
                        oldArticlesElement = (Element) oldArticlesElements.get(0);
                        log.debug(
                            "\n--------------------------" +
                            "\n- Old Dossier Parent element: " + oldArticlesElement.getName() +
                            "\n--------------------------");

                    }
                    else {
                        log.error(
                            "\n--------------------------" +
                            "\n- Old Dossier Parent node not found!" +
                            "\n--------------------------");
                    }

                    List articleElements = oldArticlesElement.elements("article");
                    Element elementToRemove = null;
                    for (Iterator i = articleElements.iterator(); i.hasNext(); ) {
                        Element articleElement = (Element) i.next();
                        if (articleElement.attribute("channel").getStringValue().equals(articleChannel) &&
                            articleElement.attribute("section").getStringValue().equals(articleSection) &&
                            articleElement.attribute("year")   .getStringValue().equals(articleYear) &&
                            articleElement.attribute("id")    .getStringValue().equals(articleDirectory)) {
                            elementToRemove = articleElement;
                        }
                    }

                    oldArticlesElement.remove(elementToRemove);
                    writeDocument(oldDossierPath, oldDossierDocument);
                    
                    // ----------------------------------------------------
                    // removing done
                    // ----------------------------------------------------
                }
            
                
                if (!newDossierId.equals("none")) {
                    
                    // ----------------------------------------------------
                    // insert new dossier element
                    // ----------------------------------------------------
                    
                    dossierElement = factory.createElement("dossier");
                    dossierElement.add(factory.createAttribute(
                            dossierElement, "id", newDossierId));
                    parent.add(dossierElement);

                    //------------------------------------
                    // new dossier file
                    //------------------------------------

                    // create backup
                    backupFile(newDossierPath);

                    File newDossierFile = new File(newDossierPath);
                    log.debug("New dossier file: " + newDossierFile.getPath());
                    Document newDossierDocument = reader.read(newDossierFile);

                    XPath dossierXPath = factory.createXPath("/dossier/articles");
                    List articlesElements = dossierXPath.selectNodes(newDossierDocument);
                    Element articlesElement = null;
                    if (articlesElements.size() > 0) {
                        articlesElement = (Element) articlesElements.get(0);
                        log.debug(
                            "\n--------------------------" +
                            "\n- Parent element: " + articlesElement.getName() +
                            "\n--------------------------");

                    }
                    else {
                        log.error(
                            "\n--------------------------" +
                            "\n- Parent node not found!" +
                            "\n--------------------------");
                    }

                    Element articleElement = factory.createElement("article");
                    articleElement.add(factory.createAttribute(articleElement, "channel", articleChannel));
                    articleElement.add(factory.createAttribute(articleElement, "section", articleSection));
                    articleElement.add(factory.createAttribute(articleElement, "year", articleYear));
                    articleElement.add(factory.createAttribute(articleElement, "id", articleDirectory));
                    articlesElement.add(articleElement);

                    writeDocument(newDossierPath, newDossierDocument);
                }

                // finally save article file
                writeDocument(articlePath, articleDocument);
            }

        }
        catch(Exception e) {
            log.error("Updating article failed: ", e);
        }
        
        //------------------------------------------------------------
        // get session
        //------------------------------------------------------------
        
	Session session = request.getSession(true);
	if (session == null) {
	    getLogger().error("No session object");
	    return null;
	}
	
        //------------------------------------------------------------
	// Return referer
        //------------------------------------------------------------

        String parent_uri = (String)session.getAttribute("org.wyona.cms.cocoon.acting.DossierAction.parent_uri");
	HashMap actionMap = new HashMap();
	actionMap.put("parent_uri", parent_uri);
	session.removeAttribute("org.wyona.cms.cocoon.acting.DossierAction.parent_uri");
	return actionMap;
    }
    
    protected void backupFile(String path) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(path));
//            String backupPath = path.replaceAll(".xml", "_backup.xml");
            String backupPath = path.substring(0, path.length() - 4);
            backupPath += "_backup.xml";
            writeDocument(backupPath, document);
        }
        catch(Exception e) {
            log.error("Backup failed: ", e);
        }
    }
    
    protected void writeDocument(String path, Document document) {
        try {
            File file = new File(path);
            file.createNewFile();

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.write(document);
            writer.close();
        }
        catch(Exception e) {
            log.error("Writing document failed: ", e);
        }
    }
    
}
