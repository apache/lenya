package org.apache.lenya.util;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.http.HttpContext;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.file.FilePublication;
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
   // UUID Generation (from Lenya 2)
   private static UUIDGen delegate;
   private static UUIDGen getDelegate() {
      if(delegate == null){
         delegate = UUIDGenFactory.getUUIDGen();
      }
      return delegate;
   }
   public static String createUUID() {
      return getDelegate().nextUUID();
   }
   // Lenya Functions
   public static String getAction() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getAction();
   }
   // null
   public static String getContentType() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getContentType();
   }
   public static final Context getContext() {
      Map objectModel = getObjectModel();
      if(null == objectModel) return (Context) null;
      return (Context) objectModel.get(ObjectModelHelper.CONTEXT_OBJECT);
   }
   /**
    * Use for modification times e.g. revision filenames.
    * 
    * @return current datetime (Date.getTime()) as String (Long.toString()).
    */
   public static String getDateString() {
      return Long.toString(new java.util.Date().getTime());
   }
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/authoring/
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/cache/module.xmap
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/lenya/modules/edit/
   // ?? Last use of map:mount
   public static String getEnvironmentContext() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getContext();
   }
   public static final Long getExpires() {
      return (Long) getObjectModel().get(ObjectModelHelper.EXPIRES_OBJECT);
   }
   public static String getModuleId() {
      // System.out.println("Globals.getModuleId");
      Source source = getSource();
      if(null == source) return "";
      return getModuleId(source.getURI());
   }
   /**
    * Retrieves ModuleId parsing after "modules". Works with filepath and URIs. Empty string returns empty string.
    * 
    * @param filepath
    * @return
    */
   public static String getModuleId(String filepath) {
      if(filepath.length() < 1){ return ""; }
      String ret = "";
      String[] strings = filepath.split("^(.*)modules");
      if(1 < strings.length){
         strings = strings[1].split("[/\\\\]");
         if(1 < strings.length){
            ret = strings[1];
         }
      }
      return ret;
   }
   /**
    * Retrieves PublicationId parsing after "pubs". Works with filepath and URIs. Empty string returns empty string.
    * 
    * @param filepath
    * @return
    */
   public static String getPublicationId(String filepath) {
      if(filepath.length() < 1){ return ""; }
      String ret = "";
      String[] strings = filepath.split("^(.*)pubs");
      if(1 < strings.length){
         strings = strings[1].split("[/\\\\]");
         if(1 < strings.length){
            ret = strings[1];
         }
      }
      return ret;
   }
   // null
   public static Map getObjectModel() {
      ContextMap contextMap = ContextMap.getCurrentContext();
      return (Map) contextMap.get("objectModel");
   }
   // public static Publication getPublication() {
   // try{
   // return PageEnvelope.getCurrent().getPublication();
   // }catch(PageEnvelopeException e){
   // // getPublication may be called without a request. Return null.
   // System.out.println("Globals.getPublication PageEnvelopeException: " + e.getLocalizedMessage());
   // }
   // return null;
   // }
   // public static Publication getPublication(String publicationId) {
   // try{
   // return PublicationFactory.getPublication(publicationId, getServletContextPath());
   // }catch(org.apache.lenya.cms.publication.PublicationException pe){
   // return (Publication) null;
   // }
   // }
   public static final Request getRequest() {
      try{
         return (Request) getObjectModel().get(ObjectModelHelper.REQUEST_OBJECT);
      }catch(NullPointerException npe){
      }
      return null;
   }
   public static final Response getResponse() {
      return (Response) getObjectModel().get(ObjectModelHelper.RESPONSE_OBJECT);
   }
   // file:///F:/eclipseWS/Lenya13x/build/lenya/webapp/
   // ??? servletContextPath with protocol
   public static String getRootContext() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getRootContext();
   }
   // Jetty/4.2
   public static String getServerName() {
      HttpContext hc = (HttpContext) getObjectModel().get("context");
      return hc.getServerInfo();
   }
   // F:\eclipseWS\Lenya13x\build\lenya\webapp
   // servletContextPath in OS' native format
   public static String getServletContextPath() {
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
   public static Source getSource() {
      // System.out.println("Globals.getSource");
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
   public static String getTempDir() {
      HttpContext hc = (HttpContext) getObjectModel().get("context");
      return (String) hc.getAttribute("javax.servlet.context.tempdir");
   }
   public static final Throwable getThrowable() {
      return (Throwable) getObjectModel().get(ObjectModelHelper.THROWABLE_OBJECT);
   }
   public static String getUser() {
      String userid = "anonymous";
      Request request = getRequest();
      if(null != request){
         Session session = request.getSession();
         if(session != null){
            Identity identity = (Identity) session.getAttribute(Identity.class.getName());
            if(identity != null){
               User user = identity.getUser();
               if(user != null) userid = user.getId();
            }
         }
      }
      return userid;
   }
   // authoring/index.html
   // edit
   // live/features_en.html
   // live/index.html
   // ??? The current match string for the XMAP.
   public static String getURI() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getURI();
   }
   // default13/
   public static String getURIPrefix() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getURIPrefix();
   }
   // null
   public static String getView() {
      HttpEnvironment he = (HttpEnvironment) getObjectModel().get("source-resolver");
      return he.getView();
   }
   // ###########################
   // ## Publication Functions ##
   // ###########################
   private static volatile Map keyToPublication = new HashMap();
   public static Publication getPublication() {
      // Creating publication from webapp URL and servlet context Webapp URL: [" + webappUrl + "]"
      Request request = getRequest();
      if(null == request){
         // This is acceptable as getPublication may be called without a request.
         return (Publication) null;
      }
      String publicationId = new URLInformation(ServletHelper.getWebappURI(request)).getPublicationId();
      Publication publication = getPublication(publicationId);
      return publication;
   }
   public static synchronized Publication getPublication(String publicationId) {
      File servletContext = new File(getContext().getRealPath(""));
      String servletContextPath = servletContext.getAbsolutePath();
      // String key = generatePublicationKey(id);
      // File servletContext = new File(getContext().getRealPath(""));
      // String key;
      String canonicalPath;
      try{
         canonicalPath = servletContext.getCanonicalPath();
      }catch(IOException e){
         System.out.println("Globals.generatePublicationKey: Publication ID='" + publicationId + "' IOException " + e.getLocalizedMessage());
         return (Publication) null;
      }
      String key = canonicalPath + "_" + publicationId;
      Publication publication = null;
      if(keyToPublication.containsKey(key)){
         publication = (Publication) keyToPublication.get(key);
         // System.out.println("Globals.getPublication ID='" + publicationId + "' Found");
      }else{
         if(existsPublication(publicationId)){
            try{
               publication = new FilePublication(publicationId, servletContextPath);
               // System.out.println("Globals.getPublication ID='" + publicationId + "' Created");
            }catch(PublicationException e){
               // This is acceptable as getPublication may be called without a request.
               System.out.println("Globals.getPublication ID='" + publicationId + "' PublicationException " + e.getLocalizedMessage());
               return (Publication) null;
            }
            keyToPublication.put(key, publication);
         }
      }
      if(publication == null){
         // This is acceptable as getPublication may be called without a request.
         System.out.println("Globals.getPublication: Publication ID='" + publicationId + "' could not be created.");
      }
      return publication;
   }
   public static boolean existsPublication(String id) {
      Context context = getContext();
      if(null == context) return false;
      File servletContext = new File(getContext().getRealPath(""));
      String servletContextPath = servletContext.getAbsolutePath();
      if(servletContextPath.endsWith("/")){
         servletContextPath = servletContextPath.substring(0, servletContextPath.length() - 1);
      }
      File publicationDirectory = new File(servletContextPath + File.separator + Publication.PUBLICATION_PREFIX + File.separator + id);
      boolean exists = true;
      exists = exists && publicationDirectory.isDirectory();
      exists = exists && new File(publicationDirectory, Publication.CONFIGURATION_FILE).exists();
      return exists;
   }
}
