package ch.unizh.unipublic.wyona.cms.authoring;

import java.io.File;

import org.wyona.cms.authoring.AbstractParentChildCreator;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner
 * @version 2002.7.2
 */
public class NewArticleCreator extends AbstractParentChildCreator{
  static Category log=Category.getInstance(NewArticleCreator.class);
/**
 *
 */
  public short getChildType(short childType) throws Exception{
    return childType;
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
    log.warn("NewArticleCreator.create() has been called.");
    }
  }
