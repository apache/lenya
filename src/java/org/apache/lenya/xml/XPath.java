/*
 <License>
 </License>
 */

package org.wyona.xml;

import java.util.StringTokenizer;

import org.w3c.dom.Node;

/**
 * @author Michael Wechner
 * @created 1.7.23
 * @version 1.7.24
 */
public class XPath{
 String xpath=null;
 String[] parts=null;
/**
 *
 */
 public XPath(String xpath){
  this.xpath=xpath;
  StringTokenizer st=new StringTokenizer(xpath,"/");
  int length=st.countTokens();
  parts=new String[length];
  for(int i=0;i<length;i++){
   parts[i]=st.nextToken();
   }
  }
/**
 *
 */
 public XPath getParent(){
  String parentXPath="";
  for(int i=0;i<parts.length-1;i++){
   parentXPath=parentXPath+"/"+parts[i];
   }
  return new XPath(parentXPath);
  }
/**
 *
 */
 public short getType(){
  if(parts[parts.length-1].indexOf("@") == 0){
   return Node.ATTRIBUTE_NODE;
   }
  return Node.ELEMENT_NODE;
  }
/**
 *
 */
 public String toString(){
  return xpath;
  }
/**
 *
 */
  public String getName(){
    if(getType() == Node.ATTRIBUTE_NODE){
      return parts[parts.length-1].substring(1);
      }
    return parts[parts.length-1];
    }
  /**
   * Describe 'getName' method here.
   *
   * @return a value of type 'String'
   */
  public String getElementName(){
    if(getType() == Node.ATTRIBUTE_NODE){
      return parts[parts.length-2];
      }
    return parts[parts.length-1];
    }
/**
 *
 */
  public String getNameWithoutPredicates(){
    return removePredicates(getName());
    }
/**
 * Remove predicates (square brackets), http://www.w3.org/TR/xpath
 */
  public String removePredicates(String s){
    int index=s.indexOf("[");
    if(index >= 0){
      return s.substring(0,index);
      }
    return s;
    }
 }
