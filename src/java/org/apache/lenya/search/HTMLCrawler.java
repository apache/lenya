package org.wyona.search;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class HTMLCrawler{
  private String rootURL;
  //private String currentURLPath;
  private java.util.Stack linkQueue;
  private java.util.TreeSet links;
  private String[] scopeURL;
/**
 *
 */
  public static void main(String[] args){
    if(args.length != 1){
      System.err.println("Usage: HTMLCrawler http://127.0.0.1:48080/index.html");
      return;
      }
 
    try{
      new HTMLCrawler().crawl(new URL(args[0]));
      }
    catch(MalformedURLException e){
      System.err.println(e);
      }
    }
/**
 * 
 */
  public void crawl(URL start){
    linkQueue=new java.util.Stack();
    links=new java.util.TreeSet();
    scopeURL=new String[1];
    //scopeURL[0]="http://www.wyona.com/";
    scopeURL[0]="http://www.nzz.ch/";
    //scopeURL[0]="http://"+start.getHost()+":"+start.getPort()+"/";

    String seedURL=start.toString();
    links.add(seedURL.toLowerCase());
    linkQueue.push(seedURL);
    this.rootURL = seedURL.substring(0, seedURL.indexOf("/", 8));    

    String status=parsePage(start.toString());
    System.out.println(".crawl(): "+status);

    java.util.Iterator iterator=links.iterator();
    while(iterator.hasNext()){
      System.out.println(".crawl(): "+iterator.next()); 
      }

    System.out.println(".crawl(): "+linkQueue.toString());
    }
/**
 * @return ok, 404
 */
  public String parsePage(String urlString){
    System.out.println(".parsePage(): Parse page: "+urlString);
    String status = "ok";
        try {
            URL currentURL = new java.net.URL(urlString);
            String currentURLPath = urlString.substring(0, urlString.lastIndexOf("/"));
            HttpURLConnection httpCon = (HttpURLConnection)currentURL.openConnection();

            httpCon.setRequestProperty("User-Agent", "Wyona Lucene Crawler");

/*
            if (cookie != null) {
                httpCon.setRequestProperty("Cookie", this.cookie);
            }
*/
            httpCon.connect();


            long lastModified = httpCon.getLastModified();

/*
            if (httpCon.getHeaderField("Set-Cookie") != null) {
                cookie = stripCookie(httpCon.getHeaderField("Set-Cookie"));
                if (reporting > 1) report.print(" got cookie : " + cookie);
            }
*/

            if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String contentType = httpCon.getContentType();
                if (contentType.indexOf("text/html") != -1) {
                    handleHTML(httpCon,currentURLPath);
                } else if (contentType.indexOf("application/pdf") != -1) {
                    handlePDF(httpCon);
                } else {
                    status = "Not an excepted content type : " + contentType;
                }
            } else {
                status = "bad";
            }
            httpCon.disconnect();
        } catch (java.net.MalformedURLException mue) {
            status = mue.toString();
        } catch (java.net.UnknownHostException uh) {
            status = uh.toString(); // Mark as a bad URL
        } catch (java.io.IOException ioe) {
            status = ioe.toString(); // Mark as a bad URL
        } catch (Exception e) {
            status = e.toString(); // Mark as a bad URL
        }

        return status;
    }
/**
 *
 */
  public void handleHTML(HttpURLConnection httpCon,String currentURLPath) throws java.io.IOException{
    ContentHandler handler = new HTMLHandler();
    handler.parse(httpCon.getInputStream());

    if(handler.getRobotFollow()){
      java.util.List links = handler.getLinks();
      System.out.println(".handleHTML(): Number of links found : " + links.size());
      for(int i = 0; i < links.size(); i++){
        System.out.println(".handleHTML(): "+(String)links.get(i));
        handleLink((String)links.get(i),currentURLPath);
        }
      }

    if(handler.getRobotIndex()){
      System.out.println(".handleHTML(): Index by Lucene: Crawler.indexLucene()");
      }
    }
/**
 *
 */
  public void handlePDF(HttpURLConnection httpCon){
    System.out.println(".handlePDF(): Not handled yet!");
    }
/**
 *
 */
  public void handleLink(String url,String currentURLPath){
    String urlLowCase = url.toLowerCase();

    if(!(urlLowCase.startsWith("http://") || urlLowCase.startsWith("https://"))){
      url = parseHREF(url,urlLowCase,currentURLPath);
      if(url != null) {
        urlLowCase = url.toLowerCase();
        }
      }

    if(url != null && inScope(urlLowCase)){
      System.out.println(".handleLink(): In scope: "+url);
      if(!links.contains(urlLowCase)){
        links.add(urlLowCase);
        linkQueue.push(url);
        System.out.println(".handleLink(): Added: "+url);
        String status=parsePage(url);
        System.out.println(".crawl(): "+status);
        }
      else{
        System.out.println(".handleLink(): Not Added (already added) : "+url);
        }
      }
    else{
      System.out.println(".handleLink(): Not in scope: "+url);
      }
    }
/**
 *
 */
    public String parseHREF(String url, String urlLowCase,String currentURLPath) {

        // Looks for incomplete URL and completes them
        if (urlLowCase.startsWith("/")) {
            url = rootURL + url;
        } else if (urlLowCase.startsWith("./")) {
            url = currentURLPath + url.substring(1, url.length());
        } else if (urlLowCase.startsWith("../")) {
            int back = 1;
            while (urlLowCase.indexOf("../", back*3) != -1) back++;
            int pos = currentURLPath.length();
            int count = back;
            while (count-- > 0) {
                pos = currentURLPath.lastIndexOf("/", pos) - 1;
            }
            url = currentURLPath.substring(0, pos+2) + url.substring(3*back, url.length());
        } else if (urlLowCase.startsWith("javascript:")) {
            // handle javascript:...
            //url = parseJavaScript(url, urlLowCase);
            System.err.print(".parseHREF(): parseJavaScript is not implemented yet.");
        } else if (urlLowCase.startsWith("#")) {
            // internal anchor... ignore.
            url = null;
        } else if (urlLowCase.startsWith("mailto:")) {
            // handle mailto:...
            url = null;
        } else {
            url = currentURLPath + "/" + url;
        }

        // strip anchor if exists otherwise crawler may index content multiple times
        // links to the same url but with unique anchors would be considered unique
        // by the crawler when they should not be
        //int i;
        if (url != null) {
            int i;
            if ((i = url.indexOf("#")) != -1) {
                url = url.substring(0,i);
            }
        }
        return url;

    }
/**
 *
 */
    public boolean inScope(String url) {
       for (int i = 0; i < scopeURL.length; i++) {
            if (url.startsWith(scopeURL[i])) {
                // in scope
                return true;
            }
        }
        // not in scope
        return false;
       }
  }
