package org.wyona.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Category;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

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
      byte[] response=wget.download(new URL(args[0]),"s/\\/wyona-cms\\/unipublic//g");
      //wget.saveToFile(new URL(args[0]).getFile(),response);
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




    String command="/usr/bin/wget "+"-P"+directory_prefix+" "+"--page-requisites"+" "+url;
    //String command="/usr/bin/wget "+"-P"+directory_prefix+" "+"--convert-links"+" "+"--page-requisites"+" "+url;
    try{
      log.info("WGET");
      byte[] wget_response=runProcess(command);


      log.info("SED");
      File file=new File(directory_prefix+"/127.0.0.1"+url.getFile());
      //File file=new File(directory_prefix+"/127.0.0.1:8080"+url.getFile());
      command="/usr/bin/sed --expression="+prefixSubstitute+" "+file.getAbsolutePath();
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
      log.error(".download(): "+e);
      }


    HttpClient httpClient=new HttpClient();

    HttpMethod httpMethod=new GetMethod();
    httpMethod.setRequestHeader("Content-type","text/plain");
    httpMethod.setPath(url.getPath());

    httpClient.startSession(url);
    httpClient.executeMethod(httpMethod);
    byte[] sresponse=httpMethod.getResponseBody();
    httpClient.endSession();

    //log.debug(".download(): Response from remote server: "+new String(sresponse));
    return sresponse;
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
    File file=new File(directory_prefix+filename);
    File parent=new File(file.getParent());
    if(!parent.exists()){
      parent.mkdirs();
      }
    FileOutputStream out=new FileOutputStream(file.getAbsolutePath());
    out.write(bytes);
    }
/**
 *
 */
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
    log.debug(".download(): InputStream: %%%S"+baout.toString()+"E%%%");


    java.io.InputStream in_e=process.getErrorStream();
    java.io.ByteArrayOutputStream baout_e=new java.io.ByteArrayOutputStream();
    while((bytes_read=in_e.read(buffer)) != -1){
      baout_e.write(buffer,0,bytes_read);
      }
    log.error(".download(): ErrorStream: ###S"+baout_e.toString()+"E###");

    return baout.toByteArray();
    }
  }
