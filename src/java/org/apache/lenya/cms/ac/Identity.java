package org.wyona.cms.ac;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.xpath.XPathAPI;

/**
 * @author Michael Wechner
 * @version 1.12.22
 */
public class Identity{
  private String username=null;
  private Vector groupnames=null;
  private static String ROOT="identity";
/**
 *
 */
  public static void main(String[] args){
    if(args.length != 1){
      System.err.println("Usage: org.wyona.cms.ac.Identity wyona.iml");
      return;
      }
    try{
      javax.xml.parsers.DocumentBuilderFactory dbf=javax.xml.parsers.DocumentBuilderFactory.newInstance();
      javax.xml.parsers.DocumentBuilder db=dbf.newDocumentBuilder();
      Document doc=db.parse(new java.io.FileInputStream(args[0]));
      Identity id=new Identity("dummy",doc);
      System.out.println(id);
      System.out.println(id.getPassword(doc));
      }
    catch(Exception e){
      System.err.println(".main(): "+e);
      }
    }
/**
 *
 */
  public Identity(String username){
    this.username=username;
    groupnames=new Vector();
    }
/**
 *
 */
  public Identity(String username,Document doc) throws Exception{
    this(username);
    NodeList groupNodes=XPathAPI.selectNodeList(doc,"/"+ROOT+"/groups/group");
    for(int i=0;i<groupNodes.getLength();i++){
      Node groupNode=groupNodes.item(i);
      addGroupname(groupNode.getFirstChild().getNodeValue());
      }
    }
/**
 *
 */
  public String getUsername(){
    return username;
    }
/**
 *
 */
  public static String getPassword(Document doc) throws Exception{
    Node passwordNode=XPathAPI.selectSingleNode(doc,"/"+ROOT+"/password");
    return passwordNode.getFirstChild().getNodeValue();
    }
/**
 *
 */
  public void addGroupname(String groupname){
    groupnames.addElement(groupname);
    }
/**
 *
 */
  public String[] getGroupnames(){
    String[] gn=new String[groupnames.size()];
    for(int i=0;i<gn.length;i++){
      gn[i]=(String)groupnames.elementAt(i);
      }
    return gn;
    }
/**
 *
 */
  public String toString(){
    String s="username="+username;
    for(int i=0;i<groupnames.size();i++){
      s=s+", groupname="+(String)groupnames.elementAt(i);
      }
    return s;
    }
  }
