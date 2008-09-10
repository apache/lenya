package org.apache.lenya.cms.content;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.cms.cocoon.components.source.impl.StringSource;
import org.apache.lenya.cms.content.flat.FlatDesign;
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
public class DesignSourceFactory extends AbstractContentSourceFactory {
   Source getResource(int requestType, Location location) throws MalformedURLException, IOException {
      // System.out.println("DesignSourceFactory.getResource " + location.toString());
      /** ******** Get Source (uses Content) ************ */
      StringTokenizer tokens = new StringTokenizer(location.getUnid(), "/", true);
      // int slashCount = 0;
      String token = tokens.nextToken();
      // Remove leading slashes
      while(token.equals("/") && tokens.hasMoreTokens()){
         // slashCount++;
         token = tokens.nextToken();
      }
      StringBuffer buffer = new StringBuffer();
      buffer.append(token);
      while(tokens.hasMoreTokens()){
         buffer.append(tokens.nextToken());
      }
      String filename = buffer.toString();
      Source source;
      FlatDesign design = super.publication.getDesign();
      if(REQUEST_INFO == requestType){
         Resource resource = design.getDesign(filename);
         if(resource == null){ throw new SourceNotFoundException("Source not found (no Resource): " + location.toString()); }
         Document doc = resource.getInfoDocument();
         if(doc == null){ throw new SourceNotFoundException("Source not found (no Document): " + location.toString()); }
         source = new StringSource(manager, doc);
         if(source == null){ throw new SourceNotFoundException("Source not found (no Source): " + location.toString()); }
         return source;
      }
      // Revision
      String revision = location.getRevision();
      if(0 == revision.length()){
         revision = Content.REVISION_DEFAULT;
      }
      if(REQUEST_META == requestType){
         source = resolver.resolveURI(design.getDesign(filename, revision).getMetaURI());
         // source = resolver.resolveURI(design.getResource(unid, revision).getMetaURI());
         if(source.exists()){
            return source;
         }else{
            throw new SourceNotFoundException("Source not found (no Meta Source): " + location.toString());
         }
      }
      String curi = design.getDesign(filename, revision).getURI();
      // System.out.println("ContentSourceFactory.getSource UNID=" + unid + " LANG=" + language + " REV=" + revision + " CURI=" + curi);
      if(curi.length() < 1){ throw new SourceNotFoundException("Source not found (no URI): " + location.toString() + " UNID=" + filename + " REV=" + revision); }
      source = resolver.resolveURI(curi);
      if(source.exists()){ return source; }
      throw new SourceNotFoundException("Source not found: " + location.toString() + " (" + curi + ")");
   }
}
