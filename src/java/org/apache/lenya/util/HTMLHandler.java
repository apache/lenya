package org.wyona.util;

import java.util.ArrayList;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;

import org.apache.log4j.Category;

/**
 *
 */
public class HTMLHandler extends ParserCallback{
  Category log=Category.getInstance(HTMLHandler.class);

  private ArrayList img_src;
  private ArrayList img_src_all;
  private ArrayList a_href;
  private ArrayList a_href_all;
  private ArrayList link_href;
  private ArrayList link_href_all;
/**
 *
 */
  public HTMLHandler(){
    //log.debug("HTMLHandler(): Initialize");

    img_src_all=new ArrayList();
    img_src=new ArrayList();
    a_href_all=new ArrayList();
    a_href=new ArrayList();
    link_href_all=new ArrayList();
    link_href=new ArrayList();
    }
/**
 *
 */
  public void handleStartTag(Tag tag, MutableAttributeSet attributes, int pos){
    if(tag.equals(HTML.Tag.A)){
      //log.debug(".handleStartTag(): A");
      String href=(String)attributes.getAttribute(HTML.Attribute.HREF);
      if(href != null){
        a_href_all.add(href);
        if(!a_href.contains(href)){
          a_href.add(href);
          }
        }
      }
    else{
      //log.debug(".handleStartTag(): Something else: "+tag.toString());
      }
    }
/**
 *
 */
  public void handleSimpleTag(Tag tag, MutableAttributeSet attributes, int pos){
    if(tag.equals(HTML.Tag.IMG)){
      //log.debug(".handleSimpleTag(): IMG");
      String src=(String)attributes.getAttribute(HTML.Attribute.SRC);
      if(src != null){
        img_src_all.add(src);
        if(!img_src.contains(src)){
          img_src.add(src);
          }
        }
      }
    if(tag.equals(HTML.Tag.LINK)){
      //log.debug(".handleSimpleTag(): LINK");
      String href=(String)attributes.getAttribute(HTML.Attribute.HREF);
      if(href != null){
        link_href_all.add(href);
        if(!link_href.contains(href)){
          link_href.add(href);
          }
        }
      }
    else{
      //log.debug(".handleSimpleTag(): Something else: "+tag.toString());
      }
    }
/**
 *
 */
  public ArrayList getImageSrcs(){
    return img_src;
    }
/**
 *
 */
  public ArrayList getAllImageSrcs(){
    return img_src_all;
    }
/**
 *
 */
  public ArrayList getLinkHRefs(){
    return link_href;
    }
/**
 *
 */
  public ArrayList getAllLinkHRefs(){
    return link_href_all;
    }
/**
 *
 */
  public ArrayList getAHRefs(){
    return a_href;
    }
/**
 *
 */
  public ArrayList getAllAHRefs(){
    return a_href_all;
    }
  }
