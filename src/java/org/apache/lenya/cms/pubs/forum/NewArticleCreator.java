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
