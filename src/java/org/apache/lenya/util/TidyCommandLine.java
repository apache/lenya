package org.wyona.util;

import org.w3c.tidy.Tidy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;

/**
 *
 */
public class TidyCommandLine{
/** 
 *
 */
  public static void main(String[] args){
    if(args.length != 3){
      System.err.println("Usage: java "+new TidyCommandLine().getClass().getName()+" http://www.wyona.org index.xhtml error.log");
      return;
      }
    try{
      new TidyCommandLine().tidy(new URL(args[0]),new File(args[1]),new File(args[2]),true);
      }
    catch(Exception e){
      System.err.println(e);
      }
    }
/**
 *
 */
/*
  public Tidy(){
    }
*/
/**
 *
 */
  public void tidy(URL url,File file,File err,boolean xhtml) throws Exception{
    Tidy tidy=new Tidy();
    tidy.setXmlOut(xhtml);
    tidy.setErrout(new PrintWriter(new FileWriter(err.getAbsolutePath()),true));
    BufferedInputStream in=new BufferedInputStream(url.openStream());
    FileOutputStream out=new FileOutputStream(file.getAbsolutePath());
    tidy.parse(in,out);
    }
  }
