package org.wyona.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.util.Vector;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner
 * @version 2002.5.30
 */
public class XPSSourceInformation{
  static Category log=Category.getInstance(XPSSourceInformation.class);

  public int lineNumber=-1;
  public URL url=null;

   public XPSSourceInformation parentInfo=null;
   public Vector children=null;
   String offset=null;

   String cocoon=null;
/**
 *
 */
  public static void main(String[] args){
    String cocoon=null;
    XPSSourceInformation xpssf=new XPSSourceInformation(args[0],cocoon);
    System.out.println(xpssf);
    }
/**
 *
 */
  public XPSSourceInformation(String fileURL,String cocoon){
    this.cocoon=cocoon;

	  parentInfo=null;
	  offset="++";
	  children=new Vector();
          try
            {
            url=new URL(fileURL);
            }
          catch(MalformedURLException e)
            {
            log.error(e);
            }
          }
/**
 *
 */
  public XPSSourceInformation(String urlString,XPSSourceInformation parentInfo,String cocoon){
    this.cocoon=cocoon;

    this.parentInfo=parentInfo;
	  offset="++";
	  children=new Vector();

          try{
            if(urlString.indexOf("/") == 0){
              url=new URL("file:"+urlString);
              }
            else if(urlString.indexOf("cocoon:") == 0){
              log.warn("Protocol 7789: COCOON ("+urlString+") -- will be transformed into http");
              if(cocoon != null){
                url=new URL(cocoon+"/"+urlString.substring(7)); // remove "cocoon:" protocol
                }
              else{
                log.error("No cocoon base set!");
                }
              }
            else{
              url=new URL(urlString);
              }

            String p=url.getProtocol();
 
            // Does not make sense, because it will be either file or http, and else an Exception will be thrown!
            if(!(p.equals("http") || p.equals("file") || p.equals("class"))){
              log.error("This type of protocol is not supported yet: "+p);
              }

            }
          catch(MalformedURLException e) // let's hope it's a relative path
            {
            log.warn("1079: "+e+" -- Let's hope it's a relative path!");
            File parent=new File(parentInfo.url.getFile());
            File file=org.wyona.util.FileUtil.file(parent.getParent(),urlString);
            try
              {
              url=new URL("file",null,-1,file.getAbsolutePath());
              }
            catch(MalformedURLException exception)
              {
              log.error(exception);
              }
            }

          if(url.getProtocol().equals("http"))
            {
            }
          else if(url.getProtocol().equals("file"))
            {
            if(parentInfo.url.getProtocol().equals("http"))
              {
              String protocol=parentInfo.url.getProtocol();
              String host=parentInfo.url.getHost();
              int port=parentInfo.url.getPort();
              try
                {
                if(url.getRef() != null)
                  {
                  url=new URL(protocol,host,port,url.getFile()+"#"+url.getRef());
                  }
                else
                  {
                  url=new URL(protocol,host,port,url.getFile());
                  }
                }
              catch(MalformedURLException e)
                {
                log.error(e);
                }
              }
            }
          else
            {
            log.error("EXCEPTION: 0.2.21");
            }
          }
/**
 *
 */
     public void addChild(XPSSourceInformation child)
          {
		  children.addElement(child);
          }
/**
 *
 */
     public String toString()
          {
		  return toString("",offset);
          }
/**
 *
 */
     public String toString(String index,String offset)
          {
		  String s=index+url.toString()+"\n";
		  for(int i=0;i<children.size();i++)
		     {
		     XPSSourceInformation child=(XPSSourceInformation)children.elementAt(i);
		     s=s+child.toString(index+offset,offset);
		     }
		  return s;
          }
/**
 *
 */
     public boolean checkLoop(XPSSourceInformation xpssf,URL url)
          {
		  //System.err.println(xpssf.url.getFile()+" "+url.getFile());
		  if(xpssf.url.getFile().equals(url.getFile()))
            {
			if(xpssf.parentInfo != null)
              {
			  return true;
              }
			else
              {
			  return false; // This is just the request (it can be dummy.xml but also something real)
              }
            }
		  if(xpssf.parentInfo != null)
            {
			return checkLoop(xpssf.parentInfo,url);
            }
		  return false;
          }
     }
