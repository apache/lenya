package ch.unizh.unipublic.wyona.cms.authoring;

import java.io.File;
import java.io.FileWriter;

import org.wyona.cms.authoring.AbstractParentChildCreator;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner
 * @author Edith Chevrier
 * @version 2002.7.4
 */
public class NewArticleCreator extends AbstractParentChildCreator{
  static Category log=Category.getInstance(NewArticleCreator.class);
/**
 *
 */
  public short getChildType(short childType) throws Exception{
    return AbstractParentChildCreator.BRANCH_NODE;
//    return childType;
    }
/**
 *
 */
  public String generateTreeId(String childId,short childType) throws Exception{
    return childId;
    }
/**
 *
 */
  public void create(File samplesDir,File parentDir,String childId,short childType) throws Exception{
    log.debug("NewArticleCreator.create() has been called.");

    //Set filenames
    String filename=parentDir+"/"+childId+"/"+"index.xml"; 
    log.debug("filename : "+filename);
    String doctypeSample=samplesDir+"/Article.xml";                                                                                         
    log.debug("doctypeSample : "+doctypeSample);

    copyFile(new File(doctypeSample),new File(filename)); 

    }
  }
