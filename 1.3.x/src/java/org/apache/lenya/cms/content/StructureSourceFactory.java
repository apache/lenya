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
public class StructureSourceFactory extends AbstractContentSourceFactory {
   Source getResource(int requestType, Location location) throws MalformedURLException, IOException {
      System.out.println("StructureSourceFactory.getResource l=" + location.toString());
      /** ******** Get Source (uses Content) ************ */
      StringTokenizer tokens = new StringTokenizer(location.getUnid(), "/", true);
      String token = tokens.nextToken();
      // Remove leading slashes
      while(token.equals("/") && tokens.hasMoreTokens()){
         token = tokens.nextToken();
      }
      StringBuffer buffer = new StringBuffer();
      buffer.append(token);
      while(tokens.hasMoreTokens()){
         buffer.append(tokens.nextToken());
      }
      String structure = buffer.toString();
      System.out.println("StructureSourceFactory.getResource s=" + structure);
      Source source;
      FlatDesign design = super.publication.getDesign();
      if(REQUEST_INFO == requestType){
         Resource resource = design.getStructure(structure);
         if(resource == null){
            System.out.println("StructureSourceFactory.getResource MISSING INFO Resource=null");
            throw new SourceNotFoundException("Source not found (no Resource): " + location.toString());
         }
         Document doc = resource.getInfoDocument();
         if(doc == null){
            System.out.println("StructureSourceFactory.getResource MISSING INFO doc=null");
            throw new SourceNotFoundException("Source not found (no Document): " + location.toString());
         }
         source = new StringSource(manager, doc);
         if(source == null){
            System.out.println("StructureSourceFactory.getResource MISSING INFO source=null");
            throw new SourceNotFoundException("Source not found (no Source): " + location.toString());
         }
         System.out.println("StructureSourceFactory.getResource FOUND INFO " + location.toString());
         return source;
      }
      // Revision
      String revision = location.getRevision();
      if(0 == revision.length()){
         revision = Content.REVISION_DEFAULT;
      }
      if(REQUEST_META == requestType){
         source = resolver.resolveURI(design.getStructure(structure, revision).getMetaURI());
         if(source.exists()){
            System.out.println("StructureSourceFactory.getResource FOUND META " + location.toString());
            return source;
         }else{
            System.out.println("StructureSourceFactory.getResource MISSING INFO " + location.toString());
            throw new SourceNotFoundException("Source not found (no Meta Source): " + location.toString());
         }
      }
      String curi = design.getStructure(structure, revision).getURI();
      System.out.println("StructureSourceFactory.getResource REV=" + revision + " CURI='" + curi + "'");
      if(curi.length() < 1){
         System.out.println("StructureSourceFactory.getResource: No URI " + location.toString());
         throw new SourceNotFoundException("Source not found (no URI): " + location.toString() + " UNID=" + structure + " REV=" + revision);
      }
      source = resolver.resolveURI(curi);
      if(source.exists()){
         System.out.println("StructureSourceFactory.getResource FOUND " + location.toString());
         return source;
      }
      System.out.println("StructureSourceFactory.getResource MISSING " + location.toString());
      throw new SourceNotFoundException("Source not found: " + location.toString() + " (" + curi + ")");
   }
}
