package org.wyona.cms.pubs.forum;

import org.wyona.cms.authoring.AbstractParentChildCreator;

import java.io.File;
import java.io.FileWriter;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.apache.log4j.Category;

import org.wyona.util.DateUtil;

/**
 * @author Michael Wechner
 * @version 2002.11.10
 */
public class NewArticleCreator extends AbstractParentChildCreator{
  static Category log=Category.getInstance(NewArticleCreator.class);
/**
 * Why do we have to overwrite this method?
 */
  public short getChildType(short childType) throws Exception{
    return AbstractParentChildCreator.BRANCH_NODE;
    }
/**
 * Does anybody care?
 */
  public String getChildName(String childname) throws Exception{
    return childname+"levi";
    }
/**
 *
 */
  public String generateTreeId(String childId,short childType) throws Exception{
    long id=System.currentTimeMillis();
    return ""+id;
    //return "articles/"+id+"/index.xml";
    }
/**
 *
 */
  public void create(File samplesDir,File parentDir,String childId,short childType) throws Exception{
    // Set filenames
    String id=generateTreeId(childId,childType);
    String filename=parentDir+"/articles/"+id+"/index.xml";
    String doctypeSample=samplesDir+"/Article.xml";

    // Read sample file
    Document doc=new SAXReader().read("file:"+doctypeSample);

    // Replace id
    Element eid=(Element)doc.selectSingleNode("/article/meta/id");
    log.error(eid.getPath()+" "+eid.getText());
    //eid.remove(org.dom4j.DocumentHelper.createText("@ID@"));
    eid.addText(id);
    log.error(eid.getPath()+" "+eid.getText());

    // Replace editor
    Element eeditor=(Element)doc.selectSingleNode("/article/meta/editor");
    log.debug(eeditor.getPath()+" "+eeditor.getText());
    eeditor.addText("levi");
    log.debug(eeditor.getPath()+" "+eeditor.getText());


    Calendar cal=new GregorianCalendar();

    // Replace year
    Element eyear=(Element)doc.selectSingleNode("/article/meta/date/year");
    log.debug(eyear.getPath()+" "+eyear.getText());
    eyear.addText(Integer.toString(cal.get(cal.YEAR)));
    log.debug(eyear.getPath()+" "+eyear.getText());

    // Replace month 
    Element emonth=(Element)doc.selectSingleNode("/article/meta/date/month");
    log.debug(emonth.getPath()+" "+emonth.getText());
    int imonth=cal.get(cal.MONTH)+1;
    emonth.addText(Integer.toString(imonth));
    emonth.addAttribute("name",DateUtil.getMonthName(imonth));
    log.debug(emonth.getPath()+" "+emonth.getText());

    // Replace day 
    Element eday=(Element)doc.selectSingleNode("/article/meta/date/day");
    log.debug(eday.getPath()+" "+eday.getText());
    eday.addText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
    eday.addAttribute("name",DateUtil.getDayName(cal.get(Calendar.DAY_OF_WEEK)));
    log.debug(eday.getPath()+" "+eday.getText());

    // Replace hour 
    Element ehour=(Element)doc.selectSingleNode("/article/meta/date/hour");
    log.debug(ehour.getPath()+" "+ehour.getText());
    ehour.addText(DateUtil.oneToTwoDigits(Integer.toString(cal.get(cal.HOUR_OF_DAY))));
    log.debug(ehour.getPath()+" "+ehour.getText());

    // Replace minute 
    Element eminute=(Element)doc.selectSingleNode("/article/meta/date/minute");
    log.debug(eminute.getPath()+" "+eminute.getText());
    eminute.addText(DateUtil.oneToTwoDigits(Integer.toString(cal.get(cal.MINUTE))));
    log.debug(eminute.getPath()+" "+eminute.getText());




    // Write file
    File parent=new File(new File(filename).getParent());
    if(!parent.exists()){
      parent.mkdirs();
      }
    FileWriter fileWriter=new FileWriter(filename);
    doc.write(fileWriter);
    fileWriter.close();
    //copyFile(new File(doctypeSample),new File(filename));
    }
  }
