package org.apache.lenya.util;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import org.apache.cocoon.environment.http.HttpContext;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.log.ContextMap;
/**
 * Static functions for accessing global and thread-based constants.
 * 
 * @author solprovider
 * @since 1.3
 * 
 */
public final class Globals {
   private Globals() {
   }
   // null
   static public String getAction() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpEnvironment he = (HttpEnvironment) map.get("source-resolver");
      return he.getAction();
   }
   // null
   static public String getContentType() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpEnvironment he = (HttpEnvironment) map.get("source-resolver");
      return he.getContentType();
   }
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/authoring/
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/cache/module.xmap
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/edit/
   // ?? Last use of map:mount
   static public String getContext() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpEnvironment he = (HttpEnvironment) map.get("source-resolver");
      return he.getContext();
  }
   static public String getModule() {
      Source source = getSource();
      if(null == source)
         return "";
      String ret = "";
      String[] strings = source.getURI().split("^(.*)modules");
      if(1 < strings.length){
         strings = strings[1].split("[/\\\\]");
         if(1 < strings.length){
            ret = strings[1];
         }
      }
      return ret;
   }
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/
   // ??? servletContextPath with protocol
   static public String getRootContext() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpEnvironment he = (HttpEnvironment) map.get("source-resolver");
      return he.getRootContext();
   }
   // Jetty/4.2
   static public String getServerName() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpContext hc = (HttpContext) map.get("context");
      return hc.getServerInfo();
   }
   // F:\eclipseWS\Lenya13x\build\lenya\webapp
   // servletContextPath in OS' native format
   static public String getServletContextPath() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpContext hc = (HttpContext) map.get("context");
      return hc.getRealPath("");
   }
   // Source.getURI() =
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/navigation/
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/nav/
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/authoring/
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/cache/module.xmap
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/edit/
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/live/
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/xhtml/
   // ??? The current XMAP (Lenya 1.3 defaults directories to "module.xmap")
   /**
    * FileSource
    */
   static public Source getSource() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpEnvironment he = (HttpEnvironment) map.get("source-resolver");
      try{
         return he.resolveURI("");
      }catch(MalformedURLException e){
         System.out.println("Globals.getSource MalformedURLException");
      }catch(SourceException e){
         System.out.println("Globals.getSource SourceException");
      }catch(IOException e){
         System.out.println("Globals.getSource IOException");
      }
      return null;
   }
   // C:\DOCUME~1\solprovider\LOCALS~1\Temp\Jetty__8888__
   static public String getTempDir() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpContext hc = (HttpContext) map.get("context");
      return (String) hc.getAttribute("javax.servlet.context.tempdir");
   }
   // authoring/index.html
   // edit
   // live/features_en.html
   // live/index.html
   // ??? The current match string for the XMAP.
   static public String getURI() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpEnvironment he = (HttpEnvironment) map.get("source-resolver");
      return he.getURI();
   }
   // default13/
   static public String getURIPrefix() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpEnvironment he = (HttpEnvironment) map.get("source-resolver");
      return he.getURIPrefix();
   }
   // null
   static public String getView() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      Map map = (Map) contextMap.get("objectModel");
      HttpEnvironment he = (HttpEnvironment) map.get("source-resolver");
      return he.getView();
   }
}
