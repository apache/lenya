package org.wyona.xml;

import java.util.List;

import org.dom4j.Element;

import org.apache.log4j.Category;

/**
 * @author Edith Chevrier
 * @version 2002.07.08
 */

public class DOM4JUtil {
  static Category log=Category.getInstance(DOM4JUtil.class);
  /**
   *
   */
  public DOM4JUtil(){
  }                                                                                                                                         

  /** insert the newElement as index-th child of the same parent like the element  
   * @param element element to define the parent node
   * @param newElement element to insert
   */
  public void insertElementAt(Element element, Element newElement, int index) {
    Element parent = element.getParent();
    List list = parent.content();
    list.add(index, newElement);  
  }

  /** insert the newElement before the element as child of the same node.
   * @param element element the newElement will be insert before this element
   * @param newElement element to insert
   */                                                                                                                                       
  public void insertElementBefore(Element element, Element newElement) {
    Element parent =element.getParent();
    insertElementAt(element, newElement, parent.indexOf(element));
  }                                                                                                                                         

  /** insert the newElement after the element as child of the same node.
   * @param element element the newElement will be insert after this element
   * @param newElement element to insert
   */                                                                                                                                       
  public void insertElementAfter(Element element, Element newElement) {
    Element parent =element.getParent();
    insertElementAt(element, newElement, parent.indexOf(element)+1);
  }                                                                                                                                         
}// DOM4JUtil
