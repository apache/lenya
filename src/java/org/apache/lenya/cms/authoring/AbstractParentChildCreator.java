package org.wyona.cms.authoring;

import java.io.File;

/**
 * @author Michael Wechner
 * @created 2002.2.26
 * @created 2002.2.26
 */
public abstract class AbstractParentChildCreator{
public static short LEAF_NODE=0;
public static short BRANCH_NODE=1;
/**
 *
 */
  public abstract short getChildType(short childType) throws Exception;
/**
 *
 */
  public abstract String generateTreeId(String childId,short childType) throws Exception;
/**
 *
 */
  public abstract void create(File samplesDir,File parentDir,String childId,short childType) throws Exception;
/**
 *
 */
  protected boolean copyFile(File source,File destination) throws Exception{
    if(source.exists()){
      File parentDestination=new File(destination.getParent());
      if(!parentDestination.exists()){
        //getLogger().warn("Directory will be created: "+parentDestination);
        parentDestination.mkdirs();
        }
      org.apache.avalon.excalibur.io.FileUtil.copyFile(source,destination);
      return true;
      }
    else{
      //getLogger().error("File does not exist: "+source);
      return false;
      }
    }
  }
