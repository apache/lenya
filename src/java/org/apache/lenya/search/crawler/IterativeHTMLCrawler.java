package org.wyona.search.crawler;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import websphinx.RobotExclusion;

/**
 *
 */
public class IterativeHTMLCrawler{
  java.util.Vector urlsToCrawl;
  java.util.TreeSet urlsToCrawlLowerCase;

  String url_list_file="url_file.txt";
  String html_dump_directory="html_dump";


  private String rootURL;
  //private String currentURLPath;
  //private java.util.Stack linkQueue;
  //private java.util.TreeSet links;
  private String[] scopeURL;

  private RobotExclusion robot;
/**
 *
 */
  public static void main(String[] args){
    if(args.length != 1){
    //if(args.length != 4){
      System.err.println("Usage: IterativeHTMLCrawler crawler.xconf");
      return;
      }
 
    try{
      CrawlerEnvironment ce=new CrawlerEnvironment(args[0]);
      new IterativeHTMLCrawler(ce.resolvePath(ce.getURIList()),ce.resolvePath(ce.getHTDocsDumpDir()),ce.getUserAgent()).crawl(new URL(ce.getBaseURL()),ce.getScopeURL());
      }
    catch(MalformedURLException e){
      System.err.println(e);
      }
    }
/**
 *
 */
  public IterativeHTMLCrawler(String url_list_file,String html_dump_directory,String userAgent){
    this.url_list_file=url_list_file;
    this.html_dump_directory=html_dump_directory;

    robot=new RobotExclusion(userAgent);
    }
/**
 * 
 */
  public void crawl(URL start,String scope){
    scopeURL=new String[1];
    scopeURL[0]=scope;

    String seedURL=start.toString();
    this.rootURL = seedURL.substring(0, seedURL.indexOf("/", 8));    

    urlsToCrawl=new java.util.Vector();
    urlsToCrawlLowerCase=new java.util.TreeSet();

    String currentURLPath = start.toString().substring(0, start.toString().lastIndexOf("/"));
    try{
      if(addURL(start.getFile(),currentURLPath) != null){
        dumpHTDoc(start);
        }
      }
    catch(MalformedURLException e){
      System.err.println(".crawl(): ERROR: "+e);
     }

    int currentPosition=0;
    while(currentPosition < urlsToCrawl.size()){
      URL currentURL=(URL)urlsToCrawl.elementAt(currentPosition);
      currentURLPath = currentURL.toString().substring(0, currentURL.toString().lastIndexOf("/"));

      System.out.println(".crawl(): INFO: Current Array Size: "+urlsToCrawl.size()+", Current Position: "+currentPosition+", Current URL: "+currentURL.toString());

      java.util.List urlsWithinPage=parsePage(currentURL.toString());
      if(urlsWithinPage != null){
        java.util.Iterator iterator=urlsWithinPage.iterator();
        while(iterator.hasNext()){
          String urlCandidate=(String)iterator.next();
          try{
            //System.out.println(".crawl(): DEBUG: URL candidate: "+urlCandidate);
            URL urlToCrawl=null;
            if((urlToCrawl=addURL(urlCandidate,currentURLPath)) != null){
              dumpHTDoc(urlToCrawl);
              }
            }
          catch(MalformedURLException e){
            System.err.println(".crawl(): ERROR: "+e);
            }
          }
        }

      currentPosition=currentPosition+1;
      //currentPosition=currentPosition+10000;
      }



    try{
      java.io.PrintWriter out=new java.io.PrintWriter(new java.io.FileOutputStream(url_list_file));
      System.out.println("\n\n\n");
      System.out.println(".crawl(): URLs to crawl:");
      for(int i=0;i<urlsToCrawl.size();i++){
        System.out.println(".crawl(): INFO: URL to crawl: "+(URL)urlsToCrawl.elementAt(i));
        out.println(""+(URL)urlsToCrawl.elementAt(i));
        }
      out.close();
      }
    catch(java.io.FileNotFoundException e){
      System.err.println(".crawl(): ERROR: "+e);
      }
    }
/**
 *
 */
  public URL addURL(String urlCandidate,String currentURLPath) throws MalformedURLException{
    URL url=new URL(parseHREF(urlCandidate,urlCandidate.toLowerCase(),currentURLPath)); //completeURL(currentURL,urlCandidate)  new URL(currentURLPath+"/"+urlCandidate);

    if(filterURL(urlCandidate,currentURLPath,urlsToCrawlLowerCase)){
      if(!robot.disallowed(url)){
        urlsToCrawl.add(url);
        urlsToCrawlLowerCase.add(url.toString().toLowerCase());
        System.out.println(".addURL(): INFO: URL added: "+url); 
        return url;
        }
      else{
        System.out.println(".addURL(): INFO: Disallowed by robots.txt: "+urlCandidate);
        }
      }

    System.out.println(".addURL(): INFO: URL not added: "+urlCandidate);
    return null;
    }
/**
 * @return ok, 404
 */
  public java.util.List parsePage(String urlString){
    System.out.println(".parsePage(): INFO: Parse page: "+urlString);

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
                    return handleHTML(httpCon);
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

        //return status;
        return null;
    }
/**
 *
 */
  public static java.util.List handleHTML(HttpURLConnection httpCon) throws java.io.IOException{
    ContentHandler handler = new HTMLHandler();
    handler.parse(httpCon.getInputStream());

    if(handler.getRobotFollow()){
      java.util.List links = handler.getLinks();
      System.out.println(".handleHTML(): Number of links found : " + links.size());

/*
      for(int i = 0; i < links.size(); i++){
        System.out.println(".handleHTML(): DEBUG: "+(String)links.get(i));
        }
*/

      if(true){
        return links;
        }
      }

/*
    if(handler.getRobotIndex()){
      System.out.println(".handleHTML(): Index by Lucene: Crawler.indexLucene()");
      }
*/
    return null;
    }
/**
 *
 */
  public void handlePDF(HttpURLConnection httpCon){
    System.err.println(".handlePDF(): Not handled yet!");
    }
/**
 *
 */
  public boolean filterURL(String url,String currentURLPath,java.util.TreeSet links){
    System.out.println(".filterURL(): DEBUG: Filtering URL: "+url+" ("+currentURLPath+")");

    String urlLowCase = url.toLowerCase();

    if(!(urlLowCase.startsWith("http://") || urlLowCase.startsWith("https://"))){
      url = parseHREF(url,urlLowCase,currentURLPath);
      if(url != null) {
        urlLowCase = url.toLowerCase();
        }
      }
    else{
      //System.out.println(".filterURL(): INFO: URL starts with \"http://\" or \"https://\": "+urlLowCase);
      }

    //System.out.println(".filterURL(): "+urlLowCase);

    if(url != null && inScope(urlLowCase)){
      //System.out.println(".filterURL(): In scope: "+url);
      if(!links.contains(urlLowCase)){
        //links.add(urlLowCase);
        //System.out.println(".filterURL(): Added: "+urlLowCase);
        return true;
        }
      else{
        //System.out.println(".filterURL(): Not Added (already added) : "+url);
        }
      }
    else{
      //System.out.println(".filterURL(): Not in scope: "+url);
      }
    return false;
    }
/**
 *
 */
  public String parseHREF(String url, String urlLowCase,String currentURLPath){
    //System.out.println(".parseHREF(): "+url);
    //System.out.println(".parseHREF(): "+urlLowCase);
    //System.out.println(".parseHREF(): "+currentURLPath);

    if(urlLowCase.startsWith("http://") || urlLowCase.startsWith("https://")){
      return url;
      }

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
            System.err.println(".parseHREF(): WARN: parseJavaScript() is not implemented yet");
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
  public boolean inScope(String url){
    //System.out.println(".inScope(): "+url);
    for(int i = 0; i < scopeURL.length; i++){
      if(url.startsWith(scopeURL[i])){
        return true;
        }
      }
    return false;
    }
/**
 *
 */
  public URL completeURL(URL parent,String child) throws MalformedURLException{
    System.out.println(".completeURL(): "+parent);
    System.out.println(".completeURL(): "+child);
    return parent;
    }
/**
 *
 */
  public void dumpHTDoc(URL url){
    String ext=getExtension(url);

    String filename=html_dump_directory+url.getFile();
    File file=new File(filename);
    if(filename.charAt(filename.length()-1) == '/'){
      file=new File(filename+"index.html");
      ext=getExtension(file);
      }

    //System.out.println(".dumpHTDoc(): DEBUG: Extension: "+ext); 

    if(ext.equals("html") || ext.equals("htm") || ext.equals("txt") || ext.equals("pdf")){
      try{
        File parent=new File(file.getParent());
        if(!parent.exists()){
          System.out.println(".dumpHTDoc(): INFO: Directory will be created: "+parent.getAbsolutePath());
          parent.mkdirs();
          }
        FileOutputStream out=new FileOutputStream(file.getAbsolutePath());
          HttpURLConnection httpConnection=(HttpURLConnection)url.openConnection();
          java.io.InputStream in=httpConnection.getInputStream();
          byte[] buffer=new byte[1024];
          int bytes_read;
          while((bytes_read=in.read(buffer)) != -1){
            out.write(buffer,0,bytes_read);
            }
          httpConnection.disconnect();
        out.close();



// In certain cases (e.g. large zip files) buffering generates an OutOfMemoryException (JVM has 60MB by default)
/*
        HttpURLConnection httpConnection=(HttpURLConnection)url.openConnection();
        java.io.InputStream in=httpConnection.getInputStream();
        byte[] buffer=new byte[1024];
        int bytes_read;
        java.io.ByteArrayOutputStream bufferOut=new java.io.ByteArrayOutputStream();
        while((bytes_read=in.read(buffer)) != -1){
          bufferOut.write(buffer,0,bytes_read);
          }
        byte[] sresponse=bufferOut.toByteArray();
        httpConnection.disconnect();

        saveToFile(html_dump_directory+url.getFile(),sresponse);
*/


        System.out.println(".dumpHTDoc(): INFO: URL dumped: "+url);
        }
      catch(Exception e){
        System.err.println(".dumpHTDoc(): ERROR: "+e);
        System.out.println(".dumpHTDoc(): ERROR: URL not dumped: "+url);
        }
      }
    else{
      System.out.println(".dumpHTDoc(): INFO: URL not dumped: "+url);
      }
    }
/**
 *
 */
  public void saveToFile(String filename,byte[] bytes) throws FileNotFoundException, IOException{
    File file=new File(filename);
    if(filename.charAt(filename.length()-1) == '/'){
      file=new File(filename+"index.html");
      }
    File parent=new File(file.getParent());
    if(!parent.exists()){
      System.out.println(".saveToFile(): Directory will be created: "+parent.getAbsolutePath());
      parent.mkdirs();
      }
    FileOutputStream out=new FileOutputStream(file.getAbsolutePath());
    out.write(bytes);
    out.close();

    //System.out.println(".saveToFile(): DEBUG: File saved: "+filename);
    }
/**
 *
 */
  public String getExtension(URL url){
    return getExtension(new File(url.getPath()));
/*
    StringTokenizer st=new StringTokenizer(url.getPath(),".");
    String extension=null;
    while(st.hasMoreElements()){
      extension=st.nextToken();
      }
    return extension;
*/
    }
/**
 *
 */
  public String getExtension(File file){
    StringTokenizer st=new StringTokenizer(file.getPath(),".");
    String extension=null;
    while(st.hasMoreElements()){
      extension=st.nextToken();
      }
    return extension;
    }
  }
