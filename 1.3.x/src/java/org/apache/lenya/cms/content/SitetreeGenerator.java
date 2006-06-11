package org.apache.lenya.cms.content.flat;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.*;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.xml.sax.XMLConsumer;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

import org.xml.sax.SAXException;
/*
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.XMLReaderFactory;
*/

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.generation.AbstractGenerator;

/**
 * The <code>SitetreeGenerator</code> is a class that reads XML from a source
 * and generates SAX Events.
 * The SitetreeGenerator implements the <code>CacheableProcessingComponent</code> interface.
 * 
 * @cocoon.sitemap.component.name   sitetree
 * @cocoon.sitemap.component.label  content
 * @cocoon.sitemap.component.logger sitemap.generator.sitetree
 * @cocoon.sitemap.component.documentation.caching
 * @cocoon.sitemap.component.pooling.min   8
 * @cocoon.sitemap.component.pooling.max  32
 * @cocoon.sitemap.component.pooling.grow  4
 *
 * @author <a href="mailto:solprovider@apache.org">Paul Ercolino</a>
 */
public class SitetreeGenerator extends ServiceableGenerator
implements CacheableProcessingComponent {

    /** The input source */
    protected Source inputSource;

    /**
     * Recycle this component.
     * All instance variables are set to <code>null</code>.
     */
    public void recycle() {
        if (null != this.inputSource) {
            super.resolver.release(this.inputSource);
            this.inputSource = null;
        }
        super.recycle();
    }

    /**
     * Setup the file generator.
     * Try to get the last modification date of the source for caching.
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
        throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, par);
        try {
   PageEnvelope envelope;
   String publication = "FAILED";
   Publication pub;
   String language = "en";
       try{
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
            pub = envelope.getPublication();
            publication = pub.getId();
            language = envelope.getDocument().getLanguage();
       }catch(org.apache.lenya.cms.publication.PageEnvelopeException pee){
            System.out.println("PEE EXCEPTION");
            throw new ProcessingException("Sitetree Generator: could not use PageEnvelope.");          
       } 
// Lenya1.3
       if(pub.getContentType().equalsIgnoreCase("flat")){
          this.inputSource = super.resolver.resolveURI(pub.getContent().getIndexFilename(src, language));
       }else{
// Lenya1.2
          File testfile = new File(pub.getContentDirectory(), src + File.separator + "sitetree.xml"); 
          if(!testfile.exists()) testfile = new File(pub.getContentDirectory(), "live" + File.separator + "sitetree.xml"); 
            this.inputSource = super.resolver.resolveURI(testfile.getPath());
          }
        } catch (SourceException se) {
            throw SourceUtil.handle("Error during resolving of '" + src + "'.", se);
        }
    }

    /**
     * Generate the unique key.
     * This key must be unique inside the space of this component.
     *
     * @return The generated key hashes the src
     */
    public Serializable getKey() {
        return this.inputSource.getURI();
    }

    /**
     * Generate the validity object.
     *
     * @return The generated validity object or <code>null</code> if the
     *         component is currently not cacheable.
     */
    public SourceValidity getValidity() {
        return this.inputSource.getValidity();
    }

    /**
     * Generate XML data.
     */
    public void generate()
        throws IOException, SAXException, ProcessingException {
        try {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Source " + super.source +
                                  " resolved to " + this.inputSource.getURI());
            }
           SourceUtil.parse(this.manager, this.inputSource, super.xmlConsumer);
        } catch (SAXException e) {
            SourceUtil.handleSAXException(this.inputSource.getURI(), e);
        }
    }


//DEV
   private void showMap(Map map){
         System.out.println("%%% MAP BEGIN %%%");
      Set keys = map.keySet();
      Iterator iterator = keys.iterator();
      while(iterator.hasNext()){
         Object key = iterator.next();
         System.out.println(key + " = " + map.get(key));
      }
         System.out.println("%%% MAP END %%%");
   }
   private void showParameters(Parameters parameters){
         System.out.println("%%% PARAMETERS BEGIN %%%");
      String[] names = parameters.getNames();
      int nlength = names.length;
      for(int i = 0; i < nlength; i++){
         try{
            System.out.println("PAR: " + names[i] + "=" + parameters.getParameter(names[i]));
         }catch(org.apache.avalon.framework.parameters.ParameterException pe){
            System.out.println("PAR: " + names[i] + " FAILURE");
         }
      }
         System.out.println("%%% PARAMETERS END %%%");
   }
   private String getModuleID(String uri) throws MalformedURLException{
        String module = "";
        int pos = uri.indexOf("modules/");
        if(pos > -1){
             pos += "modules/".length();
             int endpos = uri.indexOf("/", pos);
             if(endpos > -1){
               module = uri.substring(pos, endpos);
             }else module = uri.substring(pos);
        }
        return module;
   }
   private Publication getPublication(String publication, String servletContextPath){
      try{
         return PublicationFactory.getPublication(publication, servletContextPath);
      }catch(org.apache.lenya.cms.publication.PublicationException pe){
System.out.println("PNF: " + publication + " not found.");
         return (Publication) null;
      }
   }


}
