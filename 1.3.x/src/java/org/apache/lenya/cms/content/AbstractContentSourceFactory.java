package org.apache.lenya.cms.content;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
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
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.util.Globals;
/**
 * @author solprovider
 * @since 1.3
 */
public abstract class AbstractContentSourceFactory implements SourceFactory, ThreadSafe, URIAbsolutizer, Contextualizable {
   protected static final int REQUEST_DATA = 0;
   protected static final int REQUEST_META = 1;
   protected static final int REQUEST_INFO = 2;
   protected org.apache.avalon.framework.context.Context context;
   // private String pubsPrefix;
   // private DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
   protected SourceResolver resolver = null;
   protected Publication publication;
   // protected Content content;
   private Location location;
   protected ComponentManager manager;
   public void contextualize(org.apache.avalon.framework.context.Context context) throws ContextException {
      this.context = context;
   }
   public Source getSource(String src, Map parameters) throws IOException, MalformedURLException {
      // System.out.println("\nAbstractContentSourceFactory.getSource " + src.toString());
      try{
         manager = CocoonComponentManager.getSitemapComponentManager();
         try{
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
         }catch(org.apache.avalon.framework.component.ComponentException ce){
         }
         if(null == resolver){ throw new SourceNotFoundException("No Resolver: " + src); }
         publication = Globals.getPublication();
         // content = publication.getContent();
         location = new Location(src); // Parse Location: Removes everything after first period, underscore, or exclamation mark.
         String workLocation = location.getUnid();
         // Decide Usage
         StringTokenizer tokens = new StringTokenizer(workLocation, "/:", true);
         if(!tokens.hasMoreTokens()){ throw new MalformedURLException("No UNID specified."); }
         String token = tokens.nextToken();
         if(workLocation.indexOf(":") > 0) token = tokens.nextToken(); // Remove protocol
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
         // Sets request type without using constants
         int requestType = colonCount - 1;
         // Set request type from word overriding colon syntax.
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
         // Rebuild unid
         StringBuffer buffer = new StringBuffer();
         for(int s = 0; s < slashCount; s++){
            buffer.append("/");
         }
         buffer.append(token);
         while(tokens.hasMoreTokens()){
            buffer.append(tokens.nextToken());
         }
         location.setUnid(buffer.toString());
         // ASSUME: UNID
         return getResource(requestType, location);
      }finally{
         if(resolver != null) manager.release((Component) resolver);
      }
   }
   abstract Source getResource(int requestType, Location location) throws MalformedURLException, IOException;
   public void release(Source source1) {
   }
   public String absolutize(String baseURI, String location) {
      return SourceUtil.absolutize(baseURI, location, false, false);
   }
}
