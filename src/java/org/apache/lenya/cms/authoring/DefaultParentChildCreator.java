package org.wyona.cms.authoring;

import java.io.File;

/**
 * @author Michael Wechner
 * @created 2002.2.26
 * @created 2002.2.26
 */
public class DefaultParentChildCreator extends AbstractParentChildCreator{
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
    }
  }
