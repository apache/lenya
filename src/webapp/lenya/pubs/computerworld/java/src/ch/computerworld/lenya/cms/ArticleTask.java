/*
 * ArticlePublisher.java
 *
 * Created on January 18, 2003, 11:25 AM
 */

package ch.computerworld.wyona.cms;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.Constants;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedOutputStream; 
import java.io.FileOutputStream;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;

import org.wyona.xml.DOM4JUtil;
import org.wyona.util.DateUtil;
import org.apache.log4j.Category;
import org.dom4j.DocumentFactory;
import org.dom4j.XPath;
import org.wyona.cms.publishing.DefaultFilePublisher;
import org.wyona.cms.publishing.PublishingEnvironment;
import org.wyona.cms.task.AbstractTask;


/**
 *
 * @author  gjr
 */


public class ArticleTask
    extends AbstractTask {
    
    static Category log=Category.getInstance(ArticleTask.class);

    public void prepareArticle(
        String publicationPath,
        String authoringPath,
        String[] sources) {

	String absoluteAuthoringPath = publicationPath + authoringPath + "/";

        for (int i = 0; i < sources.length; i++) {
            File sourceFile = new File(absoluteAuthoringPath + sources[i]);
            try {
                setRevisionDateAndId(absoluteAuthoringPath + sources[i]); 
                addToHeadlines(sources[i], absoluteAuthoringPath);
                boolean published = setFirstPublishedDate(absoluteAuthoringPath + sources[i]);
                log.info("Article prepared: " + sourceFile);
            }
            catch (Exception e) {
                log.error("EXCEPTION: Article not prepared ("
                    + sourceFile + "): ", e);
            }
        }
                
    }


    /** insert the article in the frontpage
     * @param docId id to determine the article
     * @param domainPath path for the different domain (authoring or live)
     */


    private void addToHeadlines(String docId, String domainPath) throws Exception{

        File articleFile = new File(domainPath + docId);
        log.debug("\nArticle file: " + articleFile.getPath());
        
        Document articleDocument = new SAXReader().read(articleFile);
        
        String headlinePath =
            "/article" +
            "/head" +
            "/title";
        
        DocumentFactory factory = DocumentFactory.getInstance();
        XPath headlineXPath = factory.createXPath(headlinePath);
        List nodes = headlineXPath.selectNodes(articleDocument, headlineXPath);
        Element headlineElement = (Element) nodes.get(0);
        String title = headlineElement.getText();
        
        // article's id format, e.g. /news/article.xml 
        // FIXME: should be readen from the article, but now:
        StringTokenizer st=new StringTokenizer(docId,"/");
        st.nextToken();
        String id=st.nextToken();
        id=id.substring(0,id.length()-4); // Remove ".xml" extension

        String headlines_filename=domainPath + ComputerworldEnvironment.headlinesFile;
        log.debug(".addToHeadline(): "+headlines_filename);
        Document headlinesDocument = getDocument(domainPath + ComputerworldEnvironment.headlinesFile);
        insertElement(headlinesDocument, "/articles", "article", id, title);
        writeDocument(domainPath + ComputerworldEnvironment.headlinesFile, headlinesDocument);
                
    }
    
    void writeDocument(String fileName, Document document) {
        // write the headlines
        File parent=new File(new File(fileName).getParent());
        if(!parent.exists()){
            parent.mkdirs();
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
          XMLWriter writer = new XMLWriter(
                             new BufferedOutputStream(
                             new FileOutputStream(fileName)), format);
          writer.write(document);
          writer.close();
        } catch (Exception e) {
          log.debug(e);
        }                                                                                                                                     
    }                                                                                                                                       

    protected Document getDocument(String filePath) {
        //headlines in domain 
        // FIXME    headlinesPath=domainPath+conf.getChild("headlines").getAttribute("href");
        String filename = filePath;
        log.debug("filename: " + filename);
        
        try {
            Document document = new SAXReader().read("file:" + filename);
            return document;
        }
        catch(Exception e) {
            log.error("Can't get document: ", e);
            return null;
        }
    }
    
    protected void insertElement(
            Document document,
            String parentXPath,
            String elementName,
            String id,
            String title) {
                
        DocumentHelper documentHelper = new DocumentHelper();
                
        Element newArticleElement
            = (Element) document.selectSingleNode(
                parentXPath + "/" + elementName + "[@id='" + id + "']"); 
        
        if (newArticleElement != null) {
          log.info("the article  "+id+" is already on the frontpage");
          newArticleElement.setText(title);
        } else {
          newArticleElement = documentHelper.createElement(elementName);
          newArticleElement.addAttribute("id", id);
          newArticleElement.setText(title);
          Element articlesElement = (Element) document.selectSingleNode(parentXPath); 
          List children = articlesElement.elements();
          if (children.size() > 0)
            children.add(0, newArticleElement);
          else
            children.add(newArticleElement);
        }

    }

    
    /** set the published date to the article, only one time 
     *  @param filename Filename of the article
     */


    private boolean setFirstPublishedDate(String filename) throws Exception{

        //get the date
        Calendar cal=new GregorianCalendar();
        String year = Integer.toString(cal.get(cal.YEAR));
        String month = DateUtil.oneToTwoDigits(Integer.toString(cal.get(cal.MONTH)+1));
        String day = DateUtil.oneToTwoDigits(Integer.toString(cal.get(cal.DAY_OF_MONTH)));
        String hour=DateUtil.oneToTwoDigits(Integer.toString(cal.get(cal.HOUR_OF_DAY)));
        String minute=DateUtil.oneToTwoDigits(Integer.toString(cal.get(cal.MINUTE)));
        String millis = getMillis();

        //read the article 
        Document doc=new SAXReader().read("file:"+filename);

        //get the PublishedDate Node
        Element dateE=(Element)doc.selectSingleNode("/article/head/dateline/story.date"); 
        if(dateE != null){
          log.debug(".setFirstPublishedDate(): already set");
          return false;
          } 

        DocumentHelper documentHelper = new DocumentHelper();

        dateE = documentHelper.makeElement(doc, "/article/head/dateline/story.date"); 
        dateE.clearContent();


        //set the PublishedDate
        dateE.addAttribute("year", year);
        dateE.addAttribute("month", month);
        dateE.addAttribute("day", day);
        dateE.addAttribute("hour", hour);
        dateE.addAttribute("minute", minute);
        dateE.addAttribute("millis", millis);
        dateE.addAttribute("norm", day+"."+month+"."+year);  

        //write the article
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
        XMLWriter writer = new XMLWriter( 
                           new BufferedOutputStream(
                           new FileOutputStream(filename)), format);
        writer.write(doc);
        writer.close();
        } catch (Exception e) {
        log.debug(e);
        }

        return true;
    }                                                                                                                                       


    /** set the revision date and the compute the revision id
     * @param filename Filename of the article 
     */


    private void setRevisionDateAndId(String filename) throws Exception{
        //get the date
        Calendar cal=new GregorianCalendar();
        String year = Integer.toString(cal.get(cal.YEAR));
        String month = DateUtil.oneToTwoDigits(Integer.toString(cal.get(cal.MONTH)+1));
        String day = DateUtil.oneToTwoDigits(Integer.toString(cal.get(cal.DAY_OF_MONTH)));

        //read the article
        Document doc=new SAXReader().read("file:"+filename);

        DocumentHelper documentHelper = new DocumentHelper();

        //write the article
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
        XMLWriter writer = new XMLWriter(
                           new BufferedOutputStream(
                           new FileOutputStream(filename)), format);
        writer.write(doc);
        writer.close();
        } catch (Exception e) {
        log.debug(e);
        }
    }                                                                                                                                       


    /**
     *
     */


  public synchronized String getMillis(){
    String millis = Long.toString(new Date().getTime());
    // wait to be sure the milliseconds are not used more than once
    try {
      this.wait(1);
      } catch (InterruptedException e) {
      }
    return millis;
    }

  
    /** Execute the task.
    *
    */

 
   public void execute(String contextPath) {
        try {
            String publicationId = getParameters().getParameter("publication-id");
            String publicationPath = PublishingEnvironment.getPublicationPath(contextPath, publicationId);

            Parameters taskParameters = new Parameters();

            PublishingEnvironment environment = new PublishingEnvironment(contextPath,
                publicationId);

            // read default parameters from PublishingEnvironment
            taskParameters.setParameter("authoring-path", environment.getAuthoringPath());
            taskParameters.setParameter("tree-authoring-path", environment.getTreeAuthoringPath());
            taskParameters.setParameter("live-path", environment.getLivePath());
            taskParameters.setParameter("tree-live-path", environment.getTreeLivePath());

            taskParameters.merge(getParameters());
            parameterize(taskParameters);

            String sourcesString = getParameters().getParameter("sources");
            StringTokenizer st = new StringTokenizer(sourcesString,",");
            String sources[] = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                sources[i++] = st.nextToken();
            }

            prepareArticle(
                publicationPath,
                getParameters().getParameter("authoring-path"),
                sources);
	} catch (Exception e) {
	    log.error("Preparing article failed: ", e);
	}
    }
  
}
