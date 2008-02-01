package org.apache.lenya.util;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.http.HttpContext;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.log.ContextMap;
/**
 * Static functions for accessing global and thread-based constants.
 * 
 * @author solprovider
 * @since 1.3
 * 
 */
public final class Globals {
   /**
    * Prevent instantiation.
    */
   private Globals() {
   }
   static public String getAction() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getAction();
   }
   // null
   static public String getContentType() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getContentType();
   }
   public static final Context getContext() {
      return (Context) getObjectModel().get(ObjectModelHelper.CONTEXT_OBJECT);
   }
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/authoring/
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/cache/module.xmap
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/edit/
   // ?? Last use of map:mount
   static public String getEnvironmentContext() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getContext();
   }
   public static final Long getExpires() {
      return (Long) getObjectModel().get(ObjectModelHelper.EXPIRES_OBJECT);
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
   // null
   static public Map getObjectModel() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      return (Map) contextMap.get("objectModel");
   }
   static public Publication getPublication() {
      try{
         return PageEnvelope.getCurrent().getPublication();
      }catch(PageEnvelopeException e){
         System.out.println("Globals.getPublication failed: " + e.getLocalizedMessage());
      }
      return null;
   }
   static public Publication getPublication(String publicationId) {
      try{
         return PublicationFactory.getPublication(publicationId, getServletContextPath());
      }catch(org.apache.lenya.cms.publication.PublicationException pe){
         return (Publication) null;
      }
   }
   public static final Request getRequest() {
      return (Request) getObjectModel().get(ObjectModelHelper.REQUEST_OBJECT);
   }
   public static final Response getResponse() {
      return (Response) getObjectModel().get(ObjectModelHelper.RESPONSE_OBJECT);
   }
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/
   // ??? servletContextPath with protocol
   static public String getRootContext() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getRootContext();
   }
   // Jetty/4.2
   static public String getServerName() {
      HttpContext hc = (HttpContext) getObjectModel().get("context");
      return hc.getServerInfo();
   }
   // F:\eclipseWS\Lenya13x\build\lenya\webapp
   // servletContextPath in OS' native format
   static public String getServletContextPath() {
      HttpContext hc = (HttpContext) getObjectModel().get("context");
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
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
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
      HttpContext hc = (HttpContext) getObjectModel().get("context");
      return (String) hc.getAttribute("javax.servlet.context.tempdir");
   }
   public static final Throwable getThrowable() {
      return (Throwable) getObjectModel().get(ObjectModelHelper.THROWABLE_OBJECT);
   }
   // authoring/index.html
   // edit
   // live/features_en.html
   // live/index.html
   // ??? The current match string for the XMAP.
   static public String getURI() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getURI();
   }
   // default13/
   static public String getURIPrefix() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getURIPrefix();
   }
   // null
   static public String getView() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getView();
   }
}
