package org.wyona.cms.pubs.forum;

import org.wyona.cms.authoring.AbstractParentChildCreator;

import java.io.File;

/**
 * @author Michael Wechner
 * @version 2002.6.3
 */
public class NewArticleCreator extends AbstractParentChildCreator{
/**
 * Why?
 */
  public short getChildType(short childType) throws Exception{
    return AbstractParentChildCreator.BRANCH_NODE;
    }
/**
 *
 */
  public String generateTreeId(String childId,short childType) throws Exception{
    long id=System.currentTimeMillis();
    return "articles/"+id+"/index.xml";
    }
/**
 *
 */
  public void create(File samplesDir,File parentDir,String childId,short childType) throws Exception{
    String filename=parentDir+"/"+generateTreeId(childId,childType);
    String doctypeSample=samplesDir+"/Article.xml";
    copyFile(new File(doctypeSample),new File(filename));
    }
  }
