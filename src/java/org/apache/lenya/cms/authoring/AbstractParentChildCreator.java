package org.wyona.cms.authoring;

import java.io.File;

/**
 * @author Michael Wechner
 * @created 2002.2.26
 * @created 2002.2.26
 */
public abstract class AbstractParentChildCreator{
/**
 *
 */
  //public abstract void generateTreeId(String childId) throws Exception;
/**
 *
 */
  public abstract void create(File samplesDir,File parentDir,String childId) throws Exception;
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
