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
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * @author Michael Wechner
 */
public class WGet{
  String directory_prefix=null;
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
      for(int i=0;i<args.length;i++){
        if(args[i].indexOf("-P") == 0){
          wget.setDirectoryPrefix(args[i].substring(2)); // -P/home/wyona/download, 2: remove "-P"
          }
        }
      System.out.println(wget);
      byte[] response=wget.download(new URL(args[0]));
      wget.saveToFile(new URL(args[0]).getFile(),response);
      System.out.println(".main(): Response from remote server:\n\n"+new String(response));
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
  public byte[] download(URL url) throws IOException, HttpException{
    HttpClient httpClient=new HttpClient();

    HttpMethod httpMethod=new GetMethod();
    httpMethod.setRequestHeader("Content-type","text/plain");
    httpMethod.setPath(url.getPath());

    httpClient.startSession(url);
    httpClient.executeMethod(httpMethod);
    byte[] sresponse=httpMethod.getResponseBody();
    httpClient.endSession();

    //System.out.println(".download(): Response from remote server: "+new String(sresponse));
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
  public String grep(){
    try{
      RE regexp=new RE("src=\"*\"");
      boolean matched=regexp.match("src=\"/wyona-cms/unipublic/images.gif\"");
      return regexp.getParen(0);
      }
    catch(RESyntaxException e){
      return null;
      }
    }
  }
