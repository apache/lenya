package org.wyona.cms.pubs.forum;

import org.wyona.cms.authoring.AbstractParentChildCreator;

import java.io.File;
import java.io.FileWriter;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner
 * @version 2002.6.4
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

    // Replace year
    Element eyear=(Element)doc.selectSingleNode("/article/meta/date/year");
    log.debug(eyear.getPath()+" "+eyear.getText());
    //eid.remove(org.dom4j.DocumentHelper.createText("@ID@"));
    eyear.addText("2002");
    log.debug(eyear.getPath()+" "+eyear.getText());

    // Replace hour 
    Element ehour=(Element)doc.selectSingleNode("/article/meta/date/hour");
    log.debug(ehour.getPath()+" "+ehour.getText());
    ehour.addText("01");
    log.debug(ehour.getPath()+" "+ehour.getText());

    // Replace minute 
    Element eminute=(Element)doc.selectSingleNode("/article/meta/date/minute");
    log.debug(eminute.getPath()+" "+eminute.getText());
    eminute.addText("28");
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
