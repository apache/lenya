package org.wyona.lucene;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.apache.cocoon.components.crawler.CocoonCrawler;
import org.apache.cocoon.components.crawler.SimpleCocoonCrawlerImpl;

/**
 * 
 */
public class Main{
/**
 *
 */
public static void main(String[] args){
  if(args.length != 1){
    System.err.println("Usage: "+new Main().getClass().getName()+" uri");
    return;
    }
  try{
    new Main().crawl(new URL(args[0]));
    }
  catch(MalformedURLException e){
    System.err.println(e);
    }
  }
/**
 *
 */
public void crawl(URL start_url){
  CocoonCrawler crawler=new SimpleCocoonCrawlerImpl();
  System.out.println(start_url);
  crawler.crawl(start_url);
  Iterator i=crawler.iterator();
  while(i.hasNext()){
    URL url=(URL)i.next();
    System.out.println(url);
    }
  }
}
