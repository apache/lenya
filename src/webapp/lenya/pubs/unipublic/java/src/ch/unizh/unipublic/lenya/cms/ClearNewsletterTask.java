/*
 * ClearNewsletterTask.java
 *
 * Created on November 20, 2002, 5:08 PM
 */

package ch.unizh.unipublic.wyona.cms;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wyona.cms.publishing.PublishingEnvironment;
import org.wyona.cms.task.AbstractTask;
import org.wyona.xml.DOMWriter;

/**
 *
 * @author  ah
 */
public class ClearNewsletterTask
    extends AbstractTask {
    
    static Category log = Category.getInstance(ClearNewsletterTask.class);
    
    public void clearNewsletter(String publicationPath, String authoringPath) {
        String fileName = publicationPath + authoringPath + "/" + UnipublicEnvironment.newsletterFile;
        File file = new File(fileName);
        Document document = loadDocument(file);
        saveDocument(document, new File(fileName + ".backup"));
        
        Element newsletterElement = document.getDocumentElement();
        NodeList articlesElements = newsletterElement.getElementsByTagName("articles");
        Element articlesElement = (Element) articlesElements.item(0);
        
        NodeList childList = articlesElement.getElementsByTagName("article");
        Element children[] = new Element[childList.getLength()];
        for (int i = 0; i < children.length; i++)
            children[i] = (Element) childList.item(i);
        
        for (int i = 0; i < children.length; i++)
            articlesElement.removeChild(children[i]);
        
        saveDocument(document, file);
    }
    
    public Document loadDocument(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            return document;
        }
        catch(Exception e) {
            log.error("Loading document failed: ", e);
            return null;
        }
    }
        
    public void saveDocument(Document document, File file) {
        try {
            file.createNewFile();
            PrintWriter printWriter = new PrintWriter(new FileWriter(file));
            DOMWriter writer = new DOMWriter(printWriter);
            writer.print(document);
        }
        catch(Exception e) {
            log.error("Writing document failed: ", e);
        }
    }
    
    public void execute(String contextPath) {
        
        try {
            String publicationId = getParameters().getParameter("publication-id");
            PublishingEnvironment environment = new PublishingEnvironment(contextPath, publicationId);
            String publicationPath = environment.getPublicationPath();
            String authoringPath = environment.getAuthoringPath();
            clearNewsletter(publicationPath, authoringPath);
        }
        catch(Exception e) {
            log.error("Clearing newsletter failed: ", e);
        }
    }        
    
}
