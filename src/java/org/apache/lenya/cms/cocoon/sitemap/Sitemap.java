package org.wyona.cms.cocoon.sitemap;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Michael Wechner
 * @version 2002.3.18
 */
public class Sitemap{
  private File file=null;
  private Document doc=null;
/**
 *
 */
  public static void main(String[] args){
    if(args.length != 4){
      System.err.println("Usage: root-sitemap pattern prefix sub-sitemap");
      return;
      }
    try{
      File sitemapFile=new File(args[0]);
      Sitemap sitemap=new Sitemap(sitemapFile);
      sitemap.mount(args[1],args[2],args[3]);
      sitemap.save(sitemapFile);
      }
    catch(Exception e){
      System.err.println(".main(): "+e);
      }
    }
/**
 *
 */
  public Sitemap(File file) throws Exception{
    this.file=file;
    doc=new SAXReader().read("file:"+file.getAbsolutePath());
    }
/**
 *
 */
  public void save(File file) throws Exception{
    //System.out.println(doc.asXML());

    java.io.FileWriter fileWriter = new java.io.FileWriter(file.getAbsolutePath());
    doc.write(fileWriter);
    fileWriter.close();
    }
/**
 *
 */
  public void mount(String pattern,String uri_prefix,String src){
    Element pipelineElement=(Element)doc.selectSingleNode("/map:sitemap/map:pipelines/map:pipeline");
    Element matchElement=org.dom4j.DocumentHelper.createElement("map:match").addAttribute("pattern",pattern);
    java.util.List list=pipelineElement.content();
    list.add(0,matchElement);
    matchElement.addElement("map:mount").addAttribute("uri-prefix",uri_prefix).addAttribute("src",src).addAttribute("check-reload","true").addAttribute("reload-method","synchron");
    }
  }
