package ch.computerworld.wyona.cms;

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
 * @author Gregor Rothfuss
 * @version 2003.1.17
 */
public class ArticleCreator extends AbstractParentChildCreator{
  static Category log=Category.getInstance(ArticleCreator.class);
/**
 * Why do we have to overwrite this method?
 */
  public short getChildType(short childType) throws Exception{
    return AbstractParentChildCreator.LEAF_NODE;
    }
/**
 *
 */
  public String getChildName(String childname) throws Exception{
    return childname;
    }
/**
 *
 */
  public String generateTreeId(String childId,short childType) throws Exception{
    return childId;
    }
/**
 * Create the new article
 */
  public void create(File samplesDir,File parentDir,String childId,short childType,String childName) throws Exception{
    // Set filenames
    String id=generateTreeId(childId,childType);
    String filename=parentDir+"/"+id+".xml";
    String doctypeSample=samplesDir+"/Article.xml";

    // Read sample file
    log.debug(".create(): Try to read file: "+doctypeSample);
    Document doc=new SAXReader().read("file:"+doctypeSample);


    // Replace name
    Element ename=(Element)doc.selectSingleNode("/article/head/title");
    if(ename != null){
      log.debug(ename.getPath()+" "+ename.getText());
      ename.addText(childName);
      log.debug(ename.getPath()+" "+ename.getText());
      }


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
