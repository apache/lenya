package org.wyona.util;

import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLEditorKit;

/**
 * http://developer.java.sun/developer/TechTips/1999/tt0923.html
 */
public class HTML{
  Document doc;
/**
 *
 */
  public static void main(String[] args){
    if(args.length != 1){
      System.err.println("Usage: HTML uri (file or url)");
      return;
      }

    try{
      HTML html=new HTML(args[0]);

      List img_src_list=html.getImageSrcs(false);
      System.out.println("<im src");
      Iterator img_src_iterator=img_src_list.iterator();
      while(img_src_iterator.hasNext()){
        System.out.println((String)img_src_iterator.next());
        }

      List a_href_list=html.getAnchorHRefs(false);
      System.out.println("<a href");
      Iterator a_href_iterator=a_href_list.iterator();
      while(a_href_iterator.hasNext()){
        System.out.println((String)a_href_iterator.next());
        }

      List link_href_list=html.getLinkHRefs(false);
      System.out.println("<link href");
      Iterator link_href_iterator=link_href_list.iterator();
      while(link_href_iterator.hasNext()){
        System.out.println((String)link_href_iterator.next());
        }
      }
    catch(Exception e){
      System.err.println(".main(): "+e);
      }
    System.exit(1);
    }
/**
 *
 */
  public HTML(String uri) throws IOException, BadLocationException{
    Reader reader=getReader(uri);

    EditorKit ekit=new HTMLEditorKit();
    doc=ekit.createDefaultDocument();
    doc.putProperty("IgnoreCharsetDirective",Boolean.TRUE);
    ekit.read(reader,doc,0);
    }
/**
 *
 */
  public List getAnchorHRefs(boolean duplicate){
    List list=new ArrayList();
    //System.out.println(".getAnchorHRefs(): INFO: Extract Links");
    ElementIterator iterator=new ElementIterator(doc);
    Element element;
    while((element = iterator.next()) != null){
      // Extract <a href="">content</a>
      SimpleAttributeSet sas=(SimpleAttributeSet)element.getAttributes().getAttribute(javax.swing.text.html.HTML.Tag.A);
      if(sas != null){
        String href=(String)sas.getAttribute(javax.swing.text.html.HTML.Attribute.HREF);
        //System.out.println(".getAnchorHRefs(): <a href=\"\">content</a>: "+href);

        if(!duplicate){
          if(!list.contains(href)){
            list.add(href);
            }
          else{
            //System.out.println(".getAnchorHRefs(): List already contains: "+href);
            }
          }
        else{
          list.add(href);
          }

        }
      }
    return list;
    }
/**
 *
 */
  public List getLinkHRefs(boolean duplicate){
    List list=new ArrayList();
    //System.out.println(".getLinkHRefs(): INFO: Extract Links");
    ElementIterator iterator=new ElementIterator(doc);
    Element element;
    while((element = iterator.next()) != null){
      // Extract <link href=""/>
      if(element.getName().equals("link")){
        String href=(String)element.getAttributes().getAttribute(javax.swing.text.html.HTML.Attribute.HREF);
        //System.out.println(".getLinkHRefs(): <link href=\"\"/>: "+href);

        if(!duplicate){
          if(!list.contains(href)){
            list.add(href);
            }
          else{
            //System.out.println(".getLinkHRefs(): List already contains: "+href);
            }
          }
        else{
          list.add(href);
          }
        }
      }
    return list;
    }
/**
 *
 */
  public List getImageSrcs(boolean duplicate){
    List list=new ArrayList();
    //System.out.println(".getImageSrcs(): INFO: Extract Sources");
    ElementIterator iterator=new ElementIterator(doc);
    Element element;
    while((element = iterator.next()) != null){
      // Extract <im src=""/>
      if(element.getName().equals("img")){
        String src=(String)element.getAttributes().getAttribute(javax.swing.text.html.HTML.Attribute.SRC);
        //System.out.println(".getImageSrcs(): <im src=\"\"/>: "+src);

        if(!duplicate){
          if(!list.contains(src)){
            list.add(src);
            }
          else{
            //System.out.println(".getImageSrcs(): List already contains: "+src);
            }
          }
        else{
          list.add(src);
          }

        }
      }
    return list;
    }
/**
 *
 */
  private Reader getReader(String uri) throws IOException{
    if(uri.startsWith("http:")){
      // uri is url
      URLConnection connection=new URL(uri).openConnection();
      return new InputStreamReader(connection.getInputStream());
      }
    else{
      // uri is file
      return new FileReader(uri);
      }
    }
  }
