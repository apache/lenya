package org.apache.lenya.cms.content;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.CocoonComponentManager;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.URIAbsolutizer;
import org.apache.lenya.cms.cocoon.components.source.impl.StringSource;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;
import org.w3c.dom.Document;
/**
 * Implements content: protocol. <BR>
 * This should call the Content API that calls a Content Impl. <BR>
 * <BR>== Content API == <BR>
 * Source getResourceByUNID(String unid, String translation, String revision) <BR>
 * Source getResourceByID(String structure, String id, String translation, String revision) <BR>
 * <BR>== Content Impls == <BR>
 * ContentHierarchical (Lenya 1.2) <BR>
 * ContentFlat (Lenya 1.3)
 * 
 * @author solprovider
 * @since 1.3
 */
public class ContentSourceFactory implements SourceFactory, ThreadSafe, URIAbsolutizer, Contextualizable {
   private static final int REQUEST_DATA = 0;
   private static final int REQUEST_META = 1;
   private static final int REQUEST_INFO = 2;
   protected org.apache.avalon.framework.context.Context context;
   // private String servletContextPath;
   String pubsPrefix;
   DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
   SourceResolver resolver = null;
   public void contextualize(org.apache.avalon.framework.context.Context context) throws ContextException {
      this.context = context;
   }
   public Source getSource(String location, Map parameters) throws IOException, MalformedURLException {
      String workLocation = location;
      int pos;
      // Map contextmap = ContextHelper.getObjectModel(context);
      // org.apache.cocoon.environment.http.HttpContext httpcontext = (org.apache.cocoon.environment.http.HttpContext) contextmap.get("context");
      // servletContextPath = httpcontext.getRealPath("");
      // TODO: Move resolver, pubsPrefix and other init out of getSource(). Make static?
      ComponentManager manager = CocoonComponentManager.getSitemapComponentManager();
      try{
         resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
      }catch(org.apache.avalon.framework.component.ComponentException ce){
      }
      if(null == resolver){
         throw new SourceNotFoundException("No Resolver: " + location);
      }
      String uri = resolver.resolveURI("").getURI();
      pos = uri.indexOf("/pubs/");
      if(pos > 0){
         pubsPrefix = uri.substring(0, pos + 6);
      }else{
         pos = uri.indexOf("/modules/");
         if(pos > 0){
            pubsPrefix = uri.substring(0, pos) + "/pubs/";
         }
      }
      // String publication;
      // String contentpath;
      Publication pub;
      Content content;
      try{
         PageEnvelope envelope = PageEnvelope.getCurrent();
         pub = envelope.getPublication();
         // publication = pub.getId();
         content = pub.getContent();
         // contentpath = pub.getContentDirectory().getAbsolutePath() + File.separator;
      }catch(org.apache.lenya.cms.publication.PageEnvelopeException pee){
         throw new MalformedURLException("Could not get Publication ID.");
      }
      // Parse Location
      Location locationParser = new Location(workLocation);
      // Removes everything after first period, underscore, or exclamation mark.
      workLocation = locationParser.getUnid();
      // Decide Usage
      StringTokenizer tokens = new StringTokenizer(workLocation, "/:", true);
      if(!tokens.hasMoreTokens())
         throw new MalformedURLException("Nothing specified.");
      String token = tokens.nextToken();
      if(workLocation.indexOf(":") > 0)
         token = tokens.nextToken(); // Remove protocol
      int colonCount = 0;
      while(token.equals(":")){
         colonCount++;
         token = (tokens.hasMoreTokens() ? tokens.nextToken() : "");
      }
      int slashCount = 0;
      while(token.equals("/")){
         slashCount++;
         token = (tokens.hasMoreTokens() ? tokens.nextToken() : "");
      }
      int requestType = colonCount - 1;
      boolean isFormat2 = false;
      if(token.equals("DATA")){
         requestType = REQUEST_DATA;
         isFormat2 = true;
      }else if(token.equals("META")){
         requestType = REQUEST_META;
         isFormat2 = true;
      }else if(token.equals("INFO")){
         requestType = REQUEST_INFO;
         isFormat2 = true;
      }
      if(isFormat2){
         token = tokens.nextToken();
         int slashCount2 = 0;
         while(token.equals("/")){
            slashCount2++;
            token = tokens.nextToken();
         }
         slashCount = (slashCount > slashCount2 ? slashCount : slashCount2);
      }
      String structure = (2 < slashCount ? "" : token);
      String unid = token;
      StringBuffer buffer = new StringBuffer();
      while(tokens.hasMoreTokens())
         buffer.append(tokens.nextToken());
      String fullid = buffer.toString();
      if((1 < slashCount) || (0 < fullid.length()))
         unid = content.getUNID(structure, fullid);
      // ASSUME: UNID
      /** ******** Get Source (uses Content) ************ */
      Source source;
      if(REQUEST_INFO == requestType){
         // TODO: Catch errors
         Resource resource = content.getResource(unid);
         if(resource == null){
            throw new SourceNotFoundException("Source not found (no Resource): " + location);
         }
         Document doc = resource.getInfoDocument();
         if(doc == null){
            throw new SourceNotFoundException("Source not found (no Document): " + location);
         }
         source = new StringSource(manager, doc);
         if(source == null){
            throw new SourceNotFoundException("Source not found (no Source): " + location);
         }
         return source;
      }
      // Revision
      String revision = locationParser.getRevision();
      if(0 == revision.length()){
         revision = "live";
      }
      // Language
      String language = locationParser.getLanguage();
      if(language.length() < 1){
         Resource resource = content.getResource(unid);
         if(resource != null){
            language = resource.getDefaultLanguage();
         }
      }
      if(language.length() < 1){
         language = pub.getDefaultLanguage();
      }
      if(REQUEST_META == requestType){
         source = resolver.resolveURI(content.getMetaURI(unid, language, revision));
         if(source.exists()){
            if(resolver != null)
               manager.release((Component) resolver);
            return source;
         }else{
            throw new SourceNotFoundException("Source not found (no Meta Source): " + location);
         }
      }
      String curi = content.getURI(unid, language, revision);
      // System.out.println("ContentSourceFactory.getSource UNID=" + unid + " LANG=" + language + " REV=" + revision + " CURI=" + curi);
      if(curi.length() < 1){
         throw new SourceNotFoundException("Source not found (no URI): " + location + " UNID=" + unid + " LANG=" + language + " REV=" + revision);
      }
      source = resolver.resolveURI(curi);
      if(resolver != null)
         manager.release((Component) resolver);
      if(source.exists()){
         return source;
      }
      throw new SourceNotFoundException("Source not found: " + location + " (" + curi + ")");
   }
   public void release(Source source1) {
   }
   public String absolutize(String baseURI, String location) {
      return SourceUtil.absolutize(baseURI, location, false, false);
   }
}
