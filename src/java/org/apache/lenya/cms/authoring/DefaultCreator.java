package org.wyona.cms.authoring;

import org.wyona.cms.authoring.AbstractParentChildCreator;
import org.dom4j.Node;
import org.apache.log4j.Category; 
import java.io.File;

/**
 * @author  <a href="mailto:juergen.bigalke@wyona.org">Juergen Bigalke</a>
 * 
 */
public class DefaultCreator extends DefaultParentChildCreator {
  // static Category log=Category.getInstance(DefaultParentChildCreator.class);                                         
  private String prefix=null;
  private String fname="/index.xml";
  private String fnameMeta="/index-meta.xml";
  private String docNameSample="generic.xml";
  private String docNameMeta="Meta.xml";


  public void init(Node creatorNode) {
    if (creatorNode==null) return;
    String s=creatorNode.valueOf("prefix");
    if (s!=null && !s.equals("")) prefix=s;
    s=creatorNode.valueOf("sample");
    if (s!=null && !s.equals("")) docNameSample=s;
    s=creatorNode.valueOf("sampleMeta");
    if (s!=null && !s.equals("")) docNameMeta=s;
    s=creatorNode.valueOf("index");
    if (s==null) s="";
    fname=s+".xml";
    fnameMeta=s+"-meta.xml";
  }
/**
 *
 */
  public short getChildType(short childType) throws Exception{
    if (prefix==null || !prefix.startsWith("@"))  return AbstractParentChildCreator.BRANCH_NODE; 
    return childType;
    }
/**
 *  evaluate prefix:<br>
 *   
 *   &lt;prefix&gt;-   :  &lt;prefix&gt;childId<br>
 *   -&lt;prefix&gt;   :  childId.&lt;prefix&gt;<br>
 *   @&lt;prefix&gt;   :  any function (returns @&lt;prefix&gt; if function not implemented)<br>
 *   /&lt;prefix&gt;/..:  recursive call of evalPrefix<br>
 */
  private String evalPrefix(String prefix,String childId) {
      if (prefix.startsWith("/")) {
        String s=prefix.substring(1);
	int i=s.indexOf("/");
	if (i<0) return evalPrefix(s,childId);
	return evalPrefix(s.substring(0,i),childId)+"/"+evalPrefix(s.substring(i),childId);
           // without childid
      }
      if (prefix.startsWith("@")) {
        if (prefix.equals("@millis")) return ""+System.currentTimeMillis();
	return prefix; // unknown      
      }
      if (prefix.startsWith("-")) {
	return childId+"."+prefix.substring(1); //        
      }
      if (prefix.endsWith("-")) return prefix.substring(0,prefix.length()-1)+childId;
      return prefix;
  }
 
/**
 *
 */
  public String generateTreeId(String childId,short childType) throws Exception{
    if (prefix!=null) return evalPrefix(prefix,childId);       
    if(childType == AbstractParentChildCreator.BRANCH_NODE){
      return childId;
      }
    return childId+".xml";
    }
/**
 *
 */ 
  public void create(File samplesDir,File parentDir,String childId,short childType) throws Exception{
    String filename=null;
    String filenameMeta=null;
    if (prefix==null) {
      if(childType == AbstractParentChildCreator.BRANCH_NODE){
        filename=parentDir+"/"+childId+"/index.xml";
        filenameMeta=parentDir+"/"+childId+"/index-meta.xml";
        } 
      else if(childType == AbstractParentChildCreator.LEAF_NODE){
        filename=parentDir+"/"+childId+".xml";
        filenameMeta=parentDir+"/"+childId+"-meta.xml";
        }
    }
    else {
      filename=parentDir+"/"+generateTreeId(childId,childType)+fname;
      filenameMeta=parentDir+"/"+generateTreeId(childId,childType)+fnameMeta;    
    }  
    String doctypeSample=samplesDir+"/"+docNameSample; //  "/Group.xml"
    String doctypeMeta=samplesDir+"/"+docNameMeta;     //  Meta.xl
    copyFile(new File(doctypeSample),new File(filename));
    copyFile(new File(doctypeMeta),new File(filenameMeta));
  }
}
