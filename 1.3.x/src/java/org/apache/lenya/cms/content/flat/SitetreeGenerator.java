package org.apache.lenya.cms.content.flat;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.util.Globals;
import org.xml.sax.SAXException;
/**
 * The <code>SitetreeGenerator</code> is a class that reads XML from a source and generates SAX Events. The SitetreeGenerator implements the <code>CacheableProcessingComponent</code> interface.
 * 
 * @cocoon.sitemap.component.name sitetree
 * @cocoon.sitemap.component.label content
 * @cocoon.sitemap.component.logger sitemap.generator.sitetree
 * @cocoon.sitemap.component.documentation.caching
 * @cocoon.sitemap.component.pooling.min 8
 * @cocoon.sitemap.component.pooling.max 32
 * @cocoon.sitemap.component.pooling.grow 4
 * 
 * @author solprovider
 * @since 1.3
 */
public class SitetreeGenerator extends ServiceableGenerator implements CacheableProcessingComponent {
   /** The input source */
   protected Source inputSource;
   /**
    * Recycle this component. All instance variables are set to <code>null</code>.
    */
   public void recycle() {
      if(null != this.inputSource){
         super.resolver.release(this.inputSource);
         this.inputSource = null;
      }
      super.recycle();
   }
   /**
    * Setup the file generator. Try to get the last modification date of the source for caching.
    */
   public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par) throws ProcessingException, SAXException, IOException {
      if(null == src) src = "";
      super.setup(resolver, objectModel, src, par);
      try{
         PageEnvelope envelope;
         Publication pub = Globals.getPublication();
         if(null == pub){ throw new ProcessingException("SitetreeGenerator.setup: No Publication."); }
         String language = FlatContent.LANGUAGE_DEFAULT;
         try{
            envelope = PageEnvelope.getCurrent();
            language = envelope.getDocument().getLanguage();
         }catch(org.apache.lenya.cms.publication.PageEnvelopeException pee){
            System.out.println("SitetreeGenerator.setup: Could not use PageEnvelope.");
            throw new ProcessingException("SitetreeGenerator.setup: Could not use PageEnvelope.");
         }
         File indexFile;
         if(pub.getContentType().equalsIgnoreCase(FlatContent.TYPE)){
            // Flat Content
            // NOTE: Using Oresolver.resolveURI(getIndexFilename()) fails. Must create File first.
            indexFile = new File(pub.getContent().getIndexFilename(src, language));
            // System.out.println("SitetreeGenerator setup src=" + src + " " + (indexFile.exists() ? "exists" : "does not exist") + "\nFILE=" + indexFile.getAbsolutePath());
         }else{
            // Hierarchical Content
            indexFile = new File(pub.getContentDirectory(), src + File.separator + "sitetree.xml");
            if(!indexFile.exists()) indexFile = new File(pub.getContentDirectory(), "live" + File.separator + "sitetree.xml");
         }
         this.inputSource = super.resolver.resolveURI(indexFile.getPath());
      }catch(SourceException se){
         throw SourceUtil.handle("SitetreeGenerator SourceException resolving '" + src + "'.", se);
      }
   }
   /**
    * Generate the unique key. This key must be unique inside the space of this component.
    * 
    * @return The generated key hashes the src
    */
   public Serializable getKey() {
      return this.inputSource.getURI();
   }
   /**
    * Generate the validity object.
    * 
    * @return The generated validity object or <code>null</code> if the component is currently not cacheable.
    */
   public SourceValidity getValidity() {
      return this.inputSource.getValidity();
   }
   /**
    * Generate XML data.
    */
   public void generate() throws IOException, SAXException, ProcessingException {
      // System.out.println("SitetreeGenerator.generate");
      try{
         if(getLogger().isDebugEnabled()){
            getLogger().debug("Source " + super.source + " resolved to " + this.inputSource.getURI());
         }
         SourceUtil.parse(this.manager, this.inputSource, super.xmlConsumer);
      }catch(SAXException e){
         SourceUtil.handleSAXException(this.inputSource.getURI(), e);
      }
   }
}
