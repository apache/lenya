/*
 * ArticlePublisher.java
 *
 * Created on November 4, 2002, 11:25 AM
 */

package ch.unizh.unipublic.wyona.cms;

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
 * @author  ah
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
            "/NewsML" +
            "/NewsItem" +
            "/NewsComponent" +
            "/ContentItem" +
            "/DataContent" +
            "/nitf" +
            "/body" +
            "/body.head" +
            "/hedline" +
            "/hl1";
        
        DocumentFactory factory = DocumentFactory.getInstance();
        XPath headlineXPath = factory.createXPath(headlinePath);
        List nodes = headlineXPath.selectNodes(articleDocument, headlineXPath);
        Element headlineElement = (Element) nodes.get(0);
        String title = headlineElement.getText();
        
        //article's  channel, section, year, id. FIXME: should be readen from the article, but now:
        StringTokenizer st=new StringTokenizer(docId,"/");
        String channel=st.nextToken();
        String section=st.nextToken();
        String year=st.nextToken();
        String id=st.nextToken();

        Document headlinesDocument = getDocument(domainPath + UnipublicEnvironment.headlinesFile);
        insertElement(headlinesDocument, "/Articles", "Article",
            channel, section, year, id, title);
        writeDocument(domainPath + UnipublicEnvironment.headlinesFile, headlinesDocument);
        
        Document newsletterDocument = getDocument(domainPath + UnipublicEnvironment.newsletterFile);
        insertElement(newsletterDocument, "/newsletter/articles", "article",
            channel, section, year, id, title);
        writeDocument(domainPath + UnipublicEnvironment.newsletterFile, newsletterDocument);
        
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
            String channel,
            String section,
            String year,
            String id,
            String title) {
                
        DocumentHelper documentHelper = new DocumentHelper();
                
        Element newArticleElement
            = (Element) document.selectSingleNode(
                parentXPath + "/" + elementName + "[@id='" + id + "'][@section='" + section + "']"); 
        
        if (newArticleElement != null) {
          log.info("the article  "+id+" is already on the frontpage");
          newArticleElement.setText(title);
        } else {
          newArticleElement = documentHelper.createElement(elementName);
          newArticleElement.addAttribute("channel", channel);
          newArticleElement.addAttribute("section", section);
          newArticleElement.addAttribute("year", year);
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
        Element dateE=(Element)doc.selectSingleNode("/NewsML/NewsItem/NewsManagement/PublishDate"); 
        if (dateE != null) {
        return false;
        } 

        DocumentHelper documentHelper = new DocumentHelper();

        dateE = documentHelper.makeElement(doc, "/NewsML/NewsItem/NewsManagement/PublishDate"); 
        dateE.clearContent();


        //set the PublishedDate
        dateE.addAttribute("year", year);
        dateE.addAttribute("month", month);
        dateE.addAttribute("day", day);
        dateE.addAttribute("hour", hour);
        dateE.addAttribute("minute", minute);
        dateE.addAttribute("millis", millis);

        //set the PublishedDate in the head of article (for the frontpage)
        Element headDateE = documentHelper.makeElement(doc, "/NewsML/NewsItem/NewsComponent/ContentItem/DataContent/nitf/body/body.head/dateline/story.date");
        headDateE.clearContent();
        headDateE.addAttribute("norm", day+"."+month+"."+year);  
        headDateE.addAttribute("millis" , millis);

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

        //get the RevisionDate Node
        Element dateE = documentHelper.makeElement(doc, "/NewsML/NewsItem/NewsManagement/RevisionDate");
        dateE.clearContent();

        //set the RevisionDate
        dateE.addAttribute("year", year);
        dateE.addAttribute("month", month);
        dateE.addAttribute("day", day);

        //get the RevisionId Node
        Element revIdE = documentHelper.makeElement(doc, "/NewsML/NewsItem/Identification/NewsIdentifier/RevisionId");
        String id=revIdE.getText();
        if (id==null) {
        id="0"; 
        }
        int num = Integer.parseInt(id);
        num =num+1;
        String newId = new Integer(num).toString();   

        revIdE.clearContent();

        //set the Revision id
        revIdE.addAttribute("PreviousRevision", id);
        revIdE.addAttribute("Update", "N");
        revIdE.setText(newId);

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
