package org.wyona.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Category;
/*
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
*/

/**
 * @author Michael Wechner
 * @version 0.1
 */
public class WGet{
  String directory_prefix=null;

  static Category log=Category.getInstance(WGet.class);
/**
 *
 */
  public static void main(String[] args){
    if(args.length == 0){
      System.out.println("Usage: org.wyona.net.WGet [URL] -P/home/wyona/download");
      return;
      }
    try{
      WGet wget=new WGet();

/*
      //System.out.println(wget.grep("src=\"(.*)\"","<img src=\"wyonacmsunipublicimagegif\" alt=\"Image\"> <br> <img src=\"/wyona-cms/unipublic/another_image.gif\" alt=\"Another Image\">"));
      System.out.println(wget.grep("src=S(.*)E","<img src=SwyonacmsunipublicimagegifE alt=SImageE> <br> <img src=S/wyona-cms/unipublic/another_image.gifE alt=SAnother ImageE>"));
      if(true){return;}
*/

      for(int i=0;i<args.length;i++){
        if(args[i].indexOf("-P") == 0){
          wget.setDirectoryPrefix(args[i].substring(2)); // -P/home/wyona/download, 2: remove "-P"
          }
        }
      System.out.println(wget);
      byte[] response=wget.download(new URL(args[0]),"s/\\/wyona-cms\\/oscom//g");
      //System.out.println(".main(): Response from remote server:\n\n"+new String(response));
      }
    catch(MalformedURLException e){
      System.err.println(e);
      }
    catch(Exception e){
      System.err.println(e);
      }
    }
/**
 *
 */
  public WGet(){
    directory_prefix=System.getProperty("user.dir");
    }
/**
 * -P
 */
  public void setDirectoryPrefix(String directory_prefix){
    this.directory_prefix=directory_prefix;
    }
/**
 *
 */
  public byte[] download(URL url,String prefixSubstitute) throws IOException, HttpException{
    log.debug(".download(): "+url);

    if(true){
      return downloadUsingHttpClient(url,prefixSubstitute);
      }
/*
    if(true){
      return downloadUsingWgetAndSed(url,prefixSubstitute);
      }
*/

    return null;
    }
/**
 *
 */
  public byte[] downloadUsingWgetAndSed(URL url,String prefixSubstitute) throws IOException, HttpException{
    log.debug(".downloadUsingWgetAndSed(): "+url);

    String command="/usr/bin/wget "+"-P"+directory_prefix+" "+"--page-requisites"+" "+url;
    //String command="/usr/bin/wget "+"-P"+directory_prefix+" "+"--convert-links"+" "+"--page-requisites"+" "+url;
    try{
      log.info("WGET");
      byte[] wget_response=runProcess(command);


      substitutePrefix(directory_prefix+"/127.0.0.1:48080"+url.getFile(),prefixSubstitute);
      //substitutePrefix(directory_prefix+"/127.0.0.1"+url.getFile(),prefixSubstitute);



      }
    catch(Exception e){
      log.error(".downloadUsingWgetAndSed(): "+e);
      }

    return null;
    }
/**
 *
 */
  public byte[] downloadUsingHttpClient(URL url,String prefixSubstitute) throws IOException, HttpException{
    log.debug(".downloadUsingHttpClient(): "+url);


/*
    HttpURLConnection httpConnection=(HttpURLConnection)url.openConnection();
    InputStream in=httpConnection.getInputStream();
    byte[] buffer=new byte[1024];
    int bytes_read;
    ByteArrayOutputStream bufferOut=new ByteArrayOutputStream();
    while((bytes_read=in.read(buffer)) != -1){
      bufferOut.write(buffer,0,bytes_read);
      }
    byte[] sresponse=bufferOut.toByteArray();
    httpConnection.disconnect();
*/
    
/*
    HttpClient httpClient=new HttpClient();

    HttpMethod httpMethod=new GetMethod();
    //httpMethod.setRequestHeader("Content-type","text/plain");
    httpMethod.setPath(url.getPath());

    httpClient.startSession(url);
    httpClient.executeMethod(httpMethod);
    byte[] sresponse=httpMethod.getResponseBody();
    httpClient.endSession();
*/


    byte[] sresponse=getResource(url);
    log.debug(".downloadUsingHttpClient(): Response from remote server: "+new String(sresponse));

    File file=new File(directory_prefix+File.separator+url.getHost()+":"+url.getPort()+url.getFile());
    saveToFile(file.getAbsolutePath(),sresponse);

    substitutePrefix(file.getAbsolutePath(),prefixSubstitute);

    getLinks(url);
    getMoreLinks(url);

    return sresponse;
    }
/**
 *
 */
  public byte[] getResource(URL url) throws IOException, HttpException{
    log.debug(".getResource(): "+url);


    HttpURLConnection httpConnection=(HttpURLConnection)url.openConnection();
    InputStream in=httpConnection.getInputStream();
    byte[] buffer=new byte[1024];
    int bytes_read;
    ByteArrayOutputStream bufferOut=new ByteArrayOutputStream();
    while((bytes_read=in.read(buffer)) != -1){
      bufferOut.write(buffer,0,bytes_read);
      }
    byte[] sresponse=bufferOut.toByteArray();
    httpConnection.disconnect();
    
/*
    HttpClient httpClient=new HttpClient();

    HttpMethod httpMethod=new GetMethod();
    //httpMethod.setRequestHeader("Content-type","text/plain");
    httpMethod.setPath(url.getPath());

    httpClient.startSession(url);
    httpClient.executeMethod(httpMethod);
    byte[] sresponse=httpMethod.getResponseBody();
    httpClient.endSession();
*/

    //log.debug(".getResource(): Response from remote server: "+new String(sresponse));
    return sresponse;
    }
/**
 *
 */
  public URL[] getLinks(URL url) throws IOException, HttpException{
    log.debug(".getLinks(): "+url);

    java.io.BufferedReader br=null;
    try{
      String linkViewQuery="cocoon-view=links";
      String sURL = url.getFile();
      URL links = new URL(url, sURL + ((sURL.indexOf("?") == -1) ? "?" : "&") + linkViewQuery);
      java.net.URLConnection links_url_connection = links.openConnection();
      java.io.InputStream is = links_url_connection.getInputStream();
      br = new java.io.BufferedReader(new java.io.InputStreamReader(is));
      String line;
      while((line = br.readLine()) != null){
        log.debug(".getLinks(): Link: "+line);
        }
      }
    catch(Exception e){
      log.error(".getLinks(): "+e);
      }

    return null;
    }
/**
 *
 */
  public URL[] getMoreLinks(URL url) throws IOException{
    log.debug(".getMoreLinks(): "+url);

    org.wyona.search.crawler.ContentHandler handler=new org.wyona.search.crawler.HTMLHandler();
    handler.parse(((HttpURLConnection)url.openConnection()).getInputStream());
    java.util.List links=handler.getLinks();

    java.util.Iterator iterator=links.iterator();
    while(iterator.hasNext()){
      log.debug(".getMoreLinks(): Another Link: "+(String)iterator.next());
      }

    return null;
    }
/**
 *
 */
  public byte[] substitutePrefix(String filename,String prefixSubstitute) throws IOException, HttpException{
    log.debug(".substitutePrefix(): "+filename+" "+prefixSubstitute);

    try{
      //File file=new File(directory_prefix+"/127.0.0.1"+url.getFile());
      //File file=new File(directory_prefix+"/127.0.0.1:48080"+url.getFile());
      File file=new File(filename);
      String command="/usr/bin/sed --expression="+prefixSubstitute+" "+file.getAbsolutePath();
      byte[] wget_response_sed=runProcess(command);

      java.io.ByteArrayInputStream bain=new java.io.ByteArrayInputStream(wget_response_sed);
      FileOutputStream fout=new FileOutputStream(file.getAbsolutePath());
      int bytes_read=0;
      byte[] buffer=new byte[1024];
      while((bytes_read=bain.read(buffer)) != -1){
        fout.write(buffer,0,bytes_read);
        }
      }
    catch(Exception e){
      log.error(".substitutePrefix(): "+e);
      }

    return null;
    }
/**
 *
 */
  public String toString(){
    return "-P: "+directory_prefix;
    }
/**
 *
 */
  public void saveToFile(String filename,byte[] bytes) throws FileNotFoundException, IOException{
    File file=new File(filename);
    File parent=new File(file.getParent());
    if(!parent.exists()){
      log.info(".saveToFile(): Directory will be created: "+parent.getAbsolutePath());
      parent.mkdirs();
      }
    log.debug(".saveToFile(): Filename: "+file.getAbsolutePath());
    FileOutputStream out=new FileOutputStream(file.getAbsolutePath());
    out.write(bytes);
    out.close();
    }
/**
 *
 */
/*
  public String grep(String pattern,String string){
    try{
      RE regexp=new RE(pattern);
      if(regexp.match(string)){
        log.debug("Pattern matched");
        for(int i=0;i<regexp.getParenCount();i++){
          log.debug("Parenthesis: "+regexp.getParen(i));
          }
        }
      else{
        log.debug("Pattern did not match");
        }
      return "Hello Levi";
      }
    catch(RESyntaxException e){
      log.error(e);
      return null;
      }
    }
*/
/**
 *
*/
  public byte[] runProcess(String command) throws Exception{
    Process process=Runtime.getRuntime().exec(command);


    java.io.InputStream in=process.getInputStream();
    byte[] buffer=new byte[1024];
    int bytes_read=0;
    java.io.ByteArrayOutputStream baout=new java.io.ByteArrayOutputStream();
    while((bytes_read=in.read(buffer)) != -1){
      baout.write(buffer,0,bytes_read);
      }
    if(baout.toString().length() > 0){
      log.debug(".runProcess(): %%%InputStream:START"+baout.toString()+"END:InputStream%%%");
      }


    java.io.InputStream in_e=process.getErrorStream();
    java.io.ByteArrayOutputStream baout_e=new java.io.ByteArrayOutputStream();
    while((bytes_read=in_e.read(buffer)) != -1){
      baout_e.write(buffer,0,bytes_read);
      }
    if(baout_e.toString().length() > 0){
      log.error(".runProcess(): ###ErrorStream:START"+baout_e.toString()+"END:ErrorStream###");
      }

    return baout.toByteArray();
    }
  }
