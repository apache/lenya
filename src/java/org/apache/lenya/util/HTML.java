package org.wyona.util;

import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;
import java.net.URLConnection;

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
      html.getSrcs();
      html.getHRefs();
      html.getSrcs();
      html.getHRefs();
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
  public void getHRefs(){
    System.out.println(".getHRefs(): INFO: Extract Links");
    ElementIterator iterator=new ElementIterator(doc);
    Element element;
    while((element = iterator.next()) != null){
      System.out.println(".getHRefs(): Element Name: "+element.getName());
      if(element.getName().equals("a")){
        System.out.println(".getHRefs(): Element Name: "+element.getName());
        System.out.println(".getHRefs(): HRef Value: "+element.getAttributes().getAttribute(javax.swing.text.html.HTML.Attribute.HREF));
        }
      SimpleAttributeSet sas=(SimpleAttributeSet)element.getAttributes().getAttribute(javax.swing.text.html.HTML.Tag.A);
      //SimpleAttributeSet sas=(SimpleAttributeSet)element.getAttributes().getAttribute(javax.swing.text.html.HTML.Tag.LINK);
      if(sas != null){
        System.out.println(".getHRefs(): "+sas.getAttribute(javax.swing.text.html.HTML.Attribute.HREF));
        }
      }
    }
/**
 *
 */
  public void getSrcs(){
    System.out.println(".getSrcs(): INFO: Extract Sources");
    ElementIterator iterator=new ElementIterator(doc);
    Element element;
    while((element = iterator.next()) != null){
      SimpleAttributeSet sas=(SimpleAttributeSet)element.getAttributes().getAttribute(javax.swing.text.html.HTML.Tag.IMG);
      if(sas != null){
        System.out.println(".getSrcs(): "+sas.getAttribute(javax.swing.text.html.HTML.Attribute.SRC));
        }
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
