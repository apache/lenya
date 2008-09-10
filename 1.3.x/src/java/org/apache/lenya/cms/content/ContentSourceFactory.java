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
public class ContentSourceFactory extends AbstractContentSourceFactory {
   Source getResource(int requestType, Location location) throws MalformedURLException, IOException {
      // System.out.println("ContentSourceFactory.getResource " + location.toString());
      /** ******** Get Source (uses Content) ************ */
      Content content = super.publication.getContent();
      StringTokenizer tokens = new StringTokenizer(location.getUnid(), "/:", true);
      int slashCount = 0;
      String token = tokens.nextToken();
      while(token.equals("/")){
         slashCount++;
         token = tokens.nextToken();
      }
      String structure = (2 < slashCount ? "" : token);
      String unid = token;
      StringBuffer buffer = new StringBuffer();
      while(tokens.hasMoreTokens()){
         buffer.append(tokens.nextToken());
      }
      String fullid = buffer.toString();
      if((1 < slashCount) || (0 < fullid.length())){
         unid = content.getUNID(structure, fullid);
      }
      // System.out.println("ContentSourceFactory.getResource unid=" + unid);
      Source source;
      if(REQUEST_INFO == requestType){
         // TODO: Catch errors
         Resource resource = content.getResource(unid);
         if(resource == null){
            System.out.println("ContentSourceFactory.getSource Null Resource UNID=" + unid);
            throw new SourceNotFoundException("Source not found (no Resource): " + location.toString());
         }
         Document doc = resource.getInfoDocument();
         if(doc == null){
            System.out.println("ContentSourceFactory.getSource Null Document UNID=" + unid);
            throw new SourceNotFoundException("Source not found (no Document): " + location.toString());
         }
         source = new StringSource(manager, doc);
         if(source == null){
            System.out.println("ContentSourceFactory.getSource Null Source UNID=" + unid);
            throw new SourceNotFoundException("Source not found (no Source): " + location.toString());
         }
         return source;
      }
      // Revision
      String revision = location.getRevision();
      if(0 == revision.length()){
         revision = Content.REVISION_DEFAULT;
      }
      // Language
      String language = location.getLanguage();
      if((1 > language.length()) || (FlatDesign.DESIGN_LANGUAGE.equalsIgnoreCase(language))){
         Resource resource = content.getResource(unid);
         if(resource != null){
            language = resource.getDefaultLanguage();
         }
      }
      if((1 > language.length()) || (FlatDesign.DESIGN_LANGUAGE.equalsIgnoreCase(language))){
         language = publication.getDefaultLanguage();
      }
      if(REQUEST_META == requestType){
         source = resolver.resolveURI(content.getMetaURI(unid, language, revision));
         if(source.exists()){
            return source;
         }else{
            throw new SourceNotFoundException("Source not found (no Meta Source): " + location);
         }
      }
      String curi = content.getURI(unid, language, revision);
      // System.out.println("ContentSourceFactory.getSource UNID=" + unid + " LANG=" + language + " REV=" + revision + " CURI=" + curi);
      if(curi.length() < 1){ throw new SourceNotFoundException("Source not found (no URI): " + location + " UNID=" + unid + " LANG=" + language + " REV=" + revision); }
      source = resolver.resolveURI(curi);
      if(source.exists()){ return source; }
      throw new SourceNotFoundException("Source not found: " + location + " (" + curi + ")");
   }
}
