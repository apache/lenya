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

import javax.swing.text.html.parser.ParserDelegator;

/**
 * http://developer.java.sun/developer/TechTips/1999/tt0923.html
 */
public class HTML{
  HTMLHandler htmlHandler;
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
    }
/**
 *
 */
  public HTML(String uri) throws IOException{
    ParserDelegator pd=new ParserDelegator();
    htmlHandler=new HTMLHandler();
    pd.parse(getReader(uri),htmlHandler,true);
    }
/**
 *
 */
  public List getAnchorHRefs(boolean duplicate){
    if(duplicate){
      return htmlHandler.getAllAHRefs();
      }
    else{
      return htmlHandler.getAHRefs();
      }
    }
/**
 *
 */
  public List getLinkHRefs(boolean duplicate){
    if(duplicate){
      return htmlHandler.getAllLinkHRefs();
      }
    else{
      return htmlHandler.getLinkHRefs();
      }
    }
/**
 *
 */
  public List getImageSrcs(boolean duplicate){
    if(duplicate){
      return htmlHandler.getAllImageSrcs();
      }
    else{
      return htmlHandler.getImageSrcs();
      }
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
