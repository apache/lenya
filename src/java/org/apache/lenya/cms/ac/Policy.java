package org.wyona.cms.ac;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.apache.log.Logger;
import org.apache.xpath.XPathAPI;

/**
 * @author Michael Wechner
 * @created 1.12.23
 * @version 1.12.28
 */
public class Policy{
  private Document doc=null;
  private Logger logger=null;
  //private static String ROOT="policy/object[@match=\"/\"]";
  private static String ROOT="ac/policy/object[@match=\"/\"]";
  private static String SUBJECTS="subjects";
  private static String ACTIONS="actions";
/**
 *
 */
  public Policy(Document doc,org.apache.log.Logger logger){
    this.doc=doc;
    this.logger=logger;
    }
/**
 *
 */
  public boolean authorizeWorld(String action){
    String xpath="/"+ROOT+"/"+SUBJECTS+"/world/"+ACTIONS+"/"+action;
    return checkXPath(xpath);
    }
/**
 *
 */
  public boolean authorizeMachine(String action,String ip){
    String xpath="/"+ROOT+"/"+SUBJECTS+"/machine[@ip='"+ip+"']/"+ACTIONS+"/"+action;
    return checkXPath(xpath);
    }
/**
 *
 */
  public boolean authorizeUser(String action,String id){
    String xpath="/"+ROOT+"/"+SUBJECTS+"/user[@id='"+id+"']/"+ACTIONS+"/"+action;
    return checkXPath(xpath);
    }
/**
 *
 */
  public boolean authorizeGroup(String action,String id){
    String xpath="/"+ROOT+"/"+SUBJECTS+"/group[@id='"+id+"']/"+ACTIONS+"/"+action;
    return checkXPath(xpath);
    }
/**
 *
 */
  private boolean checkXPath(String xpath){
    try{
      Node node=XPathAPI.selectSingleNode(doc,xpath);
      if(node != null){
        logger.debug("XPath exists: "+xpath);
        return true;
        }
      }
    catch(Exception e){
      logger.error(""+e);
      }
    logger.debug("No such XPath: "+xpath);
    return false;
    }
  }
